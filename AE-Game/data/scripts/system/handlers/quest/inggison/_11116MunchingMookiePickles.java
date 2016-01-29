/**
 * This file is part of aion-engine <aion-engine.com>
 *
 *  aion-engine is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-engine is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with aion-engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.inggison;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Leunam
 */
public class _11116MunchingMookiePickles extends QuestHandler {

	private final static int questId = 11116;
	private final static int[] npc_ids = { 798986, 798964, 203784, 203785 };

	public _11116MunchingMookiePickles() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(798986).addOnQuestStart(questId);
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
		if (targetId == 798986) {
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798986) {
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id())
					return sendQuestDialog(env, 5);
				else
					return sendQuestEndDialog(env);
			}
		}
		else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 798964) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 0)
						return sendQuestDialog(env, 1011);
				case SETPRO1:
					if (var == 0) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
			}
		}
		else if (targetId == 203784) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 1)
						return sendQuestDialog(env, 1352);
				case CHECK_USER_HAS_QUEST_ITEM:
					if (var == 1) {
						if (player.getInventory().getItemCountByItemId(182206790) >= 10) {
							removeQuestItem(env, 182206790, 10);
							if (!giveQuestItem(env, 182206791, 1))
							{}
								changeQuestStep(env, 1, 2, false);
							updateQuestStatus(env);
							return sendQuestDialog(env, 10000);
						}
						else
							return sendQuestDialog(env, 10001);
					}
			}
		}
		else if (targetId == 203785) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 2)
						return sendQuestDialog(env, 1693);
				case SET_SUCCEED:
					if (var == 2) {
						if (!giveQuestItem(env, 182206792, 1))
							qs.setQuestVarById(0, var + 1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
			}
		}
		return false;
	}
}
