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

import org.typezero.gameserver.model.DuelResult;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RequestResponseHandler;
import org.typezero.gameserver.model.summons.SummonMode;
import org.typezero.gameserver.model.summons.UnsummonType;
import org.typezero.gameserver.model.templates.zone.ZoneType;
import org.typezero.gameserver.network.aion.serverpackets.SM_DUEL;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.summons.SummonsService;
import org.typezero.gameserver.skillengine.model.SkillTargetSlot;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.zone.ZoneInstance;
import java.util.concurrent.Future;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Simple, Sphinx, xTz
 */
public class DuelService {

	private static Logger log = LoggerFactory.getLogger(DuelService.class);
	private FastMap<Integer, Integer> duels;
	private FastMap<Integer, Future<?>> drawTasks;

	public static final DuelService getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * @param duels
	 */
	private DuelService() {
		this.duels = new FastMap<Integer, Integer>().shared();
                this.drawTasks = new FastMap<Integer, Future<?>>().shared();
		log.info("DuelService started.");
	}

	/**
	 * Send the duel request to the owner
	 *
	 * @param requester
	 *          the player who requested the duel
	 * @param responder
	 *          the player who respond to duel request
	 */
	public void onDuelRequest(Player requester, Player responder) {
		/**
		 * Check if requester isn't already in a duel and responder is same race
		 */
		if (requester.isInsideZoneType(ZoneType.PVP) || responder.isInsideZoneType(ZoneType.PVP)) {
			PacketSendUtility.sendPacket(requester, SM_SYSTEM_MESSAGE.STR_DUEL_PARTNER_INVALID(responder.getName()));
			return;
		}
		if (isDueling(requester.getObjectId()) || isDueling(responder.getObjectId())) {
			PacketSendUtility.sendPacket(requester, SM_SYSTEM_MESSAGE.STR_DUEL_HE_REJECT_DUEL(responder.getName()));
			return;
		}
		for (ZoneInstance zone : responder.getPosition().getMapRegion().getZones((Creature) responder)) {
			if (!zone.isOtherRaceDuelsAllowed() && !responder.getRace().equals(requester.getRace())
					|| (!zone.isSameRaceDuelsAllowed() && responder.getRace().equals(requester.getRace()))) {
						PacketSendUtility.sendPacket(requester, SM_SYSTEM_MESSAGE.STR_MSG_DUEL_CANT_IN_THIS_ZONE);
						return;
					}
		}

		RequestResponseHandler rrh = new RequestResponseHandler(requester) {

			@Override
			public void denyRequest(Creature requester, Player responder) {
				rejectDuelRequest((Player) requester, responder);
			}

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				startDuel((Player) requester, responder);
			}
		};
		responder.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_REQUEST, rrh);
		PacketSendUtility.sendPacket(responder, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_REQUEST, 0, 0, requester.getName()));
		PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.STR_DUEL_REQUESTED(requester.getName()));
	}

	/**
	 * Asks confirmation for the duel request
	 *
	 * @param requester
	 *          the player whose the duel was requested
	 * @param responder
	 *          the player whose the duel was responded
	 */
	public void confirmDuelWith(Player requester, Player responder) {
		/**
		 * Check if requester isn't already in a duel and responder is same race
		 */
		if (requester.isEnemy(responder))
			return;

		RequestResponseHandler rrh = new RequestResponseHandler(responder) {

			@Override
			public void denyRequest(Creature requester, Player responder) {
				log.debug("[Duel] Player " + responder.getName() + " confirmed his duel with " + requester.getName());
			}

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				cancelDuelRequest(responder, (Player) requester);
			}
		};
		requester.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_WITHDRAW_REQUEST, rrh);
		PacketSendUtility.sendPacket(requester, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_WITHDRAW_REQUEST, 0, 0, responder.getName()));
		PacketSendUtility.sendPacket(requester, SM_SYSTEM_MESSAGE.STR_DUEL_REQUEST_TO_PARTNER(responder.getName()));
	}

	/**
	 * Rejects the duel request
	 *
	 * @param requester
	 *          the duel requester
	 * @param responder
	 *          the duel responder
	 */
	private void rejectDuelRequest(Player requester, Player responder) {
		log.debug("[Duel] Player " + responder.getName() + " rejected duel request from " + requester.getName());
		PacketSendUtility.sendPacket(requester, SM_SYSTEM_MESSAGE.STR_DUEL_HE_REJECT_DUEL(responder.getName()));
		PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.STR_DUEL_REJECT_DUEL(requester.getName()));
	}

	/**
	 * Cancels the duel request
	 *
	 * @param target
	 *          the duel target
	 * @param requester
	 */
	private void cancelDuelRequest(Player owner, Player target) {
		log.debug("[Duel] Player " + owner.getName() + " cancelled his duel request with " + target.getName());
		PacketSendUtility.sendPacket(target, SM_SYSTEM_MESSAGE.STR_DUEL_REQUESTER_WITHDRAW_REQUEST(owner.getName()));
		PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_DUEL_WITHDRAW_REQUEST(target.getName()));
	}

	/**
	 * Starts the duel
	 *
	 * @param requester
	 *          the player to start duel with
	 * @param responder
	 *          the other player
	 */
	private void startDuel(Player requester, Player responder) {
		PacketSendUtility.sendPacket(requester, SM_DUEL.SM_DUEL_STARTED(responder.getObjectId()));
		PacketSendUtility.sendPacket(responder, SM_DUEL.SM_DUEL_STARTED(requester.getObjectId()));
		createDuel(requester.getObjectId(), responder.getObjectId());
		createTask(requester, responder);
	}

	/**
	 * This method will make the selected player lose the duel
	 *
	 * @param player
	 */
	public void loseDuel(Player player) {
		if (!isDueling(player.getObjectId()))
			return;
		int opponnentId = duels.get(player.getObjectId());

		player.getAggroList().clear();

		Player opponent = World.getInstance().findPlayer(opponnentId);

		if (opponent != null) {
			/**
			 * all debuffs are removed from winner, but buffs will remain Stop casting or skill use
			 */
			opponent.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
			opponent.getController().cancelCurrentSkill();
			opponent.getAggroList().clear();

			/**
			 * cancel attacking winner by summon
			 */
			if (player.getSummon() != null) {
				//if (player.getSummon().getTarget().isTargeting(opponnentId))
					SummonsService.doMode(SummonMode.GUARD, player.getSummon(), UnsummonType.UNSPECIFIED);
			}

			/**
			 * cancel attacking loser by summon
			 */
			if (opponent.getSummon() != null) {
				//if (opponent.getSummon().getTarget().isTargeting(player.getObjectId()))
					SummonsService.doMode(SummonMode.GUARD, opponent.getSummon(), UnsummonType.UNSPECIFIED);
			}

			/**
			 * cancel attacking winner by summoned object
			 */
			if (player.getSummonedObj() != null) {
				player.getSummonedObj().getController().cancelCurrentSkill();
			}

			/**
			 * cancel attacking loser by summoned object
			 */
			if (opponent.getSummonedObj() != null) {
				opponent.getSummonedObj().getController().cancelCurrentSkill();
			}

			PacketSendUtility.sendPacket(opponent, SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_WON, player.getName()));
			PacketSendUtility.sendPacket(player, SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_LOST, opponent.getName()));
		}
		else {
			log.warn("CHECKPOINT : duel opponent is already out of world");
		}

		removeDuel(player.getObjectId(), opponnentId);
	}

	public void loseArenaDuel(Player player) {
		if (!isDueling(player.getObjectId()))
			return;

		/**
		 * all debuffs are removed from loser Stop casting or skill use
		 */
		player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
		player.getController().cancelCurrentSkill();

		int opponnentId = duels.get(player.getObjectId());
		Player opponent = World.getInstance().findPlayer(opponnentId);

		if (opponent != null) {
			/**
			 * all debuffs are removed from winner, but buffs will remain Stop casting or skill use
			 */
			opponent.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
			opponent.getController().cancelCurrentSkill();
		}
		else {
			log.warn("CHECKPOINT : duel opponent is already out of world");
		}

		removeDuel(player.getObjectId(), opponnentId);
	}

	private void createTask(final Player requester, final Player responder) {
		// Schedule for draw
		Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (isDueling(requester.getObjectId(), responder.getObjectId())) {
					PacketSendUtility.sendPacket(requester, SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_DRAW, requester.getName()));
					PacketSendUtility.sendPacket(responder, SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_DRAW, responder.getName()));
					removeDuel(requester.getObjectId(), responder.getObjectId());
				}
			}
		}, 5 * 60 * 1000); // 5 minutes battle retail like

		drawTasks.put(requester.getObjectId(), task);
		drawTasks.put(responder.getObjectId(), task);
	}

	/**
	 * @param playerObjId
	 * @return true of player is dueling
	 */
	public boolean isDueling(int playerObjId) {
		return (duels.containsKey(playerObjId) && duels.containsValue(playerObjId));
	}

	/**
	 * @param playerObjId
	 * @param targetObjId
	 * @return true of player is dueling
	 */
	public boolean isDueling(int playerObjId, int targetObjId) {
		return duels.containsKey(playerObjId) && duels.get(playerObjId) == targetObjId;
	}

	/**
	 * @param requesterObjId
	 * @param responderObjId
	 */
	public void createDuel(int requesterObjId, int responderObjId) {
		duels.put(requesterObjId, responderObjId);
		duels.put(responderObjId, requesterObjId);
	}

	/**
	 * @param requesterObjId
	 * @param responderObjId
	 */
	private void removeDuel(int requesterObjId, int responderObjId) {
		duels.remove(requesterObjId);
		duels.remove(responderObjId);
		removeTask(requesterObjId);
		removeTask(responderObjId);
	}

	private void removeTask(int playerId) {
		Future<?> task = drawTasks.get(playerId);
		if (task != null && !task.isDone()) {
			task.cancel(true);
			drawTasks.remove(playerId);
		}
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final DuelService instance = new DuelService();
	}

}
