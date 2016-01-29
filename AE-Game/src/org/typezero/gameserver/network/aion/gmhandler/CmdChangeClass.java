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

import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.PacketSendUtility;

public class CmdChangeClass extends AbstractGMHandler {

	public CmdChangeClass(Player admin, String params) {
		super(admin, params);
		run();
	}

	public void run() {
		Player t = target != null ? target : admin;
		byte classId;
		String ClassChoose = params;
		if (ClassChoose.equalsIgnoreCase("warrior")){
			classId = 0;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else if (ClassChoose.equalsIgnoreCase("fighter")){
			classId = 1;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else if (ClassChoose.equalsIgnoreCase("knight")) {
			classId = 2;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else if (ClassChoose.equalsIgnoreCase("scout")){
			classId = 3;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else if (ClassChoose.equalsIgnoreCase("assassin")){
			classId = 4;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else if (ClassChoose.equalsIgnoreCase("ranger")){
			classId = 5;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else if (ClassChoose.equalsIgnoreCase("mage")){
			classId = 6;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else if (ClassChoose.equalsIgnoreCase("wizard")){
			classId = 7;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else if (ClassChoose.equalsIgnoreCase("elementalist")) {
			classId = 8;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else if (ClassChoose.equalsIgnoreCase("cleric")) {
			classId = 10;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else if (ClassChoose.equalsIgnoreCase("priest")) {
			classId = 9;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else if (ClassChoose.equalsIgnoreCase("chanter")) {
			classId = 11;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else if (ClassChoose.equalsIgnoreCase("engineer"))   {
			classId = 12;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else if (ClassChoose.equalsIgnoreCase("rider")){
			classId = 13;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else if (ClassChoose.equalsIgnoreCase("gunner")) {
			classId = 14;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else if (ClassChoose.equalsIgnoreCase("artist")){
			classId = 15;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else if (ClassChoose.equalsIgnoreCase("bard")){
			classId = 16;
			PlayerClass playerClass = PlayerClass.getPlayerClassById(classId);
			admin.getCommonData().setPlayerClass(playerClass);
			admin.getController().upgradePlayer();
			PacketSendUtility.sendMessage(admin, "You have successfuly switched class");
		}else
		PacketSendUtility.sendMessage(admin, "Invalid class switch chosen!");
	}
}
