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

package org.typezero.gameserver.model.templates;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.restrictions.DisabledRestriction;
import org.typezero.gameserver.skillengine.model.SkillTemplate;

import javax.annotation.PostConstruct;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Cain
 */
@XmlRootElement(name = "PlayerClass")
@XmlAccessorType(XmlAccessType.FIELD)
public class HiddenStigmasTemplate {

	@XmlAttribute(name = "classname", required = true)
	private String classname;

	@XmlElement(name = "hiddenstigma")
	private List<HiddenStigmaTemplate> hiddenStigmas;

	public String getClassname() {
		return classname;
	}

	public List<HiddenStigmaTemplate> getHiddenStigmas() {
		return hiddenStigmas;
	}

	public int getRegularStigmaSkillId(Race race, int skillLevel) {
		for (HiddenStigmaTemplate hst : hiddenStigmas) {
			if (hst.getRequiredId() == null) {
				return DataManager.SKILL_TREE_DATA.getStigmaTree().get(race).get(hst.getId()).get(skillLevel);
			}
		}
		return 0;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "HiddenStigmaList")
	public static class HiddenStigmaTemplate {

		@XmlAttribute(name = "id", required = true)
		private String id;

		@XmlAttribute(name = "requiredid")
		private String requiredid;

		@XmlAttribute(name = "customids")
		private String customids;

        ArrayList<String> customStacks = new ArrayList<String>();

        public void dataProcessing() {
            if (getCustomids() != null)
                for (String stack : getCustomids().split(","))
                    customStacks.add(stack);
        }


        public ArrayList<String> getCustomStacks() {
            return customStacks;
        }

        public String getId() {
			return id;
		}

        public String getCustomids() {
			return customids;
		}

        public String getRequiredId() {
			return requiredid;
		}
	}

}
