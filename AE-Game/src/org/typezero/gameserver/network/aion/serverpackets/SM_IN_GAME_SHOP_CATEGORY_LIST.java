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

package org.typezero.gameserver.network.aion.serverpackets;

import org.typezero.gameserver.configs.ingameshop.InGameShopProperty;
import org.typezero.gameserver.model.ingameshop.InGameShopEn;
import org.typezero.gameserver.model.templates.ingameshop.IGCategory;
import org.typezero.gameserver.model.templates.ingameshop.IGSubCategory;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author xTz
 */
public class SM_IN_GAME_SHOP_CATEGORY_LIST extends AionServerPacket {

	private int type;
	private int categoryId;
	private InGameShopProperty ing;

	public SM_IN_GAME_SHOP_CATEGORY_LIST(int type, int category) {
		this.type = type;
		this.categoryId = category;
		ing = InGameShopEn.getInstance().getIGSProperty();
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(type);
		switch (type) {
			case 0:
				writeH(ing.size()); // size
				for (IGCategory category : ing.getCategories()) {
					writeD(category.getId()); // categry Id
					writeS(category.getName()); // category Name
				}
				break;
			case 2:
				if (categoryId < ing.size()) {
					IGCategory iGCategory = ing.getCategories().get(categoryId);
					writeH(iGCategory.getSubCategories().size()); // size
					for (IGSubCategory subCategory : iGCategory.getSubCategories()) {
						writeD(subCategory.getId()); // sub category Id
						writeS(subCategory.getName()); // sub category Name
					}
				}
				break;
		}
	}

}
