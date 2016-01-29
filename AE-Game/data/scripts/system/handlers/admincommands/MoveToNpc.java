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

package admincommands;;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author MrPoke, lord_rex and ginho1
 */
public class MoveToNpc extends AdminCommand {

	public MoveToNpc() {
		super("movetonpc");
	}

	@Override
	public void execute(Player player, String... params) {
		int npcId = 0;
		String message = "";
		try {
			npcId = Integer.valueOf(params[0]);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			onFail(player, e.getMessage());
		}
		catch (NumberFormatException e) {
			String npcName = "";

			for(int i = 0; i < params.length; i++)
				npcName += params[i]+" ";
			npcName = npcName.substring(0, npcName.length() - 1);

			for(NpcTemplate template : DataManager.NPC_DATA.getNpcData().valueCollection()) {
				if(template.getName().equalsIgnoreCase(npcName)) {
					if(npcId == 0)
						npcId = template.getTemplateId();
					else {
						if(message.equals(""))
							message += "Found others ("+npcName+"): \n";
						message += "Id: "+template.getTemplateId()+"\n";
					}
				}
			}
			if(npcId == 0) {
				PacketSendUtility.sendMessage(player, "NPC " + npcName + " cannot be found");
			}
		}

		if(npcId > 0) {
			message = "Teleporting to Npc: "+npcId+"\n"+message;
			PacketSendUtility.sendMessage(player, message);
			TeleportService2.teleportToNpc(player, npcId);
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //movetonpc <npc_id|npc name>");
	}
}
