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
import org.typezero.gameserver.model.skill.PlayerSkillEntry;
import org.typezero.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import org.typezero.gameserver.services.craft.*;
import org.typezero.gameserver.utils.*;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;

/**
 * @author synchro2
 */
public class RelinquishCraft extends AdminCommand {

	public RelinquishCraft() {
		super("relinquishcraft");
	}

	@Override
	public void execute(Player admin, String... params) {

		Player player;
		int skillId;
		boolean isExpert = false;
		String skillIdParam = "";
		String isExpertParam = "";

		if (params.length < 2 || params.length > 3) {
			PacketSendUtility.sendMessage(admin, "syntax //relinquishcraft <character_name | target> <skillId> <expert | master>");
			return;
		}

		if (params.length == 2) {
			VisibleObject target = admin.getTarget();
			if (target == null || !(target instanceof Player)) {
				PacketSendUtility.sendMessage(admin, "Select target first.");
				return;
			}

			player = (Player) target;
			skillIdParam = params[0];
			isExpertParam = params[1];

		}
		else {
			player = World.getInstance().findPlayer(Util.convertName(params[0]));
			skillIdParam = params[1];
			isExpertParam = params[2];

			if (player == null) {
				PacketSendUtility.sendMessage(admin, "The specified player is not online.");
				return;
			}
		}

		try {
			skillId = Integer.parseInt(skillIdParam);
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(admin, "You must using only numbers in skillId.");
			return;
		}

		if (!isExpertParam.equalsIgnoreCase("Expert") && !isExpertParam.equalsIgnoreCase("Master")) {
			PacketSendUtility.sendMessage(admin, "Only master or expert.");
			return;
		}
		if (isExpertParam.equalsIgnoreCase("Expert")) {
			isExpert = true;
		}
		if (isExpertParam.equalsIgnoreCase("Master")) {
			isExpert = false;
		}

		PlayerSkillEntry skill = player.getSkillList().getSkillEntry(skillId);
		int minValue = isExpert ? RelinquishCraftStatus.getExpertMinValue() : RelinquishCraftStatus.getMasterMinValue();
		int maxValue = isExpert ? RelinquishCraftStatus.getExpertMaxValue() : RelinquishCraftStatus.getMasterMaxValue();
		int skillMessageId = RelinquishCraftStatus.getSkillMessageId();

		if (!CraftSkillUpdateService.isCraftingSkill(skillId)) {
			PacketSendUtility.sendMessage(admin, "It's not skillId.");
			return;
		}

		if (skill == null || skill.getSkillLevel() < minValue || skill.getSkillLevel() > maxValue) {
			PacketSendUtility.sendMessage(admin, "Wrong skill level.");
			return;
		}

		skill.setSkillLvl(minValue);
		PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(skill, skillMessageId, false));
		RelinquishCraftStatus.removeRecipesAbove(player, skillId, minValue);
		RelinquishCraftStatus.deleteCraftStatusQuests(skillId, player, false);
		PacketSendUtility.sendMessage(admin, "Craft status successfull relinquished.");
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //relinquishcraft <character_name | target> <skillId> <expert | master>");
	}
}
