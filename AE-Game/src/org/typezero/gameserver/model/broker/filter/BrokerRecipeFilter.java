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

package org.typezero.gameserver.model.broker.filter;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.item.actions.CraftLearnAction;
import org.typezero.gameserver.model.templates.item.actions.ItemActions;
import org.typezero.gameserver.model.templates.recipe.RecipeTemplate;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author xTz
 */
public class BrokerRecipeFilter extends BrokerFilter {

	private int craftSkillId;
	private int[] masks;

	/**
	 * @param masks
	 */
	public BrokerRecipeFilter(int craftSkillId, int... masks) {
		this.craftSkillId = craftSkillId;
		this.masks = masks;
	}

	@Override
	public boolean accept(ItemTemplate template) {
		ItemActions actions = template.getActions();
		if (actions != null) {
			CraftLearnAction craftAction = actions.getCraftLearnAction();
			if (craftAction != null) {
				int id = craftAction.getRecipeId();
				RecipeTemplate recipeTemplate = DataManager.RECIPE_DATA.getRecipeTemplateById(id);
				if (recipeTemplate != null && recipeTemplate.getSkillid() == craftSkillId) {
					return ArrayUtils.contains(masks, template.getTemplateId() / 100000);
				}
			}
		}
		return false;
	}

}
