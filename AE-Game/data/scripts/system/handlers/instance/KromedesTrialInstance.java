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

package instance;

import java.util.ArrayList;
import java.util.List;

import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_MOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import static org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE.*;
import org.typezero.gameserver.network.aion.serverpackets.SM_TRANSFORM;
import org.typezero.gameserver.services.player.PlayerReviveService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMap;
import org.typezero.gameserver.world.zone.ZoneInstance;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author xTz, Gigi
 */
@InstanceID(300230000)
public class KromedesTrialInstance extends GeneralInstanceHandler {

	private int skillId;
	private List<Integer> movies = new ArrayList<Integer>();
	private boolean isSpawned = false;

	@Override
	public void onEnterInstance(Player player) {
		if (movies.contains(453)) {
			return;
		}
		skillId = player.getRace() == Race.ASMODIANS ? 19270 : 19220;
		sendMovie(player, 453);
	}

	@Override
	public void onLeaveInstance(Player player) {
		player.getEffectController().removeEffect(skillId);
	}

	@Override
	public void onPlayerLogOut(Player player) {
		player.setTransformed(false);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, false));
	}

	@Override
	public void onPlayMovieEnd(Player player, int movieId) {
		Storage storage = player.getInventory();
		switch (movieId) {
			case 453:
				SkillEngine.getInstance().applyEffectDirectly(skillId, player, player, 0);
				break;
			case 454:
				Npc npc1 = getNpc(730308);
				if (npc1 != null && MathUtil.isIn3dRange(player, npc1, 20)) {
					storage.decreaseByItemId(185000109, storage.getItemCountByItemId(185000109));
					TeleportService2.teleportTo(player, mapId, 687.56116f, 681.68225f, 200.28648f, (byte) 30);
				}
				break;
		}
	}

	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
		if (zone.getAreaTemplate().getZoneName() == ZoneName.get("MANOR_ENTRANCE_300230000")) {
			sendMovie(player, 462);
		}
		else if (zone.getAreaTemplate().getZoneName() == ZoneName.get("KALIGA_TREASURY_300230000")) {
			{
				if (!isSpawned) {
					isSpawned = true;
					Npc npc1 = getNpc(217002);
					Npc npc2 = getNpc(217000);
					Npc npc3 = getNpc(216982);
					if (isDead(npc1) && isDead(npc2) && isDead(npc3)) {
						spawn(217005, 669.214f, 774.387f, 216.88f, (byte) 60);
						spawn(217001, 663.8805f, 779.1967f, 216.26213f, (byte) 60);
						spawn(217003, 663.0468f, 774.6116f, 216.26215f, (byte) 60);
						spawn(217004, 663.0468f, 770.03815f, 216.26212f, (byte) 60);
					}
					else {
						spawn(217006, 669.214f, 774.387f, 216.88f, (byte) 60);
					}
				}
			}
		}
	}

	private boolean isDead(Npc npc) {
		return (npc == null || npc.getLifeStats().isAlreadyDead());
	}

	private void sendMovie(Player player, int movie) {
		if (!movies.contains(movie)) {
			movies.add(movie);
			PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, movie));
		}
	}

	@Override
	public void onInstanceDestroy() {
		movies.clear();
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0,
			player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}

	@Override
	public boolean onReviveEvent(Player player) {
		WorldMap map = World.getInstance().getWorldMap(player.getWorldId());
		if (map == null) {
			PlayerReviveService.bindRevive(player);
			return true;
		}
		PlayerReviveService.revive(player, 25, 25, true, 0);
		PacketSendUtility.sendPacket(player, STR_REBIRTH_MASSAGE_ME);
		player.getGameStats().updateStatsAndSpeedVisually();
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
		TeleportService2.teleportTo(player, player.getWorldId(), 687.56f, 681.68f, 200.28f);
		SkillEngine.getInstance().applyEffectDirectly(skillId, player, player, 0);
		player.unsetResPosState();
		return true;
	}
}
