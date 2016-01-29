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

import com.aionemu.commons.database.dao.DAOManager;
import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.configs.main.PeriodicSaveConfig;
import org.typezero.gameserver.dao.InventoryDAO;
import org.typezero.gameserver.dao.ItemStoneListDAO;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.team.legion.Legion;
import org.typezero.gameserver.utils.ThreadPoolManager;

import java.util.Iterator;
import java.util.concurrent.Future;

/**
 * @author ATracer
 */
public class PeriodicSaveService {

	private static final Logger log = LoggerFactory.getLogger(PeriodicSaveService.class);

	private Future<?> legionWhUpdateTask;

	public static final PeriodicSaveService getInstance() {
		return SingletonHolder.instance;
	}

	private PeriodicSaveService() {

		int DELAY_LEGION_ITEM = PeriodicSaveConfig.LEGION_ITEMS * 1000;

		legionWhUpdateTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new LegionWhUpdateTask(),
			DELAY_LEGION_ITEM, DELAY_LEGION_ITEM);
	}

	private class LegionWhUpdateTask implements Runnable {

		@Override
		public void run() {
			log.info("Legion WH update task started.");
			long startTime = System.currentTimeMillis();
			Iterator<Legion> legionsIterator = LegionService.getInstance().getCachedLegionIterator();
			int legionWhUpdated = 0;
			while (legionsIterator.hasNext()) {
				Legion legion = legionsIterator.next();
				FastList<Item> allItems = legion.getLegionWarehouse().getItemsWithKinah();
				allItems.addAll(legion.getLegionWarehouse().getDeletedItems());
				try {
					/**
					 * 1. save items first
					 */
					DAOManager.getDAO(InventoryDAO.class).store(allItems, null, null, legion.getLegionId());

					/**
					 * 2. save item stones
					 */
					DAOManager.getDAO(ItemStoneListDAO.class).save(allItems);
				}
				catch (Exception ex) {
					log.error("Exception during periodic saving of legion WH", ex);
				}

				legionWhUpdated++;
			}
			long workTime = System.currentTimeMillis() - startTime;
			log.info("Legion WH update: " + workTime + " ms, legions: " + legionWhUpdated + ".");
		}
	}

	/**
	 * Save data on shutdown
	 */
	public void onShutdown() {
		log.info("Starting data save on shutdown.");
		// save legion warehouse
		legionWhUpdateTask.cancel(false);
		new LegionWhUpdateTask().run();
		log.info("Data successfully saved.");
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final PeriodicSaveService instance = new PeriodicSaveService();
	}
}
