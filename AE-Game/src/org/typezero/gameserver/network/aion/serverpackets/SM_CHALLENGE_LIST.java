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

import java.util.List;

import org.typezero.gameserver.model.challenge.ChallengeQuest;
import org.typezero.gameserver.model.challenge.ChallengeTask;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.challenge.ChallengeType;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author ViAl
 */
public class SM_CHALLENGE_LIST extends AionServerPacket {

	int action;
	int ownerId;
	ChallengeType ownerType;
	List<ChallengeTask> tasks;
	ChallengeTask task;

	public SM_CHALLENGE_LIST(int action, int ownerId, ChallengeType ownerType, List<ChallengeTask> tasks) {
		this.action = action;
		this.ownerId = ownerId;
		this.ownerType = ownerType;
		this.tasks = tasks;
	}

	public SM_CHALLENGE_LIST(int action, int ownerId, ChallengeType ownerType, ChallengeTask task) {
		this.action = action;
		this.ownerId = ownerId;
		this.ownerType = ownerType;
		this.task = task;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		Player player = con.getActivePlayer();
		writeC(action);
		writeD(ownerId); // legionId or townId
		writeC(ownerType.getId()); // 1 for legion, 2 for town
		writeD(player.getObjectId());
		switch(action) {
			case 2:  //send challenge tasks list
				writeD((int) (System.currentTimeMillis() / 1000));
				writeH(tasks.size());
				for(ChallengeTask task : tasks) {
					writeD(32); //unk
					writeD(task.getTaskId());
					writeC(1); //unk
					writeC(21); //unk
					writeC(0); //unk
					writeD((int) (task.getCompleteTime().getTime() / 1000));
				}
				break;
			case 7:  //send individual challenge task info
				writeD(32); //unk
				writeD(task.getTaskId());
				writeH(task.getQuestsCount());
				for(ChallengeQuest quest : task.getQuests().values()) {
					writeD(quest.getQuestId());
					writeH(quest.getMaxRepeats());
					writeD(quest.getScorePerQuest());
					writeH(quest.getCompleteCount()); //unk
				}
				break;
		}
	}

}
