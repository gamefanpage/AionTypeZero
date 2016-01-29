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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.configs.administration.AdminConfig;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.DeniedStatus;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.network.aion.serverpackets.SM_VIEW_PLAYER_DETAILS;

/**
 * @author Avol
 */
public class CM_VIEW_PLAYER_DETAILS extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_VIEW_PLAYER_DETAILS.class);

	private int targetObjectId;

	public CM_VIEW_PLAYER_DETAILS(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		targetObjectId = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player player  = this.getConnection().getActivePlayer();
		VisibleObject obj = player.getKnownList().getObject(targetObjectId);
		if (obj == null) {
			// probably targetObjectId can be 0
			log.warn("CHECKPOINT: can't show player details for " + targetObjectId);
			return;
		}

		if (obj instanceof Player) {
			Player target = (Player) obj;

			if (!target.getPlayerSettings().isInDeniedStatus(DeniedStatus.VIEW_DETAILS) || player.getAccessLevel() >= AdminConfig.ADMIN_VIEW_DETAILS)
				sendPacket(new SM_VIEW_PLAYER_DETAILS(target.getEquipment().getEquippedItemsWithoutStigma(), target));
			else {
				sendPacket(SM_SYSTEM_MESSAGE.STR_MSG_REJECTED_WATCH(target.getName()));
				return;
			}
		}
	}
}
