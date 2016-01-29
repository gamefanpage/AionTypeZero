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

package org.typezero.gameserver.utils.audit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.typezero.gameserver.configs.administration.AdminConfig;
import org.typezero.gameserver.model.ChatType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;

import javolution.util.FastMap;


/**
 * @author MrPoke
 *
 */
public class GMService {
	public static final GMService getInstance() {
		return SingletonHolder.instance;
	}

	private Map<Integer, Player> gms = new FastMap<Integer, Player>();
	private boolean announceAny = false;
	private List<Byte> announceList;
	private GMService() {
		announceList = new ArrayList<Byte>();
		announceAny = AdminConfig.ANNOUNCE_LEVEL_LIST.equals("*");
		if (!announceAny) {
			try {
				for (String level : AdminConfig.ANNOUNCE_LEVEL_LIST.split(","))
					announceList.add(Byte.parseByte(level));
			} catch (Exception e) {
				announceAny = true;
			}
		}
	}

	public Collection<Player> getGMs(){
		return gms.values();
	}
	public void onPlayerLogin(Player player){
		if (player.isGM()){
			gms.put(player.getObjectId(), player);

			if(announceAny)
				broadcastMesage("GM: "+player.getName()+ " logged in.");
			else if (announceList.contains(player.getAccessLevel()))
				broadcastMesage("GM: "+player.getName()+ " logged in.");
		}
	}

	public void onPlayerLogedOut(Player player){
		gms.remove(player.getObjectId());
	}

	public void broadcastMesage(String message){
		SM_MESSAGE packet = new SM_MESSAGE(0, null, message, ChatType.YELLOW);
		for (Player player : gms.values()){
			PacketSendUtility.sendPacket(player, packet);
		}
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final GMService instance = new GMService();
	}
}
