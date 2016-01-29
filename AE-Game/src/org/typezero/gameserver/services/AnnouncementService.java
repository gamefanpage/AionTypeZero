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

package org.typezero.gameserver.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import javolution.util.FastSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.AnnouncementsDAO;
import org.typezero.gameserver.model.Announcement;
import org.typezero.gameserver.model.ChatType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.World;

/**
 * Automatic Announcement System
 *
 * @author Divinity
 */
public class AnnouncementService {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(AnnouncementService.class);

	private Collection<Announcement> announcements;
	private List<Future<?>> delays = new ArrayList<Future<?>>();

	private AnnouncementService() {
		this.load();
	}

	public static final AnnouncementService getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * Reload the announcements system
	 */
	public void reload() {
		// Cancel all tasks
		if (delays != null && delays.size() > 0)
			for (Future<?> delay : delays)
				delay.cancel(false);

		// Clear all announcements
		announcements.clear();

		// And load again all announcements
		load();
	}

	/**
	 * Load the announcements system
	 */
	private void load() {
		announcements = new FastSet<Announcement>(getDAO().getAnnouncements()).shared();

		for (final Announcement announce : announcements) {
			delays.add(ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					final Iterator<Player> iter = World.getInstance().getPlayersIterator();
					while (iter.hasNext()) {
						Player player = iter.next();

						if (announce.getFaction().equalsIgnoreCase("ALL"))
							if (announce.getChatType() == ChatType.SHOUT || announce.getChatType() == ChatType.GROUP_LEADER)
								PacketSendUtility.sendPacket(player, new SM_MESSAGE(1, "Announcement", announce.getAnnounce(),
									announce.getChatType()));
							else
								PacketSendUtility.sendPacket(player, new SM_MESSAGE(1, "Announcement", "\ue027\ue09e "
									+ announce.getAnnounce(), announce.getChatType()));
						else if (announce.getFactionEnum() == player.getRace())
							if (announce.getChatType() == ChatType.SHOUT || announce.getChatType() == ChatType.GROUP_LEADER)
								PacketSendUtility.sendPacket(player, new SM_MESSAGE(1,
									(announce.getFaction().equalsIgnoreCase("ELYOS") ? "Elyos" : "Asmodian") + " Announcement",
									announce.getAnnounce(), announce.getChatType()));
							else
								PacketSendUtility.sendPacket(player, new SM_MESSAGE(1,
									(announce.getFaction().equalsIgnoreCase("ELYOS") ? "Elyos" : "Asmodian") + " Announcement",
									(announce.getFaction().equalsIgnoreCase("ELYOS") ? "Elyos" : "Asmodian") + " Announcement: "
										+ announce.getAnnounce(), announce.getChatType()));
					}
				}
			}, announce.getDelay() * 1000, announce.getDelay() * 1000));
		}

		log.info("Loaded " + announcements.size() + " announcements");
	}

	public void addAnnouncement(Announcement announce) {
		getDAO().addAnnouncement(announce);
	}

	public boolean delAnnouncement(final int idAnnounce) {
		return getDAO().delAnnouncement(idAnnounce);
	}

	public Set<Announcement> getAnnouncements() {
		return getDAO().getAnnouncements();
	}

	/**
	 * Retuns {@link com.aionemu.loginserver.dao.AnnouncementDAO} , just a shortcut
	 *
	 * @return {@link com.aionemu.loginserver.dao.AnnouncementDAO}
	 */
	private AnnouncementsDAO getDAO() {
		return DAOManager.getDAO(AnnouncementsDAO.class);
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final AnnouncementService instance = new AnnouncementService();
	}
}
