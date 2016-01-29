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

import org.typezero.gameserver.configs.main.AutoGroupConfig;
import org.typezero.gameserver.model.autogroup.EntryRequestType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.services.AutoGroupService;
import org.typezero.gameserver.services.instance.DredgionService2;
import org.typezero.gameserver.services.instance.KamarBattlefieldService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Shepper, Guapo, nrg
 */
public class CM_AUTO_GROUP extends AionClientPacket {

	private byte instanceMaskId;
	private byte windowId;
	private byte entryRequestId;

	public CM_AUTO_GROUP(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		instanceMaskId = (byte) readD();
		windowId = (byte) readC();
		entryRequestId = (byte) readC();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (!AutoGroupConfig.AUTO_GROUP_ENABLE) {
			PacketSendUtility.sendMessage(player, "Auto Group is disabled");
			return;
		}
		switch (windowId) {
			case 100:
				EntryRequestType ert = EntryRequestType.getTypeById(entryRequestId);
				if (ert == null) {
					return;
				}
				AutoGroupService.getInstance().startLooking(player, instanceMaskId, ert);
				break;
			case 101:
				AutoGroupService.getInstance().unregisterLooking(player, instanceMaskId);
				break;
			case 102:
				AutoGroupService.getInstance().pressEnter(player, instanceMaskId);
				break;
			case 103:
				AutoGroupService.getInstance().cancelEnter(player, instanceMaskId);
				break;
			case 104:
					DredgionService2.getInstance().showWindow(player, instanceMaskId);
					KamarBattlefieldService.getInstance().showWindow(player, instanceMaskId);
				break;
			case 105:
				// DredgionRegService.getInstance().failedEnterDredgion(player);
				break;
		}
	}

}
