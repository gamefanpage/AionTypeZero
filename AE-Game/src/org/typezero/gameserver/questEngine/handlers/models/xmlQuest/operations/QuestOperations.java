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


package org.typezero.gameserver.questEngine.handlers.models.xmlQuest.operations;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.questEngine.model.QuestEnv;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestOperations", propOrder = { "operations" })
public class QuestOperations {

	@XmlElements({ @XmlElement(name = "take_item", type = TakeItemOperation.class),
		@XmlElement(name = "npc_dialog", type = NpcDialogOperation.class),
		@XmlElement(name = "set_quest_status", type = SetQuestStatusOperation.class),
		@XmlElement(name = "give_item", type = GiveItemOperation.class),
		@XmlElement(name = "start_quest", type = StartQuestOperation.class),
		@XmlElement(name = "npc_use", type = ActionItemUseOperation.class),
		@XmlElement(name = "set_quest_var", type = SetQuestVarOperation.class),
		@XmlElement(name = "collect_items", type = CollectItemQuestOperation.class) })
	protected List<QuestOperation> operations;
	@XmlAttribute
	protected Boolean override;

	/**
	 * Gets the value of the override property.
	 *
	 * @return possible object is {@link Boolean }
	 */
	public boolean isOverride() {
		if (override == null) {
			return true;
		}
		else {
			return override;
		}
	}

	public boolean operate(QuestEnv env) {
		if (operations != null) {
			for (QuestOperation oper : operations) {
				oper.doOperate(env);
			}
		}
		return isOverride();
	}
}
