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

package org.typezero.gameserver.model.gameobjects;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.controllers.observer.ItemUseObserver;
import org.typezero.gameserver.model.DialogPage;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.templates.housing.HousingPostbox;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_OBJECT_USE_UPDATE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Rolandas
 */
public class PostboxObject extends HouseObject<HousingPostbox> {

	private AtomicReference<Player> usingPlayer = new AtomicReference<Player>();

	public PostboxObject(House owner, int objId, int templateId) {
		super(owner, objId, templateId);
	}

	@Override
	public void onUse(final Player player) {
		if (!usingPlayer.compareAndSet(null, player)) {
			// The same player is using, return. It might be double-click
			if (usingPlayer.compareAndSet(player, player))
				return;
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_OCCUPIED_BY_OTHER);
			return;
		}

		final ItemUseObserver observer = new ItemUseObserver() {
			@Override
			public void abort() {
				player.getObserveController().removeObserver(this);
				usingPlayer.set(null);
			}

		};

		player.getObserveController().attach(observer);

		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_USE(getObjectTemplate().getNameId()));
		player.getController().addTask(TaskId.HOUSE_OBJECT_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				try {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), DialogPage.MAIL.id()));
					player.getMailbox().sendMailList(false);
					PacketSendUtility.sendPacket(player, new SM_OBJECT_USE_UPDATE(player.getObjectId(), 0, 0, PostboxObject.this));
				}
				finally {
					player.getObserveController().removeObserver(observer);
					usingPlayer.set(null);
				}
			}

		}, 0));
	}

	@Override
	public boolean canExpireNow() {
		return usingPlayer.get() == null;
	}

}
