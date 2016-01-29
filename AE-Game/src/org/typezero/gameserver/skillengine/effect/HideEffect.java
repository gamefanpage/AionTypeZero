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

import org.typezero.gameserver.configs.main.SecurityConfig;
import org.typezero.gameserver.controllers.attack.AttackUtil;
import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.controllers.observer.ObserverType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureVisualState;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import org.typezero.gameserver.services.player.PlayerVisualStateService;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.Skill;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Sweetkr
 * @author Cura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HideEffect")
public class HideEffect extends BufEffect {

	@XmlAttribute
	protected CreatureVisualState state;
	@XmlAttribute(name = "bufcount")
	protected int buffCount;
	@XmlAttribute
	protected int type = 0;

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);

		final Creature effected = effect.getEffected();
		effected.getEffectController().unsetAbnormal(AbnormalState.HIDE.getId());

		effected.unsetVisualState(state);

		if (effected instanceof Player) {
			ActionObserver observer = effect.getActionObserver(position);
			effect.getEffected().getObserveController().removeObserver(observer);
		}

		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_PLAYER_STATE(effected));

		// anti-cheat
		if (SecurityConfig.INVIS && effected instanceof Player)
			PlayerVisualStateService.hideValidate((Player) effected);
	}

	@Override
	public void startEffect(final Effect effect) {
		super.startEffect(effect);

		final Creature effected = effect.getEffected();
		effected.getEffectController().setAbnormal(AbnormalState.HIDE.getId());
		effect.setAbnormal(AbnormalState.HIDE.getId());

		effected.setVisualState(state);

		// Cancel targeted enemy cast
		AttackUtil.cancelCastOn(effected);

		// send all to set new 'effected' visual state (remove all visual targetting from 'effected')
		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_PLAYER_STATE(effected));

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				// do on all who targetting on 'effected' (set target null, cancel attack skill, cancel npc pursuit)
				AttackUtil.removeTargetFrom(effected, true);
			}

		}, 500);

		/**
		 * for player adding:
		 * Remove Hide when using any item action
		 * . when requesting dialog to any npc
		 * . when being attacked
		 * . when attacking
		 */
		if (effected instanceof Player) {

			if (SecurityConfig.INVIS)
				PlayerVisualStateService.hideValidate((Player) effected);

			// Remove Hide when use skill
			ActionObserver observer = new ActionObserver(ObserverType.SKILLUSE) {
				int bufNumber = 1;

				@Override
				public void skilluse(Skill skill) {
					// [2.5] Allow self buffs = (buffCount - 1)
					if (skill.isSelfBuff() && bufNumber++ < buffCount)
						return;

					effect.endEffect();
				}

			};
			effected.getObserveController().addObserver(observer);
			effect.setActionObserver(observer, position);

			// Set attacked and dotattacked observers
			// type >= 1, hide is maintained even after damage
			if (type == 0)
				effect.setCancelOnDmg(true);

			// Remove Hide when attacking
			effected.getObserveController().attach(new ActionObserver(ObserverType.ATTACK) {

				@Override
				public void attack(Creature creature) {
						effect.endEffect();
				}
			});
                        //4.5 delete
			/*effected.getObserveController().attach(new ActionObserver(ObserverType.ITEMUSE) {

				@Override
				public void itemused(Item item) {
					effect.endEffect();
				}

			});*/
			effected.getObserveController().attach(new ActionObserver(ObserverType.NPCDIALOGREQUEST) {

				@Override
				public void npcdialogrequested(Npc npc) {
					effect.endEffect();
				}

			});
		}
		else { // effected is npc
			if (type == 0) { // type >= 1, hide is maintained even after damage
				effect.setCancelOnDmg(true);

				// Remove Hide when attacking
				effected.getObserveController().attach(new ActionObserver(ObserverType.ATTACK) {

					@Override
					public void attack(Creature creature) {
						effect.endEffect();
					}

				});

				// Remove Hide when use skill
				effected.getObserveController().attach(new ActionObserver(ObserverType.SKILLUSE) {

					@Override
					public void skilluse(Skill skill) {
						effect.endEffect();
					}

				});
			}
		}
	}

}
