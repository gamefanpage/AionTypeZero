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
import javolution.util.FastList;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.templates.recipe.RecipeTemplate;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author ATracer, MrPoke, KID
 */
@XmlRootElement(name = "recipe_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecipeData {
	@XmlElement(name = "recipe_template")
	protected List<RecipeTemplate> list;
	private TIntObjectHashMap<RecipeTemplate> recipeData;
	private FastList<RecipeTemplate> elyos, asmos, any;

	void afterUnmarshal(Unmarshaller u, Object parent) {
		recipeData = new TIntObjectHashMap<RecipeTemplate>();
		elyos = FastList.newInstance();
		asmos = FastList.newInstance();
		any = FastList.newInstance();
		for (RecipeTemplate it : list) {
			recipeData.put(it.getId(), it);
			if (it.getAutoLearn() == 0)
				continue;

			switch(it.getRace()) {
				case ASMODIANS:
					asmos.add(it);
					break;
				case ELYOS:
					elyos.add(it);
					break;
				case PC_ALL:
					any.add(it);
					break;
			}
		}
		list = null;
	}

	public FastList<RecipeTemplate> getAutolearnRecipes(Race race, int skillId, int maxLevel) {
		FastList<RecipeTemplate> list = FastList.newInstance();
		switch(race) {
			case ASMODIANS:
				for(RecipeTemplate recipe : asmos)
					if(recipe.getSkillid() == skillId && recipe.getSkillpoint() <= maxLevel)
						list.add(recipe);
				break;
			case ELYOS:
				for(RecipeTemplate recipe : elyos)
					if(recipe.getSkillid() == skillId && recipe.getSkillpoint() <= maxLevel)
						list.add(recipe);
				break;
		}

		for(RecipeTemplate recipe : any)
			if(recipe.getSkillid() == skillId && recipe.getSkillpoint() <= maxLevel)
				list.add(recipe);

		return list;
	}

	public RecipeTemplate getRecipeTemplateById(int id) {
		return recipeData.get(id);
	}

	public TIntObjectHashMap<RecipeTemplate> getRecipeTemplates() {
		return recipeData;
	}

	/**
	 * @return recipeData.size()
	 */
	public int size() {
		return recipeData.size();
	}
}
