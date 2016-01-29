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

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.StaticDoor;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import java.util.Map;

/**
 * @author xTz
 */
@InstanceID(300310000)
public class RaksangInstance extends GeneralInstanceHandler {

	private Map<Integer, StaticDoor> doors;
	private int generatorKilled;
	private int ashulagenKilled;
	private int gargoyleKilled;
	private int rakshaHelpersKilled;
	private boolean isInstanceDestroyed;

	@Override
	public void onDie(Npc npc) {
		switch (npc.getNpcId()) {
			case 730453:
			case 730454:
			case 730455:
			case 730456:
				generatorKilled ++;
				if (generatorKilled == 1) {
					sendMsg(1401133);
					doors.get(87).setOpen(true);
				}
				else if (generatorKilled == 2) {
					sendMsg(1401133);
					doors.get(167).setOpen(true);
				}
				else if (generatorKilled == 3) {
					sendMsg(1401133);
					doors.get(114).setOpen(true);
				}
				else if (generatorKilled == 4) {
					sendMsg(1401134);
					doors.get(165).setOpen(true);
				}
				despawnNpc(npc);
				break;
			case 217399:
			case 217400:
				isDeadKerops();
				break;
			case 217392:
				doors.get(103).setOpen(true);
				break;
			case 217469:
				doors.get(107).setOpen(true);
				break;
			case 217471:
			case 217472:
				gargoyleKilled++;
				if (gargoyleKilled == 2) {
					Npc magic = instance.getNpc(217473);
					if (magic != null) {
						sendMsg(1401159);
						magic.getEffectController().removeEffect(19126);
					}
				}
				despawnNpc(npc);
				break;
			case 217473:
				despawnNpc(npc);
				final Npc dust = (Npc) spawn(701075, 1068.630f, 967.205f, 138.785f, (byte) 0, 323);
				doors.get(105).setOpen(true);
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						if (!isInstanceDestroyed && dust != null && !NpcActions.isAlreadyDead(dust)) {
							NpcActions.delete(dust);
						}
					}

				}, 4000);
				break;
			case 217455:
				ashulagenKilled ++;
				if (ashulagenKilled == 1 || ashulagenKilled == 2 || ashulagenKilled == 3) {
					sendMsg(1401160);
				}
				else if (ashulagenKilled == 4) {
					spawn(217456, 615.081f, 640.660f, 524.195f, (byte) 0);
					sendMsg(1401135);
				}
				break;
			case 217425:
			case 217451:
			case 217456:
				rakshaHelpersKilled ++;
				if (rakshaHelpersKilled < 3) {
					sendMsg(1401161);
				}
				else if (rakshaHelpersKilled == 3) {
					sendMsg(1401162);
				}
				break;
			case 217647:
			case 217475:
				rakshaHelpersKilled = 4;
				break;
		}
	}

	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		doors.clear();
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		Npc melkennis = getNpc(217392);
		SkillEngine.getInstance().getSkill(melkennis, 19126, 60, melkennis).useNoAnimationSkill();
	}

	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}

	private boolean isDeadKerops() {
		Npc kerop1 = getNpc(217399);
		Npc kerop2 = getNpc(217400);
		if (isDead(kerop1) && isDead(kerop2)) {
			Npc melkennis = getNpc(217392);
			if (melkennis != null)
				melkennis.getEffectController().removeEffect(19126);
			return true;
		}
		return false;
	}

	private boolean isDead(Npc npc) {
		return (npc == null || npc.getLifeStats().isAlreadyDead());
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0
				: lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}

}
