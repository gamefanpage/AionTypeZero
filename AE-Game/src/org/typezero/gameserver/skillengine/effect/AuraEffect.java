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

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.network.aion.serverpackets.SM_MANTRA_EFFECT;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.SkillTemplate;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.utils.audit.AuditLogger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;

/**
 * @author ATracer, kecimis, xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuraEffect")
public class AuraEffect extends EffectTemplate {

	@XmlAttribute
	protected int distance;
	@XmlAttribute(name = "skill_id")
	protected int skillId;
	//TODO distancez

	@Override
	public void applyEffect(Effect effect) {
		final Player effector = (Player) effect.getEffector();
		if (effector.getEffectController().isNoshowPresentBySkillId(effect.getSkillId())) {
			AuditLogger.info(effector, "Player might be abusing CM_CASTSPELL mantra effect Player kicked skill id: " + effect.getSkillId());
			effector.getClientConnection().closeNow();
			return;
		}
		effect.addToEffectedController();
	}

	@Override
	public void onPeriodicAction(final Effect effect) {
		final Player effector = (Player) effect.getEffector();
		if (!effector.isOnline()) { // task check
			return;
		}
		if (effector.isInGroup2() || effector.isInAlliance2()) {
			Collection<Player> onlynePlayers = effector.isInGroup2() ? effector.getPlayerGroup2().getOnlineMembers()
					: effector.getPlayerAllianceGroup2().getOnlineMembers();
			final int actualRange = (int)(distance * effector.getGameStats().getStat(StatEnum.BOOST_MANTRA_RANGE, 100).getCurrent() / 100f);
			for (Player player : onlynePlayers) {
				if (MathUtil.isIn3dRange(effector, player, actualRange)) {
					applyAuraTo(player, effect);
				}
			}
		}
		else {
			applyAuraTo(effector, effect);
		}
		PacketSendUtility.broadcastPacket(effector, new SM_MANTRA_EFFECT(effector, skillId));
	}

	/**
	 * @param effector
	 */
	private void applyAuraTo(Player effected, Effect effect) {
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		Effect e = new Effect(effected, effected, template, template.getLvl(), 0);
		e.initialize();
		e.applyEffect();
	}

	@Override
	public void startEffect(final Effect effect) {
		effect.setPeriodicTask(ThreadPoolManager.getInstance().scheduleAtFixedRate(new AuraTask (effect), 0, 6500), position);
	}

	private class AuraTask implements Runnable {

		private Effect effect;

		public AuraTask(Effect effect) {
			this.effect = effect;
		}

		@Override
		public void run() {
			onPeriodicAction(effect);
			/**
			 * This has the special effect of clearing the current thread's quantum
			 * and putting it to the end of the queue for its priority level.
			 * Will just give-up the thread's turn, and gain it in the next round.
			 */
			Thread.yield();
		}
	}

	@Override
	public void endEffect(Effect effect) {
		// nothing todo
	}

}
