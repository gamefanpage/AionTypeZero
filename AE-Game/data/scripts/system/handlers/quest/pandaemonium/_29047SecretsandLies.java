/*
 * This file is part of aion-engine <aion-engine.com>
 *
 * aion-engine is private software: you can redistribute it and or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Private Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with aion-engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.pandaemonium;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

//By Evil_dnk


public class _29047SecretsandLies extends QuestHandler
{
    private final static int questId = 29047;

    public _29047SecretsandLies() {
        super(questId);
    }

    @Override
    public void register() {

        qe.registerQuestNpc(204052).addOnQuestStart(questId);
        qe.registerQuestNpc(204052).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        int targetId = 0;

        if (env.getVisibleObject() instanceof Npc)
        targetId = ((Npc) env.getVisibleObject()).getNpcId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204052) {
               if (env.getDialogId() == 26) {
                  return sendQuestDialog(env, 1011);
               }
               else
                 return sendQuestStartDialog(env);
            }
        }
        if (qs == null)
            return false;

        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 204052:
                    switch (env.getDialogId()) {
                       case 26:
                           return sendQuestDialog(env, 2375);
                   case 1009:
                       qs.setStatus(QuestStatus.REWARD);
                       updateQuestStatus(env);
                       return sendQuestEndDialog(env);

            }
        }
        }

        if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204052)
                return sendQuestEndDialog(env);
       }
        return false;
    }
}

