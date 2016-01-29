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

package org.typezero.gameserver.skillengine;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.skillengine.model.ActivationAttribute;
import org.typezero.gameserver.skillengine.model.ChargeSkill;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.Skill;
import org.typezero.gameserver.skillengine.model.SkillTemplate;

/**
 * @author ATracer
 */
public class SkillEngine {

	public static final SkillEngine skillEngine = new SkillEngine();

	/**
	 * should not be instantiated directly
	 */
	private SkillEngine() {

	}

	/**
	 * This method is used for skills that were learned by player
	 *
	 * @param player
	 * @param skillId
	 * @return Skill
	 */
	public Skill getSkillFor(Player player, int skillId, VisibleObject firstTarget) {
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);

		if (template == null)
			return null;

		return getSkillFor(player, template, firstTarget);
	}

	/**
	 * This method is used for skills that were learned by player
	 *
	 * @param player
	 * @param template
	 * @param firstTarget
	 * @return
	 */
	public Skill getSkillFor(Player player, SkillTemplate template, VisibleObject firstTarget) {
		// player doesn't have such skill and ist not provoked
		if (template.getActivationAttribute() != ActivationAttribute.PROVOKED) {
			if (!player.getSkillList().isSkillPresent(template.getSkillId()))
				return null;
		}

		Creature target = null;
		if (firstTarget instanceof Creature)
			target = (Creature) firstTarget;

		return new Skill(template, player, target);
	}

	public Skill getSkillFor(Player player, SkillTemplate template, VisibleObject firstTarget, int skillLevel) {
		Creature target = null;
		if (firstTarget instanceof Creature)
			target = (Creature) firstTarget;

		return new Skill(template, player, target, skillLevel);
	}

	/**
	 * This method is used for not learned skills (item skills etc)
	 *
	 * @param creature
	 * @param skillId
	 * @param skillLevel
	 * @return Skill
	 */
	public Skill getSkill(Creature creature, int skillId, int skillLevel, VisibleObject firstTarget) {
		return getSkill(creature, skillId, skillLevel, firstTarget, null);
	}

	public Skill getSkill(Creature creature, int skillId, int skillLevel, VisibleObject firstTarget,
		ItemTemplate itemTemplate) {
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);

		if (template == null)
			return null;

		Creature target = null;
		if (firstTarget instanceof Creature)
			target = (Creature) firstTarget;
		return new Skill(template, creature, skillLevel, target, itemTemplate);
	}


	public ChargeSkill getChargeSkill(Player creature, int skillId, int skillLevel, VisibleObject firstTarget) {
		return getChargeSkill(creature, skillId, skillLevel, firstTarget, null);
	}

	public ChargeSkill getChargeSkill(Player creature, int skillId, int skillLevel, VisibleObject firstTarget,
		ItemTemplate itemTemplate) {
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);

		if (template == null)
			return null;

		Creature target = null;
		if (firstTarget instanceof Creature)
			target = (Creature) firstTarget;
		return new ChargeSkill(template, creature, skillLevel, target, itemTemplate);
	}

	public static SkillEngine getInstance() {
		return skillEngine;
	}
	/**
	 * This method is used to apply directly effect of given skill without checking properties, sending packets, etc
	 * Should be only used from quest scripts, or when you are sure about it
	 *
	 * @param skillId
	 * @param effector
	 * @param effected
	 * @param duration => 0 takes duration from skill_templates, >0 forced duration
	 */
	public void applyEffectDirectly(int skillId, Creature effector, Creature effected, int duration) {
		SkillTemplate st = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (st == null)
			return;

		final Effect ef = new Effect(effector, effected, st, st.getLvl(), duration);
		ef.setIsForcedEffect(true);
		ef.initialize();
		if(duration > 0)
			ef.setForcedDuration(true);
		ef.applyEffect();
	}
}
