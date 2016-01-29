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

import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.typezero.gameserver.skillengine.model.SkillTemplate;

/**
 * @author ATracer
 */
@XmlRootElement(name = "skill_data")
@XmlAccessorType(XmlAccessType.FIELD)
public class SkillData {

	@XmlElement(name = "skill_template")
	private List<SkillTemplate> skillTemplates;
	private HashMap<Integer, ArrayList<SkillTemplate>> cooldownGroups;
	/**
	 * Map that contains skillId - SkillTemplate key-value pair
	 */
	private TIntObjectHashMap<SkillTemplate> skillData = new TIntObjectHashMap<SkillTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		skillData.clear();
		for (SkillTemplate skillTempalte : skillTemplates) {
			skillData.put(skillTempalte.getSkillId(), skillTempalte);
		}
	}

	/**
	 * @param skillId
	 * @return SkillTemplate
	 */
	public SkillTemplate getSkillTemplate(int skillId) {
		return skillData.get(skillId);
	}

	/**
	 * @return skillData.size()
	 */
	public int size() {
		return skillData.size();
	}

	/**
	 * @return the skillTemplates
	 */
	public List<SkillTemplate> getSkillTemplates() {
		return skillTemplates;
	}

    public TIntObjectHashMap<SkillTemplate> getSkillData() {
        return skillData;
    }
	/**
	 * @param skillTemplates
	 *          the skillTemplates to set
	 */
	public void setSkillTemplates(List<SkillTemplate> skillTemplates) {
		this.skillTemplates = skillTemplates;
		afterUnmarshal(null, null);
	}
	
	/**
	 * This method creates a HashMap with all skills assigned to their representative cooldownIds
	 */
    public void initializeCooldownGroups() {
        cooldownGroups = new HashMap<Integer, ArrayList<SkillTemplate>>();
        for (SkillTemplate skillTemplate : skillTemplates) {
        	int cooldownId = skillTemplate.getCooldownId();
            if(!cooldownGroups.containsKey(cooldownId)) {
                cooldownGroups.put(cooldownId, new ArrayList<SkillTemplate>());
            }
            cooldownGroups.get(cooldownId).add(skillTemplate);
        }
    }
    
    /**
     * This method is used to get all skills assigned to a specific cooldownId
     * 
     * @param cooldownId
     * @return ArrayList<SkillTemplate> including all skills for asked cooldownId
     */
    public ArrayList<SkillTemplate> getSkillsForCooldownId(int cooldownId) {
        if(cooldownGroups == null)
            initializeCooldownGroups();
        return cooldownGroups.get(cooldownId);
    }
}
