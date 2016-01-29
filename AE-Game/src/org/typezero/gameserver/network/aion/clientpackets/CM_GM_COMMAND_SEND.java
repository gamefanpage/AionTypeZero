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

package org.typezero.gameserver.network.aion.clientpackets;

import org.typezero.gameserver.configs.administration.AdminConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gm.GmPanelCommands;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.gmhandler.*;
import org.typezero.gameserver.network.aion.gmhandler.CmdLevelUpDown.LevelUpDownState;
import org.typezero.gameserver.utils.PacketSendUtility;

public class CM_GM_COMMAND_SEND extends AionClientPacket {

	private String cmd = "";
	private String params = "";
	private Player admin;

	public CM_GM_COMMAND_SEND(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		admin = getConnection().getActivePlayer();
		String clientCmd = readS();

		int index = clientCmd.indexOf(" ");

		cmd = clientCmd;
		if (index >= 0) {
			cmd = clientCmd.substring(0, index).toUpperCase();
			params = clientCmd.substring(index + 1);
		}
	}

	@Override
	protected void runImpl() {
		if (admin == null) {
			return;
		}

		// check accesslevel - not needed but to be sure
		if (admin.getAccessLevel() < AdminConfig.GM_PANEL) {
			return;
		}

		switch (GmPanelCommands.getValue(cmd)) {
			case REMOVE_SKILL_DELAY_ALL:
				//new CmdRemoveSkillDelayAll(admin); // Buggy
				break;
			case ITEMCOOLTIME:
				new CmdItemCoolTime(admin);
				break;
			case ATTRBONUS:
				new CmdAttrBonus(admin, params);
				break;
			case TELEPORTTO:
				new CmdTeleportTo(admin, params);
				break;
			case TELEPORT_TO_NAMED:
				new CmdTeleportToNamed(admin, params);
				break;
			case RESURRECT:
				new CmdResurrect(admin, "");
				break;
			case INVISIBLE:
				new CmdInvisible(admin, "");
				break;
			case VISIBLE:
				new CmdVisible(admin, "");
				break;
			case LEVELDOWN:
				new CmdLevelUpDown(admin, params, LevelUpDownState.DOWN);
				break;
			case LEVELUP:
				new CmdLevelUpDown(admin, params, LevelUpDownState.UP);
				break;
			case WISHID:
				new CmdWishId(admin, params);
				break;
			case DELETECQUEST:
				new CmdDeleteQuest(admin, params);
				break;
			case GIVETITLE:
				new CmdGiveTitle(admin, params);
				break;
			case DELETE_ITEMS:
				PacketSendUtility.sendMessage(admin, "Invalid command: " + cmd.toString());
				break;
			case CHANGECLASS:
				new CmdChangeClass(admin, params);
				break;
			case CLASSUP:
				new CmdChangeClass(admin, params);
				break;
			case WISH:
				new CmdWish(admin, params);
				break;
			case ADDQUEST:
                new CmdstartQuest(admin, params);
                break;
			case ENDQUEST:
                new CmdendQuest(admin, params);
                break;
            case ADDSKILL:
                new Cmdaddskill(admin, params);
                break;
			case SETINVENTORYGROWTH:
			case SKILLPOINT:
			case COMBINESKILL:
			case DELETESKILL:
			case ENCHANT100:
			case SEARCH:
			case BOOKMARK_ADD:
				PacketSendUtility.sendMessage(admin, "Invalid command: " + cmd.toString());
				break;
			case FREEFLY:
				PacketSendUtility.sendMessage(admin, "Freefly On");
				break;
			default:
				PacketSendUtility.sendMessage(admin, "Invalid command: " + cmd.toString());
				break;
		}
	}

}
