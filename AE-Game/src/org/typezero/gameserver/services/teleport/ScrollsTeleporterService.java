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

package org.typezero.gameserver.services.teleport;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.portal.PortalLoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author GiGatR00n
 */
public class ScrollsTeleporterService {

    private static final Logger log = LoggerFactory.getLogger(ScrollsTeleporterService.class);

    /**
     * @param player
     * @param LocId
     * @param worldId
     */
    public static void ScrollTeleprter(Player player, int LocId ,int worldId) {

        PortalLoc loc = DataManager.PORTAL_LOC_DATA.getPortalLoc(LocId);

        if (loc == null) {
            log.warn("No Portal location for locId" + LocId);
            return;
        }

        	TeleportService2.teleportTo(player, worldId, loc.getX(), loc.getY(), loc.getZ(), player.getHeading(), TeleportAnimation.BEAM_ANIMATION);
    }

    /**
     * @param worldId
     * @param race
     */
    public static int getScrollLocIdbyWorldId(int worldId, Race race) {

        switch (worldId) {

    		case 600050000: //Kaisinel's Beacon (Elyos Katalam)  |  Danuar Spire (Asmo  Katalam)
    			return (race == Race.ELYOS ? 6000502 : 6000503);
    		case 600070000: //Idian Depths
    			return (race == Race.ELYOS ? 6000700 : 6000701);
    		case 400010000: //Teminon Fortress (Elyos)   |   Primum Fortress (Asmo)
    			return (race == Race.ELYOS ? 4000100 : 4000101);
	        case 700010000: //Oriel (Elyos)   |   Pernon (Asmo)
	        case 710010000:
	        	return (race == Race.ELYOS ? 7000101 : 7100100);
	        case 110070000: //Kaisinel Academy (Elyos)   |   Marchutan Priory (Asmo)
	        case 120080000:
	        	return (race == Race.ELYOS ? 1100702 : 1200800);
	        case 210070000:
	        case 220080000:
	        	return (race == Race.ELYOS ? 2100700 : 2200800);
	        case 600100000: //Levinshor Gerha (Elyos)   |   Levinshor Gerha (Asmo)
	        	return (race == Race.ELYOS ? 6001007 : 6001008);
	    	case 600030000: //Rancora Fortress
	    		return (race == Race.ELYOS ? 6000350 : 6000351);
    		case 600060000: //Pandarunerk's Delve (Elyos Danarina)   |   Pandarunerk (Asmo Danarina)
    			return 6000603;
	        case 600020000: //Kamar
	        	return 6000210;
	    }
        return 0;
    }
}
