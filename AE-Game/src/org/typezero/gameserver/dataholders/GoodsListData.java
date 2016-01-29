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

package org.typezero.gameserver.dataholders;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.typezero.gameserver.model.templates.goods.GoodsList;

/**
 * @author ATracer
 */
@XmlRootElement(name = "goodslists")
@XmlAccessorType(XmlAccessType.FIELD)
public class GoodsListData {

	@XmlElement(required = true)
	protected List<GoodsList> list;

	@XmlElement(name = "in_list")
	protected List<GoodsList> inList;

	@XmlElement(name = "purchase_list")
	protected List<GoodsList> purchaseList;

	/** A map containing all goodslist templates */
	private TIntObjectHashMap<GoodsList> goodsListData;
	private TIntObjectHashMap<GoodsList> goodsInListData;
	private TIntObjectHashMap<GoodsList> goodsPurchaseListData;

	void afterUnmarshal(Unmarshaller u, Object parent) {
		goodsListData = new TIntObjectHashMap<GoodsList>();
		for (GoodsList it : list) {
			goodsListData.put(it.getId(), it);
		}
		goodsInListData = new TIntObjectHashMap<GoodsList>();
		for (GoodsList it : inList) {
			goodsInListData.put(it.getId(), it);
		}
		goodsPurchaseListData = new TIntObjectHashMap<GoodsList>();
		for (GoodsList it : purchaseList) {
			goodsPurchaseListData.put(it.getId(), it);
		}
		list = null;
		inList = null;
		purchaseList = null;
	}

	public GoodsList getGoodsListById(int id) {
		return goodsListData.get(id);
	}

	public GoodsList getGoodsInListById(int id) {
		return goodsInListData.get(id);
	}

	public GoodsList getGoodsPurchaseListById(int id) {
		return goodsPurchaseListData.get(id);
	}
	/**
	 * @return goodListData.size()
	 */
	public int size() {
		return goodsListData.size()+goodsInListData.size()+goodsPurchaseListData.size();
	}
}
