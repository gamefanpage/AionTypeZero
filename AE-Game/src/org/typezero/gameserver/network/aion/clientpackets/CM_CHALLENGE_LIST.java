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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.challenge.ChallengeType;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.services.ChallengeTaskService;
import org.typezero.gameserver.utils.audit.AuditLogger;

/**
 * @author Rolandas
 */
public class CM_CHALLENGE_LIST extends AionClientPacket {

	public CM_CHALLENGE_LIST(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	int action;
	int taskOwner;
	int ownerType;
	int playerId;
	int dateSince;

	@Override
	protected void readImpl() {
		action = readC();
		taskOwner = readD();
		ownerType = readC();
		playerId = readD();
		dateSince = readD();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if(ownerType == 1) {
			if(player.getLegion() == null) {
				AuditLogger.info(player, "Trying to receive legion challenge task without legion.");
				return;
			}
			ChallengeTaskService.getInstance().showTaskList(player, ChallengeType.LEGION, taskOwner);
		}
		else {
			ChallengeTaskService.getInstance().showTaskList(player, ChallengeType.TOWN, taskOwner);
		}
	}

}
