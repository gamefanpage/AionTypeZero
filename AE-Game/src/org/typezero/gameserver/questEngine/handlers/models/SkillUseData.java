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

package org.typezero.gameserver.questEngine.handlers.models;

import javolution.util.FastMap;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.handlers.template.SkillUse;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author vlog, modified Bobobear
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillUseData")
public class SkillUseData extends XMLQuest {

	@XmlAttribute(name = "start_npc_id")
	protected int startNpc;
	@XmlAttribute(name = "end_npc_id")
	protected int endNpc;
	@XmlElement(name = "skill", required = true)
	protected List<QuestSkillData> skills;

	@Override
	public void register(QuestEngine questEngine) {
		FastMap<List<Integer>, QuestSkillData> questSkills = new FastMap<List<Integer>, QuestSkillData>();
		for (QuestSkillData qsd : skills) {
			questSkills.put(qsd.getSkillIds(), qsd);
		}
		SkillUse questTemplate = new SkillUse(id, startNpc, endNpc, questSkills);
		questEngine.addQuestHandler(questTemplate);
	}
}
