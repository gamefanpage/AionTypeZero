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


package org.typezero.gameserver.questEngine.handlers.models.xmlQuest.conditions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestStatusCondition")
public class QuestStatusCondition extends QuestCondition {

	@XmlAttribute(required = true)
	protected QuestStatus value;
	@XmlAttribute(name = "quest_id")
	protected Integer questId;

	/*
	 * (non-Javadoc)
	 * @see
	 * org.typezero.gameserver.questEngine.handlers.template.xmlQuest.condition.QuestCondition#doCheck(org.typezero.gameserver
	 * .questEngine.model.QuestEnv)
	 */
	@Override
	public boolean doCheck(QuestEnv env) {
		Player player = env.getPlayer();
		int qstatus = 0;
		int id = env.getQuestId();
		if (questId != null)
			id = questId;
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if (qs != null)
			qstatus = qs.getStatus().value();

		switch (getOp()) {
			case EQUAL:
				return qstatus == value.value();
			case GREATER:
				return qstatus > value.value();
			case GREATER_EQUAL:
				return qstatus >= value.value();
			case LESSER:
				return qstatus < value.value();
			case LESSER_EQUAL:
				return qstatus <= value.value();
			case NOT_EQUAL:
				return qstatus != value.value();
			default:
				return false;
		}
	}
}
