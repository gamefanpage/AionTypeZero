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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.configs.main.CleaningConfig;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.services.player.PlayerService;
import org.typezero.gameserver.utils.ThreadPoolManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Offers the functionality to delete all data about inactive players
 *
 * @author nrg
 */
public class DatabaseCleaningService {

	private Logger log = LoggerFactory.getLogger(DatabaseCleaningService.class);
	private PlayerDAO dao = DAOManager.getDAO(PlayerDAO.class);
	//A limit of cleaning period for security reasons
	private final int SECURITY_MINIMUM_PERIOD = 30;
	//Worker execution check time
	private final int WORKER_CHECK_TIME = 10 * 1000;
	//Singelton
	private static DatabaseCleaningService instance = new DatabaseCleaningService();
	//Workers
	private List<Worker> workers;
	//Starttime of service
	private long startTime;

	/**
	 * Private constructor to avoid extern access and ensure singelton
	 */
	private DatabaseCleaningService() {
		if (CleaningConfig.CLEANING_ENABLE) {
			runCleaning();
		}
	}

	/**
	 * Cleans the databse from inactive player data
	 */
	private void runCleaning() {
		//Execution time
		log.info("DatabaseCleaningService: Executing database cleaning");
		startTime = System.currentTimeMillis();

		//getting period for deletion
		int periodInDays = CleaningConfig.CLEANING_PERIOD;

		//only a security feature
		if (periodInDays > SECURITY_MINIMUM_PERIOD) {
			delegateToThreads(CleaningConfig.CLEANING_THREADS, dao.getInactiveAccounts(periodInDays, CleaningConfig.CLEANING_LIMIT));
			monitoringProcess();
		} else {
			log.warn("The configured days for database cleaning is to low. For security reasons the service will only execute with periods over 30 days!");
		}
	}

	@SuppressWarnings("SleepWhileInLoop")
	private void monitoringProcess() {
		while (!allWorkersReady()) {
			try {
				Thread.sleep(WORKER_CHECK_TIME);
				log.info("DatabaseCleaningService: Until now " + currentlyDeletedChars() + " chars deleted in " + (System.currentTimeMillis() - startTime) / 1000 + " seconds!");
			} catch (InterruptedException ex) {
				log.error("DatabaseCleaningService: Got Interrupted!");
			}
		}
	}

	private boolean allWorkersReady() {
		for (Worker w : workers) {
			if (!w._READY) {
				return false;
			}
		}
		return true;
	}

	private int currentlyDeletedChars() {
		int deletedChars = 0;
		for (Worker w : workers) {
			deletedChars += w.deletedChars;
		}
		return deletedChars;
	}

	private void delegateToThreads(int numberOfThreads, Set<Integer> idsToDelegate) {
		workers = new ArrayList<Worker>();
		log.info("DatabaseCleaningService: Executing deletion over " + numberOfThreads + " longrunning threads");

		//every id to another worker with maximum of n different workers
		Iterator<Integer> i = idsToDelegate.iterator();
		for (int workerNo = 0; i.hasNext(); workerNo = ++workerNo % numberOfThreads) {
			if (workerNo >= workers.size()) {
				workers.add(new Worker());
			}
			workers.get(workerNo).ids.add(i.next());
		}

		//get them working on our longrunning
		for (Worker w : workers) {
			ThreadPoolManager.getInstance().executeLongRunning(w);
		}
	}

	/**
	 * The only extern access
	 *
	 * @return a singleton DatabaseCleaningService
	 */
	public static DatabaseCleaningService getInstance() {
		return instance;
	}

	private class Worker implements Runnable {

		private List<Integer> ids = new ArrayList<Integer>();
		private int deletedChars = 0;
		private boolean _READY = false;

		@Override
		public void run() {
			for (int id : ids) {
				deletedChars += PlayerService.deleteAccountsCharsFromDB(id);
			}
			_READY = true;
		}
	}
}
