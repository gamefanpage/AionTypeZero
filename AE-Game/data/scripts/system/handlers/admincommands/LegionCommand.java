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

import java.util.List;

import javolution.util.FastList;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.model.team.legion.Legion;
import org.typezero.gameserver.model.team.legion.LegionRank;
import org.typezero.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_MEMBER;
import org.typezero.gameserver.services.LegionService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;

/**
 * @author KID
 */
public class LegionCommand extends AdminCommand {
	private LegionService service;
	public LegionCommand() {
		super("legion");
		service = LegionService.getInstance();
	}

	@Override
	public void execute(Player player, String... params) {

		if(params[0].equalsIgnoreCase("disband")) {
			if(!verifyLenght(player, 2, params)) //legion disband NAME
				return;

			Legion legion = verifyLegionExists(player, params[1]);
			if(legion == null)
				return;

			service.disbandLegion(legion);
			PacketSendUtility.sendMessage(player, "legion "+legion.getLegionName()+" was disbanded.");
		}
		else if(params[0].equalsIgnoreCase("setlevel")) {
			if(!verifyLenght(player, 3, params)) //legion setlevel NAME level
				return;

			Legion legion = verifyLegionExists(player, params[1]);
			if(legion == null)
				return;

			byte level = -1;
			try {
				level = Byte.parseByte(params[2]);
			} catch(Exception e) { }

			if(level < 1 || level > 8) {
				PacketSendUtility.sendMessage(player, "1-8 legion level is allowed.");
				return;
			}
			else if(level == legion.getLegionLevel()) {
				PacketSendUtility.sendMessage(player, "legion "+params[1]+" is already with that level.");
				return;
			}

			int old = legion.getLegionLevel();
			service.changeLevel(legion, level, true);
			PacketSendUtility.sendMessage(player, "legion "+legion.getLegionName()+" has raised from "+old+" to "+level+" level.");
		}
		else if(params[0].equalsIgnoreCase("setpoints")) {
			if(!verifyLenght(player, 3, params)) //legion setpoints NAME points
				return;

			Legion legion = verifyLegionExists(player, params[1]);
			if(legion == null)
				return;

			long points = -1;
			try {
				points = Long.parseLong(params[2]);
			} catch(Exception e) { }

			if(points < 1 || points > Long.MAX_VALUE) {
				PacketSendUtility.sendMessage(player, "1-2.1bil points allowed.");
				return;
			}

			long old = legion.getContributionPoints();
			service.setContributionPoints(legion, points, true);
			PacketSendUtility.sendMessage(player, "legion "+legion.getLegionName()+" has raised from "+old+" to "+points+" contributiong points.");
		}
		else if(params[0].equalsIgnoreCase("setname")) {
			if(!verifyLenght(player, 3, params)) //legion setname NAME NEWNAME
				return;

			Legion legion = verifyLegionExists(player, params[1]);
			if(legion == null)
				return;

			if (!service.isValidName(params[2])) {
				PacketSendUtility.sendMessage(player, params[2]+" is incorrect for legion name!");
				return;
			}
			String old = legion.getLegionName();
			service.setLegionName(legion, params[2], true);
			PacketSendUtility.sendMessage(player, "legion "+old+" has changed name from "+old+" to "+params[2]+".");
		}
		else if(params[0].equalsIgnoreCase("info")) {
			if(!verifyLenght(player, 2, params)) //legion info NAME
				return;

			Legion legion = verifyLegionExists(player, params[1]);
			if(legion == null)
				return;

			FastList<String> message = FastList.newInstance(), online = FastList.newInstance(), offline = FastList.newInstance();
			message.add("name: "+legion.getLegionName());
			message.add("contrib points: "+legion.getContributionPoints());
			message.add("level: "+legion.getLegionLevel());
			message.add("id: "+legion.getLegionId());
			List<Integer> members = legion.getLegionMembers();
			message.add("members: "+members.size());

			PlayerDAO dao = null;
			for(int memberId : members) {
				Player pl = World.getInstance().findPlayer(memberId);
				if(pl != null)
					online.add(pl.getName()+" (lv"+pl.getLevel()+") classId "+pl.getPlayerClass().getClassId());
				else {
					if(dao == null)
						dao = DAOManager.getDAO(PlayerDAO.class);

					PlayerCommonData pcd = dao.loadPlayerCommonData(memberId);
					offline.add(pcd.getName()+" (lv"+pcd.getLevel()+") classId "+pcd.getPlayerClass().getClassId());
				}
			}

			message.add("--ONLINE-------- "+online.size());
			message.addAll(online);
			FastList.recycle(online);
			message.add("--OFFLINE-------- "+offline.size());
			message.addAll(offline);
			FastList.recycle(offline);

			for(String msg : message)
				PacketSendUtility.sendMessage(player, msg);

			FastList.recycle(message);
		}
		else if(params[0].equalsIgnoreCase("kick")) {
			if(!verifyLenght(player, 2, params)) //legion kick PLAYER
				return;

			Player target = World.getInstance().findPlayer(Util.convertName(params[1]));
			if(target == null) {
				PacketSendUtility.sendMessage(player, "player "+params[1]+" not exists.");
				return;
			}else if(target.getLegionMember().getRank() == LegionRank.BRIGADE_GENERAL) {
				PacketSendUtility.sendMessage(player, "player "+target.getName()+" is a brigade general. Disband legion!");
				return;
			}

			if(service.removePlayerFromLegionAsItself(target))
				PacketSendUtility.sendMessage(player, "player "+target.getName()+" was kicked from legion.");
			else
				PacketSendUtility.sendMessage(player, "You have failed to kick player "+target.getName()+" from legion.");
		}
		else if(params[0].equalsIgnoreCase("invite")) {
			if(!verifyLenght(player, 3, params)) //legion invite NAME PLAYER
				return;

			Legion legion = verifyLegionExists(player, params[1]);
			if(legion == null)
				return;

			Player target = World.getInstance().findPlayer(Util.convertName(params[2]));
			if(target == null) {
				PacketSendUtility.sendMessage(player, "player "+params[2]+" not exists.");
				return;
			}

			if(target.isLegionMember()) {
				PacketSendUtility.sendMessage(player, "player "+target.getName()+" is a already member of "+target.getLegion().getLegionName()+"!");
				return;
			}

			if(service.directAddPlayer(legion, target)) {
				PacketSendUtility.sendMessage(player, "player "+target.getName()+" was added to "+legion.getLegionName());
			} else {
				PacketSendUtility.sendMessage(player, "probably legion "+legion.getLegionName()+" is full");
			}
		}
		else if(params[0].equalsIgnoreCase("bg")) {
			if(!verifyLenght(player, 3, params)) //legion bg NAME PLAYER
				return;

			Legion legion = verifyLegionExists(player, params[1]);
			if(legion == null)
				return;

			Player target = World.getInstance().findPlayer(Util.convertName(params[2]));
			if(target == null) {
				PacketSendUtility.sendMessage(player, "player "+params[2]+" not exists.");
				return;
			}

			if(!legion.isMember(target.getObjectId())) {
				PacketSendUtility.sendMessage(player, "player "+target.getName()+" is not a member of "+legion.getLegionName()+", invite them!");
				return;
			}

			List<Integer> members = legion.getLegionMembers();
			Player bgplayer = null;
			for(int memberId : members) {
				Player pl = World.getInstance().findPlayer(memberId);
				if(pl != null) {
					if(pl.getLegionMember().getRank() == LegionRank.BRIGADE_GENERAL) {
						bgplayer = pl;
						break;
					}
				}
			}
			if(bgplayer == null) { //TODO
				PacketSendUtility.sendMessage(player, "You can't assign a new general while old is offline.");
				return;
			}

			bgplayer.getLegionMember().setRank(LegionRank.LEGIONARY);
			PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(bgplayer, 0, ""));
			PacketSendUtility.sendMessage(player, "You have sucessfully demoted " + bgplayer.getName() + " to Legionary rank.");
			target.getLegionMember().setRank(LegionRank.BRIGADE_GENERAL);
			PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(target, 0, ""));
			PacketSendUtility.sendMessage(player, "You have sucessfully promoted " + target.getName() + " to BG rank.");
		}
		else if(params[0].equalsIgnoreCase("help")) {
			this.onFail(player, null);
		}
		else if(params[0].equalsIgnoreCase("setrank")) {
			if(!verifyLenght(player, 3, params)) //legion setrank PLAYER RANK
				return;

			Player target = World.getInstance().findPlayer(Util.convertName(params[1]));
			if(target == null) {
				PacketSendUtility.sendMessage(player, "player "+params[1]+" not exists.");
				return;
			}

			if(!target.isLegionMember()) {
				PacketSendUtility.sendMessage(player, "player "+target.getName()+" is not a member of legion.");
				return;
			}

			if(params[2].equalsIgnoreCase("centurion")) {
				target.getLegionMember().setRank(LegionRank.CENTURION);
				PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(target, 0, ""));
				PacketSendUtility.sendMessage(player, "you have promoted player " + target.getName() + " as centurion.");
			} else if(params[2].equalsIgnoreCase("deputy")) {
				target.getLegionMember().setRank(LegionRank.DEPUTY);
				PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(target, 0, ""));
				PacketSendUtility.sendMessage(player, "you have promoted player " + target.getName() + " as deputy.");
			} else if(params[2].equalsIgnoreCase("legionary")) {
				target.getLegionMember().setRank(LegionRank.LEGIONARY);
				PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(target, 0, ""));
				PacketSendUtility.sendMessage(player, "you have promoted player " + target.getName() + " as legionary.");
			} else if(params[2].equalsIgnoreCase("volunteer")) {
				target.getLegionMember().setRank(LegionRank.VOLUNTEER);
				PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(target, 0, ""));
				PacketSendUtility.sendMessage(player, "you have promoted player " + target.getName() + " as volunteer.");
			} else {
				PacketSendUtility.sendMessage(player, "rank " + params[2] + " is not supported.");
			}
		}
	}

	private Legion verifyLegionExists(Player player, String name) {
		if(name.contains("_"))
			name = name.replaceAll("_", " ");
		Legion legion = service.getLegion(name.toLowerCase());
		if(legion == null) {
			PacketSendUtility.sendMessage(player, "legion "+name+" not exists.");
			return null;
		}
		return legion;
	}

	private boolean verifyLenght(Player player, int size, String... cmd) {
		boolean ok = cmd.length >= size;
		if(!ok)
			this.onFail(player, size+" parameters required for element //legion "+cmd[0]+".");

		return ok;
	}

	@Override
	public void onFail(Player player, String message) {
		if(message != null)
			PacketSendUtility.sendMessage(player, "FailReason: "+message);

		PacketSendUtility.sendMessage(player, "//legion info <legion name> : get list of legion members");
		PacketSendUtility.sendMessage(player, "//legion bg <legion name> <new bg name> : set a new brigade general to the legion");
		PacketSendUtility.sendMessage(player, "//legion kick <player name> : kick player to this legion");
		PacketSendUtility.sendMessage(player, "//legion invite <legion name> <player name> : add player to legion");
		PacketSendUtility.sendMessage(player, "//legion disband <legion name> : disbands legion");
		PacketSendUtility.sendMessage(player, "//legion setlevel <legion name> <level> : sets legion level");
		PacketSendUtility.sendMessage(player, "//legion setpoints <legion name> <points> : set contributing points");
		PacketSendUtility.sendMessage(player, "//legion setname <legion name> <new name> : change legion name");
	}
}
