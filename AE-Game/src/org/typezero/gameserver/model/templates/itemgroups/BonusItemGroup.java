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

package org.typezero.gameserver.model.templates.itemgroups;

import javax.xml.bind.annotation.*;

import org.typezero.gameserver.model.templates.rewards.BonusType;

/**
 * @author Rolandas
 *
 */

/**
 * <p>
 * Java class for ItemGroup complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BonusItemGroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="bonusType" use="required" type="{}BonusType" />
 *       &lt;attribute name="chance" type="{http://www.w3.org/2001/XMLSchema}float" default="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BonusItemGroup")
@XmlSeeAlso({ CraftItemGroup.class, CraftRecipeGroup.class, ManastoneGroup.class, FoodGroup.class, MedicineGroup.class,
	OreGroup.class, GatherGroup.class, EnchantGroup.class, BossGroup.class })
public abstract class BonusItemGroup {

	@XmlAttribute(name = "bonusType", required = true)
	protected BonusType bonusType;

	@XmlAttribute(name = "chance")
	protected Float chance;

	/**
	 * Gets the value of the bonusType property.
	 *
	 * @return possible object is {@link BonusType }
	 */
	public BonusType getBonusType() {
		return bonusType;
	}

	/**
	 * Gets the value of the chance property.
	 *
	 * @return possible object is {@link Float }
	 */
	public float getChance() {
		if (chance == null) {
			return 0.0F;
		}
		else {
			return chance;
		}
	}

	public abstract ItemRaceEntry[] getRewards();

}
