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


package org.typezero.gameserver.services.abyss;

import com.aionemu.commons.callbacks.Callback;
import com.aionemu.commons.callbacks.CallbackResult;
import com.aionemu.commons.callbacks.metadata.GlobalCallback;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.AbyssRank;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.siege.SiegeNpc;
import org.typezero.gameserver.network.aion.serverpackets.SM_ABYSS_RANK;
import org.typezero.gameserver.network.aion.serverpackets.SM_ABYSS_RANK_UPDATE;
import org.typezero.gameserver.network.aion.serverpackets.SM_LEGION_EDIT;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.stats.AbyssRankEnum;
import org.typezero.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ATracer
 */
public class AbyssPointsService {

	private static final Logger log = LoggerFactory.getLogger(AbyssPointsService.class);
    private static final Logger debuglog = LoggerFactory.getLogger("ABYSSRANK_LOG");

    @GlobalCallback(AddAPGlobalCallback.class)
    public static void addAGp(Player player, VisibleObject obj, int ap, int gp)
    {
        if ((ap > 30000) || (gp > 30000)) {
            log.warn("WARN BIG COUNT AP: " + ap + " GP:" + gp + " name: " + obj.getName() + " obj: " + obj.getObjectId() + " player: " + player.getObjectId());
        }
        addAGp(player, ap, gp);
    }

    public static void addAp(Player player, int ap){
        addAGp(player, ap, 0);
    }

    public static void addAp(Player player, VisibleObject obj, int ap){
        addAGp(player, obj, ap, 0);
    }

    public static void addAGp(Player player, int ap, int gp)
    {
        if (player == null) {
            return;
        }
        // ОС начисляется только для офицеров и выше
        if (player.getAbyssRank().getRank().getId() < AbyssRankEnum.GRADE1_SOLDIER.getId()) {
            gp = 0;
        }
        if ((gp != 0) && (gp > 0)) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_GLORY_POINT_GAIN(gp));
        } else if (gp < 0) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_GLORY_POINT_LOSE(gp * -1));
        }
        if ((ap != 0) && (ap > 0)) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_MY_ABYSS_POINT_GAIN(ap));
        } else if (ap < 0) {
            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300965, new Object[] { Integer.valueOf(ap * -1) }));
        }
        setAGp(player, ap, gp);
        if ((player.isLegionMember()) && (ap > 0))
        {
            player.getLegion().addContributionPoints(ap);
            PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_EDIT(3, player.getLegion()));
        }
        if ((player.isLegionMember()) && (gp > 0))
        {
            player.getLegion().addContributionPoints(gp);
            PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_EDIT(3, player.getLegion()));
        }
    }

    public static void setAGp(Player player, int ap, int gp)
    {
        if (player == null) {
            return;
        }
        AbyssRank rank = player.getAbyssRank();
        if (gp != 0)
            debuglog.info("[GP REWARD LOG] Player: " + player.getName() + ". Have GP before: " + player.getAbyssRank().getGp() + ". Reward: " + gp);
        AbyssRankEnum oldAbyssRank = rank.getRank();
        if ((ap != 0) || (gp != 0)) {
            rank.addAGp(ap, gp);
        }
        AbyssRankEnum newAbyssRank = rank.getRank();

        checkRankChanged(player, oldAbyssRank, newAbyssRank);

        PacketSendUtility.sendPacket(player, new SM_ABYSS_RANK(player.getAbyssRank()));
    }

    public static void checkRankChanged(Player player, AbyssRankEnum oldAbyssRank, AbyssRankEnum newAbyssRank)
    {
        if (oldAbyssRank == newAbyssRank) {
            return;
        }
        PacketSendUtility.broadcastPacketAndReceive(player, new SM_ABYSS_RANK_UPDATE(0, player));

        player.getEquipment().checkRankLimitItems();
        AbyssSkillService.updateSkills(player);
    }

    public static abstract class AddAPGlobalCallback implements Callback
    {
        public CallbackResult beforeCall(Object obj, Object[] args)
        {
            return CallbackResult.newContinue();
        }

        public CallbackResult afterCall(Object obj, Object[] args, Object methodResult)
        {
            Player player = (Player)args[0];
            VisibleObject creature = (VisibleObject)args[1];
            int abyssPoints = (Integer)args[2];

            if ((creature instanceof Player)) {
                onAbyssPointsAdded(player, abyssPoints);
            } else if (((creature instanceof SiegeNpc)) && (!((SiegeNpc)creature).getSpawn().isPeace())) {
                onAbyssPointsAdded(player, abyssPoints);
            }

            return CallbackResult.newContinue();
        }

        public Class<? extends Callback> getBaseClass()
        {
            return AddAPGlobalCallback.class;
        }

        public abstract void onAbyssPointsAdded(Player paramPlayer, int paramInt);
    }
}
