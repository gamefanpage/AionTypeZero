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

package quest.daevation;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author kecimis
 */
public class _2990MakingTheDaevanionWeapon extends QuestHandler {

	private final static int questId = 2990;
	private final static int[] npc_ids = { 204146 };

	public _2990MakingTheDaevanionWeapon() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204146).addOnQuestStart(questId);// Kanensa
		qe.registerQuestNpc(256617).addOnKillEvent(questId);// Strange Lake Spirit
		qe.registerQuestNpc(253720).addOnKillEvent(questId);// Lava Hoverstone
		qe.registerQuestNpc(254513).addOnKillEvent(questId);// Disturbed Resident
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204146)// Kanensa
			{
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					int plate = player.getEquipment().itemSetPartsEquipped(9);
					int chain = player.getEquipment().itemSetPartsEquipped(8);
					int leather = player.getEquipment().itemSetPartsEquipped(7);
					int cloth = player.getEquipment().itemSetPartsEquipped(6);
					int gunner = player.getEquipment().itemSetPartsEquipped(378);

					if (plate != 5 && chain != 5 && leather != 5 && cloth != 5 && gunner != 5)
						return sendQuestDialog(env, 4848);
					else
						return sendQuestDialog(env, 4762);
				}
				else
					return sendQuestStartDialog(env);
			}
		}

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int var1 = qs.getQuestVarById(1);

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 204146) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 0)
							return sendQuestDialog(env, 1011);
						if (var == 1)
							return sendQuestDialog(env, 1352);
						if (var == 2 && var1 == 60)
							return sendQuestDialog(env, 1693);
						if (var == 3 && player.getInventory().getItemCountByItemId(186000040) > 0)
							return sendQuestDialog(env, 2034);
					case CHECK_USER_HAS_QUEST_ITEM:
						if (var == 0) {
							if (QuestService.collectItemCheck(env, true)) {
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(env);
								return sendQuestDialog(env, 10000);
							}
							else
								return sendQuestDialog(env, 10001);
						}
						break;
					case SELECT_ACTION_1352:
						if (var == 0)
							return sendQuestDialog(env, 1352);
					case SELECT_ACTION_2035:
						if (var == 3) {
							if (player.getCommonData().getDp() == 4000 && player.getInventory().getItemCountByItemId(186000040) > 0) {
								removeQuestItem(env, 186000040, 1);
								player.getCommonData().setDp(0);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env, 5);
							}
							else
								return sendQuestDialog(env, 2120);
						}
						break;
					case SETPRO2:
						if (var == 1) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
						break;
					case SETPRO3:
						if (var == 2) {
							qs.setQuestVar(3);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
						break;
				}
			}
			return false;
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204146)// Kanensa
			{
				return sendQuestEndDialog(env);
			}
			return false;
		}
		return false;
	}

	@Override
  public boolean onKillEvent(QuestEnv env) {
      Player player = env.getPlayer();
      QuestState qs = player.getQuestStateList().getQuestState(questId);
      if (qs == null || qs.getStatus() != QuestStatus.START)
          return false;

      int var1 = qs.getQuestVarById(1);
      int var2 = qs.getQuestVarById(2);
      int var3 = qs.getQuestVarById(3);
      int targetId = env.getTargetId();

      if ((targetId == 256617 || targetId == 253720 || targetId == 254513) && qs.getQuestVarById(0) == 2) {
          switch (targetId) {
              case 256617:
                  if (var1 >= 0 && var1 < 60) {
                      ++var1;
                      qs.setQuestVarById(1, var1 + 1);
                      updateQuestStatus(env);
                  }
                  break;
              case 253720:
                  if (var2 >= 0 && var2 < 120) {
                      ++var2;
                      qs.setQuestVarById(2, var2 + 3);
                      updateQuestStatus(env);
                  }
                  break;
              case 254513:
                  if (var3 >= 0 && var3 < 240) {
                      ++var3;
                      qs.setQuestVarById(3, var3 + 7);
                      updateQuestStatus(env);
                  }
                  break;
          }
      }
      if (qs.getQuestVarById(0) == 2 && var1 == 60 && var2 == 120 && var3 == 240) {
          qs.setQuestVarById(1, 60);
          updateQuestStatus(env);
          return true;
      }
      return false;
  }
}
