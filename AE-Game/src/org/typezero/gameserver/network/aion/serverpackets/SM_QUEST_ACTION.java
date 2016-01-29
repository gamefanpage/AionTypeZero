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

package org.typezero.gameserver.network.aion.serverpackets;


import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.templates.QuestTemplate;
import org.typezero.gameserver.model.templates.quest.QuestExtraCategory;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author VladimirZ
 */
public class SM_QUEST_ACTION extends AionServerPacket {

	protected int questId;
	private int status;
	private int step;
	protected int action;
	private int timer;
	private int sharerId;
	@SuppressWarnings("unused")
	private boolean unk;

	SM_QUEST_ACTION(){

	}
	/**
	 * Accept Quest(1)
	 *
	 * @param questId
	 * @param status
	 * @param step
	 */
	public SM_QUEST_ACTION(int questId, int status, int step) {
		this.action = 1;
		this.questId = questId;
		this.status = status;
		this.step = step;
	}

	/**
	 * Quest Steps/Finish (2)
	 *
	 * @param questId
	 * @param status
	 * @param step
	 */
	public SM_QUEST_ACTION(int questId, QuestStatus status, int step) {
		this.action = 2;
		this.questId = questId;
		this.status = status.value();
		this.step = step;
	}

	/**
	 * Delete Quest(3)
	 *
	 * @param questId
	 */
	public SM_QUEST_ACTION(int questId) {
		this.action = 3;
		this.questId = questId;
	}

	/**
	 * Display Timer(4)
	 *
	 * @param questId
	 * @param timer
	 */
	public SM_QUEST_ACTION(int questId, int timer) {
		this.action = 4;
		this.questId = questId;
		this.timer = timer;
		this.step = 0;
	}

	public SM_QUEST_ACTION(int questId, int sharerId, boolean unk) {
		this.action = 5;
		this.questId = questId;
		this.sharerId = sharerId;
		this.unk = unk;
	}

	/**
	 * Display Timer(4)
	 *
	 * @param questId
	 * @param timer
	 */
	public SM_QUEST_ACTION(int questId, boolean fake) {
		this.action = 6;
		this.questId = questId;
		this.timer = 0;
		this.step = 0;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aionemu.commons.network.mmocore.SendablePacket#writeImpl(com.aionemu.commons.network.mmocore.MMOConnection)
	 */
	@Override
	protected void writeImpl(AionConnection con) {

		QuestTemplate questTemplate = DataManager.QUEST_DATA.getQuestById(questId);
		if (questTemplate != null && questTemplate.getExtraCategory() != QuestExtraCategory.NONE)
			return;
		writeC(action);
		writeD(questId);
		switch (action) {
			case 1:
				writeC(status);// quest status goes by ENUM value
				writeC(0x0);
				writeD(step);// current quest step
				writeH(0);
				writeC(0);
				break;
			case 2:
				writeC(status);// quest status goes by ENUM value
				writeC(0x0);
				writeD(step);// current quest step
				writeH(0);
				break;
			case 3:
				writeD(0);
				break;
			case 4:
				writeD(timer);// sets client timer ie 84030000 is 900 seconds/15 mins
				writeC(0x01);
				writeH(0x0);
				writeC(0x01);
			case 5:
				writeD(this.sharerId);
				writeD(0);
			break;
			case 6:
				writeH(0x01);// ???
				writeH(0x0);
		}
	}
}
