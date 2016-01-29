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

package org.typezero.gameserver.model.instance.instancereward;

import static ch.lambdaj.Lambda.maxFrom;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.mutable.MutableInt;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.geometry.Point3D;
import org.typezero.gameserver.model.instance.playerreward.*;
import org.typezero.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import org.typezero.gameserver.network.aion.serverpackets.SM_INSTANCE_SCORE;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 *
 * @author Sharlatan
 */
public class KamarBattlefieldReward extends InstanceReward<KamarBattlefieldPlayerReward>
{

    private int winnerPoints;
    private int looserPoints;
    private int capPoints;
    private MutableInt asmodiansPoints = new MutableInt(4000);
    private MutableInt elyosPoins = new MutableInt(4000);
    private MutableInt asmodiansPvpKills = new MutableInt(0);
    private MutableInt elyosPvpKills = new MutableInt(0);
    private Race race;
    private Point3D asmodiansStartPosition;
    private Point3D elyosStartPosition;
    protected WorldMapInstance instance;
    private long instanceTime;
    private int bonusTime;

    public KamarBattlefieldReward(Integer mapId, int instanceId, WorldMapInstance instance)
    {
        super(mapId, instanceId);
        this.instance = instance;
        winnerPoints = 3000;
        looserPoints = 2500;
        capPoints = 30000;
        bonusTime = 12000;
        setStartPositions();
    }

    public List<KamarBattlefieldPlayerReward> sortPoints()
    {
        return sort(getInstanceRewards(), on(PvPArenaPlayerReward.class).getScorePoints(), new Comparator<Integer>()
        {
            @Override
            public int compare(Integer o1, Integer o2)
            {
                return o2 != null ? o2.compareTo(o1) : -o1.compareTo(o2);
            }
        });
    }

    private void setStartPositions()
    {
        Point3D a = new Point3D(1535.6626f, 1573.9294f, 612.42f);
        Point3D b = new Point3D(1463.7689f, 1227.7777f, 581.62f);
        Point3D c = new Point3D(1204.9827f, 1350.6719f, 612.91f);
        Point3D d = new Point3D(1098.1141f, 1540.7119f, 585.10f);
        if (Rnd.get(2) == 0)
        {
            asmodiansStartPosition = a;
            elyosStartPosition = c;
        } else
        {
            asmodiansStartPosition = b;
            elyosStartPosition = d;
        }
    }

    public void portToPosition(Player player)
    {
        if (player.getRace() == Race.ASMODIANS)
        {
            TeleportService2.teleportTo(player, mapId, instanceId, asmodiansStartPosition.getX(), asmodiansStartPosition.getY(), asmodiansStartPosition.getZ());
        } else
        {
            TeleportService2.teleportTo(player, mapId, instanceId, elyosStartPosition.getX(), elyosStartPosition.getY(), elyosStartPosition.getZ());
        }
    }

    public MutableInt getPointsByRace(Race race)
    {
        switch (race)
        {
            case ELYOS:
                return elyosPoins;
            case ASMODIANS:
                return asmodiansPoints;
        }
        return null;
    }

    public void addPointsByRace(Race race, int points)
    {
        MutableInt racePoints = getPointsByRace(race);
        racePoints.add(points);
        if (racePoints.intValue() < 0)
        {
            racePoints.setValue(0);
        }
    }

    public MutableInt getPvpKillsByRace(Race race)
    {
        switch (race)
        {
            case ELYOS:
                return elyosPvpKills;
            case ASMODIANS:
                return asmodiansPvpKills;
        }
        return null;
    }

    public void addPvpKillsByRace(Race race, int points)
    {
        MutableInt racePoints = getPvpKillsByRace(race);
        racePoints.add(points);
        if (racePoints.intValue() < 0)
        {
            racePoints.setValue(0);
        }
    }

    public int getLooserPoints()
    {
        return looserPoints;
    }

    public int getWinnerPoints()
    {
        return winnerPoints;
    }

    public void setWinningRace(Race race)
    {
        this.race = race;
    }

    public Race getWinningRace()
    {
        return race;
    }

    public Race getWinningRaceByScore()
    {
        return asmodiansPoints.compareTo(elyosPoins) > 0 ? Race.ASMODIANS : Race.ELYOS;
    }

    @Override
    public void clear()
    {
        super.clear();
    }

    public void regPlayerReward(Player player)
    {
        if (!containPlayer(player.getObjectId()))
        {
            addPlayerReward(new KamarBattlefieldPlayerReward(player.getObjectId(), bonusTime, player.getRace()));
        }
    }

    @Override
    public void addPlayerReward(KamarBattlefieldPlayerReward reward)
    {
        super.addPlayerReward(reward);
    }

    @Override
    public KamarBattlefieldPlayerReward getPlayerReward(Integer object)
    {
        return (KamarBattlefieldPlayerReward) super.getPlayerReward(object);
    }

    public void sendPacket(final int type, final Integer object)
    {
        instance.doOnAllPlayers(new Visitor<Player>()
        {
            @Override
            public void visit(Player player)
            {
                PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(type, getTime(), getInstanceReward(), object));
            }
        });
    }

    public int getTime()
    {
        long result = System.currentTimeMillis() - instanceTime;
        if (result < 45000)
        {
            return (int) (45000 - result);
        } else if (result < 1800000)
        { //30 Minutes.
            return (int) (1800000 - (result - 20000));
        }
        return 0;
    }

    public void setInstanceStartTime()
    {
        this.instanceTime = System.currentTimeMillis();
    }

    public int getCapPoints()
    {
        return capPoints;
    }

    public boolean hasCapPoints()
    {
        return maxFrom(getInstanceRewards()).getPoints() >= capPoints;
    }
}
