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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.typezero.gameserver.model.account.PlayerAccountData;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.skill.PlayerSkillEntry;
import org.typezero.gameserver.model.team.legion.Legion;
import org.typezero.gameserver.model.team.legion.LegionMemberEx;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.services.LegionService;
import org.typezero.gameserver.utils.ChatUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;
import com.google.common.base.Predicate;

/**
 * @author lyahim @modified antness
 */
public class PlayerInfo extends AdminCommand {

	public PlayerInfo() {
		super("playerinfo");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(admin, "syntax //playerinfo <playername> <loc | item | group | skill | legion | ap | chars | knownlist[info|add|remove] | visual[see|notsee]> ");
			return;
		}

		Player target = World.getInstance().findPlayer(Util.convertName(params[0]));

		if (target == null) {
			PacketSendUtility.sendMessage(admin, "Selected player is not online!");
			return;
		}

		PacketSendUtility.sendMessage(
				admin,
				"\n[Info about " + target.getName() + "]\n-common: lv" + target.getLevel() + "("
				+ target.getCommonData().getExpShown() + " xp), " + target.getRace() + ", "
				+ target.getPlayerClass() + "\n-ip: " + target.getClientConnection().getIP() + "\n" + "-account name: "
				+ target.getClientConnection().getAccount().getName() + "\n");

		if (params.length < 2)
			return;

		if (params[1].equals("item")) {
			StringBuilder strbld = new StringBuilder("-items in inventory:\n");

			List<Item> items = target.getInventory().getItemsWithKinah();
			Iterator<Item> it = items.iterator();

			if (items.isEmpty())
				strbld.append("none\n");
			else
				while (it.hasNext()) {

					Item act = it.next();
					strbld.append("    " + act.getItemCount() + "(s) of " + ChatUtil.item(act.getItemTemplate().getTemplateId())
							+ "\n");
				}
			items.clear();
			items = target.getEquipment().getEquippedItemsWithoutStigma();
			it = items.iterator();
			strbld.append("-equipped items:\n");
			if (items.isEmpty())
				strbld.append("none\n");
			else
				while (it.hasNext()) {
					Item act = it.next();
					strbld.append("    " + act.getItemCount() + "(s) of " + ChatUtil.item(act.getItemTemplate().getTemplateId())
							+ "\n");
				}

			items.clear();
			items = target.getWarehouse().getItemsWithKinah();
			it = items.iterator();
			strbld.append("-items in warehouse:\n");
			if (items.isEmpty())
				strbld.append("none\n");
			else
				while (it.hasNext()) {
					Item act = it.next();
					strbld.append("    " + act.getItemCount() + "(s) of " + "[item:" + act.getItemTemplate().getTemplateId()
							+ "]" + "\n");
				}
			showAllLines(admin, strbld.toString());
		}
		else if (params[1].equals("group")) {
			final StringBuilder strbld = new StringBuilder("-group info:\n  Leader: ");

			PlayerGroup group = target.getPlayerGroup2();
			if (group == null)
				PacketSendUtility.sendMessage(admin, "-group info: no group");
			else {
				strbld.append(group.getLeader().getName() + "\n  Members:\n");
				group.applyOnMembers(new Predicate<Player>() {

					@Override
					public boolean apply(Player player) {
						strbld.append("    " + player.getName() + "\n");
						return true;
					}

				});
				PacketSendUtility.sendMessage(admin, strbld.toString());
			}

		}
		else if (params[1].equals("skill")) {
			StringBuilder strbld = new StringBuilder("-list of skills:\n");

			PlayerSkillEntry sle[] = target.getSkillList().getAllSkills();

			for (int i = 0; i < sle.length; i++)
				strbld.append("    level " + sle[i].getSkillLevel() + " of " + sle[i].getSkillName() + "\n");
			showAllLines(admin, strbld.toString());
		}
		else if (params[1].equals("loc")) {
			String chatLink = ChatUtil.position(target.getName(), target.getPosition());
			PacketSendUtility.sendMessage(
					admin,
					"- " + chatLink + "'s location:\n  mapid: " + target.getWorldId() + "\n  X: " + target.getX() + " Y: "
					+ target.getY() + "Z: " + target.getZ() + "heading: " + target.getHeading());
		}
		else if (params[1].equals("legion")) {
			StringBuilder strbld = new StringBuilder();

			Legion legion = target.getLegion();
			if (legion == null)
				PacketSendUtility.sendMessage(admin, "-legion info: no legion");
			else {
				ArrayList<LegionMemberEx> legionmemblist = LegionService.getInstance().loadLegionMemberExList(legion, null);
				Iterator<LegionMemberEx> it = legionmemblist.iterator();

				strbld.append("-legion info:\n  name: " + legion.getLegionName() + ", level: " + legion.getLegionLevel()
						+ "\n  members(online):\n");
				while (it.hasNext()) {
					LegionMemberEx act = it.next();
					strbld.append("    " + act.getName() + "(" + ((act.isOnline() == true) ? "online" : "offline") + ")"
							+ act.getRank().toString() + "\n");
				}
			}
			showAllLines(admin, strbld.toString());
		}
		else if (params[1].equals("ap")) {
			PacketSendUtility.sendMessage(admin, "AP info about " + target.getName());
			PacketSendUtility.sendMessage(admin, "Total AP = " + target.getAbyssRank().getAp());
			PacketSendUtility.sendMessage(admin, "Total Kills = " + target.getAbyssRank().getAllKill());
			PacketSendUtility.sendMessage(admin, "Today Kills = " + target.getAbyssRank().getDailyKill());
			PacketSendUtility.sendMessage(admin, "Today AP = " + target.getAbyssRank().getDailyAP());
		}
		else if (params[1].equals("chars")) {
			PacketSendUtility.sendMessage(admin, "Others characters of " + target.getName() + " (" + target.getClientConnection().getAccount().size() + ") :");

			Iterator<PlayerAccountData> data = target.getClientConnection().getAccount().iterator();
			while (data.hasNext()) {
				PlayerAccountData d = data.next();
				if (d != null && d.getPlayerCommonData() != null) {
					PacketSendUtility.sendMessage(admin, d.getPlayerCommonData().getName());
				}
			}
		}
		else if (params[1].equals("knownlist")) {
			if (params[2].equals("info")) {
				PacketSendUtility.sendMessage(admin, "KnownList of " + target.getName());

				for (VisibleObject obj : target.getKnownList().getKnownObjects().values())
					PacketSendUtility.sendMessage(admin, obj.getName() + " objectId:" + obj.getObjectId());
			}
			else if (params[2].equals("add")) {
				int objId = Integer.parseInt(params[3]);
				VisibleObject obj = World.getInstance().findVisibleObject(objId);
				if (obj != null && !target.getKnownList().getKnownObjects().containsKey(objId))
					target.getKnownList().getKnownObjects().put(objId, obj);
			}
			else if (params[2].equals("remove")) {
				int objId = Integer.parseInt(params[3]);
				VisibleObject obj = World.getInstance().findVisibleObject(objId);
				if (obj != null && target.getKnownList().getKnownObjects().containsKey(objId))
					target.getKnownList().getKnownObjects().remove(objId);
			}
		}
		else if (params[1].equals("visual")) {
			if (params[2].equals("info")) {
				PacketSendUtility.sendMessage(admin, "VisualList of " + target.getName());

				for (VisibleObject obj : target.getKnownList().getVisibleObjects().values())
					PacketSendUtility.sendMessage(admin, obj.getName() + " objectId:" + obj.getObjectId());
			}
			else if (params[2].equals("see")) {
				int objId = Integer.parseInt(params[3]);
				Player player = World.getInstance().findPlayer(objId);
				target.getController().see(player);
			}
			else if (params[2].equals("notsee")) {
				int objId = Integer.parseInt(params[3]);
				Player player = World.getInstance().findPlayer(objId);
				target.getController().notSee(player, true);
			}
		}
		else {
			PacketSendUtility.sendMessage(admin, "bad switch!");
			PacketSendUtility.sendMessage(admin, "syntax //playerinfo <playername> <loc | item | group | skill | legion | ap | chars | knownlist[info|add|remove] | visual[see|notsee]> ");
		}
	}

	private void showAllLines(Player admin, String str) {
		int index = 0;
		String[] strarray = str.split("\n");

		while (index < strarray.length - 20) {
			StringBuilder strbld = new StringBuilder();
			for (int i = 0; i < 20; i++, index++) {
				strbld.append(strarray[index]);
				if (i < 20 - 1)
					strbld.append("\n");
			}
			PacketSendUtility.sendMessage(admin, strbld.toString());
		}
		int odd = strarray.length - index;
		StringBuilder strbld = new StringBuilder();
		for (int i = 0; i < odd; i++, index++)
			strbld.append(strarray[index] + "\n");
		PacketSendUtility.sendMessage(admin, strbld.toString());
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //playerinfo <playername> <loc | item | group | skill | legion | ap | chars> ");
	}

}
