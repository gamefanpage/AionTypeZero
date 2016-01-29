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


package org.typezero.gameserver.questEngine.handlers.models.xmlQuest.events;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import org.typezero.gameserver.questEngine.handlers.models.Monster;
import org.typezero.gameserver.questEngine.handlers.models.xmlQuest.operations.QuestOperations;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Mr. Poke, modified Bobobear
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OnKillEvent", propOrder = { "monster", "complite" })
public class OnKillEvent extends QuestEvent {

	@XmlElement(name = "monster")
	protected List<Monster> monster;
	protected QuestOperations complite;

	public List<Monster> getMonsters() {
		if (monster == null) {
			monster = new ArrayList<Monster>();
		}
		return this.monster;
	}

	public boolean operate(QuestEnv env) {
		if (monster == null || !(env.getVisibleObject() instanceof Npc))
			return false;

		QuestState qs = env.getPlayer().getQuestStateList().getQuestState(env.getQuestId());
		if (qs == null)
			return false;

		Npc npc = (Npc) env.getVisibleObject();
		for (Monster m : monster) {
			if (m.getNpcIds().contains(npc.getNpcId())) {
				int var = qs.getQuestVarById(m.getVar());
				if (var >= (m.getStartVar() == null ? 0 : m.getStartVar()) && var < m.getEndVar()) {
					qs.setQuestVarById(m.getVar(), var + 1);
					PacketSendUtility.sendPacket(env.getPlayer(), new SM_QUEST_ACTION(env.getQuestId(), qs.getStatus(), qs
						.getQuestVars().getQuestVars()));
				}
			}
		}

		if (complite != null) {
			for (Monster m : monster) {
				if (qs.getQuestVarById(m.getVar()) != qs.getQuestVarById(m.getVar()))
					return false;
			}
			complite.operate(env);
		}
		return false;
	}
}
