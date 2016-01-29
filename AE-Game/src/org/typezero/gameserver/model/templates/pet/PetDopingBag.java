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

package org.typezero.gameserver.model.templates.pet;

import java.util.Arrays;

/**
 * @author Rolandas
 */
public class PetDopingBag {

	private int[] itemBag = null;
	private boolean isDirty = false;

	public void setFoodItem(int itemId) {
		setItem(itemId, 0);
	}

	public int getFoodItem() {
		if (itemBag == null || itemBag.length < 1)
			return 0;
		return itemBag[0];
	}

	public void setDrinkItem(int itemId) {
		setItem(itemId, 1);
	}

	public int getDrinkItem() {
		if (itemBag == null || itemBag.length < 2)
			return 0;
		return itemBag[1];
	}

	/**
	 * Adds or removes item to the bag
	 * @param itemId - item Id, or 0 to remove
	 * @param slot - slot number; 0 for food, 1 for drink, the rest are for scrolls
	 */
	public void setItem(int itemId, int slot) {
		if (itemBag == null) {
			itemBag = new int[slot + 1];
			isDirty = true;
		}
		else if (slot > itemBag.length - 1) {
			itemBag = Arrays.copyOf(itemBag, slot + 1);
			isDirty = true;
		}
		if (itemBag[slot] != itemId) {
			itemBag[slot] = itemId;
			isDirty = true;
		}
	}

	public int[] getScrollsUsed() {
		if (itemBag == null || itemBag.length < 3)
			return new int[0];
		return Arrays.copyOfRange(itemBag, 2, itemBag.length);
	}

	/**
	 * @return true if the bag needs saving
	 */
	public boolean isDirty() {
		return isDirty;
	}

}
