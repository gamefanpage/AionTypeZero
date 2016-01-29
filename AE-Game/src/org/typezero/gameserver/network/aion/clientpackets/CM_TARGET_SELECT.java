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

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Trap;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.TeamMember;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_TARGET_SELECTED;
import org.typezero.gameserver.network.aion.serverpackets.SM_TARGET_UPDATE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.audit.AuditLogger;

/**
 * Client Sends this packet when /Select NAME is typed.<br> I believe it's the
 * same as mouse click on a character.<br> If client want's to select target - d
 * is object id.<br> If client unselects target - d is 0;
 *
 * @author SoulKeeper, Sweetkr, KID
 */
public class CM_TARGET_SELECT extends AionClientPacket {

	/**
	 * Target object id that client wants to select or 0 if wants to unselect
	 */
	private int targetObjectId;
	private int type;

	/**
	 * Constructs new client packet instance.
	 *
	 * @param opcode
	 */
	public CM_TARGET_SELECT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * Read packet.<br> d - object id; c - selection type;
	 */
	@Override
	protected void readImpl() {
		targetObjectId = readD();
		type = readC();
	}

	/**
	 * Do logging
	 */
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();

		VisibleObject obj;
		if (targetObjectId == player.getObjectId())
			obj = player;
		else {
			obj = player.getKnownList().getObject(targetObjectId);

			if (obj == null && player.isInTeam()) {
				TeamMember<Player> member = player.getCurrentTeam().getMember(targetObjectId);
				if (member != null) {
					obj = member.getObject();
				}
			}
		}

		if (obj != null) {
			if (type == 1) {
				if (obj.getTarget() == null)
					return;
				player.setTarget(obj.getTarget());
			}
			else
				player.setTarget(obj);

			if (obj instanceof Player) {
				Player target = (Player) obj;
				if (player != obj && !player.canSee(target))
					AuditLogger.info(player, "Possible radar hacker detected, targeting on invisible Player name: "
							+ target.getName() + " objectId: " + target.getObjectId() + " by");
			}
			else if (obj instanceof Trap) {
				Trap target = (Trap) obj;
				boolean isSameTeamTrap = false;
				if (target.getMaster() instanceof Player)
					isSameTeamTrap = ((Player) target.getMaster()).isInSameTeam(player);
				if (player != obj && !player.canSee(target) && !isSameTeamTrap)
					AuditLogger.info(player, "Possible radar hacker detected, targeting on invisible Trap name: "
							+ target.getName() + " objectId: " + target.getObjectId() + " by");

			}
			else if (obj instanceof Creature) {
				Creature target = (Creature) obj;
				if (player != obj && !player.canSee(target))
					AuditLogger.info(player, "Possible radar hacker detected, targeting on invisible Npc name: "
							+ target.getName() + " objectId: " + target.getObjectId() + " by");
			}
		}
		else
			player.setTarget(null);

		sendPacket(new SM_TARGET_SELECTED(player));
		PacketSendUtility.broadcastPacket(player, new SM_TARGET_UPDATE(player));
	}

}
