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

import com.aionemu.commons.taskmanager.AbstractLockManager;
import com.aionemu.commons.utils.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.GameServer;
import org.typezero.gameserver.GameServer.StartupHook;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author lord_rex and MrPoke based on l2j-free engines. This can be used for periodic calls.
 */
public abstract class AbstractPeriodicTaskManager extends AbstractLockManager implements Runnable, StartupHook {

	protected static final Logger log = LoggerFactory.getLogger(AbstractPeriodicTaskManager.class);

	private final int period;

	public AbstractPeriodicTaskManager(int period) {
		this.period = period;

		GameServer.addStartupHook(this);

		log.info(getClass().getSimpleName() + ": Initialized.");
	}

	@Override
	public final void onStartup() {
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000 + Rnd.get(period), Rnd.get(period - 5, period + 5));
	}

	@Override
	public abstract void run();
}
