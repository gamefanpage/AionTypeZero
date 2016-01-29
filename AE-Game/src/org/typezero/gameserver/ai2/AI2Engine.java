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

package org.typezero.gameserver.ai2;

import static ch.lambdaj.Lambda.join;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectDistinct;
import static ch.lambdaj.collection.LambdaCollections.with;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import org.typezero.gameserver.GameServerError;
import org.typezero.gameserver.configs.main.AIConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.GameEngine;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;

/**
 * @author ATracer
 */
public class AI2Engine implements GameEngine {

	private static final Logger log = LoggerFactory.getLogger(AI2Engine.class);
	private static ScriptManager scriptManager = new ScriptManager();
	public static final File INSTANCE_DESCRIPTOR_FILE = new File("./data/scripts/system/aihandlers.xml");

	private final Map<String, Class<? extends AbstractAI>> aiMap = new HashMap<String, Class<? extends AbstractAI>>();

	@Override
	public void load(CountDownLatch progressLatch) {
		log.info("AI2 engine load started");
		scriptManager = new ScriptManager();

		AggregatedClassListener acl = new AggregatedClassListener();
		acl.addClassListener(new OnClassLoadUnloadListener());
		acl.addClassListener(new ScheduledTaskClassListener());
		acl.addClassListener(new AI2HandlerClassListener());
		scriptManager.setGlobalClassListener(acl);

		try {
			scriptManager.load(INSTANCE_DESCRIPTOR_FILE);
			log.info("Loaded " + aiMap.size() + " ai handlers.");
			validateScripts();
		}
		catch (Exception e) {
			throw new GameServerError("Can't initialize ai handlers.", e);
		}
		finally {
			if (progressLatch != null)
				progressLatch.countDown();
		}
	}

	@Override
	public void shutdown() {
		log.info("AI2 engine shutdown started");
		scriptManager.shutdown();
		scriptManager = null;
		aiMap.clear();
		log.info("AI2 engine shutdown complete");
	}

	public void registerAI(Class<? extends AbstractAI> class1) {
		AIName nameAnnotation = class1.getAnnotation(AIName.class);
		if (nameAnnotation != null) {
			aiMap.put(nameAnnotation.value(), class1);
		}
	}

	public final AI2 setupAI(String name, Creature owner) {
		AbstractAI aiInstance = null;
		try {
			aiInstance = aiMap.get(name).newInstance();
			aiInstance.setOwner(owner);
			owner.setAi2(aiInstance);
			if (AIConfig.ONCREATE_DEBUG) {
				aiInstance.setLogging(true);
			}
		}
		catch (Exception e) {
			log.error("[AI2] AI factory error: " + name, e);
		}
		return aiInstance;
	}

	/**
	 * @param aiName
	 * @param owner
	 */
	public void setupAI(AiNames aiName, Npc owner) {
		setupAI(aiName.getName(), owner);
	}

	private void validateScripts() {
		Collection<String> npcAINames = selectDistinct(with(DataManager.NPC_DATA.getNpcData().valueCollection()).extract(on(NpcTemplate.class).getAi()));
		npcAINames.removeAll(aiMap.keySet());
		if(npcAINames.size() > 0){
			log.warn("Bad AI names: " + join(npcAINames));
		}
	}

	public static final AI2Engine getInstance() {
		return SingletonHolder.instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final AI2Engine instance = new AI2Engine();
	}
}
