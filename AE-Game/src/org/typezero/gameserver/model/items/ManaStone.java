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

package org.typezero.gameserver.model.items;

import java.util.List;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.stats.calc.functions.StatFunction;
import org.typezero.gameserver.model.templates.item.ItemCategory;
import org.typezero.gameserver.model.templates.item.ItemTemplate;

/**
 * @author ATracer
 */
public class ManaStone extends ItemStone {

	private List<StatFunction> modifiers;
    private boolean AdvMana = false;

	public ManaStone(int itemObjId, int itemId, int slot, PersistentState persistentState) {
		super(itemObjId, itemId, slot, persistentState);

		ItemTemplate stoneTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if (stoneTemplate != null && stoneTemplate.getModifiers() != null) {
			this.modifiers = stoneTemplate.getModifiers();
		}
        if (stoneTemplate != null && stoneTemplate.getCategory() == ItemCategory.ANCIENT_MANASTONE) {
            this.AdvMana = true;
        }
	}

    public boolean isAncient()
    {
        return this.AdvMana;
    }

	/**
	 * @return modifiers
	 */
	public List<StatFunction> getModifiers() {
		return modifiers;
	}

	public StatFunction getFirstModifier() {
		return (modifiers != null && modifiers.size() > 0) ? modifiers.get(0) : null;
	}

}
