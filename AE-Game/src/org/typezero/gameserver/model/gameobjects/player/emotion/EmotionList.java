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

package org.typezero.gameserver.model.gameobjects.player.emotion;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.configs.main.MembershipConfig;
import org.typezero.gameserver.dao.PlayerEmotionListDAO;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION_LIST;
import org.typezero.gameserver.taskmanager.tasks.ExpireTimerTask;
import org.typezero.gameserver.utils.PacketSendUtility;


/**
 * @author MrPoke
 *
 */
public class EmotionList {
	private Map<Integer, Emotion> emotions;
	private Player owner;
	/**
	 * @param owner
	 */
	public EmotionList(Player owner) {
		this.owner = owner;
	}

	public void add(int emotionId, int dispearTime, boolean isNew){
		if (emotions == null) {
			emotions = new HashMap<Integer, Emotion>();
		}
		Emotion emotion = new Emotion(emotionId, dispearTime);
		emotions.put(emotionId, emotion);

		if (isNew){
			if (emotion.getExpireTime() != 0)
				ExpireTimerTask.getInstance().addTask(emotion, owner);
			DAOManager.getDAO(PlayerEmotionListDAO.class).insertEmotion(owner, emotion);
			PacketSendUtility.sendPacket(owner, new SM_EMOTION_LIST((byte) 1, Collections.singletonList(emotion)));
		}
	}

	public void remove(int emotionId){
		emotions.remove(emotionId);
		DAOManager.getDAO(PlayerEmotionListDAO.class).deleteEmotion(owner.getObjectId(), emotionId);
		PacketSendUtility.sendPacket(owner, new SM_EMOTION_LIST((byte)0, getEmotions()));
	}

	public boolean contains(int emotionId){
		if (emotions == null)
			return false;
		return emotions.containsKey(emotionId);
	}

	public boolean canUse(int emotionId) {
		return emotionId < 64 || emotionId > 129 || (emotions != null && emotions.containsKey(emotionId)) || owner.havePermission(MembershipConfig.EMOTIONS_ALL);
	}

	public Collection<Emotion> getEmotions(){
		if (emotions == null)
			return Collections.emptyList();
		return emotions.values();
	}
}
