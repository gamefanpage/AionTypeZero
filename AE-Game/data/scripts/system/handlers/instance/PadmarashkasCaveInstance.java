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

import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.skillengine.effect.AbnormalState;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.knownlist.Visitor;
import org.typezero.gameserver.world.zone.ZoneInstance;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Ritsu, Luzien
 * @see http://gameguide.na.aiononline.com/aion/Padmarashka%27s+Cave+Walkthrough
 */
@InstanceID(320150000)
public class PadmarashkasCaveInstance extends GeneralInstanceHandler {

	boolean moviePlayed = false;
	private int killedPadmarashkaProtector = 0;
	private int killedEggs = 0;

	@Override
	public void onDie(Npc npc) {
		final int npcId = npc.getNpcId();
		switch (npcId) {
			case 218670:
			case 218671:
			case 218673:
			case 218674:
				if (++killedPadmarashkaProtector == 4) {
					killedPadmarashkaProtector = 0;
					final Npc padmarashka = getNpc(218756);
					if (padmarashka != null && !padmarashka.getLifeStats().isAlreadyDead()) {
						padmarashka.getEffectController().unsetAbnormal(AbnormalState.SLEEP.getId());
						padmarashka.getEffectController().broadCastEffectsImp();
						SkillEngine.getInstance().getSkill(padmarashka, 19187, 55, padmarashka).useNoAnimationSkill();
						padmarashka.getEffectController().removeEffect(19186); //skill should handle this TODO: fix
						ThreadPoolManager.getInstance().schedule(new Runnable() {

							@Override
							public void run() {
								padmarashka.getAi2().onCreatureEvent(AIEventType.CREATURE_AGGRO, instance.getPlayersInside().get(0));
							}
						}, 1000);
					}
				}
				break;
			case 282613:
			case 282614:
				if (++killedEggs == 20) { //TODO: find value
					final Npc padmarashka = getNpc(218756);
					if (padmarashka != null && !padmarashka.getLifeStats().isAlreadyDead()) {
						SkillEngine.getInstance().applyEffectDirectly(20101, padmarashka, padmarashka, 0);
					}
				}
				break;
		}
	}

	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
		if (zone.getAreaTemplate().getZoneName() == ZoneName.get("PADMARASHKAS_NEST_320150000")) {
			if (!moviePlayed)
				sendMovie();
		}
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0
				: lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}

	private void sendMovie() {
		instance.doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 488));
				moviePlayed = true;
			}
		});
	}

	@Override
	public void onInstanceDestroy() {
		moviePlayed = false;
		killedPadmarashkaProtector = 0;
	}
}
