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

package org.typezero.gameserver.services.abyss;

import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.stats.AbyssRankEnum;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author ATracer
 */
public class AbyssService {

	private static final int[] abyssMapList = {210050000, 220070000, 400010000, 600010000, 600020000, 600030000, 600040000, 600050000, 600060000,600090000, 600100000};

	/**
	 * @param player
	 */
	public static final boolean isOnPvpMap(Player player) {
		for (int i : abyssMapList) {
			if (i == player.getWorldId())
				return true;
		}
		return false;
	}

	/**
	 * @param victim
	 */
	public static final void rankedKillAnnounce(final Player victim) {

		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player p) {
				if (p != victim && victim.getWorldType() == p.getWorldType() && !p.isInInstance()) {
					PacketSendUtility.sendPacket(p, SM_SYSTEM_MESSAGE.STR_ABYSS_ORDER_RANKER_DIE(victim, AbyssRankEnum.getRankDescriptionId(victim)));
				}
			}
		});
	}

	public static final void rankerSkillAnnounce(final Player player, final int nameId) {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player p) {
				if (p != player && player.getWorldType() == p.getWorldType() && !p.isInInstance()) {
					PacketSendUtility.sendPacket(p, SM_SYSTEM_MESSAGE.STR_SKILL_ABYSS_SKILL_IS_FIRED(player, new DescriptionId(nameId)));
				}
			}
		});
	}
}
