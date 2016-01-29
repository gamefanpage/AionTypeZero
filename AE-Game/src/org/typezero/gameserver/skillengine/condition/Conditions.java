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

package org.typezero.gameserver.skillengine.condition;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.stats.calc.Stat2;
import org.typezero.gameserver.model.stats.calc.functions.IStatFunction;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.Skill;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Conditions", propOrder = { "conditions" })
public class Conditions {

	@XmlElements({
		@XmlElement(name = "abnormal", type = AbnormalStateCondition.class),
		@XmlElement(name = "target", type = TargetCondition.class),
		@XmlElement(name = "mp", type = MpCondition.class),
		@XmlElement(name = "hp", type = HpCondition.class),
		@XmlElement(name = "dp", type = DpCondition.class),
		@XmlElement(name = "move_casting", type = PlayerMovedCondition.class),
		@XmlElement(name = "arrowcheck", type = ArrowCheckCondition.class),
		@XmlElement(name = "onfly", type = OnFlyCondition.class),
		@XmlElement(name = "weapon", type = WeaponCondition.class),
		@XmlElement(name = "noflying", type = NoFlyingCondition.class),
		@XmlElement(name = "lefthandweapon", type = LeftHandCondition.class),
		@XmlElement(name = "charge", type = ItemChargeCondition.class),
		@XmlElement(name = "chargeweapon", type = ChargeWeaponCondition.class),
		@XmlElement(name = "chargearmor", type = ChargeArmorCondition.class),
		@XmlElement(name = "polishchargeweapon", type = PolishChargeCondition.class),
		@XmlElement(name = "skillcharge", type = SkillChargeCondition.class),
		@XmlElement(name = "targetflying", type = TargetFlyingCondition.class),
		@XmlElement(name = "selfflying", type = SelfFlyingCondition.class),
		@XmlElement(name = "combatcheck", type = CombatCheckCondition.class),
		@XmlElement(name = "chain", type = ChainCondition.class),
		@XmlElement(name = "front", type = FrontCondition.class),
		@XmlElement(name = "back", type = BackCondition.class),
		@XmlElement(name = "form", type = FormCondition.class)

	})
	protected List<Condition> conditions;

	/**
	 * Gets the value of the conditions property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the conditions property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getConditions().add(newItem);
	 * </pre>
	 */
	public List<Condition> getConditions() {
		if (conditions == null) {
			conditions = new ArrayList<Condition>();
		}
		return this.conditions;
	}

	public boolean validate(Skill skill) {
		if (conditions != null) {
			for (Condition condition : getConditions()) {
				if (!condition.validate(skill)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean validate(Stat2 stat, IStatFunction statFunction) {
		if (conditions != null) {
			for (Condition condition : getConditions()) {
				if (!condition.validate(stat, statFunction)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean validate(Effect effect) {
		if (conditions != null) {
			for (Condition condition : getConditions()) {
				if (!condition.validate(effect)) {
					return false;
				}
			}
		}
		return true;
	}
}
