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

package org.typezero.gameserver.model.broker;

import org.typezero.gameserver.model.gameobjects.BrokerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ATracer
 */
public class BrokerPlayerCache {

	private BrokerItem[] brokerListCache = new BrokerItem[0];
	private int brokerMaskCache;
	private int brokerSoftTypeCache;
	private int brokerStartPageCache;
	private List<Integer> itemList = new ArrayList<Integer>();

	/**
	 * @return the brokerListCache
	 */
	public BrokerItem[] getBrokerListCache() {
		return brokerListCache;
	}

	/**
	 * @param brokerListCache
	 *          the brokerListCache to set
	 */
	public void setBrokerListCache(BrokerItem[] brokerListCache) {
		this.brokerListCache = brokerListCache;
	}

	/**
	 * @return the brokerMaskCache
	 */
	public int getBrokerMaskCache() {
		return brokerMaskCache;
	}

	/**
	 * @param brokerMaskCache
	 *          the brokerMaskCache to set
	 */
	public void setBrokerMaskCache(int brokerMaskCache) {
		this.brokerMaskCache = brokerMaskCache;
	}

	/**
	 * @return the brokerSoftTypeCache
	 */
	public int getBrokerSortTypeCache() {
		return brokerSoftTypeCache;
	}

	/**
	 * @param brokerSoftTypeCache
	 *          the brokerSoftTypeCache to set
	 */
	public void setBrokerSortTypeCache(int brokerSoftTypeCache) {
		this.brokerSoftTypeCache = brokerSoftTypeCache;
	}

	/**
	 * @return the brokerStartPageCache
	 */
	public int getBrokerStartPageCache() {
		return brokerStartPageCache;
	}

	/**
	 * @param the
	 *          getSearchItemList
	 */
	public List<Integer> getSearchItemList() {
		if (this.itemList == null)
			return null;
		return this.itemList;
	}

	/**
	 * @param brokerStartPageCache
	 *          the brokerStartPageCache to set
	 */
	public void setBrokerStartPageCache(int brokerStartPageCache) {
		this.brokerStartPageCache = brokerStartPageCache;
	}

	/**
	 * @param setSearchItemsList
	 *          the searched item list to set
	 */
	public void setSearchItemsList(List<Integer> itemList) {
		this.itemList = itemList;
	}
}
