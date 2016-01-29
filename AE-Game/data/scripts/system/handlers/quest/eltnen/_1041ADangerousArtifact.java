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

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Xitanium
 * @reworked vlog
 */
public class _1041ADangerousArtifact extends QuestHandler {

	private final static int questId = 1041;
    String[] _zone = { "LF2_SENSORYAREA_Q1041_B_206042_23_210020000", "LF2_SENSORYAREA_Q1041_A_206040_21_210020000"};
	public _1041ADangerousArtifact() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 203901, 204015, 203833, 278500, 204042, 700181 };
		qe.registerGetingItem(182201011, questId);
		qe.registerOnLogOut(questId);
		qe.registerAddOnReachTargetEvent(questId);
		qe.registerAddOnLostTargetEvent(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
        qe.registerOnEnterZone(ZoneName.get("LF2_SENSORYAREA_Q1041_A_206040_21_210020000"), questId);

        qe.registerOnLevelUp(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

    @Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        if (zoneName != ZoneName.get("LF2_SENSORYAREA_Q1041_A_206040_21_210020000"))
            return false;
        final Player player = env.getPlayer();
        if (player == null)
            return false;
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null)
            return false;
        if (qs.getQuestVarById(0) == 2) {
            defaultFollowEndEvent(env, 2, 3, false);
            return true;
        }
        return false;
    }

    @Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();
		if (qs == null) {
			return false;
		}

		if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 203901: { // Telemachus
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
							else if (var == 3) {
								return sendQuestDialog(env, 1693);
							}
							else if (var == 6) {
								return sendQuestDialog(env, 2716);
							}
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
						case SETPRO3: {
							return defaultCloseDialog(env, 3, 4); // 4
						}
						case SETPRO6: {
							return defaultCloseDialog(env, 6, 7); // 7
						}
					}
					break;
				}
				case 204015: { // Civil Engineer
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
						}
						case SETPRO2: {
                            defaultStartFollowEvent(env, (Npc) env.getVisibleObject(), ZoneName.get("LF2_SENSORYAREA_Q1041_B_206042_23_210020000"), 1, 2);
                            return true;

                        }
					}
					break;
				}
				case 203833: { // Xenophon
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 4) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SETPRO4: {
							return defaultCloseDialog(env, 4, 5); // 5
						}
					}
					break;
				}
				case 278500: { // Yuditio
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 5) {
								return sendQuestDialog(env, 2375);
							}
						}
						case SETPRO5: {
							return defaultCloseDialog(env, 5, 6); // 6
						}
					}
					break;
				}
				case 204042: { // Laigas
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 7) {
								return sendQuestDialog(env, 3057);
							}
							else if (var == 9) {
								return sendQuestDialog(env, 3398);
							}
						}
						case SETPRO7: {
							giveQuestItem(env, 182201011, 1);
							return closeDialogWindow(env);
						}
						case SETPRO8: {
							changeQuestStep(env, 9, 9, true); // reward
							playQuestMovie(env, 38);
							return closeDialogWindow(env);
						}
					}
					break;
				}
				case 700181: { // Stolen Artifact
					if (dialog == DialogAction.USE_OBJECT) {
						return useQuestObject(env, 8, 9, false, 0, 0, 0, 182201011, 1); // 9
					}
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203901) { // Telemachus
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 3739);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onGetItemEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 7) {
				changeQuestStep(env, 7, 8, false); // 8
				return playQuestMovie(env, 37);
			}
		}
		return false;
	}

	@Override
	public boolean onLogOutEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 2) {
				changeQuestStep(env, 2, 1, false);
			}
		}
		return false;
	}

	@Override
	public boolean onNpcReachTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 2, 3, false); // 3
	}

	@Override
	public boolean onNpcLostTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 2, 1, false); // 1
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1300, true);
	}
}
