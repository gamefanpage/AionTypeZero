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

package org.typezero.gameserver.questEngine.model;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Gatherable;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.StaticObject;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.QuestEngine;

/**
 * @author MrPoke
 */
public class QuestEnv {

	private VisibleObject visibleObject;
	private Player player;
	private int questId;
	private int dialogId;
	private int extendedRewardIndex;

	/**
	 * @param creature
	 * @param player
	 * @param questId
	 */
	public QuestEnv(VisibleObject visibleObject, Player player, Integer questId, Integer dialogId) {
		super();
		this.visibleObject = visibleObject;
		this.player = player;
		this.questId = questId;
		this.dialogId = dialogId;
	}

	/**
	 * @return the visibleObject
	 */
	public VisibleObject getVisibleObject() {
		return visibleObject;
	}

	/**
	 * @param visibleObject
	 *          the visibleObject to set
	 */
	public void setVisibleObject(VisibleObject visibleObject) {
		this.visibleObject = visibleObject;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player
	 *          the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @return the questId
	 */
	public Integer getQuestId() {
		return questId;
	}

	/**
	 * @param questId
	 *          the questId to set
	 */
	public void setQuestId(Integer questId) {
		this.questId = questId;
	}

	/**
	 * @return the dialogId
	 */
	public Integer getDialogId() {
		return dialogId;
	}

	public DialogAction getDialog() {
		DialogAction dialog = QuestEngine.getInstance().getDialog(dialogId);
		if (dialog == null) {
			return DialogAction.NULL;
		}
		return dialog;
	}

	/**
	 * @param dialogId
	 *          the dialogId to set
	 */
	public void setDialogId(Integer dialogId) {
		this.dialogId = dialogId;
	}

	public int getTargetId() {
		if (visibleObject == null) {
			return 0;
		}
		else if (visibleObject instanceof Npc) {
			return ((Npc) visibleObject).getNpcId();
		}
		else if (visibleObject instanceof Gatherable) {
			return ((Gatherable) visibleObject).getObjectTemplate().getTemplateId();
		}
		else if (visibleObject instanceof StaticObject) {
			return ((StaticObject) visibleObject).getObjectTemplate().getTemplateId();
		}
		return 0;
	}

	public void setExtendedRewardIndex(int index) {
		this.extendedRewardIndex = index;
	}

	public int getExtendedRewardIndex() {
		return this.extendedRewardIndex;
	}
}
