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

package org.typezero.gameserver.model.templates.tradelist;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author orz
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "tradelist_template")
public class TradeListTemplate {

	/**
	 * Npc Id.
	 */
	@XmlAttribute(name = "npc_id", required = true)
	private int npcId;
	@XmlAttribute(name = "npc_type")
	private TradeNpcType tradeNpcType = TradeNpcType.NORMAL;
	@XmlAttribute(name = "sell_price_rate")
	private int sellPriceRate = 100;
	@XmlAttribute(name = "buy_price_rate")
	private int buyPriceRate ;
	@XmlElement(name = "tradelist")
	protected List<TradeTab> tradeTablist;
	/**
	 * @return List<TradeTab>
	 */
	public List<TradeTab> getTradeTablist() {
		if (tradeTablist == null)
			tradeTablist = new ArrayList<TradeTab>();
		return this.tradeTablist;
	}

	public int getNpcId() {
		return npcId;
	}

	public int getCount() {
		return tradeTablist.size();
	}

	/**
	 * @return the Npc Type
	 */
	public TradeNpcType getTradeNpcType() {
		return tradeNpcType;
	}

	/**
	 * @return the sellPriceRate
	 */
	public int getSellPriceRate() {
		return sellPriceRate;
	}

	/**
	 * @return the buyPriceRate
	 */
	public int getBuyPriceRate() {
		return buyPriceRate;
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
	@XmlType(name = "Tradelist")
	public static class TradeTab {

		@XmlAttribute
		protected int id;

		/**
		 * Gets the value of the id property.
		 */
		public int getId() {
			return id;
		}
	}
}
