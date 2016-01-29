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

package org.typezero.gameserver.services;

import java.util.Iterator;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.dao.LegionDAO;
import org.typezero.gameserver.dao.OldNamesDAO;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_RENAME;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.player.PlayerService;
import org.typezero.gameserver.utils.audit.AuditLogger;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

/**
 * @author ATracer modified cura
 */
public class RenameService {

	public static boolean renamePlayer(Player player, String oldName, String newName, int item) {
		if (!NameRestrictionService.isValidName(newName)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400151));
			return false;
		}
		if (NameRestrictionService.isForbiddenWord(newName)) {
			PacketSendUtility.sendMessage(player, "You are trying to use a forbidden name. Choose another one!");
			return false;
		}
		if (!PlayerService.isFreeName(newName)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400155));
			return false;
		}
		if (player.getName().equals(newName)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400153));
			return false;
		}
		if (!CustomConfig.OLD_NAMES_COUPON_DISABLED && PlayerService.isOldName(newName)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400155));
			return false;
		}
		if ((player.getInventory().getItemByObjId(item).getItemId() != 169670000 && player.getInventory()
			.getItemByObjId(item).getItemId() != 169670001)
			|| (!player.getInventory().decreaseByObjectId(item, 1))) {
			AuditLogger.info(player, "Try rename youself without coupon.");
			return false;
		}
		if (!CustomConfig.OLD_NAMES_COUPON_DISABLED)
			DAOManager.getDAO(OldNamesDAO.class).insertNames(player.getObjectId(), player.getName(), newName);
		player.getCommonData().setName(newName);

		Iterator<Player> onlinePlayers = World.getInstance().getPlayersIterator();
		while (onlinePlayers.hasNext()) {
			Player p = onlinePlayers.next();
			if (p != null && p.getClientConnection() != null)
				PacketSendUtility.sendPacket(p, new SM_RENAME(player.getObjectId(), oldName, newName));
		}
		DAOManager.getDAO(PlayerDAO.class).storePlayer(player);

		return true;
	}

	public static boolean renameLegion(Player player, String name, int item) {
		if (!player.isLegionMember())
			return false;
		if (!LegionService.getInstance().isValidName(name)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400152));
			return false;
		}
		if (NameRestrictionService.isForbiddenWord(name)) {
			PacketSendUtility.sendMessage(player, "You are trying to use a forbidden name. Choose another one!");
			return false;
		}
		if (DAOManager.getDAO(LegionDAO.class).isNameUsed(name)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400156));
			return false;
		}
		if (player.getLegion().getLegionName().equals(name)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400154));
			return false;
		}
		if ((player.getInventory().getItemByObjId(item).getItemId() != 169680000 && player.getInventory()
			.getItemByObjId(item).getItemId() != 169680001)
			|| (!player.getInventory().decreaseByObjectId(item, 1))) {
			AuditLogger.info(player, "Try rename legion without coupon.");
			return false;
		}
		LegionService.getInstance().setLegionName(player.getLegion(), name, true);

		return true;
	}
}
