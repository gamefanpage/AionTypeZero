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

package quest.verteron;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * Talk with Estino (203146). Use the transformation potion (182200505) at the entrance of the Tursin Totem Pole
 * (210030000) and scout the base. Scouting completed! Talk with Spatalos (203098). Talk with Meteina (203147). Lure the
 * Tursin Loudmouth Boss (210158) to Meteina (203147). Talk with Meteina (203147). Destroy the flags of the Tursin Totem
 * Pole (700037) (3). Use a flint (182200023) and set fire to the Tursin Totem Pole of Tursin Totem Pole (210030000).
 * Terminate either Ziloota the Seer (210697) or High Priest Munuka (216891). Lure out Tursin Loudmouth Boss (210158)
 * again. Talk with Spatalos (203098).
 *
 * @author Mr. Poke
 * @modified Rice
 * @reworked vlog
 */
public class _1019FlyingReconnaissance extends QuestHandler {

	private final static int questId = 1019;
	private final static int[] npcs = { 203146, 203098, 203147, 700037 };
	private final static int[] mobs = { 210697, 216891 };
	private final static int[] items = { 182200505, 182200023 };

	public _1019FlyingReconnaissance() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int npc : npcs)
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		for (int item : items)
			qe.registerQuestItem(item, questId);
		for (int mob : mobs)
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		qe.registerQuestNpc(210158).addOnAttackEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1130, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		final int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203146: // Estino
					if (var == 0 && env.getDialog() == DialogAction.QUEST_SELECT)
						return sendQuestDialog(env, 1011);
					if (env.getDialog() == DialogAction.SETPRO1)
						return defaultCloseDialog(env, 0, 1, 182200505, 1, 0, 0); // 1
					break;
				case 203098: // Spatalos
					if (var == 2 && env.getDialog() == DialogAction.QUEST_SELECT)
						return sendQuestDialog(env, 1352);
					if (env.getDialog() == DialogAction.SETPRO2)
						return defaultCloseDialog(env, 2, 3); // 3
					break;
				case 203147: // Meteina
					if (var == 3 && env.getDialog() == DialogAction.QUEST_SELECT)
						return sendQuestDialog(env, 1438);
					else if (var == 5 && env.getDialog() == DialogAction.QUEST_SELECT)
						return sendQuestDialog(env, 1693);
					if (env.getDialog() == DialogAction.SETPRO3)
						return defaultCloseDialog(env, 3, 4); // 4
					if (env.getDialog() == DialogAction.SETPRO4)
						return defaultCloseDialog(env, 5, 6, 182200023, 1, 0, 0); // 6
					break;
				case 700037: // Tursin Tribal Flag
					if (env.getDialog() == DialogAction.USE_OBJECT && var >= 6 && var < 9) {
						return useQuestObject(env, var, var + 1, false, 0, 0, 0, 0, 0, 0, true); // disappear
					}
					break;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203098) { // Spatalos
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 2034);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onAttackEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (targetId == 210158) // Tursin Loudmouth Boss
		{
			if (MathUtil.getDistance(env.getVisibleObject(), 1552.7401f, 1160.3622f, 114.06791f) <= 30) {
				if (qs.getQuestVarById(0) == 11) {
					playQuestMovie(env, 22);
					((Npc) env.getVisibleObject()).getController().onDie(player);
					qs.setQuestVar(10);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return true;
				}
				else if (qs.getQuestVarById(0) == 4) {
					playQuestMovie(env, 13);
					((Npc) env.getVisibleObject()).getController().onDie(player);
					qs.setQuestVarById(0, 5); // 5
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, final Item item) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return HandlerResult.UNKNOWN;
		int var = qs.getQuestVarById(0);

		final int id = item.getItemTemplate().getTemplateId();

		if (id == 182200505) // Transformation potion
			if (var == 1 && player.isInsideZone(ZoneName.get("TURSIN_OUTPOST_210030000")))
				return HandlerResult.fromBoolean(useQuestItem(env, item, 1, 2, false, 18));

		if (id == 182200023) // Flint
			if (var == 9 && player.isInsideZone(ZoneName.get("TURSIN_TOTEM_POLE_210030000")))
				return HandlerResult.fromBoolean(useQuestItem(env, item, 9, 10, false));
		return HandlerResult.FAILED;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		switch (targetId) {
			case 210697: // Ziloota the Seer
				return defaultOnKillEvent(env, targetId, 10, true);
			case 216891: // High Priest Munuka
				return defaultOnKillEvent(env, targetId, 10, true);
		}
		return false;
	}
}
