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

package quest.eltnen;

import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Balthazar
 */

public class _1393NewFlightPath extends QuestHandler {

	private final static int questId = 1393;

	public _1393NewFlightPath() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204041).addOnQuestStart(questId);
		qe.registerQuestNpc(204041).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("LEPHARIST_BASTION_210020000"), questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204041) {
				switch (env.getDialog()) {
					case QUEST_SELECT: {
						return sendQuestDialog(env, 1011);
					}
					default:
						return sendQuestStartDialog(env);
				}
			}
		}
		if (qs == null)
			return false;

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 204041) {
				switch (env.getDialog()) {
					case SETPRO1: {
						player.setState(CreatureState.FLIGHT_TELEPORT);
						player.unsetState(CreatureState.ACTIVE);
						player.setFlightTeleportId(17001);
						PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 17001, 0));
						return defaultCloseDialog(env, 0, 1, true, false);
					}
					default:
						return closeDialogWindow(env);
				}
			}
		}

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204041) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						return sendQuestDialog(env, 5);
					}
					default:
						return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}
