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

package org.typezero.gameserver.services;

import com.aionemu.commons.database.dao.DAOManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.cache.HTMLCache;
import org.typezero.gameserver.configs.main.WeddingsConfig;
import org.typezero.gameserver.dao.WeddingDAO;
import org.typezero.gameserver.model.*;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_MESSAGE;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author synchro2
 */

public class WeddingService {
    private static final Logger log = LoggerFactory.getLogger(WeddingService.class);

	private Map<Integer, Wedding> weddings = new HashMap<Integer, Wedding>();
    public String MarryName = "MarryName";
	public static final WeddingService getInstance() {
		return SingletonHolder.instance;
	}

	public void registerOffer(Player partner1, Player partner2) {
		if (!canRegister(partner1, partner2)) {
			PacketSendUtility.sendMessage(partner1, "" +partner2.getName()+ " \u0443\u0436\u0435 \u0432 \u0431\u0440\u0430\u043a\u0435" );
			return;
		}
        if(!checkTargets(partner1, partner2)) {
            return;
        }
        if (!checkConditions(partner1, partner2))
        {
            cleanWedding(partner1, partner2);
            return;
        }

        if (WeddingsConfig.WEDDINGS_TOLL != 0) {
            if (partner1.getPlayerAccount().getToll() < WeddingsConfig.WEDDINGS_TOLL) {
                PacketSendUtility.sendMessage(partner1, "\u0423 \u0432\u0430\u0441 \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u0441\u0440\u0435\u0434\u0441\u0442\u0432 \u043d\u0430 \u0431\u0430\u043b\u0430\u043d\u0441\u0435. \u0414\u043b\u044f \u0446\u0435\u0440\u0435\u043c\u043e\u043d\u0438\u0438 \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e " +WeddingsConfig.WEDDINGS_TOLL+ "P");
                cleanWedding(partner1, partner2);
                return;
            }
        }
        PacketSendUtility.sendMessage(partner1, "\u0412\u043e\u043f\u0440\u043e\u0441 \u043f\u043e\u0441\u043b\u0430\u043d.");
        Npc priest = (Npc) partner1.getTarget();
        PacketSendUtility.broadcastPacket(priest, new SM_MESSAGE(priest.getObjectId(), priest.getName(), "" + partner1.getName() + " \u0412\u044b \u0445\u043e\u0442\u0438\u0442\u0435 \u0432\u0437\u044f\u0442\u044c \u0432 \u0436\u0435\u043d\u044b " + partner2.getName() + "?", ChatType.NORMAL));
        HTMLService.showHTML(partner1, HTMLCache.getInstance().getHTML("weddings.xhtml"));
        PacketSendUtility.broadcastPacket(priest, new SM_MESSAGE(priest.getObjectId(), priest.getName(), "" + partner2.getName() + " \u0412\u044b \u0445\u043e\u0442\u0438\u0442\u0435 \u0432\u0437\u044f\u0442\u044c \u0432 \u043c\u0443\u0436\u044c\u044f " + partner1.getName() + "?", ChatType.NORMAL));
        HTMLService.showHTML(partner2, HTMLCache.getInstance().getHTML("weddings.xhtml"));

        weddings.put(partner1.getObjectId(), new Wedding(partner1, partner2));
		weddings.put(partner2.getObjectId(), new Wedding(partner2, partner1));
	}

	private boolean canRegister(Player partner1, Player partner2) {
		return (getWedding(partner1) == null && getWedding(partner2) == null && !partner1.isMarried() && !partner2
			.isMarried());
	}

	public void acceptWedding(Player player) {
		Player partner = getPartner(player);
		Wedding playersWedding = getWedding(player);
		Wedding partnersWedding = getWedding(partner);
		playersWedding.setAccept();

		if (partnersWedding.isAccepted()) {
		     if ((Npc) player.getTarget() == null) {
                 PacketSendUtility.sendMessage(player, "Sdelay target na NPC Svadebnogo!!!");
                 PacketSendUtility.sendMessage(partner, "Sdelay target na NPC Svadebnogo!!!");
                 return;
             }
		     Npc priest = (Npc) player.getTarget();
                sendPriestMessages(priest, player, partner);
		}
	}

	private void doWedding(Player player, Player partner) {

        if (!checkConditions(player, partner)) {
            cleanWedding(player, partner);
            return;
        }

        if (WeddingsConfig.WEDDINGS_TOLL != 0) {
            if (player.getPlayerAccount().getToll() < WeddingsConfig.WEDDINGS_TOLL && player.getGender()== Gender.MALE) {
                PacketSendUtility.sendMessage(player, "\u0423 \u0432\u0430\u0441 \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u0441\u0440\u0435\u0434\u0441\u0442\u0432 \u043d\u0430 \u0431\u0430\u043b\u0430\u043d\u0441\u0435. \u0414\u043b\u044f \u0446\u0435\u0440\u0435\u043c\u043e\u043d\u0438\u0438 \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e " +WeddingsConfig.WEDDINGS_TOLL+ "P");
                cleanWedding(player, partner);
                return;
            }
        }
        if ( !player.getInventory().tryDecreaseKinah(WeddingsConfig.WEDDINGS_KINAH)) {
            log.warn("[WEDDING]  "+player+ " wrong kinah count");
            return;
        }
        log.info("[WEDDING]  "+player+ " kinah was decreased");
        if ( !partner.getInventory().tryDecreaseKinah(WeddingsConfig.WEDDINGS_KINAH))
        {
            log.warn("[WEDDING]  "+partner+ " wrong kinah count");
            return;
        }
        log.info("[WEDDING]  "+partner+ " kinah was decreased");
		DAOManager.getDAO(WeddingDAO.class).storeWedding(player, partner);
		player.setPartnerId(partner.getObjectId());
        player.setPartnerName(partner.getName());
		partner.setPartnerId(player.getObjectId());
        partner.setPartnerName(player.getName());
        if (player.hasVar(MarryName))
        {
            player.delVar(MarryName, true);
        }
            player.setVar(MarryName, "on", true);

        if (partner.hasVar(MarryName))
        {
            partner.delVar(MarryName, true);
        }
        partner.setVar(MarryName, "on", true);
        PacketSendUtility.broadcastPacketAndReceive(player, new SM_PLAYER_INFO(player, false));
        PacketSendUtility.broadcastPacketAndReceive(player, new SM_PLAYER_INFO(partner, false));
        player.clearKnownlist();
        player.updateKnownlist();
        partner.clearKnownlist();
        partner.updateKnownlist();
		PacketSendUtility.sendMessage(player, "\u0412\u044b \u0436\u0435\u043d\u0438\u043b\u0438\u0441\u044c \u043d\u0430 " + partner.getName() + ".");
		PacketSendUtility.sendMessage(partner, "\u0412\u044b \u0432\u044b\u0448\u043b\u0438 \u0437\u0430\u043c\u0443\u0436 \u0437\u0430 " + player.getName() + ".");
		cleanWedding(player, partner);
		runNpcDance(player, partner);
        if (WeddingsConfig.WEDDINGS_TOLL != 0){
            if (player.getGender()== Gender.MALE){
            long toll = player.getPlayerAccount().getToll() - WeddingsConfig.WEDDINGS_TOLL;
            player.getPlayerAccount().setToll(toll);
            PacketSendUtility.sendMessage(player, "\u0421 \u0431\u0430\u043b\u0430\u043d\u0441\u0430 \u0441\u043d\u044f\u0442\u043e  " + WeddingsConfig.WEDDINGS_TOLL + " \u041f\u043e\u0438\u043d\u0442\u043e\u0432.");
            }
            else{
                long toll = partner.getPlayerAccount().getToll() - WeddingsConfig.WEDDINGS_TOLL;
                partner.getPlayerAccount().setToll(toll);
                PacketSendUtility.sendMessage(partner, "\u0421 \u0431\u0430\u043b\u0430\u043d\u0441\u0430 \u0441\u043d\u044f\u0442\u043e " + WeddingsConfig.WEDDINGS_TOLL + " \u041f\u043e\u0438\u043d\u0442\u043e\u0432.");
            }

        }
        if (WeddingsConfig.WEDDINGS_GIFT_ENABLE) {
            giveGifts(player, partner);
        }
        if (WeddingsConfig.WEDDINGS_ANNOUNCE) {
            announceWedding(player, partner);
        }
	}

	/**
	 * @return true if both players targeted priests of their race.
	 */
	private static boolean checkTargets(Player player, Player partner) {
		VisibleObject ht = player.getTarget();
		VisibleObject wt = partner.getTarget();

		switch(player.getCommonData().getRace()) {
			case ELYOS:
				if( (!(ht instanceof Npc) || ((Npc)ht).getNpcId() != 203752) || (!(wt instanceof Npc) || ((Npc)wt).getNpcId() != 203752))
				{
                    PacketSendUtility.sendMessage(partner, "\u0426\u0435\u0440\u0435\u043c\u043e\u043d\u0438\u044f \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u043f\u0440\u043e\u0432\u0435\u0434\u0435\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0432 \u0421\u0432\u044f\u0442\u0438\u043b\u0438\u0449\u0435 \u042d\u043b\u0438\u0437\u0438\u0443\u043c\u0430 \u0432\u0435\u0440\u0445\u043e\u0432\u043d\u044b\u043c \u0436\u0440\u0435\u0446\u043e\u043c \u042e\u043a\u043b\u0438\u0430\u0441\u043e\u043c. \u0414\u043b\u044f \u0441\u0442\u0430\u0440\u0442\u0430 \u0446\u0435\u0440\u0435\u043c\u043e\u043d\u0438\u0438 \u0441\u0443\u043f\u0440\u0443\u0433\u0430\u043c \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e \u0432\u0437\u044f\u0442\u044c \u042e\u043a\u043b\u0438\u0430\u0441\u0430 \u0432 \u0442\u0430\u0440\u0433\u0435\u0442.");
                    PacketSendUtility.sendMessage(player, "\u0426\u0435\u0440\u0435\u043c\u043e\u043d\u0438\u044f \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u043f\u0440\u043e\u0432\u0435\u0434\u0435\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0432 \u0421\u0432\u044f\u0442\u0438\u043b\u0438\u0449\u0435 \u042d\u043b\u0438\u0437\u0438\u0443\u043c\u0430 \u0432\u0435\u0440\u0445\u043e\u0432\u043d\u044b\u043c \u0436\u0440\u0435\u0446\u043e\u043c \u042e\u043a\u043b\u0438\u0430\u0441\u043e\u043c. \u0414\u043b\u044f \u0441\u0442\u0430\u0440\u0442\u0430 \u0446\u0435\u0440\u0435\u043c\u043e\u043d\u0438\u0438 \u0441\u0443\u043f\u0440\u0443\u0433\u0430\u043c \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e \u0432\u0437\u044f\u0442\u044c \u042e\u043a\u043b\u0438\u0430\u0441\u0430 \u0432 \u0442\u0430\u0440\u0433\u0435\u0442.");
					return false;
				}
				break;
			case ASMODIANS:
				if( (!(ht instanceof Npc) || ((Npc)ht).getNpcId() != 204075) || (!(wt instanceof Npc) || ((Npc)wt).getNpcId() != 204075))
				{
                    PacketSendUtility.sendMessage(partner, "\u0426\u0435\u0440\u0435\u043c\u043e\u043d\u0438\u044f \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u043f\u0440\u043e\u0432\u0435\u0434\u0435\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0432 \u0425\u0440\u0430\u043c\u0435 \u041f\u0430\u043d\u0434\u0435\u043c\u043e\u043d\u0438\u0443\u043c\u0430 \u0432\u0435\u0440\u0445\u043e\u0432\u043d\u044b\u043c \u0436\u0440\u0435\u0446\u043e\u043c \u0411\u0430\u043b\u044c\u0434\u0440\u043e\u043c. \u0414\u043b\u044f \u0441\u0442\u0430\u0440\u0442\u0430 \u0446\u0435\u0440\u0435\u043c\u043e\u043d\u0438\u0438 \u0441\u0443\u043f\u0440\u0443\u0433\u0430\u043c \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e \u0432\u0437\u044f\u0442\u044c \u0411\u0430\u043b\u044c\u0434\u0440\u0430 \u0432 \u0442\u0430\u0440\u0433\u0435\u0442.");
                    PacketSendUtility.sendMessage(player, "\u0426\u0435\u0440\u0435\u043c\u043e\u043d\u0438\u044f \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u043f\u0440\u043e\u0432\u0435\u0434\u0435\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0432 \u0425\u0440\u0430\u043c\u0435 \u041f\u0430\u043d\u0434\u0435\u043c\u043e\u043d\u0438\u0443\u043c\u0430 \u0432\u0435\u0440\u0445\u043e\u0432\u043d\u044b\u043c \u0436\u0440\u0435\u0446\u043e\u043c \u0411\u0430\u043b\u044c\u0434\u0440\u043e\u043c. \u0414\u043b\u044f \u0441\u0442\u0430\u0440\u0442\u0430 \u0446\u0435\u0440\u0435\u043c\u043e\u043d\u0438\u0438 \u0441\u0443\u043f\u0440\u0443\u0433\u0430\u043c \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e \u0432\u0437\u044f\u0442\u044c \u0411\u0430\u043b\u044c\u0434\u0440\u0430 \u0432 \u0442\u0430\u0440\u0433\u0435\u0442.");
					return false;
				}
				break;
		}

		return true;
	}

	public void unDoWedding(Player player, Player partner) {
		DAOManager.getDAO(WeddingDAO.class).deleteWedding(player, partner);

        player.setPartnerName("");
		player.setPartnerId(0);
        PacketSendUtility.sendMessage(player, "\u0412\u044b \u0440\u0430\u0437\u0432\u0435\u043b\u0438\u0441\u044c.");
        if (player.hasVar(MarryName))
        {
            player.delVar(MarryName, true);
        }
        player.setVar(MarryName, "off", true);
        PacketSendUtility.broadcastPacketAndReceive(player, new SM_PLAYER_INFO(player, false));
        partner.setPartnerName("");
        partner.setPartnerId(0);
        PacketSendUtility.sendMessage(partner, "\u0412\u044b \u0440\u0430\u0437\u0432\u0435\u043b\u0438\u0441\u044c.");
        if (player.hasVar(MarryName))
        {
            player.delVar(MarryName, true);
        }
        player.setVar(MarryName, "off", true);
        PacketSendUtility.broadcastPacketAndReceive(partner, new SM_PLAYER_INFO(partner, false));
        player.clearKnownlist();
        player.updateKnownlist();
        partner.clearKnownlist();
        partner.updateKnownlist();
	}


    public void dismissMarriage(Player player) {
        DAOManager.getDAO(WeddingDAO.class).deleteWeddingSingle(player);

        player.setPartnerName("");
        player.setPartnerId(0);
        PacketSendUtility.sendMessage(player, "\u0412\u044b \u0440\u0430\u0437\u0432\u0435\u043b\u0438\u0441\u044c.");
        if (player.hasVar(MarryName))
        {
            player.delVar(MarryName, true);
        }
        player.setVar(MarryName, "off", true);
        PacketSendUtility.broadcastPacketAndReceive(player, new SM_PLAYER_INFO(player, false));
        player.clearKnownlist();
        player.updateKnownlist();
    }

    /**
	 * Sends priest messages.
	 * @param priest
	 * @param husband
	 * @param wife
	 */
	private void sendPriestMessages(final Npc priest, final Player husband, final Player wife) {
        if (!checkConditions(husband, wife)) {
            cleanWedding(husband, wife);
            return;
        }
        if (WeddingsConfig.WEDDINGS_TOLL != 0) {
            if (husband.getPlayerAccount().getToll() < WeddingsConfig.WEDDINGS_TOLL && husband.getGender()== Gender.MALE) {
                PacketSendUtility.sendMessage(husband, "\u0423 \u0432\u0430\u0441 \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u0441\u0440\u0435\u0434\u0441\u0442\u0432 \u043d\u0430 \u0431\u0430\u043b\u0430\u043d\u0441\u0435. \u0414\u043b\u044f \u0446\u0435\u0440\u0435\u043c\u043e\u043d\u0438\u0438 \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e " +WeddingsConfig.WEDDINGS_TOLL+ "P");
                cleanWedding(husband, wife);
                return;
            }
        }

		PacketSendUtility.broadcastPacket(priest, new SM_MESSAGE(priest.getObjectId(), priest.getName(), "*\u0434\u043e\u043b\u0433\u043e \u0431\u0443\u0431\u043d\u0438\u0442 \u0447\u0442\u043e-\u0442\u043e \u0441\u0435\u0431\u0435 \u043f\u043e\u0434\u043d\u043e\u0441 c \u0437\u0430\u043a\u0440\u044b\u0442\u044b\u043c\u0438 \u0433\u043b\u0430\u0437\u0430\u043c\u0438*", ChatType.NORMAL));
		ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                PacketSendUtility.broadcastPacket(priest, new SM_MESSAGE(priest.getObjectId(), priest.getName(), "*\u0432\u043d\u0435\u0437\u0430\u043f\u043d\u043e \u043f\u0440\u043e\u0441\u044b\u043f\u0430\u0435\u0442\u0441\u044f * \u0410\u0445 \u044d\u0442\u0430 \u0441\u0432\u0430\u0434\u044c\u0431\u0430-\u0441\u0432\u0430\u0434\u044c\u0431\u0430-\u0441\u0432\u0430\u0434\u044c\u0431\u0430! \u041f\u0435\u043b\u0430 \u0438 \u043f\u043b\u044f\u0441\u0430\u043b\u0430! \u041a\u0445\u043c..\u041e \u0447\u0435\u043c \u044d\u0442\u043e \u044f?...\u041d\u0443, \u0432\u044b \u0441\u043e\u0433\u043b\u0430\u0441\u043d\u044b?", ChatType.NORMAL));
            }
        }, 5000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if(husband.getGender() == Gender.MALE){
                PacketSendUtility.broadcastPacketAndReceive(husband, new SM_MESSAGE(husband.getObjectId(), husband.getName(), "\u0421\u043e\u0433\u043b\u0430\u0441\u0435\u043d.", ChatType.NORMAL));
                }
                 else
                {
                    PacketSendUtility.broadcastPacketAndReceive(husband, new SM_MESSAGE(husband.getObjectId(), husband.getName(), "\u0421\u043e\u0433\u043b\u0430\u0441\u043d\u0430.", ChatType.NORMAL));
                }
                if(wife.getGender() == Gender.MALE){
                PacketSendUtility.broadcastPacketAndReceive(wife, new SM_MESSAGE(wife.getObjectId(), wife.getName(), "\u0421\u043e\u0433\u043b\u0430\u0441\u0435\u043d.", ChatType.NORMAL));
                }
                else
                {
                    PacketSendUtility.broadcastPacketAndReceive(husband, new SM_MESSAGE(husband.getObjectId(), husband.getName(), "\u0421\u043e\u0433\u043b\u0430\u0441\u043d\u0430.", ChatType.NORMAL));
                }
                PacketSendUtility.broadcastPacketAndReceive(husband, new SM_EMOTION(husband, EmotionType.EMOTE, 6, priest.getObjectId()));
                PacketSendUtility.broadcastPacketAndReceive(husband, new SM_EMOTION(wife, EmotionType.EMOTE, 6, priest.getObjectId()));
            }
        }, 12000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                PacketSendUtility.broadcastPacket(priest, new SM_MESSAGE(priest.getObjectId(), priest.getName(), "\u041e\u0431\u044a\u044f\u0432\u043b\u044f\u044e \u0432\u0430\u0441 \u043c\u0443\u0436\u0435\u043c \u0438 \u0436\u0435\u043d\u043e\u0439! \u0416\u0435\u043d\u0438\u0445 \u043c\u043e\u0436\u0435\u0442 \u043f\u043e\u0446\u0435\u043b\u043e\u0432\u0430\u0442\u044c \u043d\u0435\u0432\u0435\u0441\u0442\u0443!", ChatType.NORMAL));
                PacketSendUtility.broadcastPacketAndReceive(husband, new SM_EMOTION(husband, EmotionType.EMOTE, 72, wife.getObjectId()));
                doWedding(husband, wife);
            }
        }, 19000);
	}

	private boolean checkConditions(Player player, Player partner) {
		if (player.isMarried() || partner.isMarried()) {
			PacketSendUtility.sendMessage(player, "\u041e\u0434\u0438\u043d \u0438\u0437 \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0443\u0436\u0435 \u0432 \u0441\u043e\u0441\u0442\u043e\u0438\u0442 \u0432 \u0431\u0440\u0430\u043a\u0435.");
			PacketSendUtility.sendMessage(partner, "\u041e\u0434\u0438\u043d \u0438\u0437 \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0443\u0436\u0435 \u0432 \u0441\u043e\u0441\u0442\u043e\u0438\u0442 \u0432 \u0431\u0440\u0430\u043a\u0435.");
		}

		if (WeddingsConfig.WEDDINGS_SUIT_ENABLE) {
			String[] suits = WeddingsConfig.WEDDINGS_SUITS.split(",");
			boolean success1 = false;
			boolean success2 = false;
			try {
				for (String suit : suits) {
					int suitId = Integer.parseInt(suit);
					if (!player.getEquipment().getEquippedItemsByItemId(suitId).isEmpty()) {
						success1 = true;
					}
					if (!partner.getEquipment().getEquippedItemsByItemId(suitId).isEmpty()) {
						success2 = true;
					}
				}
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
			}
			finally {
				if (!success1 || !success2) {
					PacketSendUtility.sendMessage(player, "\u0423 \u0432\u0430\u0441 \u0438\u043b\u0438 \u043f\u0430\u0440\u0442\u043d\u0435\u0440\u0430 \u043e\u0442\u0441\u0443\u0442\u0441\u0442\u0432\u0443\u0435\u0442 \u0441\u0432\u0430\u0434\u0435\u0431\u043d\u043e\u0435 \u043f\u043b\u0430\u0442\u044c\u0435(\u0444\u0440\u0430\u043a)");
					PacketSendUtility.sendMessage(partner, "\u0423 \u0432\u0430\u0441 \u0438\u043b\u0438 \u043f\u0430\u0440\u0442\u043d\u0435\u0440\u0430 \u043e\u0442\u0441\u0443\u0442\u0441\u0442\u0432\u0443\u0435\u0442 \u0441\u0432\u0430\u0434\u0435\u0431\u043d\u043e\u0435 \u043f\u043b\u0430\u0442\u044c\u0435(\u0444\u0440\u0430\u043a)");
					return false;
				}
			}
		}

		if (player.getKnownList().getObject(partner.getObjectId()) == null) {
			PacketSendUtility.sendMessage(player, "\u0412\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u043f\u043e \u0431\u043b\u0438\u0437\u043e\u0441\u0442\u0438 \u043e\u0442 \u043d\u0435\u0432\u0435\u0441\u0442\u044b.");
			PacketSendUtility.sendMessage(partner, "\u0412\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u043f\u043e \u0431\u043b\u0438\u0437\u043e\u0441\u0442\u0438 \u043e\u0442 \u043d\u0435\u0432\u0435\u0441\u0442\u044b.");
			return false;
		}

		if (!player.havePermission(WeddingsConfig.WEDDINGS_MEMBERSHIP)
			|| !partner.havePermission(WeddingsConfig.WEDDINGS_MEMBERSHIP)) {
			PacketSendUtility.sendMessage(player, "One of players not have required membership.");
			PacketSendUtility.sendMessage(partner, "One of players not have required membership.");
			return false;
		}

		if (!WeddingsConfig.WEDDINGS_SAME_SEX
			&& player.getCommonData().getGender().equals(partner.getCommonData().getGender())) {
			PacketSendUtility.sendMessage(player, "\u0420\u0435\u0433\u0438\u0441\u0442\u0440\u0430\u0446\u0438\u044f \u043e\u0434\u043d\u043e\u043f\u043e\u043b\u044b\u0445 \u0431\u0440\u0430\u043a\u043e\u0432 \u0437\u0430\u043f\u0440\u0435\u0449\u0435\u043d\u0430 \u0437\u0430\u043a\u043e\u043d\u043e\u0434\u0430\u0442\u0435\u043b\u044c\u0441\u0442\u0432\u043e\u043c.");
			PacketSendUtility.sendMessage(partner, "\u0420\u0435\u0433\u0438\u0441\u0442\u0440\u0430\u0446\u0438\u044f \u043e\u0434\u043d\u043e\u043f\u043e\u043b\u044b\u0445 \u0431\u0440\u0430\u043a\u043e\u0432 \u0437\u0430\u043f\u0440\u0435\u0449\u0435\u043d\u0430 \u0437\u0430\u043a\u043e\u043d\u043e\u0434\u0430\u0442\u0435\u043b\u044c\u0441\u0442\u0432\u043e\u043c.");
			return false;
		}

		if (!WeddingsConfig.WEDDINGS_DIFF_RACES
			&& !player.getCommonData().getRace().equals(partner.getCommonData().getRace())) {
			PacketSendUtility.sendMessage(player, "\u0412\u044b \u0441 \u0441\u0443\u043f\u0440\u0443\u0433\u043e\u043c \u0434\u043e\u043b\u0436\u043d\u044b \u043f\u0440\u0438\u043d\u0430\u0434\u043b\u0435\u0436\u0430\u0442\u044c \u043a \u043e\u0434\u043d\u043e\u0439 \u0440\u0430\u0441\u0435.");
			PacketSendUtility.sendMessage(partner, "\u0412\u044b \u0441 \u0441\u0443\u043f\u0440\u0443\u0433\u043e\u043c \u0434\u043e\u043b\u0436\u043d\u044b \u043f\u0440\u0438\u043d\u0430\u0434\u043b\u0435\u0436\u0430\u0442\u044c \u043a \u043e\u0434\u043d\u043e\u0439 \u0440\u0430\u0441\u0435.");
			return false;
		}

		if (WeddingsConfig.WEDDINGS_KINAH != 0) {
			if (player.getInventory().getKinah() < WeddingsConfig.WEDDINGS_KINAH || partner.getInventory().getKinah() < WeddingsConfig.WEDDINGS_KINAH) {
				PacketSendUtility.sendMessage(player, "\u0423 \u043e\u0434\u043d\u043e\u0433\u043e \u0438\u0437 \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u043d\u0435 \u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u041a\u0438\u043d\u0430\u0440. \u0414\u043b\u044f \u0446\u0435\u0440\u0435\u043c\u043e\u043d\u0438\u0438 \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e " +WeddingsConfig.WEDDINGS_KINAH+ " \u041a\u0438\u043d\u0430\u0440");
				PacketSendUtility.sendMessage(partner, "\u0423 \u043e\u0434\u043d\u043e\u0433\u043e \u0438\u0437 \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u043d\u0435 \u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u041a\u0438\u043d\u0430\u0440. \u0414\u043b\u044f \u0446\u0435\u0440\u0435\u043c\u043e\u043d\u0438\u0438 \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e " +WeddingsConfig.WEDDINGS_KINAH+ " \u041a\u0438\u043d\u0430\u0440");
				return false;
			}
        }
		return true;
	}
	private static void runNpcDance(final Player husband, final Player wife) {
		//dancing npcs
		husband.getKnownList().doOnAllNpcs(new Visitor<Npc>() {
			@Override
			public void visit(Npc npc) {
				if(husband.getCommonData().getRace() == Race.ELYOS)	{
					switch(npc.getNpcId()) {
						case 203752:
							PacketSendUtility.broadcastPacketAndReceive(npc, new SM_EMOTION(npc, EmotionType.EMOTE, 64, wife.getObjectId()));
							break;
						case 203753:
							PacketSendUtility.broadcastPacketAndReceive(npc, new SM_EMOTION(npc, EmotionType.EMOTE, 84, wife.getObjectId()));
							break;
						case 203754:
                            PacketSendUtility.broadcastPacketAndReceive(npc, new SM_EMOTION(npc, EmotionType.EMOTE, 84, husband.getObjectId()));
							break;
					}
				}
				else {
					switch(npc.getNpcId()) {
						case 204075:
							PacketSendUtility.broadcastPacketAndReceive(npc, new SM_EMOTION(npc, EmotionType.EMOTE, 64, wife.getObjectId()));
							break;
						case 204076:
							PacketSendUtility.broadcastPacketAndReceive(npc, new SM_EMOTION(npc, EmotionType.EMOTE, 84, wife.getObjectId()));
							break;
						case 204077:
							PacketSendUtility.broadcastPacketAndReceive(npc, new SM_EMOTION(npc, EmotionType.EMOTE, 84, husband.getObjectId()));
							break;
					}
				}
			}
		});
    }

	private void giveGifts(Player player, Player partner) {
		ItemService.addItem(player, WeddingsConfig.WEDDINGS_GIFT, 1);
		ItemService.addItem(partner, WeddingsConfig.WEDDINGS_GIFT, 1);
	}

	private void announceWedding(Player player, Player partner) {
		String message = player.getName() + " \u0438 " + partner.getName() + " \u0422\u0415\u041f\u0415\u0420\u042c \u0421\u041e\u0421\u0422\u041e\u042f\u0422 \u0412 \u0411\u0420\u0410\u041a\u0415.";
		Iterator<Player> iter = World.getInstance().getPlayersIterator();
		while (iter.hasNext()) {
			PacketSendUtility.sendBrightYellowMessageOnCenter(iter.next(), message);
		}
	}

	public void cancelWedding(Player player) {
		PacketSendUtility.sendMessage(player, "\u0421\u0432\u0430\u0434\u044c\u0431\u0430 \u043e\u0442\u043c\u0435\u043d\u0435\u043d\u0430.");
		PacketSendUtility.sendMessage(getPartner(player), "\u041f\u0435\u0440\u0441\u043e\u043d\u0430\u0436 " + player.getName() + " \u043e\u0442\u043a\u0430\u0437\u0430\u043b\u0441\u044f \u043e\u0442 \u0441\u0432\u0430\u0434\u044c\u0431\u044b.");
		cleanWedding(player, getPartner(player));
	}

	private void cleanWedding(Player player, Player partner) {
		weddings.remove(player.getObjectId());
		weddings.remove(partner.getObjectId());
	}

	public Wedding getWedding(Player player) {
		return weddings.get(player.getObjectId());
	}

	private Player getPartner(Player player) {
		Wedding wedding = weddings.get(player.getObjectId());
		return wedding.getPartner();
	}


	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final WeddingService instance = new WeddingService();
	}
}
