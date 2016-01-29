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

import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Cain
 *
 */
public class NpcLvlDrop implements DropCalculator{
    protected List<DropGroup> dropGroup;
    protected int lvlmin;
    protected int lvlmax;
    protected int changeType;

    /**
		 * @param dropGroup
		 * @param lvlmin
         * @param lvlmax
		 */
        public NpcLvlDrop(List<DropGroup> dropGroup, int lvlmin, int lvlmax, int changeType) {
            super();
            this.dropGroup = dropGroup;
            this.lvlmin = lvlmin;
            this.lvlmax = lvlmax;
            this.changeType = changeType;  // 0 - ничего не требуется, 1 - замена полностью дропа моба(replace), 2 - добавление (merge)
        }

		public List<DropGroup> getDropGroup() {
    	if (dropGroup == null)
    		return Collections.emptyList();
    	return this.dropGroup;
    }

    /**
     * Gets the value of the npcId property.
     *
     */
    public int getMinLvl() {
        return lvlmin;
    }

    public int getMaxLvl() {
        return lvlmax;
    }

    @Override
    public int dropCalculator(Set<DropItem> result, int index, float dropModifier, Race race, Collection<Player> groupMembers){
    	if (dropGroup == null || dropGroup.isEmpty())
    		return index;
    	for (DropGroup dg : dropGroup){
    		if (dg.getRace() == Race.PC_ALL || dg.getRace() == race){
    			index = dg.dropCalculator(result, index, dropModifier, race, groupMembers);
    		}
    	}
      return index;
    }

    public int getChangeType()
    {
        return changeType;
    }
}
