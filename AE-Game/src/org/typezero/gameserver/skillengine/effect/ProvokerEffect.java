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

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.controllers.observer.ObserverType;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.ProvokeTarget;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author ATracer modified by kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvokerEffect")
public class ProvokerEffect extends ShieldEffect {

	@XmlAttribute(name = "provoke_target")
	protected ProvokeTarget provokeTarget;
	@XmlAttribute(name = "skill_id")
	protected int skillId;

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
		if (effect.getEffected() != effect.getEffector() && effect.getEffector() instanceof Player) {
			PacketSendUtility.sendPacket((Player)effect.getEffector(), new SM_SYSTEM_MESSAGE(1301062, new DescriptionId(effect.getSkillTemplate().getNameId())));
		}
	}

	@Override
	public void startEffect(Effect effect) {
		ActionObserver observer = null;
		final Creature effector = effect.getEffector();
		final int prob2 = this.hitTypeProb;
		final int radius = this.radius;
		switch (this.hitType) {
			case NMLATK://ATTACK
				observer = new ActionObserver(ObserverType.ATTACK) {

					@Override
					public void attack(Creature creature) {
						if (Rnd.get(0, 100) <= prob2) {
							Creature target = getProvokeTarget(provokeTarget, effector, creature);
							createProvokedEffect(effector, target);
						}
					}

				};
				break;
			case EVERYHIT://ATTACKED
				observer = new ActionObserver(ObserverType.ATTACKED) {

					@Override
					public void attacked(Creature creature) {
						if (radius > 0) {
							if (!MathUtil.isIn3dRange(effector, creature, radius))
								return;
						}
						if (Rnd.get(0, 100) <= prob2) {
							Creature target = getProvokeTarget(provokeTarget, effector, creature);
							createProvokedEffect(effector, target);
						}
					}
				};
				break;
				//TODO MAHIT and PHHIT
			default:
				break;
		}

		if (observer == null)
			return;

		effect.setActionObserver(observer, position);
		effect.getEffected().getObserveController().addObserver(observer);
	}

	/**
	 * @param effector
	 * @param target
	 */
	private void createProvokedEffect(final Creature effector, Creature target) {
		SkillEngine.getInstance().applyEffectDirectly(skillId, effector, target, 0);
	}

	/**
	 * @param provokeTarget
	 * @param effector
	 * @param target
	 * @return
	 */
	private Creature getProvokeTarget(ProvokeTarget provokeTarget, Creature effector, Creature target) {
		switch (provokeTarget) {
			case ME:
				return effector;
			case OPPONENT:
				return target;
		}
		throw new IllegalArgumentException("Provoker target is invalid " + provokeTarget);
	}

	@Override
	public void endEffect(Effect effect) {
		ActionObserver observer = effect.getActionObserver(position);
		if (observer != null)
			effect.getEffected().getObserveController().removeObserver(observer);
	}
}
