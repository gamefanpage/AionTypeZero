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

import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.skill.PlayerSkillList;
import org.typezero.gameserver.model.skill.PlayerSkillEntry;
import org.typezero.gameserver.services.SkillLearnService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;

/**
 * @author xTz
 */
public class DelSkill extends AdminCommand {

	public DelSkill() {
		super("delskill");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length < 1 || params.length > 2) {
			PacketSendUtility.sendMessage(admin, "No parameters detected.\n"
				+ "Please use //delskill <Player name> <all | skillId>\n" + "or use //delskill [target] <all | skillId>");
			return;
		}

		Player player;
		PlayerSkillList playerSkillList = null;
		String recipient = null;
		recipient = Util.convertName(params[0]);
		int skillId = 0;
		if (params.length == 2) {
			player = World.getInstance().findPlayer(recipient);
			if (player == null) {
				PacketSendUtility.sendMessage(admin, "The specified player is not online.");
				return;
			}

			if ("all".startsWith(params[1]))
				playerSkillList = player.getSkillList();
			else {
				try {
					skillId = Integer.parseInt(params[1]);
				}
				catch (NumberFormatException e) {
					PacketSendUtility.sendMessage(admin, "Param 1 must be an integer or <all>.");
					return;
				}

				if (!check(admin, player, skillId))
					return;
			}
			apply(admin, player, skillId, playerSkillList);

		}
		if (params.length == 1) {
			VisibleObject target = admin.getTarget();
			if (target == null) {
				PacketSendUtility.sendMessage(admin, "You should select a target first!");
				return;
			}

			if (target instanceof Player) {
				player = (Player) target;

				if ("all".startsWith(params[0]))
					playerSkillList = player.getSkillList();
				else {
					try {
						skillId = Integer.parseInt(params[0]);
					}
					catch (NumberFormatException e) {
						PacketSendUtility.sendMessage(admin, "Param 0 must be an integer or <all>.");
						return;
					}

					if (!check(admin, player, skillId))
						return;
				}
				if (target instanceof Player)
					apply(admin, player, skillId, playerSkillList);
			}
			else
				PacketSendUtility.sendMessage(admin, "This command can only be used on a player !");
		}
	}

	private static boolean check(Player admin, Player player, int skillId) {
		if (skillId != 0 && !player.getSkillList().isSkillPresent(skillId)) {
			PacketSendUtility.sendMessage(admin, "Player dont have this skill.");
			return false;
		}
		if (player.getSkillList().getSkillEntry(skillId).isStigma()) {
			PacketSendUtility.sendMessage(admin, "You can't remove stigma skill.");
			return false;
		}
		return true;
	}

	public void apply(Player admin, Player player, int skillId, PlayerSkillList playerSkillList) {
		if (skillId != 0) {
			SkillLearnService.removeSkill(player, skillId);
			PacketSendUtility.sendMessage(admin, "You have successfully deleted the specified skill.");
		}
		else {
			for (PlayerSkillEntry skillEntry : playerSkillList.getAllSkills()) {
				if (!skillEntry.isStigma()) {
					SkillLearnService.removeSkill(player, skillEntry.getSkillId());
				}
			}

			PacketSendUtility.sendMessage(admin, "You have success delete All skills.");
		}

	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "No parameters detected.\n"
			+ "Please use //delskill <Player name> <all | skillId>\n" + "or use //delskill [target] <all | skillId>");
	}
}
