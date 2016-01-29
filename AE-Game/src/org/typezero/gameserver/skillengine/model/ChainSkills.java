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

package org.typezero.gameserver.skillengine.model;

import javolution.util.FastMap;
import org.typezero.gameserver.model.gameobjects.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


/**
 * @author kecimis
 */
public class ChainSkills {

	private Map<String, ChainSkill> multiSkills = new FastMap<String, ChainSkill>();
	private ChainSkill chainSkill = new ChainSkill("", 0, 0);

	//private Logger log = LoggerFactory.getLogger(ChainSkills.class);

	public int getChainCount(Player player, SkillTemplate template, String category) {
		if (category == null) {
			return 0;
		}
		long nullTime = player.getSkillCoolDown(template.getCooldownId());
		if (this.multiSkills.get(category) != null) {
			if (System.currentTimeMillis() >= nullTime && this.multiSkills.get(category).getUseTime() <= nullTime) {
				this.multiSkills.get(category).setChainCount(0);
			}

			return this.multiSkills.get(category).getChainCount();
		}

		return 0;
	}

	public long getLastChainUseTime(String category) {
		if (this.multiSkills.get(category) != null) {
			return this.multiSkills.get(category).getUseTime();
		}
		else if (chainSkill.getCategory().equals(category))
			return this.chainSkill.getUseTime();
		else
			return 0;
	}

	/**
	 * returns true if next chain skill can still be casted, or time is over
	 *
	 * @param category
	 * @param time
	 * @return
	 */
	public boolean chainSkillEnabled(String category, int time) {
		long useTime = 0;
		if (this.multiSkills.get(category) != null) {
			useTime = this.multiSkills.get(category).getUseTime();
		}
		else if (chainSkill.getCategory().equals(category)) {
			useTime = chainSkill.getUseTime();
		}

		if ((useTime + time) >= System.currentTimeMillis())
			return true;
		else
			return false;
	}

	public void addChainSkill(String category, boolean multiCast) {
		if (multiCast) {
			if (this.multiSkills.get(category) != null) {
				if (multiCast) {
					this.multiSkills.get(category).increaseChainCount();
				}
				this.multiSkills.get(category).setUseTime(System.currentTimeMillis());
			}
			else
				this.multiSkills.put(category, new ChainSkill(category, (multiCast ? 1 : 0), System.currentTimeMillis()));
		}
		else
			chainSkill.updateChainSkill(category);
	}

	public Collection<ChainSkill> getChainSkills() {
		Collection<ChainSkill> collection = new ArrayList<ChainSkill>();
		collection.add(this.chainSkill);
		collection.addAll(this.multiSkills.values());

		return collection;
	}
}

