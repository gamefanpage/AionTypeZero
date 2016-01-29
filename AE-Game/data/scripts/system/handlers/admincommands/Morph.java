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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_TRANSFORM;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author ATracer
 * @modified By aionchs- Wylovech
 */
public class Morph extends AdminCommand {

	public Morph() {
		super("morph");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length != 1) {
			PacketSendUtility.sendMessage(admin, "syntax //morph <NPC Id | cancel> ");
			return;
		}

		Player target = admin;
		int param = 0;

		if (admin.getTarget() instanceof Player)
			target = (Player) admin.getTarget();

		if (!("cancel").startsWith(params[0].toLowerCase())) {
			try {
				param = Integer.parseInt(params[0]);

			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "Parameter must be an integer, or cancel.");
				return;
			}
		}

		if ((param != 0 && param < 200000) || param > 900000) {
			PacketSendUtility.sendMessage(admin, "Something wrong with the NPC Id!");
			return;
		}

		target.getTransformModel().setModelId(param);
		PacketSendUtility.broadcastPacketAndReceive(target, new SM_TRANSFORM(target, true));

		if (param == 0) {
			if (target.equals(admin)) {
				PacketSendUtility.sendMessage(target, "Morph successfully cancelled.");
			}
			else {
				PacketSendUtility.sendMessage(target, "Your morph has been cancelled by " + admin.getName() + ".");
				PacketSendUtility.sendMessage(admin, "You have cancelled " + target.getName() + "'s morph.");
			}
		}
		else {
			if (target.equals(admin)) {
				PacketSendUtility.sendMessage(target, "Successfully morphed to npcId " + param + ".");
			}
			else {
				PacketSendUtility.sendMessage(target, admin.getName() + " morphs you into an NPC form.");
				PacketSendUtility.sendMessage(admin, "You morph " + target.getName() + " to npcId " + param + ".");
			}
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //morph <NPC Id | cancel> ");
	}
}
