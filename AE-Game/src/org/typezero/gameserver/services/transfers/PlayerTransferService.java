/*
 * Copyright (c) 2015, TypeZero Engine (game.developpers.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of TypeZero Engine nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.typezero.gameserver.services.transfers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.configs.main.GSConfig;
import org.typezero.gameserver.configs.main.PlayerTransferConfig;
import org.typezero.gameserver.dao.LegionMemberDAO;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.loginserver.LoginServer;
import org.typezero.gameserver.network.loginserver.serverpackets.SM_PTRANSFER_CONTROL;
import org.typezero.gameserver.services.AccountService;
import org.typezero.gameserver.services.BrokerService;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.services.player.PlayerService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author KID
 */
public class PlayerTransferService {
	private final Logger log = LoggerFactory.getLogger(PlayerTransferService.class);
	private final Logger textLog = LoggerFactory.getLogger("PLAYERTRANSFER");

	private static PlayerTransferService instance = new PlayerTransferService();
	public static PlayerTransferService getInstance() {
		return instance;
	}

	private PlayerDAO dao;
	private Map<Integer, TransferablePlayer> transfers = FastMap.newInstance();
	private List<Integer> rsList = FastList.newInstance();

	public PlayerTransferService() {
		this.dao = DAOManager.getDAO(PlayerDAO.class);
		if(!PlayerTransferConfig.REMOVE_SKILL_LIST.equals("*")) {
			for(String skillId : PlayerTransferConfig.REMOVE_SKILL_LIST.split(","))
				rsList.add(Integer.parseInt(skillId));
		}
		log.info("PlayerTransferService loaded. With "+rsList.size()+" restricted skills.");
	}

	private String ptsnameitem = "ptsnameitem";

	public void onEnterWorld(Player player) {
		if(player.getName().endsWith(PlayerTransferConfig.NAME_PREFIX)) {
			PacketSendUtility.sendMessage(player, "You can add your oldnickname-friend to your friendlist!");
			if(!player.hasVar(ptsnameitem)) {
				long count = ItemService.addItem(player, 169670001, 1);
				if(count == 1) {
					PacketSendUtility.sendMessage(player, "Please empty your inventory and relogin again. After that you'll be able to receive item that allows you to change your name.");
				} else
					player.setVar(ptsnameitem, 1, true);
			}
		}
	}

	/**
	 * first method - sent to source server
	 */
	public void startTransfer(int accountId, int targetAccountId, int playerId, byte targetServerId, int taskId) {
		boolean exist = false;
		for(int id :  DAOManager.getDAO(PlayerDAO.class).getPlayerOidsOnAccount(accountId))
			if(id == playerId ) {
				exist = true;
				break;
			}

		if(!exist) {
			log.warn("transfer #"+taskId+" player "+playerId+" is not present on account "+accountId+".");
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.TASK_STOP, taskId, "player "+playerId+" is not present on account "+accountId));
			return;
		}

		if(DAOManager.getDAO(LegionMemberDAO.class).isIdUsed(playerId)) {
			log.warn("cannot transfer #"+taskId+" player with existing legion "+playerId+".");
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.TASK_STOP, taskId, "cannot transfer player with existing legion "+playerId));
			return;
		}

		PlayerCommonData common = dao.loadPlayerCommonData(playerId);
		if(common.isOnline()) {
			log.warn("cannot transfer #"+taskId+" online players "+playerId+".");
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.TASK_STOP, taskId, "cannot transfer online players "+playerId));
			return;
		}

		if(PlayerTransferConfig.REUSE_HOURS > 0 && common.getLastTransferTime() + PlayerTransferConfig.REUSE_HOURS * 3600000 > System.currentTimeMillis()) {
			log.warn("cannot transfer #"+taskId+" that player so often "+playerId+".");
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.TASK_STOP, taskId, "cannot transfer that player so often "+playerId));
			return;
		}

		Player player = PlayerService.getPlayer(playerId, AccountService.loadAccount(accountId));
		long kinah = player.getInventory().getKinah() + player.getWarehouse().getKinah();
		if(PlayerTransferConfig.MAX_KINAH > 0 && kinah >= PlayerTransferConfig.MAX_KINAH) {
			log.warn("cannot transfer #"+taskId+" players with "+kinah+" kinah in inventory/wh.");
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.TASK_STOP, taskId, "cannot transfer players with "+kinah+" kinah in inventory/wh."));
			return;
		}

		if(BrokerService.getInstance().hasRegisteredItems(player)) {
			log.warn("cannot transfer #"+taskId+" player while he own some items in broker.");
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.TASK_STOP, taskId, "cannot transfer player while he own some items in broker."));
			return;
		}

		TransferablePlayer tp = new TransferablePlayer(playerId, accountId, targetAccountId);
		tp.player = player;
		tp.targetServerId = targetServerId;
		tp.accountId = accountId;
		tp.targetAccountId = targetAccountId;
		tp.taskId = taskId;
		transfers.put(taskId, tp);

		textLog.info("taskId:"+taskId+"; [StartTransfer]");
		LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.CHARACTER_INFORMATION, tp));
	}

	/**
	 * sent from login to target server with character information from source server
	 */
	public void cloneCharacter(int taskId, int targetAccountId, String name, String account, byte[] db) {
		if(!PlayerService.isFreeName(name)) {
			if(PlayerTransferConfig.BLOCK_SAMENAME) {
				LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.ERROR, taskId, "Name is already in use"));
				return;
			}

			log.info("Name is already in use `"+name+"`");
			textLog.info("taskId:"+taskId+"; [CloneCharacter:!isFreeName]");
			String newName = name + PlayerTransferConfig.NAME_PREFIX;

			int i = 0;
			while(!PlayerService.isFreeName(newName)){
				newName = name + PlayerTransferConfig.NAME_PREFIX+i;
			}
			name = newName;
		}
		if(AccountService.loadAccount(targetAccountId).size() >= GSConfig.CHARACTER_LIMIT_COUNT) {
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.ERROR, taskId, "No free character slots"));
			return;
		}

		CMT_CHARACTER_INFORMATION acp = new CMT_CHARACTER_INFORMATION(0, State.CONNECTED);
		acp.setBuffer(ByteBuffer.wrap(db).order(ByteOrder.LITTLE_ENDIAN));
		Player cha = acp.readInfo(name, targetAccountId, account, rsList, textLog);

		if(cha == null) { //something went wrong!
			log.error("clone failed #"+taskId+" `"+name+"`");
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.ERROR, taskId, "unexpected sql error while creating a clone"));
		} else {
			DAOManager.getDAO(PlayerDAO.class).setPlayerLastTransferTime(cha.getObjectId(), System.currentTimeMillis());
			LoginServer.getInstance().sendPacket(new SM_PTRANSFER_CONTROL(SM_PTRANSFER_CONTROL.OK, taskId));
			log.info("clone successful #"+taskId+" `"+name+"`");
			textLog.info("taskId:"+taskId+"; [CloneCharacter:Done]");
		}
	}

	/**
	 * from login server to source, after response from target server
	 */
	public void onOk(int taskId) {
		TransferablePlayer tplayer = this.transfers.remove(taskId);
		textLog.info("taskId:"+taskId+"; [TransferComplete]");
		PlayerService.deletePlayerFromDB(tplayer.playerId);
	}

	/**
	 * from login server to source, after response from target server
	 */
	public void onError(int taskId, String reason) {
		this.transfers.remove(taskId);
		textLog.info("taskId:"+taskId+"; [Error. Transfer failed] "+reason);
	}
}
