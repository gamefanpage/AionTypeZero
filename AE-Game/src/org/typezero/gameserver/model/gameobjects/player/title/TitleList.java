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

package org.typezero.gameserver.model.gameobjects.player.title;

import java.util.Collection;

import javolution.util.FastMap;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.PlayerTitleListDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.stats.listeners.TitleChangeListener;
import org.typezero.gameserver.model.templates.TitleTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.network.aion.serverpackets.SM_TITLE_INFO;
import org.typezero.gameserver.taskmanager.tasks.ExpireTimerTask;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author xavier, cura, xTz
 */
public class TitleList {

	private final FastMap<Integer, Title> titles;
	private Player owner;

	public TitleList() {
		this.titles = new FastMap<Integer, Title>();
		this.owner = null;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public Player getOwner() {
		return owner;
	}

	public boolean contains(int titleId) {
		return titles.containsKey(titleId);
	}

	public void addEntry(int titleId, int remaining) {
		TitleTemplate tt = DataManager.TITLE_DATA.getTitleTemplate(titleId);
		if (tt == null) {
			throw new IllegalArgumentException("Invalid title id " + titleId);
		}
		titles.put(titleId, new Title(tt, titleId, remaining));
	}

	public boolean addTitle(int titleId, boolean questReward, int time) {
		TitleTemplate tt = DataManager.TITLE_DATA.getTitleTemplate(titleId);
		if (tt == null) {
			throw new IllegalArgumentException("Invalid title id " + titleId);
		}
		if (owner != null) {
			if (owner.getRace() != tt.getRace() && tt.getRace() != Race.PC_ALL) {
				PacketSendUtility.sendMessage(owner, "This title is not available for your race.");
				return false;
			}
			Title entry = new Title(tt, titleId, time);
			if (!titles.containsKey(titleId)) {
				titles.put(titleId, entry);
				if (time != 0)
					ExpireTimerTask.getInstance().addTask(entry, owner);
				DAOManager.getDAO(PlayerTitleListDAO.class).storeTitles(owner, entry);
			}
			else {
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_TOOLTIP_LEARNED_TITLE);
				return false;
			}
			if (questReward)
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_QUEST_GET_REWARD_TITLE(tt.getNameId()));
			else
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_MSG_GET_CASH_TITLE(tt.getNameId()));

			PacketSendUtility.sendPacket(owner, new SM_TITLE_INFO(owner));
			return true;
		}
		return false;
	}

	public void setDisplayTitle(int titleId) {
		PacketSendUtility.sendPacket(owner, new SM_TITLE_INFO(titleId));
		PacketSendUtility.broadcastPacketAndReceive(owner, (new SM_TITLE_INFO(owner, titleId)));
		owner.getCommonData().setTitleId(titleId);
	}

	public void setBonusTitle(int bonusTitleId) {
		PacketSendUtility.sendPacket(owner, new SM_TITLE_INFO(6, bonusTitleId));
		if (owner.getCommonData().getBonusTitleId() > 0) {
			if (owner.getGameStats() != null) {
				TitleChangeListener.onBonusTitleChange(owner.getGameStats(), owner.getCommonData().getBonusTitleId(), false);
			}
		}
		owner.getCommonData().setBonusTitleId(bonusTitleId);
		if (bonusTitleId > 0 && owner.getGameStats() != null) {
			TitleChangeListener.onBonusTitleChange(owner.getGameStats(), bonusTitleId, true);
		}
	}

	public void removeTitle(int titleId) {
		if (!titles.containsKey(titleId))
			return;
		if (owner.getCommonData().getTitleId() == titleId)
			setDisplayTitle(-1);
		if (owner.getCommonData().getBonusTitleId() == titleId)
		   setBonusTitle(-1);
		titles.remove(titleId);
		PacketSendUtility.sendPacket(owner, new SM_TITLE_INFO(owner));
		DAOManager.getDAO(PlayerTitleListDAO.class).removeTitle(owner.getObjectId(), titleId);
	}

	public int size() {
		return titles.size();
	}

	public Collection<Title> getTitles() {
		return titles.values();
	}
}
