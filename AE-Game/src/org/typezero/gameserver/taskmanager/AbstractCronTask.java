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

package org.typezero.gameserver.taskmanager;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import org.quartz.CronExpression;
import org.typezero.gameserver.dao.ServerVariablesDAO;
import org.typezero.gameserver.utils.ThreadPoolManager;

import java.text.ParseException;
import java.util.Date;

/**
 * @author Rolandas
 */
public abstract class AbstractCronTask implements Runnable {

	private String cronExpressionString;
	private CronExpression runExpression;
	private int runTime;
	private long period;

	/**
	 * Timestamp in Seconds of the task run start, based on DB server variable
	 */
	public final int getRunTime() {
		return runTime;
	}

	/**
	 * The same as a milliseconds left, but any extended class may specify a
	 * little delay. if delay is not needed then it's simple "runTime" minus "now"
	 * function
	 */
	abstract protected long getRunDelay();

	/**
	 * Any pre-init, pre-load tasks when the instance is created
	 */
	protected void preInit() {
	}

	/**
	 * Any post-init, post-load tasks when the instance is created
	 */
	protected void postInit() {
	}

	public final String getCronExpressionString() {
		return cronExpressionString;
	}

	/**
	 * Variable name of the task start time stored in the server_variables DB
	 * table
	 */
	abstract protected String getServerTimeVariable();

	public long getPeriod() {
		return period;
	}

	/**
	 * Tasks which have to be run before the actual scheduled task
	 */
	protected void preRun() {
	}

	/**
	 * The main execution code goes here
	 */
	abstract protected void executeTask();

	/**
	 * Is the task allowed to run on its initialization (if runDelay = 0) or only
	 * at times defined by cron
	 */
	abstract protected boolean canRunOnInit();

	/**
	 * Tasks which have to be run after the task is complete and saved to DB
	 */
	protected void postRun() {
	}

	public AbstractCronTask(String cronExpression) throws ParseException {
		if (cronExpression == null)
			throw new NullPointerException("cronExpressionString");

		cronExpressionString = cronExpression;

		ServerVariablesDAO dao = DAOManager.getDAO(ServerVariablesDAO.class);
		runTime = dao.load(getServerTimeVariable());

		preInit();
		runExpression = new CronExpression(cronExpressionString);
		Date nextDate = runExpression.getTimeAfter(new Date());
		Date nextAfterDate = runExpression.getTimeAfter(nextDate);
		period = nextAfterDate.getTime() - nextDate.getTime();
		postInit();

		if (getRunDelay() == 0) {
			if (canRunOnInit())
				ThreadPoolManager.getInstance().schedule(this, 0);
			else {
				saveNextRunTime();
			}
		}
		scheduleNextRun();
	}

	private void scheduleNextRun() {
		CronService.getInstance().schedule(this, cronExpressionString, true);
	}

	private void saveNextRunTime() {
		Date nextDate = runExpression.getTimeAfter(new Date());
		ServerVariablesDAO dao = DAOManager.getDAO(ServerVariablesDAO.class);
		runTime = (int) (nextDate.getTime() / 1000);
		dao.store(getServerTimeVariable(), runTime);
	}

	@Override
	public final void run() {
      if (getRunDelay() > 0) {
        ThreadPoolManager.getInstance().schedule(this, getRunDelay());
      }
      else {
		preRun();

		executeTask();
		saveNextRunTime();

		postRun();
      }
	}
}
