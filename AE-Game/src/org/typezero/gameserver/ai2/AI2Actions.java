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

package org.typezero.gameserver.ai2;

import org.typezero.gameserver.controllers.observer.DialogObserver;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RequestResponseHandler;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.services.drop.DropRegistrationService;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.SkillTemplate;
import org.typezero.gameserver.utils.PacketSendUtility;

import java.util.Collection;

/**
 * Here will be placed some common AI2 actions. These methods have access to AI2's owner
 *
 * @author ATracer
 */
public class AI2Actions {

	/**
	 * Despawn and delete owner
	 */
	public static void deleteOwner(AbstractAI ai2) {
		ai2.getOwner().getController().onDelete();
	}

	/**
	 * Target will die with all notifications using ai's owner as the last attacker
	 */
	public static void killSilently(AbstractAI ai2, Creature target) {
		target.getController().onDie(ai2.getOwner());
	}

	/**
	 * AI's owner will die from specified attacker
	 */
	public static void dieSilently(AbstractAI ai2, Creature attacker) {
		ai2.getOwner().getController().onDie(attacker);
	}

	/**
	 * Use skill or add intention to use (will be implemented later)
	 */
	public static void useSkill(AbstractAI ai2, int skillId) {
		ai2.getOwner().getController().useSkill(skillId);
	}

	public static void useSkill(AbstractAI ai2, int skillId, int lvl) {
		ai2.getOwner().getController().useSkill(skillId, lvl);
	}

	/**
	 * Effect will be created and applied to target with 100% success
	 */
	public static void applyEffect(AbstractAI ai2, SkillTemplate template, Creature target) {
		Effect effect = new Effect(ai2.getOwner(), target, template, template.getLvl(), 0);
		effect.setIsForcedEffect(true);
		effect.initialize();
		effect.applyEffect();
	}

	public static void targetSelf(AbstractAI ai2) {
		ai2.getOwner().setTarget(ai2.getOwner());
	}

	public static void targetCreature(AbstractAI ai2, Creature target) {
		ai2.getOwner().setTarget(target);
	}

	public static void handleUseItemFinish(AbstractAI ai2, Player player) {
		ai2.getPosition().getWorldMapInstance().getInstanceHandler().handleUseItemFinish(player, ((Npc) ai2.getOwner()));
	}

	public static void fireNpcKillInstanceEvent(AbstractAI ai2, Player player) {
		ai2.getPosition().getWorldMapInstance().getInstanceHandler().onDie((Npc) ai2.getOwner());
	}

	public static void registerDrop(AbstractAI ai2, Player player, Collection<Player> registeredPlayers) {
		DropRegistrationService.getInstance().registerDrop((Npc) ai2.getOwner(), player, registeredPlayers);
	}

	public static void scheduleRespawn(NpcAI2 ai2){
		ai2.getOwner().getController().scheduleRespawn();
	}

	public static SelectDialogResult selectDialog(AbstractAI ai2, Player player, int questId, int dialogId) {
		QuestEnv env = new QuestEnv(ai2.getOwner(), player, questId, dialogId);
		boolean result =  QuestEngine.getInstance().onDialog(env);
		return new SelectDialogResult(result, env);
	}

	public static final class SelectDialogResult{
		private final boolean success;
		private final QuestEnv env;

		private SelectDialogResult(boolean success, QuestEnv env) {
			this.success = success;
			this.env = env;
		}
		public boolean isSuccess() {
			return success;
		}
		public QuestEnv getEnv() {
			return env;
		}

	}

	/**
	 * Add RequestResponseHandler to player with senderId equal to objectId of AI owner
	 */
	public static void addRequest(AbstractAI ai2, Player player, int requestId, AI2Request request,
		Object... requestParams) {
		addRequest(ai2, player, requestId, ai2.getObjectId(), request, requestParams);
	}

	/**
	 * Add RequestResponseHandler to player, which cancels request on movement
	 */
	public static void addRequest(AbstractAI ai2, Player player, int requestId, int senderId, int range, final AI2Request request,
		Object... requestParams) {

		boolean requested = player.getResponseRequester().putRequest(requestId, new RequestResponseHandler(ai2.getOwner()) {

			@Override
			public void denyRequest(Creature requester, Player responder) {
				request.denyRequest(requester, responder);
			}

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				request.acceptRequest(requester, responder);
			}
		});

		if (requested) {
                        if (range > 0) {
                                player.getObserveController().addObserver(new DialogObserver(ai2.getOwner(), player, range) {

                                        @Override
                                        public void tooFar(Creature requester, Player responder) {
                                                request.denyRequest(requester, responder);
                                        }
                                });
                        }
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(requestId, senderId, range, requestParams));
		}
	}

        /**
         * Add RequestResponseHandler to player
         */
        public static void addRequest(AbstractAI ai2, Player player, int requestId, int senderId, final AI2Request request,
		Object... requestParams) {
                addRequest(ai2, player, requestId, senderId, 0, request, requestParams);
        }
}
