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
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team.legion.Legion;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_LEGION_INFO;
import org.typezero.gameserver.services.LegionService;
import org.typezero.gameserver.services.NameRestrictionService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Simple
 */
public class CM_LEGION extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_LEGION.class);

	/**
	 * exOpcode and the rest
	 */
	private int exOpcode;
	private short deputyPermission;
	private short centurionPermission;
	private short legionarPermission;
	private short volunteerPermission;
	private int rank;
	private String legionName;
	private String charName;
	private String newNickname;
	private String announcement;
	private String newSelfIntro;

	/**
	 * Constructs new instance of CM_LEGION packet
	 *
	 * @param opcode
	 */
	public CM_LEGION(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		exOpcode = readC();

		switch (exOpcode) {
			/** Create a legion **/
			case 0x00:
				readD(); // 00 78 19 00 40
				legionName = readS();
				break;
			/** Invite to legion **/
			case 0x01:
				readD(); // empty
				charName = readS();
				break;
			/** Leave legion **/
			case 0x02:
				readD(); // empty
				readH(); // empty
				break;
			/** Kick member from legion **/
			case 0x04:
				readD(); // empty
				charName = readS();
				break;
			/** Appoint a new Brigade General **/
			case 0x05:
				readD();
				charName = readS();
				break;
			/** Appoint Centurion **/
			case 0x06:
				rank = readD();
				charName = readS();
				break;
			/** Demote to Legionary **/
			case 0x07:
				readD(); // char id? 00 78 19 00 40
				charName = readS();
				break;
			/** Refresh legion info **/
			case 0x08:
				readD();
				readH();
				break;
			/** Edit announcements **/
			case 0x09:
				readD(); // empty or char id?
				announcement = readS();
				break;
			/** Change self introduction **/
			case 0x0A:
				readD(); // empty char id?
				newSelfIntro = readS();
				break;
			/** Edit permissions **/
			case 0x0D:
				deputyPermission = (short) readH();
				centurionPermission = (short) readH();
				legionarPermission = (short) readH();
				volunteerPermission = (short) readH();
				break;
			/** Level legion up **/
			case 0x0E:
				readD(); // empty
				readH(); // empty
				break;
			case 0x0F:
				charName = readS();
				newNickname = readS();
				break;
			default:
				log.info("Unknown Legion exOpcode? 0x" + Integer.toHexString(exOpcode).toUpperCase());
				break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		final Player activePlayer = getConnection().getActivePlayer();
		if (activePlayer.isLegionMember()) {
			final Legion legion = activePlayer.getLegion();

			if (charName != null) {
				LegionService.getInstance().handleCharNameRequest(exOpcode, activePlayer, charName, newNickname, rank);
			}
			else {
				switch (exOpcode) {
					/** Refresh legion info **/
					case 0x08:
						sendPacket(new SM_LEGION_INFO(legion));
						break;
					/** Edit announcements **/
					case 0x09:
						LegionService.getInstance().handleLegionRequest(exOpcode, activePlayer, announcement);
						break;
					/** Change self introduction **/
					case 0x0A:
						LegionService.getInstance().handleLegionRequest(exOpcode, activePlayer, newSelfIntro);
						break;
					/** Edit permissions **/
					case 0x0D:
						if (activePlayer.getLegionMember().isBrigadeGeneral())
							LegionService.getInstance().changePermissions(legion, deputyPermission, centurionPermission, legionarPermission, volunteerPermission);
						break;
					/** Misc. **/
					default:
						LegionService.getInstance().handleLegionRequest(exOpcode, activePlayer);
						break;
				}
			}
		}
		else {
			switch (exOpcode) {
				/** Create a legion **/
				case 0x00:
					if (NameRestrictionService.isForbiddenWord(legionName)) {
						PacketSendUtility.sendMessage(activePlayer, "You are trying to use a forbidden name. Choose another one!");
					}
					else {
						LegionService.getInstance().createLegion(activePlayer, legionName);
					}
					break;
			}
		}
	}
}
