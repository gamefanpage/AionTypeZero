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

package ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AI2Request;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.dao.PlayerBindPointDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TribeClass;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.BindPointPosition;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.BindPointTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import static org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE.*;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldType;

/**
 * @author ATracer
 */
@AIName("resurrect")
public class ResurrectAI2 extends NpcAI2 {

	private static Logger log = LoggerFactory.getLogger(ResurrectAI2.class);

	@Override
	protected void handleDialogStart(Player player) {
		BindPointTemplate bindPointTemplate = DataManager.BIND_POINT_DATA.getBindPointTemplate(getNpcId());
		Race race = player.getRace();
		if (bindPointTemplate == null) {
			log.info("There is no bind point template for npc: " + getNpcId());
			return;
		}

		if (player.getBindPoint() != null
			&& player.getBindPoint().getMapId() == getPosition().getMapId()
			&& MathUtil.getDistance(player.getBindPoint().getX(), player.getBindPoint().getY(), player.getBindPoint().getZ(),
				getPosition().getX(), getPosition().getY(), getPosition().getZ()) < 20) {
			PacketSendUtility.sendPacket(player, STR_ALREADY_REGISTER_THIS_RESURRECT_POINT);
			return;
		}

		WorldType worldType = player.getWorldType();
		if (!CustomConfig.ENABLE_CROSS_FACTION_BINDING && !getTribe().equals(TribeClass.FIELD_OBJECT_ALL)) {
			if ((!getRace().equals(Race.NONE) && !getRace().equals(race)) ||
					(race.equals(Race.ASMODIANS) && getTribe().equals(TribeClass.FIELD_OBJECT_LIGHT)) ||
					(race.equals(Race.ELYOS) && getTribe().equals(TribeClass.FIELD_OBJECT_DARK))) {
				PacketSendUtility.sendPacket(player, STR_MSG_BINDSTONE_CANNOT_FOR_INVALID_RIGHT(player.getCommonData().
						getOppositeRace().toString()));
				return;
			}
		}
		if (worldType == WorldType.PRISON) {
			PacketSendUtility.sendPacket(player, STR_CANNOT_REGISTER_RESURRECT_POINT_FAR_FROM_NPC);
			return;
		}
		bindHere(player, bindPointTemplate);
	}

	private void bindHere(Player player, final BindPointTemplate bindPointTemplate) {

		String price = Integer.toString(bindPointTemplate.getPrice());
		AI2Actions.addRequest(this, player, SM_QUESTION_WINDOW.STR_ASK_REGISTER_RESURRECT_POINT, 0, new AI2Request() {

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				// check if this both creatures are in same world
				if (responder.getWorldId() == requester.getWorldId()) {
					// check enough kinah
					if (responder.getInventory().getKinah() < bindPointTemplate.getPrice()) {
						PacketSendUtility.sendPacket(responder,
							STR_CANNOT_REGISTER_RESURRECT_POINT_NOT_ENOUGH_FEE);
						return;
					}
					else if (MathUtil.getDistance(requester, responder) > 5) {
						PacketSendUtility.sendPacket(responder, STR_CANNOT_REGISTER_RESURRECT_POINT_FAR_FROM_NPC);
						return;
					}

					BindPointPosition old = responder.getBindPoint();
					BindPointPosition bpp = new BindPointPosition(requester.getWorldId(), responder.getX(), responder.getY(),
						responder.getZ(), responder.getHeading());
					bpp.setPersistentState(old == null ? PersistentState.NEW : PersistentState.UPDATE_REQUIRED);
					responder.setBindPoint(bpp);
					if (DAOManager.getDAO(PlayerBindPointDAO.class).store(responder)) {
						responder.getInventory().decreaseKinah(bindPointTemplate.getPrice());
						TeleportService2.sendSetBindPoint(responder);
						PacketSendUtility.broadcastPacket(responder, new SM_LEVEL_UPDATE(responder.getObjectId(), 2, responder.getCommonData().getLevel()), true);
						PacketSendUtility.sendPacket(responder, STR_DEATH_REGISTER_RESURRECT_POINT("")); //TODO
						old = null;
					}
					else
						// if any errors happen, left that player with old bind point
						responder.setBindPoint(old);
				}
			}
		}, price);
	}

}
