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


package org.typezero.gameserver.model.drop;

import java.util.List;
import java.util.Set;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import java.util.Collection;

/**
 * @author MrPoke
 *
 */
public class DropGroup implements DropCalculator{
    protected List<Drop> drop;
    protected Race race = Race.PC_ALL;
    protected Boolean useCategory = true;
    protected String group_name;


    /**
		 * @param drop
		 * @param race
		 * @param useCategory
		 * @param group_name
		 */
		public DropGroup(List<Drop> drop, Race race, Boolean useCategory, String group_name) {
			this.drop = drop;
			this.race = race;
			this.useCategory = useCategory;
			this.group_name = group_name;
		}

		public List<Drop> getDrop() {
        return this.drop;
    }

    public Race getRace() {
        return race;
    }

    public Boolean isUseCategory() {
        return useCategory;
    }


	/**
	 * @return the name
	 */
	public String getGroupName() {
		if (group_name == null)
			return "";
		return group_name;
	}

	@Override
	public int dropCalculator(Set<DropItem> result, int index, float dropModifier, Race race, Collection<Player> groupMembers) {
			if (useCategory) {
				Drop d = drop.get(Rnd.get(0, drop.size() - 1));
				return d.dropCalculator(result, index, dropModifier, race, groupMembers);
			}
			else {
				for (int i = 0; i < drop.size(); i++) {
					Drop d = drop.get(i);
					index = d.dropCalculator(result, index, dropModifier, race, groupMembers);
				}
			}
		return index;
	}
}
