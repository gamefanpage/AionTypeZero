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

package org.typezero.gameserver.model.templates.goods;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.limiteditems.LimitedItem;
import javax.xml.bind.annotation.XmlElement;
import javolution.util.FastList;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GoodsList")
public class GoodsList {

	@XmlElement(name = "item")
	private List<Item> items;
	@XmlAttribute(name = "id")
	private int id;
	@XmlElement(name = "salestime")
	private String salesTime;

	private List<Integer> itemIdList;

	void afterUnmarshal(Unmarshaller u, Object parent) {
		itemIdList = new ArrayList<Integer>();
		if (items == null)
			return;

		for (Item item : items) {
			itemIdList.add(item.getId());
		}
	}

	/**
	 * return the limitedItems.
	 */
	public FastList<LimitedItem> getLimitedItems() {
		FastList<LimitedItem> limitedItems = new FastList<LimitedItem>();
		if (items != null) {
			for (Item item : items) {
				if (item.getBuyLimit() != null && item.getSellLimit() != null) {
					limitedItems.add(new LimitedItem(item.getId(), item.getSellLimit(), item.getBuyLimit(), salesTime));
				}
			}
		}
		return limitedItems;
	}

	/**
	 * Gets the value of the id property.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the itemIdList
	 */
	public List<Integer> getItemIdList() {
		return itemIdList;
	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * <p>
	 * The following schema fragment specifies the expected content contained within this class.
	 *
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}int" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class Item {

		@XmlAttribute
		private int id;
		@XmlAttribute(name = "sell_limit")
		private Integer sellLimit;
		@XmlAttribute(name = "buy_limit")
		private Integer buyLimit;

		/**
		 * Gets the value of the id property.
		 */
		public int getId() {
			return id;
		}

		/**
		 * return sellLimit.
		 */
		public Integer getSellLimit() {
			return sellLimit;
		}

		/**
		 * return buyLimit.
		 */
		public Integer getBuyLimit() {
			return buyLimit;
		}
	}
}
