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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.skillengine.model.SkillLearnTemplate;

/**
 * @author ATracer
 */
@XmlRootElement(name = "skill_tree")
@XmlAccessorType(XmlAccessType.FIELD)
public class SkillTreeData {

	@XmlElement(name = "skill")
	private List<SkillLearnTemplate> skillTemplates;

	private final TIntObjectHashMap<ArrayList<SkillLearnTemplate>> templates = new TIntObjectHashMap<ArrayList<SkillLearnTemplate>>();
	private final TIntObjectHashMap<ArrayList<SkillLearnTemplate>> templatesById = new TIntObjectHashMap<ArrayList<SkillLearnTemplate>>();

    // [race, [stack, [lvl, skill_id]]]
    private final HashMap<Race, HashMap<String, HashMap<Integer, Integer>>> stigmaTree = new HashMap<Race, HashMap<String, HashMap<Integer, Integer>>>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (SkillLearnTemplate template : skillTemplates) {
			addTemplate(template);
		}
		//skillTemplates = null;
	}

	private void addTemplate(SkillLearnTemplate template) {
		Race race = template.getRace();
		if (race == null)
			race = Race.PC_ALL;

		int hash = makeHash(template.getClassId().ordinal(), race.ordinal(), template.getMinLevel());
		ArrayList<SkillLearnTemplate> value = templates.get(hash);
		if (value == null) {
			value = new ArrayList<SkillLearnTemplate>();
			templates.put(hash, value);
		}

		value.add(template);

		value = templatesById.get(template.getSkillId());
		if (value == null) {
			value = new ArrayList<SkillLearnTemplate>();
			templatesById.put(template.getSkillId(), value);
		}

		value.add(template);
	}

	/**
	 * @return the templates
	 */
	public TIntObjectHashMap<ArrayList<SkillLearnTemplate>> getTemplates() {
		return templates;
	}

	/**
	 * Perform search for: - class specific skills (race = ALL) - class and race specific skills - non-specific skills
	 * (race = ALL, class = ALL)
	 *
	 * @param playerClass
	 * @param level
	 * @param race
	 * @return SkillLearnTemplate[]
	 */
	public SkillLearnTemplate[] getTemplatesFor(PlayerClass playerClass, int level, Race race) {
		List<SkillLearnTemplate> newSkills = new ArrayList<SkillLearnTemplate>();

		List<SkillLearnTemplate> classRaceSpecificTemplates = templates.get(makeHash(playerClass.ordinal(), race.ordinal(),
			level));
		List<SkillLearnTemplate> classSpecificTemplates = templates.get(makeHash(playerClass.ordinal(),
			Race.PC_ALL.ordinal(), level));
		List<SkillLearnTemplate> generalTemplates = templates.get(makeHash(PlayerClass.ALL.ordinal(),
			Race.PC_ALL.ordinal(), level));

		if (classRaceSpecificTemplates != null)
			newSkills.addAll(classRaceSpecificTemplates);
		if (classSpecificTemplates != null)
			newSkills.addAll(classSpecificTemplates);
		if (generalTemplates != null)
			newSkills.addAll(generalTemplates);

		return newSkills.toArray(new SkillLearnTemplate[newSkills.size()]);
	}

	public SkillLearnTemplate[] getTemplatesForSkill(int skillId) {
		List<SkillLearnTemplate> searchSkills = new ArrayList<SkillLearnTemplate>();

		List<SkillLearnTemplate> byId = templatesById.get(skillId);
		if (byId != null)
			searchSkills.addAll(byId);

		return searchSkills.toArray(new SkillLearnTemplate[searchSkills.size()]);
	}

	public boolean isLearnedSkill(int skillId) {
		return templatesById.get(skillId) != null;
	}

	public int size() {
		int size = 0;
		for (Integer key : templates.keys())
			size += templates.get(key).size();
		return size;
	}

	private static int makeHash(int classId, int race, int level) {
		int result = classId << 8;
		result = (result | race) << 8;
		return result | level;
	}

    public void setStigmaTree() {
        for (SkillLearnTemplate skillLearnTemplate : skillTemplates) {
            String skillStack = DataManager.SKILL_DATA.getSkillTemplate(skillLearnTemplate.getSkillId()).getStack();
            int skillRealLvl = DataManager.SKILL_DATA.getSkillTemplate(skillLearnTemplate.getSkillId()).getLvl();
            ArrayList<Race> addRaceList = new ArrayList<Race>();
            if (skillLearnTemplate.getRace() == Race.PC_ALL) {
                addRaceList.add(Race.ASMODIANS);
                addRaceList.add(Race.ELYOS);
            } else
                addRaceList.add(skillLearnTemplate.getRace());
            for (Race addRace : addRaceList) {
                if (stigmaTree.get(addRace) != null) {
                    if (stigmaTree.get(addRace).get(skillStack) != null) {
                        stigmaTree.get(addRace).get(skillStack).put(skillRealLvl, skillLearnTemplate.getSkillId());
                    } else {
                        HashMap<Integer, Integer> skillMap = new HashMap<Integer, Integer>();
                        skillMap.put(skillRealLvl, skillLearnTemplate.getSkillId());
                        stigmaTree.get(addRace).put(skillStack, skillMap);
                    }
                } else {
                    HashMap<String, HashMap<Integer, Integer>> stackMap = new HashMap<String, HashMap<Integer, Integer>>();
                    HashMap<Integer, Integer> skillMap = new HashMap<Integer, Integer>();
                    skillMap.put(skillLearnTemplate.getSkillId(), skillRealLvl);
                    stackMap.put(skillStack, skillMap);
                    stigmaTree.put(addRace, stackMap);
                }
            }
        }
        skillTemplates = null;
    }

    public HashMap<Race, HashMap<String, HashMap<Integer, Integer>>> getStigmaTree() {
        return stigmaTree;
    }
}
