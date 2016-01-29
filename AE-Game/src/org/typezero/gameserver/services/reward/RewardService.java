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

package org.typezero.gameserver.services.reward;

import javolution.util.FastList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.RewardServiceDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.LetterType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.rewards.RewardEntryItem;
import org.typezero.gameserver.services.mail.SystemMailService;

/**
 *
 * @author KID
 *
 */
public class RewardService {

	private static RewardService controller = new RewardService();
	private static final Logger log = LoggerFactory.getLogger(RewardService.class);
	private RewardServiceDAO dao;

	public static RewardService getInstance() {
		return controller;
	}

	public RewardService() {
		dao = DAOManager.getDAO(RewardServiceDAO.class);
	}

	public void verify(Player player) {
		FastList<RewardEntryItem> list = dao.getAvailable(player.getObjectId());
		if (list.size() == 0 || player.getMailbox() == null)
			return;

		FastList<Integer> rewarded = FastList.newInstance();

		for (RewardEntryItem item : list) {
			if (DataManager.ITEM_DATA.getItemTemplate(item.id) == null) {
				log.warn("[RewardController]["+item.unique+"] null template for item " + item.id + " on player " + player.getObjectId() + ".");
				continue;
			}

			try {
				if (!SystemMailService.getInstance().sendMail("$$CASH_ITEM_MAIL", player.getName(), item.id + ", " + item.count, "0, "+(System.currentTimeMillis()/1000)+",", item.id, (int)item.count, 0, LetterType.BLACKCLOUD)) {
					continue;
				}
 				log.info("[RewardController]["+item.unique+"] player " + player.getName() + " has received (" + item.count + ")" + item.id + ".");
				rewarded.add(item.unique);
			} catch (Exception e) {
				log.error("[RewardController]["+item.unique+"] failed to add item (" + item.count + ")" + item.id + " to " + player.getObjectId(), e);
				continue;
			}
		}

		if (rewarded.size() > 0) {
			dao.uncheckAvailable(rewarded);

			FastList.recycle(rewarded);
			FastList.recycle(list);
		}
	}
}
