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

package org.typezero.gameserver.network.aion.serverpackets;

import java.util.List;

import org.typezero.gameserver.configs.administration.AdminConfig;
import org.typezero.gameserver.model.actions.PlayerMode;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerAppearance;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.model.items.GodStone;
import org.typezero.gameserver.model.items.ItemSlot;
import org.typezero.gameserver.model.stats.calc.Stat2;
import org.typezero.gameserver.model.team.legion.LegionEmblemType;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.services.AccessLevelEnum;
import org.typezero.gameserver.services.WeddingService;

/**
 * This packet is displaying visible players.
 *
 * @author -Nemesiss-, Avol, srx47 modified cura
 * @modified -Enomine- -Artur-
 */
public class SM_PLAYER_INFO extends AionServerPacket {

    /**
     * Visible player
     */
    private final Player player;
    private boolean enemy;

    /**
     * Constructs new <tt>SM_PLAYER_INFO </tt> packet
     *
     * @param player actual player.
     * @param enemy
     */
    public SM_PLAYER_INFO(Player player, boolean enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeImpl(AionConnection con) {
        Player activePlayer = con.getActivePlayer();
        if (activePlayer == null || player == null) {
            return;
        }
        PlayerCommonData pcd = player.getCommonData();
        final int raceId;
        if (player.getAdminNeutral() > 1 || activePlayer.getAdminNeutral() > 1) {
            raceId = activePlayer.getRace().getRaceId();
        } else if (activePlayer.isEnemy(player)) {
            raceId = (activePlayer.getRace().getRaceId() == 0 ? 1 : 0);
        } else {
            raceId = player.getRace().getRaceId();
        }

        final int genderId = pcd.getGender().getGenderId();
        final PlayerAppearance playerAppearance = player.getPlayerAppearance();

        writeF(player.getX());// x
        writeF(player.getY());// y
        writeF(player.getZ());// z
        writeD(player.getObjectId());
        /**
         * A3 female asmodian A2 male asmodian A1 female elyos A0 male elyos
         */
        writeD(pcd.getTemplateId());
		writeD(player.getRobotId());//rider id new 4.5
        /**
         * Transformed state - send transformed model id Regular state - send
         * player model id (from common data)
         */
        int model = player.getTransformModel().getModelId();
        writeD(model != 0 ? model : pcd.getTemplateId());
        writeC(0x00); // new 2.0 Packet --- probably pet info?
        writeD(player.getTransformModel().getType().getId());
        writeC(enemy ? 0x00 : 0x26);

        writeC(raceId); // race
        writeC(pcd.getPlayerClass().getClassId());
        writeC(genderId); // sex
        writeH(player.getState());

        writeB(new byte[8]);

        writeC(player.getHeading());

        String nameFormat = "%s";
        StringBuilder sb = new StringBuilder(nameFormat);
        // orphaned players - later find/remove them
        if (player.getClientConnection() != null) {
            if (AdminConfig.CUSTOMTAG_ENABLE && player.getAccessLevel() > 0) {
                String al = AccessLevelEnum.getAlName(player.getAccessLevel());
                nameFormat = sb.insert(0, al.substring(0, al.length() - 3)).toString();
            }
            if (player.isMarried()) {
                if (player.hasVar("MarryName") && player.getVar("MarryName").toString().equals("on")) {
                    nameFormat += "\uE020" + player.getPartnerName();
                }
                if (player.hasVar("MarryName") && player.getVar("MarryName").toString().equals("heart")) {
                    nameFormat += "\uE020";
                }
            }
            if (!player.isMarried()) {
                if (player.hasVar("MarryName")) {
                    player.delVar("MarryName", true);
                }
            }
        }
        /* Icon Membership & VIP
        if (player.getClientConnection().getAccount().getMembership() == 1) {
            nameFormat = "\uE0B0" + player.getName();
        }
        if (player.getClientConnection().getAccount().getMembership() == 2) {
            nameFormat = "\uE0AE" + player.getName();
        }*/
        writeS(String.format(nameFormat, player.getName()));

        writeH(pcd.getTitleId());
        writeH(player.getCommonData().isHaveMentorFlag() ? 1 : 0);

        writeH(player.getCastingSkillId());

        if (player.isLegionMember()) {
            writeD(player.getLegion().getLegionId());
            writeC(player.getLegion().getLegionEmblem().getEmblemId());
            writeC(player.getLegion().getLegionEmblem().getEmblemType().getValue());
            writeC(player.getLegion().getLegionEmblem().getEmblemType() == LegionEmblemType.DEFAULT ? 0x00 : 0xFF);
            writeC(player.getLegion().getLegionEmblem().getColor_r());
            writeC(player.getLegion().getLegionEmblem().getColor_g());
            writeC(player.getLegion().getLegionEmblem().getColor_b());
            writeS(AccessLevelEnum.getStatusName(player));
        } else {
            if (player.getAccessLevel() > 0) {
                writeD(0);
                writeC(0);
                writeC(0);
                writeC(0);
                writeC(0);
                writeC(0);
                writeC(0);
                writeS(AccessLevelEnum.getStatusName(player));
            } else {
                writeB(new byte[12]);
            }
        }
        int maxHp = player.getLifeStats().getMaxHp();
        int currHp = player.getLifeStats().getCurrentHp();
        writeC(100 * currHp / maxHp);// %hp
        writeH(pcd.getDp());// current dp
        writeC(0x00);// unk (0x00)

        List<Item> items = player.getEquipment().getEquippedForApparence();
        int mask = 0;
        for (Item item : items) {
            if (item.getItemTemplate().isTwoHandWeapon()) {
                ItemSlot[] slots = ItemSlot.getSlotsFor(item.getEquipmentSlot());
                mask |= slots[0].getSlotIdMask();
            } else {
                mask |= item.getEquipmentSlot();
            }
        }
        writeD(mask); // Wrong !!! It's item count, but doesn't work

        for (Item item : items) {
            writeD(item.getItemSkinTemplate().getTemplateId());
            GodStone godStone = item.getGodStone();
            writeD(godStone != null ? godStone.getItemId() : 0);
            writeD(item.getItemColor());
            if (item.getAuthorize() > 0 && item.getItemTemplate().isAccessory()) {
                if (item.getItemTemplate().isPlume()) {
                    float aLvl = item.getAuthorize() / 5;
                    if (item.getAuthorize() >= 5) {
                        aLvl = aLvl > 2.0f ? 2.0f : aLvl;
                        writeD((int) aLvl << 3);
                    } else {
                        writeD(0);
                    }
                } else {
                    writeD(item.getAuthorize() >= 5 ? 2 : 0);
                }
            } else {
                if (item.getItemTemplate().isWeapon() || item.getItemTemplate().isTwoHandWeapon()) {
                    writeD(item.getEnchantLevel() == 15 ? 2 : item.getEnchantLevel() >= 20 ? 4 : 0);
                } else {
                    writeD(0);
                }
            }
        }

        writeD(playerAppearance.getSkinRGB());
        writeD(playerAppearance.getHairRGB());
        writeD(playerAppearance.getEyeRGB());
        writeD(playerAppearance.getLipRGB());
        writeC(playerAppearance.getFace());
        writeC(playerAppearance.getHair());
        writeC(playerAppearance.getDeco());
        writeC(playerAppearance.getTattoo());
        writeC(playerAppearance.getFaceContour());
        writeC(playerAppearance.getExpression());

        writeC(5); // unk 0x05 0x06

        writeC(playerAppearance.getJawLine());
        writeC(playerAppearance.getForehead());

        writeC(playerAppearance.getEyeHeight());
        writeC(playerAppearance.getEyeSpace());
        writeC(playerAppearance.getEyeWidth());
        writeC(playerAppearance.getEyeSize());
        writeC(playerAppearance.getEyeShape());
        writeC(playerAppearance.getEyeAngle());

        writeC(playerAppearance.getBrowHeight());
        writeC(playerAppearance.getBrowAngle());
        writeC(playerAppearance.getBrowShape());

        writeC(playerAppearance.getNose());
        writeC(playerAppearance.getNoseBridge());
        writeC(playerAppearance.getNoseWidth());
        writeC(playerAppearance.getNoseTip());

        writeC(playerAppearance.getCheek());
        writeC(playerAppearance.getLipHeight());
        writeC(playerAppearance.getMouthSize());
        writeC(playerAppearance.getLipSize());
        writeC(playerAppearance.getSmile());
        writeC(playerAppearance.getLipShape());
        writeC(playerAppearance.getJawHeigh());
        writeC(playerAppearance.getChinJut());
        writeC(playerAppearance.getEarShape());
        writeC(playerAppearance.getHeadSize());
        // 1.5.x 0x00, shoulderSize, armLength, legLength (BYTE) after HeadSize

        writeC(playerAppearance.getNeck());
        writeC(playerAppearance.getNeckLength());
        writeC(playerAppearance.getShoulderSize());

        writeC(playerAppearance.getTorso());
        writeC(playerAppearance.getChest()); // only woman
        writeC(playerAppearance.getWaist());

        writeC(playerAppearance.getHips());
        writeC(playerAppearance.getArmThickness());
        writeC(playerAppearance.getHandSize());
        writeC(playerAppearance.getLegThicnkess());

        writeC(playerAppearance.getFootSize());
        writeC(playerAppearance.getFacialRate());

        writeC(0x00); // always 0
        writeC(playerAppearance.getArmLength());
        writeC(playerAppearance.getLegLength());
        writeC(playerAppearance.getShoulders());
        writeC(playerAppearance.getFaceShape());
        writeC(0x00); // always 0

        writeC(playerAppearance.getVoice());

        writeF(playerAppearance.getHeight());
        writeF(0.25f); // scale
        writeF(2.0f); // gravity or slide surface o_O
        writeF(player.getGameStats().getMovementSpeedFloat()); // move speed

        Stat2 attackSpeed = player.getGameStats().getAttackSpeed();
        writeH(attackSpeed.getBase());
        writeH(attackSpeed.getCurrent());
        writeC(player.getPortAnimation());

        writeS(player.hasStore() ? player.getStore().getStoreMessage() : "");// private store message

        /**
         * Movement
         */
        writeF(0.0f);
        writeF(0.0f);
        writeF(0.0f);

        writeF(player.getX());// x
        writeF(player.getY());// y
        writeF(player.getZ());// z
        writeC(0x00); // move type

        if (player.isUsingFlyTeleport()) {
            writeD(player.getFlightTeleportId());
            writeD(player.getFlightDistance());
        } else if (player.isInPlayerMode(PlayerMode.WINDSTREAM)) {
            writeD(player.windstreamPath.teleportId);
            writeD(player.windstreamPath.distance);
        }
        writeC(player.getVisualState()); // visualState
        writeS(player.getCommonData().getNote()); // note show in right down windows if your target on player

        writeH(player.getLevel()); // [level]
        writeH(player.getPlayerSettings().getDisplay()); // unk - 0x04
        writeH(player.getPlayerSettings().getDeny()); // unk - 0x00
        writeH(player.getAbyssRank().getRank().getId()); // abyss rank
        writeH(0x00); // unk - 0x01
        writeD(player.getTarget() == null ? 0 : player.getTarget().getObjectId());
        writeC(0); // suspect id
        writeD(player.getCurrentTeamId());
        writeC(player.isMentor() ? 1 : 0);
        writeD(player.getHouseOwnerId()); // 3.0
		if (player.getClientConnection().getAccount().getMembership() == 2) {
			writeD(5);
		} else {
		writeD(player.getItemEffectId());
		}
        writeD(0x00);//4.7
	    writeC(raceId == 0 ? 3 : 5); // Game language Asmo 3 Ely 5
        writeC(0); // ???
        writeC(0); // 0.1.2.3
    }
}
