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

package org.typezero.gameserver.skillengine.effect;

import java.util.concurrent.ScheduledFuture;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.geoEngine.collision.CollisionIntention;
import org.typezero.gameserver.geoEngine.math.Vector3f;

import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.configs.main.GeoDataConfig;
import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.controllers.observer.ObserverType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.network.aion.serverpackets.SM_TARGET_IMMOBILIZE;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.PositionUtil;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.geo.GeoService;

/**
 * @author Sarynth
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FearEffect")
public class FearEffect extends EffectTemplate {

	@XmlAttribute
	protected int resistchance = 100;

	@Override
	public void applyEffect(Effect effect) {
		effect.getEffected().getEffectController().removeHideEffects();
		effect.addToEffectedController();
	}

	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.FEAR_RESISTANCE, null);
	}

	@Override
	public void startEffect(final Effect effect) {
		final Creature effector = effect.getEffector();
		final Creature effected = effect.getEffected();
		effected.getController().cancelCurrentSkill();
		effect.setAbnormal(AbnormalState.FEAR.getId());
		effected.getEffectController().setAbnormal(AbnormalState.FEAR.getId());

		//PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TARGET_IMMOBILIZE(effected));
		effected.getController().stopMoving();

		if (effected instanceof Npc)
			((NpcAI2)effected.getAi2()).setStateIfNot(AIState.FEAR);
		if (GeoDataConfig.FEAR_ENABLE) {
			ScheduledFuture<?> fearTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(
				new FearTask(effector, effected), 0, 1000);
			effect.setPeriodicTask(fearTask, position);
		}

		//resistchance of fear effect to damage, if value is lower than 100, fear can be interrupted bz damage
		//example skillId: 540 Terrible howl
		if (resistchance < 100) {
			ActionObserver observer = new ActionObserver(ObserverType.ATTACKED) {

				@Override
				public void attacked(Creature creature) {
					if (Rnd.get(0, 100) > resistchance)
						effected.getEffectController().removeEffect(effect.getSkillId());
				}
			};
			effected.getObserveController().addObserver(observer);
			effect.setActionObserver(observer, position);
		}
	}

	@Override
	public void endEffect(Effect effect) {
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.FEAR.getId());

		// for now we support only players
		if (GeoDataConfig.FEAR_ENABLE) {
			effect.getEffected().getMoveController().abortMove();// TODO impl stopMoving?
		}
		if (effect.getEffected() instanceof Npc){
			((NpcAI2)effect.getEffected().getAi2()).onCreatureEvent(AIEventType.ATTACK, effect.getEffector());
		}
		PacketSendUtility.broadcastPacketAndReceive(effect.getEffected(), new SM_TARGET_IMMOBILIZE(effect.getEffected()));

		if (resistchance < 100) {
			ActionObserver observer = effect.getActionObserver(position);
			if (observer != null)
				effect.getEffected().getObserveController().removeObserver(observer);
		}
	}

	class FearTask implements Runnable {

		private Creature effector;
		private Creature effected;

		FearTask(Creature effector, Creature effected) {
			this.effector = effector;
			this.effected = effected;
		}

		@Override
		public void run() {
			if (effected.getEffectController().isUnderFear()) {
				float x = effected.getX();
				float y = effected.getY();
				if (!MathUtil.isNearCoordinates(effected, effector, 40))
					return;
				byte moveAwayHeading = PositionUtil.getMoveAwayHeading(effector, effected);
				double radian = Math.toRadians(MathUtil.convertHeadingToDegree(moveAwayHeading));
				float maxDistance = effected.getGameStats().getMovementSpeedFloat();
				float x1 = (float) (Math.cos(radian) * maxDistance);
				float y1 = (float) (Math.sin(radian) * maxDistance);
				byte intentions = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId());
				Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effected, x+x1, y+y1, effected.getZ(), true, intentions);
				if (effected.isFlying()) {
					closestCollision.setZ(effected.getZ());
				}
				if (effected instanceof Npc){
					((Npc)effected).getMoveController().resetMove();
					((Npc)effected).getMoveController().moveToPoint(closestCollision.getX(), closestCollision.getY(),
					closestCollision.getZ());
				}
				else{
					effected.getMoveController().setNewDirection(closestCollision.getX(), closestCollision.getY(),
						closestCollision.getZ(), moveAwayHeading);
					effected.getMoveController().startMovingToDestination();
				}
			}
		}
	}
}
