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

package org.typezero.gameserver.network.aion.gmhandler;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;

public class CmdTeleportToNamed extends AbstractGMHandler {

	public CmdTeleportToNamed(Player admin, String params) {
		super(admin, params);
		run();
	}

	public void run() {
		int npcId = 0;
		String message = "";
		try {
			npcId = Integer.valueOf(params);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			onFail(admin, e.getMessage());
		}
		catch (NumberFormatException e) {
            String npcDesc = params;

            for (NpcTemplate template : DataManager.NPC_DATA.getNpcData().valueCollection()) {
                if (template.getDesc() != null && template.getDesc().equalsIgnoreCase(npcDesc)) {
                    TeleportService2.teleportToNpc(admin, template.getTemplateId());
                    message = "Teleporting to Npc: " + template.getTemplateId();
                    PacketSendUtility.sendMessage(admin, message);
                }
            }
        }

		if (npcId > 0) {
			if (!message.equals(""))
				message = "Teleporting to Npc: " + npcId + "\n" + message;
			else
				message = "Teleporting to Npc: " + npcId;
			PacketSendUtility.sendMessage(admin, message);
			TeleportService2.teleportToNpc(admin, npcId);
		}
	}

	public void onFail(Player admin, String message) {
		PacketSendUtility.sendMessage(admin, "syntax //movetonpc <npc_id|npc name>");
	}
}
