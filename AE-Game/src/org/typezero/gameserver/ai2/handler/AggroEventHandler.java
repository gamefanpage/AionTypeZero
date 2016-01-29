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

package org.typezero.gameserver.ai2.handler;

import java.util.Collections;

import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.controllers.attack.AttackResult;
import org.typezero.gameserver.controllers.attack.AttackStatus;
import org.typezero.gameserver.model.TribeClass;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.npc.NpcTemplateType;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK;
import org.typezero.gameserver.services.TribeRelationService;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.geo.GeoService;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author ATracer
 */
public class AggroEventHandler {

	/**
	 * @param npcAI
	 * @param myTarget
	 */
	public static void onAggro(NpcAI2 npcAI, final Creature myTarget) {
		final Npc owner = npcAI.getOwner();
		// TODO move out?
		if (myTarget.getAdminNeutral() == 1 || myTarget.getAdminNeutral() == 3 || myTarget.getAdminEnmity() == 1
			|| myTarget.getAdminEnmity() == 3 || TribeRelationService.isFriend(owner, myTarget))
			return;
		PacketSendUtility
			.broadcastPacket(
				owner,
				new SM_ATTACK(owner, myTarget, 0, 633, 0, Collections
					.singletonList(new AttackResult(0, AttackStatus.NORMALHIT))));

		ThreadPoolManager.getInstance().schedule(new AggroNotifier(owner, myTarget, true), 500);
	}

	public static boolean onCreatureNeedsSupport(NpcAI2 npcAI, Creature notMyTarget) {
		Npc owner = npcAI.getOwner();
		if (TribeRelationService.isSupport(notMyTarget, owner) && MathUtil.isInRange(owner, notMyTarget, owner.getAggroRange())
			&& GeoService.getInstance().canSee(owner, notMyTarget)) {
			VisibleObject myTarget = notMyTarget.getTarget();
			if (myTarget != null && myTarget instanceof Creature) {
				Creature targetCreature = (Creature) myTarget;
				PacketSendUtility.broadcastPacket(
					owner,
					new SM_ATTACK(owner, targetCreature, 0, 633, 0, Collections.singletonList(new AttackResult(0,
						AttackStatus.NORMALHIT))));
				ThreadPoolManager.getInstance().schedule(new AggroNotifier(owner, targetCreature, false), 500);
				return true;
			}
		}
		return false;
	}

	public static boolean onGuardAgainstAttacker(NpcAI2 npcAI, Creature attacker) {
		Npc owner = npcAI.getOwner();
		TribeClass tribe = owner.getTribe();
		if (!tribe.isGuard() && owner.getObjectTemplate().getNpcTemplateType() != NpcTemplateType.GUARD) {
			return false;
		}
		VisibleObject target = attacker.getTarget();
		if (target != null && target instanceof Player) {
			Player playerTarget = (Player) target;
			if (!owner.isEnemy(playerTarget) && owner.isEnemy(attacker)
				&& MathUtil.isInRange(owner, playerTarget, owner.getAggroRange())
				&& GeoService.getInstance().canSee(owner, attacker)) {
				owner.getAggroList().startHate(attacker);
				return true;
			}
		}
		return false;
	}

	private static final class AggroNotifier implements Runnable {

		private Npc aggressive;
		private Creature target;
		private boolean broadcast;

		AggroNotifier(Npc aggressive, Creature target, boolean broadcast) {
			this.aggressive = aggressive;
			this.target = target;
			this.broadcast = broadcast;
		}

		@Override
		public void run() {
			aggressive.getAggroList().addHate(target, 1);
			if (broadcast) {
				aggressive.getKnownList().doOnAllNpcs(new Visitor<Npc>() {

					@Override
					public void visit(Npc object) {
						object.getAi2().onCreatureEvent(AIEventType.CREATURE_NEEDS_SUPPORT, aggressive);
					}
				});
			}
			aggressive = null;
			target = null;
		}

	}

}
