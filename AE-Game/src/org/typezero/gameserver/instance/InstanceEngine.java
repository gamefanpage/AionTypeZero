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

package org.typezero.gameserver.instance;

import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.GameServerError;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.GameEngine;
import org.typezero.gameserver.world.WorldMapInstance;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author ATracer
 */
public class InstanceEngine implements GameEngine {

	private static final Logger log = LoggerFactory.getLogger(InstanceEngine.class);
	private static ScriptManager scriptManager = new ScriptManager();
	public static final File INSTANCE_DESCRIPTOR_FILE = new File("./data/scripts/system/instancehandlers.xml");
	public static final InstanceHandler DUMMY_INSTANCE_HANDLER = new GeneralInstanceHandler();

	private Map<Integer, Class<? extends InstanceHandler>> handlers = new HashMap<Integer, Class<? extends InstanceHandler>>();

	@Override
	public void load(CountDownLatch progressLatch) {
		log.info("Instance engine load started");
		scriptManager = new ScriptManager();

		AggregatedClassListener acl = new AggregatedClassListener();
		acl.addClassListener(new OnClassLoadUnloadListener());
		acl.addClassListener(new ScheduledTaskClassListener());
		acl.addClassListener(new InstanceHandlerClassListener());
		scriptManager.setGlobalClassListener(acl);

		try {
			scriptManager.load(INSTANCE_DESCRIPTOR_FILE);
			log.info("Loaded " + handlers.size() + " instance handlers.");
		}
		catch (Exception e) {
			throw new GameServerError("Can't initialize instance handlers.", e);
		}
		finally {
			if (progressLatch != null)
				progressLatch.countDown();
		}
	}

	@Override
	public void shutdown() {
		log.info("Instance engine shutdown started");
		scriptManager.shutdown();
		scriptManager = null;
		handlers.clear();
		log.info("Instance engine shutdown complete");
	}

	public InstanceHandler getNewInstanceHandler(int worldId) {
		Class<? extends InstanceHandler> instanceClass = handlers.get(worldId);
		InstanceHandler instanceHandler = null;
		if (instanceClass != null) {
			try {
				instanceHandler = instanceClass.newInstance();
			}
			catch (Exception ex) {
				log.warn("Can't instantiate instance handler " + worldId, ex);
			}
		}
		if (instanceHandler == null) {
			instanceHandler = DUMMY_INSTANCE_HANDLER;
		}
		return instanceHandler;
	}

	/**
	 * @param handler
	 */
	final void addInstanceHandlerClass(Class<? extends InstanceHandler> handler) {
		InstanceID idAnnotation = handler.getAnnotation(InstanceID.class);
		if (idAnnotation != null) {
			handlers.put(idAnnotation.value(), handler);
		}
	}

	/**
	 * @param instance
	 */
	public void onInstanceCreate(WorldMapInstance instance) {
		instance.getInstanceHandler().onInstanceCreate(instance);
	}

	public static final InstanceEngine getInstance() {
		return SingletonHolder.instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final InstanceEngine instance = new InstanceEngine();
	}
}
