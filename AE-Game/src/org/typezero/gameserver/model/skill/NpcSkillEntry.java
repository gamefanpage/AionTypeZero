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

package org.typezero.gameserver.model.skill;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.model.templates.npcskill.NpcSkillTemplate;

/**
 * @author ATracer, nrg
 */
public abstract class NpcSkillEntry extends SkillEntry {
	protected long lastTimeUsed = 0;
	
	public NpcSkillEntry(int skillId, int skillLevel) {
		super(skillId, skillLevel);
	}
        
	public abstract boolean isReady(int hpPercentage, long fightingTimeInMSec);

	public abstract boolean chanceReady();

	public abstract boolean hpReady(int hpPercentage);
        
	public abstract boolean timeReady(long fightingTimeInMSec);
        
	public abstract boolean hasCooldown();
	
	public abstract boolean UseInSpawned();
        
	public long getLastTimeUsed() {
		return lastTimeUsed;
	}
        
	public void setLastTimeUsed() {
		this.lastTimeUsed = System.currentTimeMillis();
	}
}

/**
 * Skill entry which inherits properties from template (regular npc skills)
 */
class NpcSkillTemplateEntry extends NpcSkillEntry {

	private final NpcSkillTemplate template;

	public NpcSkillTemplateEntry(NpcSkillTemplate template) {
		super(template.getSkillid(), template.getSkillLevel());
		this.template = template;
	}
        
	@Override
	public boolean isReady(int hpPercentage, long fightingTimeInMSec) {
		if(hasCooldown() || !chanceReady())
			return false;
            
		switch(template.getConjunctionType()) {
			case XOR:
				return (hpReady(hpPercentage) && !timeReady(fightingTimeInMSec)) || (!hpReady(hpPercentage) && timeReady(fightingTimeInMSec));
			case OR:
				return hpReady(hpPercentage) || timeReady(fightingTimeInMSec);
			case AND:
				return hpReady(hpPercentage) && timeReady(fightingTimeInMSec);
			default:
				return false;
            }
        }

	@Override
	public boolean chanceReady() {
		return Rnd.get(0, 100) < template.getProbability();
	}

	@Override
	public boolean hpReady(int hpPercentage) {
		if (template.getMaxhp() == 0 && template.getMinhp() == 0) //it's not about hp
			return true;
		else if (template.getMaxhp() >= hpPercentage && template.getMinhp() <= hpPercentage) //in hp range
			return true;
		else
			return false;
	}

	@Override
	public boolean timeReady(long fightingTimeInMSec) {
		if (template.getMaxTime() == 0 && template.getMinTime() == 0) //it's not about time
			return true;
		else if (template.getMaxTime() >= fightingTimeInMSec && template.getMinTime() <= fightingTimeInMSec) //in time range
			return true;
		else
			return false;
	}
        
	@Override
	public boolean hasCooldown() {
		return template.getCooldown() > (System.currentTimeMillis() - lastTimeUsed);
	}
	
	@Override
	public boolean UseInSpawned() {
		return template.getUseInSpawned();
	}
}

/**
 * Skill entry which can be created on the fly (skills of servants, traps)
 */
class NpcSkillParameterEntry extends NpcSkillEntry {

	public NpcSkillParameterEntry(int skillId, int skillLevel) {
		super(skillId, skillLevel);
	}
        
	@Override
	public boolean isReady(int hpPercentage, long fightingTimeInMSec) {
		return true;
	}

	@Override
	public boolean chanceReady() {
		return true;
	}

	@Override
	public boolean hpReady(int hpPercentage) {
		return true;
	}

	@Override
	public boolean timeReady(long fightingTimeInMSec) {
		return true;
	} 
        
	@Override
	public boolean hasCooldown() {
		return false;
	}
	
	@Override
	public boolean UseInSpawned() {
		return true;
	}
}
