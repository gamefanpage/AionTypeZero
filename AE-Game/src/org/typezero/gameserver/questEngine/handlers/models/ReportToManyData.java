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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import javolution.util.FastMap;

import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.handlers.template.ReportToMany;

/**
 * @author Hilgert
 * @modified Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportToManyData")
public class ReportToManyData extends XMLQuest {

	@XmlAttribute(name = "start_item_id")
	protected int startItemId;

	@XmlAttribute(name = "start_npc_ids")
	protected List<Integer> startNpcIds;

	@XmlAttribute(name = "end_npc_ids")
	protected List<Integer> endNpcIds;

	@XmlAttribute(name = "start_dialog_id")
	protected int startDialog;

	@XmlAttribute(name = "end_dialog_id")
	protected int endDialog;

	@XmlElement(name = "npc_infos", required = true)
	protected List<NpcInfos> npcInfos;

	@Override
	public void register(QuestEngine questEngine) {
		int maxVar = 0;
		FastMap<Integer, NpcInfos> NpcInfo = new FastMap<Integer, NpcInfos>();
		for (NpcInfos mi : npcInfos) {
			NpcInfo.put(mi.getNpcId(), mi);
			if (mi.getVar() > maxVar)
				maxVar = mi.getVar();
		}
		ReportToMany template = new ReportToMany(id, startItemId, startNpcIds, endNpcIds, NpcInfo, startDialog, endDialog, maxVar, mission);
		questEngine.addQuestHandler(template);
	}
}
