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

package zone.pvpZones;

import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import static org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE.*;
import org.typezero.gameserver.services.player.PlayerReviveService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.knownlist.Visitor;
import org.typezero.gameserver.world.zone.SiegeZoneInstance;
import org.typezero.gameserver.world.zone.ZoneInstance;
import org.typezero.gameserver.world.zone.ZoneName;
import org.typezero.gameserver.world.zone.handler.AdvencedZoneHandler;


/**
 * @author MrPoke
 *
 */
public abstract class PvPZone implements AdvencedZoneHandler {

	@Override
	public void onEnterZone(Creature player, ZoneInstance zone) {
	}

	@Override
	public void onLeaveZone(Creature player, ZoneInstance zone) {
	}

	@Override
	public boolean onDie(final Creature lastAttacker, Creature target, final ZoneInstance zone) {
		if (!(target instanceof Player)) {
			return false;
		}

		final Player player = (Player) target;

		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0
			: lastAttacker.getObjectId()), true);
		if (zone instanceof SiegeZoneInstance) {
			((SiegeZoneInstance) zone).doOnAllPlayers(new Visitor<Player>() {

				@Override
				public void visit(Player p) {
					PacketSendUtility.sendPacket(p, STR_PvPZONE_OUT_MESSAGE(player.getName()));
				}
			});

			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					PlayerReviveService.duelRevive(player);
					doTeleport(player, zone.getZoneTemplate().getName());
					PacketSendUtility.sendPacket(player, STR_MSG_PvPZONE_MY_DEATH_TO_B(lastAttacker.getName()));
				}
			}, 5000);
		}
		return true;
	}

	protected abstract void doTeleport(Player player, ZoneName zoneName);
}
