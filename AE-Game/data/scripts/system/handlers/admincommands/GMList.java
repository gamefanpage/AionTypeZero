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

import java.util.Collection;

import org.typezero.gameserver.configs.administration.AdminConfig;
import org.typezero.gameserver.model.gameobjects.player.FriendList;
import org.typezero.gameserver.model.gameobjects.player.FriendList.Status;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.audit.GMService;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Aion Gates
 */
public class GMList extends AdminCommand {

	public GMList() {
		super("gmlist");
	}

	@Override
	public void execute(Player admin, String... params) {

		String sGMNames = "";
		Collection<Player> gms = GMService.getInstance().getGMs();
		int GMCount = 0;

		for (Player pPlayer : gms) {
			if (pPlayer.isGM() && !pPlayer.isProtectionActive()
				&& pPlayer.getFriendList().getStatus() != FriendList.Status.OFFLINE) {
				String nameFormat = "%s";
				GMCount++;
				if (AdminConfig.CUSTOMTAG_ENABLE) {
					switch (pPlayer.getAccessLevel()) {
						case 1:
							nameFormat = AdminConfig.CUSTOMTAG_ACCESS1;
							break;
						case 2:
							nameFormat = AdminConfig.CUSTOMTAG_ACCESS2;
							break;
						case 3:
							nameFormat = AdminConfig.CUSTOMTAG_ACCESS3;
							break;
						case 4:
							nameFormat = AdminConfig.CUSTOMTAG_ACCESS4;
							break;
						case 5:
							nameFormat = AdminConfig.CUSTOMTAG_ACCESS5;
							break;
					}
				}

				sGMNames += String.format(nameFormat, pPlayer.getName()) + " : "
					+ returnStringStatus(pPlayer.getFriendList().getStatus()) + ";\n";
			}
		}

		if (GMCount == 0) {
			PacketSendUtility.sendMessage(admin, "There is no GM online !");
		}
		else if (GMCount == 1) {
			PacketSendUtility.sendMessage(admin, "There is " + GMCount + " GM online !");
		}
		else {
			PacketSendUtility.sendMessage(admin, "There are " + GMCount + " GMs online !");
		}
		if (GMCount != 0)
			PacketSendUtility.sendMessage(admin, "List : \n" + sGMNames);
	}

	private String returnStringStatus(Status p_status) {
		String return_string = "";
		if (p_status == FriendList.Status.ONLINE)
			return_string = "online";
		if (p_status == FriendList.Status.AWAY)
			return_string = "away";
		return return_string;
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
