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

package org.typezero.gameserver.taskmanager.fromdb;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.TaskFromDBDAO;
import org.typezero.gameserver.taskmanager.fromdb.trigger.TaskFromDBTrigger;

/**
 * @author nrg
 */
public class TaskFromDBManager {

	private static final Logger log = LoggerFactory.getLogger(TaskFromDBManager.class);
	private ArrayList<TaskFromDBTrigger> tasksList;

	private TaskFromDBManager() {
		tasksList = getDAO().getAllTasks();
		log.info("Loaded " + tasksList.size() + " task" + (tasksList.size() > 1 ? "s" : "") + " from the database");

		registerTaskInstances();
	}

	/**
	 * Launching & checking task process
	 */
	private void registerTaskInstances() {
		// For all tasks from DB
		for (TaskFromDBTrigger trigger : tasksList) {
			if (trigger.isValid()) {
				trigger.initTrigger();
			} else {
				log.error("Invalid task from db with ID: " + trigger.getTaskId());
			}
		}
	}

	/**
	 * Retuns {@link org.typezero.gameserver.dao.TaskFromDBDAO} , just a shortcut
	 *
	 * @return {@link org.typezero.gameserver.dao.TaskFromDBDAO}
	 */
	private static TaskFromDBDAO getDAO() {
		return DAOManager.getDAO(TaskFromDBDAO.class);
	}

	/**
	 * Get the instance
	 *
	 * @return
	 */
	public static TaskFromDBManager getInstance() {
		return TaskFromDBManager.SingletonHolder.instance;
	}

	/**
	 * SingletonHolder
	 */
	private static class SingletonHolder {

		protected static final TaskFromDBManager instance = new TaskFromDBManager();
	}
}
