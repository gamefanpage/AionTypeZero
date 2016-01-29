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

package org.typezero.gameserver.network.aion.gmhandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.typezero.gameserver.model.gameobjects.HouseObject;
import org.typezero.gameserver.model.gameobjects.UseableItemObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.ItemCooldown;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_COOLDOWN;
import org.typezero.gameserver.network.aion.serverpackets.SM_SKILL_COOLDOWN;
import org.typezero.gameserver.utils.PacketSendUtility;

public class CmdItemCoolTime extends AbstractGMHandler {

	public CmdItemCoolTime(Player admin) {
		super(admin, "");
		run();
	}

	private void run() {
		Player playerT = target != null ? target : admin;

		List<Integer> delayIds = new ArrayList<Integer>();
		if (playerT.getSkillCoolDowns() != null) {
			long currentTime = System.currentTimeMillis();
			for (Entry<Integer, Long> en : playerT.getSkillCoolDowns().entrySet()) {
				delayIds.add(en.getKey());
			}
			for (Integer delayId : delayIds) {
				playerT.setSkillCoolDown(delayId, currentTime);
			}
			delayIds.clear();
			PacketSendUtility.sendPacket(playerT, new SM_SKILL_COOLDOWN(playerT.getSkillCoolDowns()));
		}

		if (playerT.getItemCoolDowns() != null) {
			for (Entry<Integer, ItemCooldown> en : playerT.getItemCoolDowns().entrySet()) {
				delayIds.add(en.getKey());
			}
			for (Integer delayId : delayIds) {
				playerT.addItemCoolDown(delayId, 0, 0);
			}
			delayIds.clear();
			PacketSendUtility.sendPacket(playerT, new SM_ITEM_COOLDOWN(playerT.getItemCoolDowns()));
		}

		if (playerT.getHouseRegistry() != null && playerT.getHouseObjectCooldownList().getHouseObjectCooldowns().size() > 0) {
			Iterator<HouseObject<?>> iter = playerT.getHouseRegistry().getObjects().iterator();
			while (iter.hasNext()) {
				HouseObject<?> obj = iter.next();
				if (obj instanceof UseableItemObject) {
					if (!playerT.getHouseObjectCooldownList().isCanUseObject(obj.getObjectId()))
						playerT.getHouseObjectCooldownList().addHouseObjectCooldown(obj.getObjectId(), 0);
				}
			}
		}

	}

}
