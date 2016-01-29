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

package org.typezero.gameserver.services.vortexservice;

import com.aionemu.commons.callbacks.EnhancedObject;
import org.typezero.gameserver.ai2.AbstractAI;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.vortex.VortexLocation;
import org.typezero.gameserver.model.vortex.VortexStateType;
import org.typezero.gameserver.services.VortexService;
import java.util.concurrent.atomic.AtomicBoolean;
import javolution.util.FastMap;

/**
 * @author Source
 */
public abstract class DimensionalVortex<VL extends VortexLocation> {

	private final VL vortexLocation;
	private final GeneratorDestroyListener generatorDestroyListener = new GeneratorDestroyListener(this);
	private final AtomicBoolean finished = new AtomicBoolean();
	private boolean generatorDestroyed;
	private Npc generator;
	private boolean started;

	protected abstract void startInvasion();

	protected abstract void stopInvasion();

	public abstract void addPlayer(Player player, boolean isInvader);

	public abstract void kickPlayer(Player player, boolean isInvader);

	public abstract void updateDefenders(Player defender);

	public abstract void updateInvaders(Player invader);

	public abstract FastMap<Integer, Player> getDefenders();

	public abstract FastMap<Integer, Player> getInvaders();

	public DimensionalVortex(VL vortexLocation) {
		this.vortexLocation = vortexLocation;
	}

	public final void start() {

		boolean doubleStart = false;

		synchronized (this) {
			if (started) {
				doubleStart = true;
			}
			else {
				started = true;
			}
		}

		if (doubleStart) {
			return;
		}

		startInvasion();
	}

	public final void stop() {
		if (finished.compareAndSet(false, true)) {
			stopInvasion();
		}
	}

	protected void initRiftGenerator() {

		Npc gen = null;

		for (VisibleObject obj : getVortexLocation().getSpawned()) {
			int npcId = ((Npc) obj).getNpcId();
			if (npcId == 209487 || npcId == 209486) {
				gen = (Npc) obj;
			}
		}

		if (gen == null) {
			throw new NullPointerException("No generator was found in loc:" + getVortexLocationId());
		}

		setGenerator(gen);
		registerSiegeBossListeners();
	}

	protected void spawn(VortexStateType type) {
		VortexService.getInstance().spawn(getVortexLocation(), type);
	}

	protected void despawn() {
		VortexService.getInstance().despawn(getVortexLocation());
	}

	protected void registerSiegeBossListeners() {
		AbstractAI ai = (AbstractAI) getGenerator().getAi2();
		EnhancedObject eo = (EnhancedObject) ai;
		eo.addCallback(getGeneratorDestroyListener());
	}

	protected void unregisterSiegeBossListeners() {
		AbstractAI ai = (AbstractAI) getGenerator().getAi2();
		EnhancedObject eo = (EnhancedObject) ai;
		eo.removeCallback(getGeneratorDestroyListener());
	}

	public boolean isGeneratorDestroyed() {
		return generatorDestroyed;
	}

	public void setGeneratorDestroyed(boolean state) {
		this.generatorDestroyed = state;
	}

	public Npc getGenerator() {
		return generator;
	}

	public void setGenerator(Npc generator) {
		this.generator = generator;
	}

	public GeneratorDestroyListener getGeneratorDestroyListener() {
		return generatorDestroyListener;
	}

	public boolean isFinished() {
		return finished.get();
	}

	public VL getVortexLocation() {
		return vortexLocation;
	}

	public int getVortexLocationId() {
		return vortexLocation.getId();
	}

}
