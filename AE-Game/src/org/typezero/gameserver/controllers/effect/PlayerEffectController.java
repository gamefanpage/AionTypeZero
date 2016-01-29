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

package org.typezero.gameserver.controllers.effect;

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_ABNORMAL_STATE;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAYER_STANCE;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.SkillTargetSlot;
import org.typezero.gameserver.skillengine.model.SkillTemplate;
import org.typezero.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import org.typezero.gameserver.taskmanager.tasks.TeamEffectUpdater;
import org.typezero.gameserver.utils.PacketSendUtility;

import java.util.Collection;
import java.util.Collections;

/**
 * @author ATracer
 */
public class PlayerEffectController extends EffectController {

	public PlayerEffectController(Creature owner) {
		super(owner);
	}

	@Override
	public void addEffect(Effect effect) {
		if (checkDuelCondition(effect) && !effect.getIsForcedEffect())
			return;

		super.addEffect(effect);
		updatePlayerIconsAndGroup(effect);
	}

	@Override
	public void clearEffect(Effect effect) {
		super.clearEffect(effect);
		updatePlayerIconsAndGroup(effect);
	}

	@Override
	public Player getOwner() {
		return (Player) super.getOwner();
	}

	/**
	 * @param effect
	 */
	private void updatePlayerIconsAndGroup(Effect effect) {
		if (!effect.isPassive()) {
			updatePlayerEffectIcons();
			if (getOwner().isInTeam()) {
				TeamEffectUpdater.getInstance().startTask(getOwner());
			}
		}
	}

	@Override
	public void updatePlayerEffectIcons() {
		getOwner().addPacketBroadcastMask(BroadcastMode.UPDATE_PLAYER_EFFECT_ICONS);
	}

	@Override
	public void updatePlayerEffectIconsImpl() {
		Collection<Effect> effects = getAbnormalEffectsToShow();
		PacketSendUtility.sendPacket((Player) getOwner(), new SM_ABNORMAL_STATE(effects, abnormals));
	}

	/**
	 * Effect of DEBUFF should not be added if duel ended (friendly unit)
	 *
	 * @param effect
	 * @return
	 */
	private boolean checkDuelCondition(Effect effect) {
		Creature creature = effect.getEffector();
		if (creature instanceof Player) {
			if (!getOwner().isEnemy(creature) && effect.getTargetSlot() == SkillTargetSlot.DEBUFF.ordinal()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param skillId
	 * @param skillLvl
	 * @param currentTime
	 * @param reuseDelay
	 */
	public void addSavedEffect(int skillId, int skillLvl, int remainingTime, long endTime) {
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);

		if (remainingTime <= 0)
			return;
		if (CustomConfig.ABYSSXFORM_LOGOUT
			&& template.isDeityAvatar()) {

			if (System.currentTimeMillis() >= endTime)
				return;
			else
				remainingTime = (int)(endTime - System.currentTimeMillis());
		}

		Effect effect = new Effect(getOwner(), getOwner(), template, skillLvl, remainingTime);
		abnormalEffectMap.put(effect.getStack(), effect);
		effect.addAllEffectToSucess();
		effect.startEffect(true);

		if (effect.getSkillTemplate().getTargetSlot() != SkillTargetSlot.NOSHOW)
			PacketSendUtility.sendPacket(getOwner(), new SM_ABNORMAL_STATE(Collections.singletonList(effect), abnormals));

	}

	@Override
	public void broadCastEffectsImp() {
		super.broadCastEffectsImp();
		Player player = getOwner();
		if (player.getController().isUnderStance()) {
			PacketSendUtility.sendPacket(player, new SM_PLAYER_STANCE(player, 1));
		}
	}

}
