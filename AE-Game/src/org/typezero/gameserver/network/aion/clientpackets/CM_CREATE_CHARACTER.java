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
import org.typezero.gameserver.configs.main.GSConfig;
import org.typezero.gameserver.configs.main.MembershipConfig;
import org.typezero.gameserver.dao.InventoryDAO;
import org.typezero.gameserver.model.Gender;
import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.account.Account;
import org.typezero.gameserver.model.account.PlayerAccountData;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerAppearance;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_CREATE_CHARACTER;
import org.typezero.gameserver.services.AccountService;
import org.typezero.gameserver.services.NameRestrictionService;
import org.typezero.gameserver.services.player.PlayerService;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.idfactory.IDFactory;
import java.sql.Timestamp;
import java.util.List;

/**
 * In this packets aion client is requesting creation of character.
 *
 * @author -Nemesiss-
 * @modified cura
 */
public class CM_CREATE_CHARACTER extends AionClientPacket {

    /**
     * Character appearance
     */
    private PlayerAppearance playerAppearance;
    /**
     * Player base data
     */
    private PlayerCommonData playerCommonData;

    private int step;

    /**
     * Constructs new instance of <tt>CM_CREATE_CHARACTER </tt> packet
     *
     * @param opcode
     */
    public CM_CREATE_CHARACTER(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl() {
        readD(); // ignored for now
        readS(); // something + accointId

        playerCommonData = new PlayerCommonData(IDFactory.getInstance().nextId());
        String name = Util.convertName(readS());

        playerCommonData.setName(name);

        readB(50 - (name.length() * 2)); // some shit? 2.5.x

        playerCommonData.setLevel(1);
        playerCommonData.setGender(readD() == 0 ? Gender.MALE : Gender.FEMALE);
        playerCommonData.setRace(readD() == 0 ? Race.ELYOS : Race.ASMODIANS);
        playerCommonData.setPlayerClass(PlayerClass.getPlayerClassById((byte) readD()));

        if (getConnection().getAccount().getMembership() >= MembershipConfig.STIGMA_SLOT_QUEST) {
            playerCommonData.setAdvencedStigmaSlotSize(11);
        }

        playerAppearance = new PlayerAppearance();

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
        step = readC();
    }

    /**
     * Actually does the dirty job
     */
    @Override
    protected void runImpl() {
        AionConnection client = getConnection();

        Account account = client.getAccount();

        /* Some reasons why player can' be created */
        if (client.getActivePlayer() != null) {
            return;
        }

        if (step == 1) {
            client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_CREATE_CHAR));
            return;
        }

        if (account.getMembership() >= MembershipConfig.CHARACTER_ADDITIONAL_ENABLE) {
            if (MembershipConfig.CHARACTER_ADDITIONAL_COUNT <= account.size()) {
                client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_SERVER_LIMIT_EXCEEDED));
                IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
                return;
            }
        }
        else if (GSConfig.CHARACTER_LIMIT_COUNT <= account.size()) {
            client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_SERVER_LIMIT_EXCEEDED));
            IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
            return;
        }
        if (!PlayerService.isFreeName(playerCommonData.getName())) {
            if (GSConfig.CHARACTER_CREATION_MODE == 2) {
                client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_NAME_RESERVED));
            }
            else {
                client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_NAME_ALREADY_USED));
            }
            IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
            return;
        }
        if (PlayerService.isOldName(playerCommonData.getName())) {
            client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_NAME_ALREADY_USED));
            IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
            return;
        }
        if (!NameRestrictionService.isValidName(playerCommonData.getName())) {
            client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_INVALID_NAME));
            IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
            return;
        }
        if (NameRestrictionService.isForbiddenWord(playerCommonData.getName())) {
            client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_FORBIDDEN_CHAR_NAME));
            IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
            return;
        }
        if (!playerCommonData.getPlayerClass().isStartingClass()) {
            client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.FAILED_TO_CREATE_THE_CHARACTER));
            IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
            return;
        }
        if (GSConfig.CHARACTER_CREATION_MODE == 0) {
            for (PlayerAccountData data : account.getSortedAccountsList()) {
                if (data.getPlayerCommonData().getRace() != playerCommonData.getRace()) {
                    client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.FAILED_TO_CREATE_THE_CHARACTER));
                    IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
                    return;
                }
            }
        }
        AccountService.removeDeletedCharacters(account);
        Player player = PlayerService.newPlayer(playerCommonData, playerAppearance, account);

        if (!PlayerService.storeNewPlayer(player, account.getName(), account.getId())) {
            client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_DB_ERROR));
            IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
        }
        else {
            List<Item> equipment = DAOManager.getDAO(InventoryDAO.class).loadEquipment(player.getObjectId());
            PlayerAccountData accPlData = new PlayerAccountData(playerCommonData, null, playerAppearance, equipment, null);

            accPlData.setCreationDate(new Timestamp(System.currentTimeMillis()));
            PlayerService.storeCreationTime(player.getObjectId(), accPlData.getCreationDate());

            account.addPlayerAccountData(accPlData);
            client.sendPacket(new SM_CREATE_CHARACTER(accPlData, SM_CREATE_CHARACTER.RESPONSE_OK));
        }
    }
}
