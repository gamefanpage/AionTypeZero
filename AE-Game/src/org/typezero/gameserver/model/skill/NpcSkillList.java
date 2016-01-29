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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.templates.npcskill.NpcSkillTemplate;
import org.typezero.gameserver.model.templates.npcskill.NpcSkillTemplates;

/**
 * @author ATracer
 */
public class NpcSkillList implements SkillList<Npc> {

	private List<NpcSkillEntry> skills;

	public NpcSkillList(Npc owner) {
		initSkillList(owner.getNpcId());
	}

	private void initSkillList(int npcId) {
		NpcSkillTemplates npcSkillList = DataManager.NPC_SKILL_DATA.getNpcSkillList(npcId);
		if (npcSkillList != null) {
			initSkills();
			for (NpcSkillTemplate template : npcSkillList.getNpcSkills()) {
				skills.add(new NpcSkillTemplateEntry(template));
			}
		}
	}

	@Override
	public boolean addSkill(Npc creature, int skillId, int skillLevel) {
		initSkills();
		skills.add(new NpcSkillParameterEntry(skillId, skillLevel));
		return true;
	}

	@Override
	public boolean removeSkill(int skillId) {
		Iterator<NpcSkillEntry> iter = skills.iterator();
		while (iter.hasNext()) {
			NpcSkillEntry next = iter.next();
			if (next.getSkillId() == skillId) {
				iter.remove();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSkillPresent(int skillId) {
		if (skills == null) {
			return false;
		}
		return getSkill(skillId) != null;
	}

	@Override
	public int getSkillLevel(int skillId) {
		return getSkill(skillId).getSkillLevel();
	}

	@Override
	public int size() {
		return skills != null ? skills.size() : 0;
	}

	private void initSkills() {
		if (skills == null) {
			skills = new ArrayList<NpcSkillEntry>();
		}
	}

	public NpcSkillEntry getRandomSkill() {
		if (skills == null || skills.size() == 0)
			return null;
		return skills.get(Rnd.get(0, skills.size() - 1));
	}

	private SkillEntry getSkill(int skillId) {
		for (SkillEntry entry : skills) {
			if (entry.getSkillId() == skillId) {
				return entry;
			}
		}
		return null;
	}

	public NpcSkillEntry getUseInSpawnedSkill() {
		if(this.skills == null)
			return null;
		Iterator<NpcSkillEntry> iter = skills.iterator();
		while (iter.hasNext()) {
			NpcSkillEntry next = iter.next();
			NpcSkillTemplateEntry tmpEntry = (NpcSkillTemplateEntry) next;
			if (tmpEntry.UseInSpawned()) {
				return next;
			}
		}
		return null;
	}

}
