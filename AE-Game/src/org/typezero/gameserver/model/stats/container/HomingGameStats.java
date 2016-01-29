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

package org.typezero.gameserver.model.stats.container;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Homing;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.stats.calc.Stat2;
import org.typezero.gameserver.skillengine.model.SkillTemplate;


/**
 * @author Cheatkiller
 *
 */
public class HomingGameStats extends SummonedObjectGameStats {

	public HomingGameStats(Npc owner) {
		super(owner);
	}
	
	
	@Override
	public Stat2 getStat(StatEnum statEnum, int base) {
		Stat2 stat = super.getStat(statEnum, base);
		if (owner.getMaster() == null)
			return stat;
		switch (statEnum) {
			case MAGICAL_ATTACK:
				stat.setBonusRate(0.2f);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);

		}
		return stat;
	}

	@Override
	public Stat2 getMainHandPAttack() {
		Homing homing = (Homing) owner;
		int power = homing.getObjectTemplate().getStatsTemplate().getPower();
		SkillTemplate skill = DataManager.SKILL_DATA.getSkillTemplate(homing.getSkillId());
		int skillLvl = skill.getLvl();
		switch (skillLvl) {
			case 3:
				if(homing.getName().equals("stone energy"))
					power = 316;
				if(homing.getName().equals("water energy"))
					power = 362;
				break;
			case 4:
				if(homing.getName().equals("cyclone servant"))
					power = 1166;
				if(homing.getName().equals("fire energy"))
					power = 313;
				if(homing.getName().equals("wind servant"))
					power = 373;
				break;
			case 5:
				if(homing.getName().equals("cyclone servant"))
					power = 1221;
				break;
			case 6:
				if(homing.getName().equals("cyclone servant"))
					power = 1283;
				break;
			default:
				break;
		}
		return getStat(StatEnum.MAGICAL_ATTACK, power);
	}

}