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

package org.typezero.gameserver.model.autogroup;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.instance.instancereward.PvPArenaReward;
import org.typezero.gameserver.network.aion.serverpackets.SM_AUTO_GROUP;
import org.typezero.gameserver.services.AutoGroupService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 *
 * @author xTz
 */
public class AutoPvPFFAInstance extends AutoInstance {

	@Override
	public AGQuestion addPlayer(Player player, SearchInstance searchInstance) {
		super.writeLock();
		try {
			if (!satisfyTime(searchInstance) || (players.size() >= agt.getPlayerSize())) {
				return AGQuestion.FAILED;
			}
			players.put(player.getObjectId(), new AGPlayer(player));
			return instance != null ? AGQuestion.ADDED : (players.size() == agt.getPlayerSize() ? AGQuestion.READY : AGQuestion.ADDED);
		}
		finally {
			super.writeUnlock();
		}
	}

	@Override
	public void onPressEnter(Player player) {
		super.onPressEnter(player);
		if (agt.isPvPFFAArena() || agt.isPvPSoloArena() || agt.isGloryArena()) {
			long size = 1;
			int itemId = 186000135;
			if (agt.isGloryArena()) {
				size = 3;
				itemId = 186000185;
			}
			if (!decrease(player, itemId, size)) {
				players.remove(player.getObjectId());
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 5));
				if (players.isEmpty()) {
					AutoGroupService.getInstance().unRegisterInstance(instance.getInstanceId());
				}
				return;
			}
		}
		((PvPArenaReward) instance.getInstanceHandler().getInstanceReward()).portToPosition(player);
		instance.register(player.getObjectId());
	}

	@Override
	public void onLeaveInstance(Player player) {
		super.unregister(player);
	}
	
}
