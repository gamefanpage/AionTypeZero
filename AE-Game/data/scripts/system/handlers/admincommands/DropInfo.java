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

package admincommands;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.drop.Drop;
import org.typezero.gameserver.model.drop.DropGroup;
import org.typezero.gameserver.model.drop.NpcDrop;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Romanz
 */
public class DropInfo extends AdminCommand {

	public DropInfo() {
		super("dropinfo");
	}

	@Override
    public void execute(Player player, String... params) {
		Npc npc = (Npc) player.getTarget();
        NpcDrop npcDrop = null;
        if (params.length > 0) {
            int npcId = Integer.parseInt(params[0]);
            NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);
            if (npcTemplate == null){
                PacketSendUtility.sendMessage(player, "\u041d\u0435 \u0432\u0435\u0440\u043d\u043e\u0435 id: "+ npcId);
                return;
            }
            npcDrop = npcTemplate.getNpcDrop();
        }
        else {
            VisibleObject visibleObject = player.getTarget();

            if (visibleObject == null) {
                PacketSendUtility.sendMessage(player, "\u0412\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u0432\u044b\u0431\u0440\u0430\u0442\u044c \u0446\u0435\u043b\u044c !");
                return;
            }

            if (visibleObject instanceof Npc) {
                npcDrop = ((Npc)visibleObject).getNpcDrop();
            }
        }
        if (npcDrop == null){
            PacketSendUtility.sendMessage(player, "\u0412 \u0434\u0430\u043d\u043d\u043e\u043c \u041d\u041f\u0421 \u043e\u0442\u0441\u0443\u0442\u0441\u0442\u0432\u0443\u044e\u0442 \u0438\u0442\u0435\u043c\u044b (.");
            return;
        }

        int count = 0;
		PacketSendUtility.sendMessage(player, "\n[color:Rate:;1 0 0] x " + player.getRates().getDropRate());
        PacketSendUtility.sendMessage(player, "[color:ID ;0 1 1] [color:NPC :;0 1 1] " + npc.getNpcId() + " ");
        for (DropGroup dropGroup: npcDrop.getDropGroup()){
            PacketSendUtility.sendMessage(player, "\u0413\u0440\u0443\u043f\u043f\u0430 \u0438\u0442\u0435\u043c\u043e\u0432: "+ dropGroup.getGroupName());
            for (Drop drop : dropGroup.getDrop()){
                PacketSendUtility.sendMessage(player, "[item:" + drop.getItemId() + "]" + " ~ " + drop.getChance() + " [color:%;0 1 0] ");
                count ++;
            }
        }
        PacketSendUtility.sendMessage(player, " \u0412\u0441\u0435\u0433\u043e \u0438\u0442\u0435\u043c\u043e\u0432 : " + count);
    }

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
