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

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.PlayerAppearanceDAO;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.model.Gender;
import org.typezero.gameserver.model.account.Account;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerAppearance;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.player.PlayerEnterWorldService;
import org.typezero.gameserver.services.player.PlayerService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * In this packets aion client is requesting edit of character.
 *
 * @author IlBuono
 */
public class CM_CHARACTER_EDIT extends AionClientPacket {

	private int objectId;

	private boolean gender_change;

	private boolean check_ticket = true;

	/**
	 * Constructs new instance of <tt>CM_CREATE_CHARACTER </tt> packet
	 *
	 * @param opcode
	 */
	public CM_CHARACTER_EDIT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		AionConnection client = getConnection();
		Account account = client.getAccount();
		objectId = readD();
		readB(52);
		if (account.getPlayerAccountData(objectId) == null) {
			return;
		}
		Player player = PlayerService.getPlayer(objectId, account);
		if (player == null) {
			return;
		}
		PlayerCommonData playerCommonData = player.getCommonData();
		PlayerAppearance playerAppearance = player.getPlayerAppearance();
		// Before modify appearance, we do a check of ticket
		int gender = readD();
		gender_change = playerCommonData.getGender().getGenderId() == gender ? false : true;
        if (!gender_change) {
        	if (player.getInventory().getItemCountByItemId(169650000) == 0
        		&& player.getInventory().getItemCountByItemId(169650001) == 0
			    && player.getInventory().getItemCountByItemId(169650002) == 0
				&& player.getInventory().getItemCountByItemId(169650003) == 0
				&& player.getInventory().getItemCountByItemId(169650004) == 0
				&& player.getInventory().getItemCountByItemId(169650005) == 0
				&& player.getInventory().getItemCountByItemId(169650006) == 0
				&& player.getInventory().getItemCountByItemId(169650007) == 0) {
                check_ticket = false;
                return;
            }
        } else {
        	if (player.getInventory().getItemCountByItemId(169660000) == 0
        		&& player.getInventory().getItemCountByItemId(169660001) == 0
			    && player.getInventory().getItemCountByItemId(169660002) == 0
				&& player.getInventory().getItemCountByItemId(169660003) == 0) {
                check_ticket = false;
                return;
            }
        }
		playerCommonData.setGender(gender == 0 ? Gender.MALE : Gender.FEMALE);
		readD(); // race
		readD(); // player class

		playerAppearance.setVoice(readD());
		playerAppearance.setSkinRGB(readD());
		playerAppearance.setHairRGB(readD());
		playerAppearance.setEyeRGB(readD());
		playerAppearance.setLipRGB(readD());
		playerAppearance.setFace(readC());
		playerAppearance.setHair(readC());
		playerAppearance.setDeco(readC());
		playerAppearance.setTattoo(readC());
		playerAppearance.setFaceContour(readC());
		playerAppearance.setExpression(readC());
		readC(); // always 4 o0 // 5 in 1.5.x
		playerAppearance.setJawLine(readC());
		playerAppearance.setForehead(readC());

		playerAppearance.setEyeHeight(readC());
		playerAppearance.setEyeSpace(readC());
		playerAppearance.setEyeWidth(readC());
		playerAppearance.setEyeSize(readC());
		playerAppearance.setEyeShape(readC());
		playerAppearance.setEyeAngle(readC());

		playerAppearance.setBrowHeight(readC());
		playerAppearance.setBrowAngle(readC());
		playerAppearance.setBrowShape(readC());

		playerAppearance.setNose(readC());
		playerAppearance.setNoseBridge(readC());
		playerAppearance.setNoseWidth(readC());
		playerAppearance.setNoseTip(readC());

		playerAppearance.setCheek(readC());
		playerAppearance.setLipHeight(readC());
		playerAppearance.setMouthSize(readC());
		playerAppearance.setLipSize(readC());
		playerAppearance.setSmile(readC());
		playerAppearance.setLipShape(readC());
		playerAppearance.setJawHeigh(readC());
		playerAppearance.setChinJut(readC());
		playerAppearance.setEarShape(readC());
		playerAppearance.setHeadSize(readC());

		playerAppearance.setNeck(readC());
		playerAppearance.setNeckLength(readC());

		playerAppearance.setShoulderSize(readC());

		playerAppearance.setTorso(readC());
		playerAppearance.setChest(readC()); // only woman
		playerAppearance.setWaist(readC());
		playerAppearance.setHips(readC());

		playerAppearance.setArmThickness(readC());

		playerAppearance.setHandSize(readC());
		playerAppearance.setLegThicnkess(readC());

		playerAppearance.setFootSize(readC());
		playerAppearance.setFacialRate(readC());

		readC(); // always 0
		playerAppearance.setArmLength(readC());
		playerAppearance.setLegLength(readC()); // wrong??
		playerAppearance.setShoulders(readC()); // 1.5.x May be ShoulderSize
		playerAppearance.setFaceShape(readC());
		readC();
		readC();
		readC();
		playerAppearance.setHeight(readF());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    protected void runImpl() {
        AionConnection client = getConnection();
        PlayerEnterWorldService.enterWorld(client, objectId);
        Player player = client.getActivePlayer();
        if (!check_ticket) {
            if (!gender_change)
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_EDIT_CHAR_ALL_CANT_NO_ITEM);
            else
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_EDIT_CHAR_GENDER_CANT_NO_ITEM);
        } else {
            // Remove ticket and save appearance
            if (!gender_change) {
            	if (player.getInventory().getItemCountByItemId(169650000) > 0) { //Plastic Surgery Ticket
					player.getInventory().decreaseByItemId(169650000, 1);
				} else if (player.getInventory().getItemCountByItemId(169650001) > 0) { //[Event] Plastic Surgery Ticket
					player.getInventory().decreaseByItemId(169650001, 1);
				} else if (player.getInventory().getItemCountByItemId(169650002) > 0) { //[Special] Plastic Surgery Ticket
					player.getInventory().decreaseByItemId(169650002, 1);
				} else if (player.getInventory().getItemCountByItemId(169650003) > 0) { //[Special] Plastic Surgery Ticket
					player.getInventory().decreaseByItemId(169650003, 1);
				} else if (player.getInventory().getItemCountByItemId(169650004) > 0) { //Plastic Surgery Ticket (60 mins)
					player.getInventory().decreaseByItemId(169650004, 1);
				} else if (player.getInventory().getItemCountByItemId(169650005) > 0) { //Plastic Surgery Ticket (60 mins)
					player.getInventory().decreaseByItemId(169650005, 1);
				} else if (player.getInventory().getItemCountByItemId(169650006) > 0) { //[Event] Plastic Surgery Ticket
					player.getInventory().decreaseByItemId(169650006, 1);
				} else if (player.getInventory().getItemCountByItemId(169650007) > 0) { //[Event] Plastic Surgery Ticket
					player.getInventory().decreaseByItemId(169650007, 1);
				}
            } else {
            	if (player.getInventory().getItemCountByItemId(169660000) > 0) { //Gender Switch Ticket
					player.getInventory().decreaseByItemId(169660000, 1);
				} else if (player.getInventory().getItemCountByItemId(169660001) > 0) { //[Event] Gender Switch Ticket
					player.getInventory().decreaseByItemId(169660001, 1);
				} else if (player.getInventory().getItemCountByItemId(169660002) > 0) { //Gender Switch Ticket (60 min)
					player.getInventory().decreaseByItemId(169660002, 1);
				} else if (player.getInventory().getItemCountByItemId(169660003) > 0) { //[Event] Gender Switch Ticket
					player.getInventory().decreaseByItemId(169660003, 1);
				}
                DAOManager.getDAO(PlayerDAO.class).storePlayer(player); // save new gender
            }
            DAOManager.getDAO(PlayerAppearanceDAO.class).store(player); // save new appearance
        }
    }
}
