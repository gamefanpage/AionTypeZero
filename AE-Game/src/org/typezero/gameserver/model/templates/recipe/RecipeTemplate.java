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

package org.typezero.gameserver.model.templates.recipe;

import org.typezero.gameserver.model.Race;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RecipeTemplate")
public class RecipeTemplate {

	protected List<Component> component;
	protected List<ComboProduct> comboproduct;
	@XmlAttribute(name = "max_production_count")
	protected Integer maxProductionCount;
	@XmlAttribute(name = "craft_delay_time")
	protected Integer craftDelayTime;
	@XmlAttribute(name = "craft_delay_id")
	protected Integer craftDelayId;
	@XmlAttribute
	protected int quantity;
	@XmlAttribute
	protected int productid;
	@XmlAttribute
	protected int autolearn;
	@XmlAttribute
	protected int dp;
	@XmlAttribute
	protected int skillpoint;
	@XmlAttribute
	protected Race race;
	@XmlAttribute
	protected int skillid;
	@XmlAttribute
	protected int itemid;
	@XmlAttribute
	protected int nameid;
	@XmlAttribute
	protected int id;

	/**
	 * Gets the value of the component property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the component property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getComponent().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Component }
	 */
	public List<Component> getComponent() {
		if (component == null) {
			component = new ArrayList<Component>();
		}
		return this.component;
	}

	public Integer getComboProduct(int num) {
		if (comboproduct == null || comboproduct.get(num - 1) == null) {
			return null;
		}
		return comboproduct.get(num - 1).getItemid();
	}

	public Integer getComboProductSize() {
		if (comboproduct == null) {
			return 0;
		}
		return comboproduct.size();
	}

	/**
	 * Gets the value of the quantity property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * Gets the value of the productid property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getProductid() {
		return productid;
	}

	/**
	 * Gets the value of the autolearn property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public int getAutoLearn() {
		return autolearn;
	}

	/**
	 * Gets the value of the dp property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getDp() {
		return dp;
	}

	/**
	 * Gets the value of the skillpoint property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getSkillpoint() {
		return skillpoint;
	}

	/**
	 * Gets the value of the race property.
	 *
	 * @return possible object is {@link String }
	 */
	public Race getRace() {
		return race;
	}

	/**
	 * Gets the value of the skillid property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getSkillid() {
		return skillid;
	}

	/**
	 * Gets the value of the itemid property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getItemid() {
		return itemid;
	}

	/**
	 * @return the nameid
	 */
	public int getNameid() {
		return nameid;
	}

	/**
	 * Gets the value of the id property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return Returns the maxProductionCount.
	 */
	public Integer getMaxProductionCount() {
		return maxProductionCount;
	}

	/**
	 * @return Returns the craftDelayTime.
	 */
	public Integer getCraftDelayTime() {
		return craftDelayTime;
	}

	/**
	 * @return Returns the craftDelayId.
	 */
	public Integer getCraftDelayId() {
		return craftDelayId;
	}
}
