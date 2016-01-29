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

package org.typezero.gameserver.model.templates.housing;

import javax.xml.bind.annotation.*;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HousingUseableItem", propOrder = { "action" })
public class HousingUseableItem extends PlaceableHouseObject {

	@XmlElement(required = true)
	protected UseItemAction action;

	@XmlAttribute(required = true)
	protected boolean owner;

	@XmlAttribute
	protected Integer cd;

	@XmlAttribute(required = true)
	protected int delay;

	@XmlAttribute(name = "use_count")
	protected Integer useCount;

	@XmlAttribute(name = "required_item")
	protected Integer requiredItem;

	public UseItemAction getAction() {
		return action;
	}

	/**
	 * Can the object be used only by the owner or visitors too
	 */
	public boolean isOwnerOnly() {
		return owner;
	}

	/**
	 * @return null if no Cooltime is used
	 */
	public Integer getCd() {
		return cd;
	}

	public int getDelay() {
		return delay;
	}

	/**
	 * @return null if use is not restricted
	 */
	public Integer getUseCount() {
		return useCount;
	}

	/**
	 * @return null if no item is required
	 */
	public Integer getRequiredItem() {
		return requiredItem;
	}

	@Override
	public byte getTypeId() {
		return 1;
	}

}
