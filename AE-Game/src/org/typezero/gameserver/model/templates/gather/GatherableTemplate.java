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

package org.typezero.gameserver.model.templates.gather;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.typezero.gameserver.model.templates.VisibleObjectTemplate;

/**
 * @author ATracer, KID
 */

@XmlRootElement(name = "gatherable_template")
@XmlAccessorType(XmlAccessType.FIELD)
public class GatherableTemplate extends VisibleObjectTemplate {
	@XmlElement(required = true)
	protected Materials materials;
	@XmlElement(required = true)
	protected ExMaterials exmaterials;
	@XmlAttribute
	protected int id;
	@XmlAttribute
	protected String name;
	@XmlAttribute
	protected int nameId;
	@XmlAttribute
	protected String sourceType;
	@XmlAttribute
	protected int harvestCount;
	@XmlAttribute
	protected int skillLevel;
	@XmlAttribute
	protected int harvestSkill;
	@XmlAttribute
	protected int successAdj;
	@XmlAttribute
	protected int failureAdj;
	@XmlAttribute
	protected int aerialAdj;
	@XmlAttribute
	protected int captcha;
	@XmlAttribute
	protected int lvlLimit;
	@XmlAttribute
	protected int reqItem;
	@XmlAttribute
	protected int reqItemNameId;
	@XmlAttribute
	protected int checkType;
	@XmlAttribute
	protected int eraseValue;
	/**
	 * Gets the value of the materials property.
	 *
	 * @return possible object is {@link Materials }
	 */
	public Materials getMaterials() {
		return materials;
	}

	public ExMaterials getExtraMaterials() {
		return exmaterials;
	}

	/**
	 * Gets the value of the id property.
	 */
	@Override
	public int getTemplateId() {
		return id;
	}

	/**
	 * Gets the value of the aerialAdj property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public int getAerialAdj() {
		return aerialAdj;
	}

	/**
	 * Gets the value of the failureAdj property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public int getFailureAdj() {
		return failureAdj;
	}

	/**
	 * Gets the value of the successAdj property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public int getSuccessAdj() {
		return successAdj;
	}

	/**
	 * Gets the value of the harvestSkill property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public int getHarvestSkill() {
		return harvestSkill;
	}

	/**
	 * Gets the value of the skillLevel property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public int getSkillLevel() {
		return skillLevel;
	}

	/**
	 * Gets the value of the harvestCount property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public int getHarvestCount() {
		return harvestCount;
	}

	/**
	 * Gets the value of the sourceType property.
	 *
	 * @return possible object is {@link String }
	 */
	public String getSourceType() {
		return sourceType;
	}

	/**
	 * Gets the value of the name property.
	 *
	 * @return possible object is {@link String }
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @return the nameId
	 */
	@Override
	public int getNameId() {
		return nameId;
	}

	public int getCaptchaRate() {
		return captcha;
	}

	public int getLevelLimit() {
		return lvlLimit;
	}

	public int getRequiredItemId() {
		return reqItem;
	}

	public int getRequiredItemNameId() {
		return reqItemNameId * 2 + 1;
	}

	public int getCheckType() {
		return checkType;
	}

	public int getEraseValue() {
		return eraseValue;
	}
}
