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

import javolution.util.FastMap;
import org.typezero.gameserver.model.gameobjects.Kisk;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_BIND_POINT_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;

import java.util.Map;

/**
 * @author Sarynth, nrg
 */
public class KiskService {

	private static final KiskService instance = new KiskService();
	private final Map<Integer, Kisk> boundButOfflinePlayer = new FastMap<Integer, Kisk>().shared();
	private final Map<Integer, Kisk> ownerPlayer = new FastMap<Integer, Kisk>().shared();

	/**
	 * Remove kisk references and containers.
	 *
	 * @param kisk
	 */
	public void removeKisk(Kisk kisk) {
		//remove offline binds
		for (int memberId : kisk.getCurrentMemberIds()) {
			boundButOfflinePlayer.remove(memberId);
		}

		for (Integer obj : ownerPlayer.keySet()) {
			if (ownerPlayer.get(obj).equals(kisk)) {
				ownerPlayer.remove(obj);
				break;
			}
		}

		//send players SET_BIND_POINT and send them die packet again, if they lie dead, but are still not revived
		for (Player member : kisk.getCurrentMemberList()) {
			member.setKisk(null);
			PacketSendUtility.sendPacket(member, new SM_BIND_POINT_INFO(0, 0f, 0f, 0f, member));
			if (member.getLifeStats().isAlreadyDead())
				member.getController().sendDie();
		}
	}

	/**
	 * @param kisk
	 * @param player
	 */
	public void onBind(Kisk kisk, Player player) {
		if (player.getKisk() != null)
			player.getKisk().removePlayer(player);

		kisk.addPlayer(player);

		// Send Bind Point Data
		TeleportService2.sendSetBindPoint(player);

		// Send System Message
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_BINDSTONE_REGISTER);

		// Send Animated Bind Flash
		PacketSendUtility.broadcastPacket(player, new SM_LEVEL_UPDATE(player.getObjectId(), 2, player.getCommonData().getLevel()), true);
	}

	/**
	 * @param player
	 */
	public void onLogin(Player player) {
		Kisk kisk = this.boundButOfflinePlayer.get(player.getObjectId());
		if (kisk != null) {
			kisk.addPlayer(player);
			this.boundButOfflinePlayer.remove(player.getObjectId());
		}
	}

	public void onLogout(Player player) {
		Kisk kisk = player.getKisk();
		//store binding if existent
		if (kisk != null) {
			this.boundButOfflinePlayer.put(player.getObjectId(), kisk);
		}
	}

	public void regKisk(Kisk kisk, Integer objOwnerId) {
		ownerPlayer.put(objOwnerId, kisk);
	}

	public boolean haveKisk(Integer objOwnerId) {
		return ownerPlayer.containsKey(objOwnerId);
	}

	public static KiskService getInstance() {
		return instance;
	}
}
