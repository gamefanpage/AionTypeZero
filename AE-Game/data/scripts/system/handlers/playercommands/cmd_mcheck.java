/**
 * This file is part of Aion Eternity Core <Ver:4.5>.
 *
 * Aion Eternity Core is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Aion Eternity Core is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Aion Eternity Core. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package playercommands;

import java.util.Collection;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.PlayerCommand;

/**
 * Checks all LOCKED missions for start conditions immediately And starts them,
 * if conditions are fulfilled
 *
 * @author vlog
 */
public class cmd_mcheck extends PlayerCommand {

    public cmd_mcheck() {
        super("mcheck");
    }

    @Override
    public void execute(Player player, String... params) {
        Collection<QuestState> qsl = player.getQuestStateList().getAllQuestState();
        for (QuestState qs : qsl) {
            if (qs.getStatus() == QuestStatus.LOCKED) {
                int questId = qs.getQuestId();
                QuestEngine.getInstance().onLvlUp(new QuestEnv(null, player, questId, 0));
            }
        }
        PacketSendUtility.sendMessage(player, "Missions checked successfully");
    }

    @Override
    public void onFail(Player player, String message) {
        // TODO Auto-generated method stub
    }
}
