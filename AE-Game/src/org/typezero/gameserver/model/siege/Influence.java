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

package org.typezero.gameserver.model.siege;

import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_INFLUENCE_RATIO;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

import java.util.Iterator;

/**
 * Calculates fortresses as 10 points and artifacts as 1 point each. Need to
 * find retail calculation. (Upper forts worth more...)
 *
 * @author Sarynth
 */
public class Influence {

    private static final Influence instance = new Influence();
    private float inggison_e = 0;
    private float inggison_a = 0;
    private float inggison_b = 0;
    private float gelkmaros_e = 0;
    private float gelkmaros_a = 0;
    private float gelkmaros_b = 0;
    private float abyss_e = 0;
    private float abyss_a = 0;
    private float abyss_b = 0;
    private float tiamaranta_e = 0;
    private float tiamaranta_a = 0;
    private float tiamaranta_b = 0;
    private float katalam_e = 0;
    private float katalam_a = 0;
    private float katalam_b = 0;
    private float danaria_e = 0;
    private float danaria_a = 0;
    private float danaria_b = 0;
    private float global_e = 0;
    private float global_a = 0;
    private float global_b = 0;

    private Influence() {
        calculateInfluence();
    }

    public static Influence getInstance() {
        return instance;
    }

    /**
     * Recalculates Influence
     */
    public void recalculateInfluence() {
        calculateInfluence();
    }

    /**
     * calculate influence
     */
    private void calculateInfluence() {
        float balaurea = 0.0017512194f;
        float abyss = 0.002967561f;
        float e_inggison = 0;
        float a_inggison = 0;
        float b_inggison = 0;
        float t_inggison = 0;
        float e_gelkmaros = 0;
        float a_gelkmaros = 0;
        float b_gelkmaros = 0;
        float t_gelkmaros = 0;
        float e_abyss = 0;
        float a_abyss = 0;
        float b_abyss = 0;
        float t_abyss = 0;
        float e_katalam = 0;
        float a_katalam = 0;
        float b_katalam = 0;
        float t_katalam = 0;
        float e_danaria = 0;
        float a_danaria = 0;
        float b_danaria = 0;
        float t_danaria = 0;

        for (SiegeLocation sLoc : SiegeService.getInstance().getSiegeLocations().values()) {
            switch (sLoc.getWorldId()) {
                case 210050000:
                    if (sLoc instanceof FortressLocation) {
                        t_inggison += sLoc.getInfluenceValue();
                        switch (sLoc.getRace()) {
                            case ELYOS:
                                e_inggison += sLoc.getInfluenceValue();
                                break;
                            case ASMODIANS:
                                a_inggison += sLoc.getInfluenceValue();
                                break;
                            case BALAUR:
                                b_inggison += sLoc.getInfluenceValue();
                                break;
                        }
                    }
                    break;
                case 220070000:
                    if (sLoc instanceof FortressLocation) {
                        t_gelkmaros += sLoc.getInfluenceValue();
                        switch (sLoc.getRace()) {
                            case ELYOS:
                                e_gelkmaros += sLoc.getInfluenceValue();
                                break;
                            case ASMODIANS:
                                a_gelkmaros += sLoc.getInfluenceValue();
                                break;
                            case BALAUR:
                                b_gelkmaros += sLoc.getInfluenceValue();
                                break;
                        }
                    }
                    break;
                case 400010000:
                    t_abyss += sLoc.getInfluenceValue();
                    switch (sLoc.getRace()) {
                        case ELYOS:
                            e_abyss += sLoc.getInfluenceValue();
                            break;
                        case ASMODIANS:
                            a_abyss += sLoc.getInfluenceValue();
                            break;
                        case BALAUR:
                            b_abyss += sLoc.getInfluenceValue();
                            break;
                    }
                    break;
                case 600050000:
                    t_danaria += sLoc.getInfluenceValue();
                    switch (sLoc.getRace()) {
                        case ELYOS:
                            e_danaria += sLoc.getInfluenceValue();
                            break;
                        case ASMODIANS:
                            a_danaria += sLoc.getInfluenceValue();
                            break;
                        case BALAUR:
                            b_danaria += sLoc.getInfluenceValue();
                            break;
                    }
                    break;
                case 600060000:
                    t_katalam += sLoc.getInfluenceValue();
                    switch (sLoc.getRace()) {
                        case ELYOS:
                            e_katalam += sLoc.getInfluenceValue();
                            break;
                        case ASMODIANS:
                            a_katalam += sLoc.getInfluenceValue();
                            break;
                        case BALAUR:
                            b_katalam += sLoc.getInfluenceValue();
                            break;
                    }
                    break;
            }
        }

        inggison_e = e_inggison / t_inggison;
        inggison_a = a_inggison / t_inggison;
        inggison_b = b_inggison / t_inggison;

        gelkmaros_e = e_gelkmaros / t_gelkmaros;
        gelkmaros_a = a_gelkmaros / t_gelkmaros;
        gelkmaros_b = b_gelkmaros / t_gelkmaros;

        abyss_e = e_abyss / t_abyss;
        abyss_a = a_abyss / t_abyss;
        abyss_b = b_abyss / t_abyss;

        katalam_e = e_katalam / t_katalam;
        katalam_a = a_katalam / t_katalam;
        katalam_b = b_katalam / t_katalam;
        danaria_e = e_danaria / t_danaria;
        danaria_a = a_danaria / t_danaria;
        danaria_b = b_danaria / t_danaria;
        global_e = (inggison_e * balaurea + gelkmaros_e * balaurea + abyss_e * abyss + danaria_e * balaurea + katalam_e * balaurea) * 100f;
        global_a = (inggison_a * balaurea + gelkmaros_a * balaurea + abyss_a * abyss + danaria_a * balaurea + katalam_a * balaurea) * 100f;
        global_b = (inggison_b * balaurea + gelkmaros_b * balaurea + abyss_b * abyss + danaria_b * balaurea + katalam_b * balaurea) * 100f;
    }

    /**
     * Broadcast packet with influence update to all players. - Responsible for
     * the message "The Divine Fortress is now vulnerable."
     */
    @SuppressWarnings("unused")
    private void broadcastInfluencePacket() {
        SM_INFLUENCE_RATIO pkt = new SM_INFLUENCE_RATIO();

        Player player;
        Iterator<Player> iter = World.getInstance().getPlayersIterator();
        while (iter.hasNext()) {
            player = iter.next();
            PacketSendUtility.sendPacket(player, pkt);
        }
    }

    /**
     * @return elyos control
     */
    public float getGlobalElyosInfluence() {
        return this.global_e;
    }

    /**
     * @return asmos control
     */
    public float getGlobalAsmodiansInfluence() {
        return this.global_a;
    }

    /**
     * @return balaur control
     */
    public float getGlobalBalaursInfluence() {
        return this.global_b;
    }

    /**
     * @return elyos control
     */
    public float getInggisonElyosInfluence() {
        return this.inggison_e;
    }

    /**
     * @return asmos control
     */
    public float getInggisonAsmodiansInfluence() {
        return this.inggison_a;
    }

    /**
     * @return balaur control
     */
    public float getInggisonBalaursInfluence() {
        return this.inggison_b;
    }

    /**
     * @return elyos control
     */
    public float getGelkmarosElyosInfluence() {
        return this.gelkmaros_e;
    }

    /**
     * @return asmos control
     */
    public float getGelkmarosAsmodiansInfluence() {
        return this.gelkmaros_a;
    }

    /**
     * @return balaur control
     */
    public float getGelkmarosBalaursInfluence() {
        return this.gelkmaros_b;
    }

    /**
     * @return elyos control
     */
    public float getAbyssElyosInfluence() {
        return this.abyss_e;
    }

    /**
     * @return asmos control
     */
    public float getAbyssAsmodiansInfluence() {
        return this.abyss_a;
    }

    /**
     * @return balaur control
     */
    public float getAbyssBalaursInfluence() {
        return this.abyss_b;
    }

    /**
     * @return elyos control
     */
    public float getTiamarantaElyosInfluence() {
        return this.tiamaranta_e;
    }

    /**
     * @return asmos control
     */
    public float getTiamarantaAsmodiansInfluence() {
        return this.tiamaranta_a;
    }

    /**
     * @return balaur control
     */
    public float getTiamarantaBalaursInfluence() {
        return this.tiamaranta_b;
    }

    /**
     * @return elyos control
     */
    public float getKatalamElyosInfluence() {
        return this.katalam_e;
    }

    /**
     * @return asmos control
     */
    public float getKatalamAsmodiansInfluence() {
        return this.katalam_a;
    }

    /**
     * @return balaur control
     */
    public float getKatalamBalaursInfluence() {
        return this.katalam_b;
    }

    /**
     * @return elyos control
     */
    public float getDanariaElyosInfluence() {
        return this.danaria_e;
    }

    /**
     * @return asmos control
     */
    public float getDanariaAsmodiansInfluence() {
        return this.danaria_a;
    }

    /**
     * @return balaur control
     */
    public float getDanariaBalaursInfluence() {
        return this.danaria_b;
    }

    /**
     * @return float containing dmg modifier for disadvantaged race
     */
    public float getPvpRaceBonus(Race attRace) {
        float bonus = 1;
        float elyos = getGlobalElyosInfluence();
        float asmo = getGlobalAsmodiansInfluence();
        switch (attRace) {
            case ASMODIANS:
                if (elyos >= 0.81f && asmo <= 0.10f)
                    bonus = 1.2f;
                else if (elyos >= 0.81f || (elyos >= 0.71f && asmo <= 0.10f))
                    bonus = 1.15f;
                else if (elyos >= 0.71f)
                    bonus = 1.1f;
                break;
            case ELYOS:
                if (asmo >= 0.81f && elyos <= 0.10f)
                    bonus = 1.2f;
                else if (asmo >= 0.81f || (asmo >= 0.71f && elyos <= 0.10f))
                    bonus = 1.15f;
                else if (asmo >= 0.71f)
                    bonus = 1.1f;
                break;
        default:
            break;
        }
        return bonus;
    }

}
