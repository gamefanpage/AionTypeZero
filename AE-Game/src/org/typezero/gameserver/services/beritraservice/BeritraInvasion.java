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

package org.typezero.gameserver.services.beritraservice;

import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.commons.callbacks.EnhancedObject;
import org.typezero.gameserver.ai2.AbstractAI;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.beritra.BeritraLocation;
import org.typezero.gameserver.model.beritra.BeritraStateType;
import org.typezero.gameserver.services.BeritraService;

/**
 * @author Rinzler (Encom)
 */

public abstract class BeritraInvasion<BL extends BeritraLocation>
{
	private Npc worldBoss;
	private boolean started;
	private final BL beritraLocation;
	private boolean worldBossDestroyed;
	protected abstract void stopBeritraInvasion();
	protected abstract void startBeritraInvasion();
	private final AtomicBoolean finished = new AtomicBoolean();
	private final WorldBossDestroyListener worldBossDestroyListener = new WorldBossDestroyListener(this);

	public BeritraInvasion(BL beritraLocation) {
		this.beritraLocation = beritraLocation;
	}

	public final void start() {
		boolean doubleStart = false;
		synchronized (this) {
			if (started) {
				doubleStart = true;
			} else {
				started = true;
			}
		} if (doubleStart) {
			return;
		}
		startBeritraInvasion();
	}

	public final void stop() {
		if (finished.compareAndSet(false, true)) {
			stopBeritraInvasion();
		}
	}

	protected void initWorldBoss() {
		Npc wb = null;
		for (VisibleObject obj : getBeritraLocation().getSpawned()) {
			int npcId = ((Npc) obj).getNpcId();
			if ((npcId < 234558) || npcId > 234615) {
			    wb = (Npc) obj;
			}
		} if (wb == null) {
			throw new NullPointerException("No <World Boss> was found in loc:" + getBeritraLocationId());
		}
		setWorldBoss(wb);
		addWorldBossListeners();
	}

	protected void spawn(BeritraStateType type) {
		BeritraService.getInstance().spawn(getBeritraLocation(), type);
	}

	protected void despawn() {
		BeritraService.getInstance().despawn(getBeritraLocation());
	}

	protected void addWorldBossListeners() {
		AbstractAI ai = (AbstractAI) getWorldBoss().getAi2();
		EnhancedObject eo = (EnhancedObject) ai;
		eo.addCallback(getWorldBossDestroyListener());
	}

	protected void rmvWorldBossListener() {
		AbstractAI ai = (AbstractAI) getWorldBoss().getAi2();
		EnhancedObject eo = (EnhancedObject) ai;
		eo.removeCallback(getWorldBossDestroyListener());
	}

	public boolean isWorldBossDestroyed() {
		return worldBossDestroyed;
	}

	public void setWorldBossDestroyed(boolean state) {
		this.worldBossDestroyed = state;
	}

	public Npc getWorldBoss() {
		return worldBoss;
	}

	public void setWorldBoss(Npc worldBoss) {
		this.worldBoss = worldBoss;
	}

	public WorldBossDestroyListener getWorldBossDestroyListener() {
		return worldBossDestroyListener;
	}

	public boolean isFinished() {
		return finished.get();
	}

	public BL getBeritraLocation() {
		return beritraLocation;
	}

	public int getBeritraLocationId() {
		return beritraLocation.getId();
	}
}
