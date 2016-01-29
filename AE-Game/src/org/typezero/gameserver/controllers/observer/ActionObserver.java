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

package org.typezero.gameserver.controllers.observer;

import java.util.concurrent.atomic.AtomicBoolean;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.effect.AbnormalState;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.Skill;

/**
 * @author ATracer
 */
public class ActionObserver {

	private AtomicBoolean used;

	private ObserverType observerType;

	public ActionObserver(ObserverType observerType) {
		this.observerType = observerType;
	}

	/**
	 * Make this observer usable exactly one time
	 */
	public void makeOneTimeUse() {
		used = new AtomicBoolean(false);
	}

	/**
	 * Try to use this observer. Will return true only once.
	 *
	 * @return
	 */
	public boolean tryUse() {
		return used.compareAndSet(false, true);
	}

	/**
	 * @return the observerType
	 */
	public ObserverType getObserverType() {
		return observerType;
	}

	public void moved() {
	};

	/**
	 * @param creature
	 */
	public void attacked(Creature creature) {
	};

	/**
	 * @param creature
	 */
	public void attack(Creature creature) {
	};

	/**
	 * @param item
	 * @param owner
	 */
	public void equip(Item item, Player owner) {
	};

	/**
	 * @param item
	 * @param owner
	 */
	public void unequip(Item item, Player owner) {
	};

	/**
	 * @param skill
	 */
	public void skilluse(Skill skill) {
	};

	/**
	 * @param creature
	 */
	public void died(Creature creature) {
	};

	/**
	 * @param creature
	 * @param dotEffect
	 */
	public void dotattacked(Creature creature, Effect dotEffect) {
	};

	/**
	 *
	 * @param item
	 */
	public void itemused(Item item) {
	};

	/**
	 *
	 * @param npc
	 */
	public void npcdialogrequested(Npc npc) {
	};

	/**
	 *
	 * @param state
	 */
	public void abnormalsetted(AbnormalState state) {
	};

	/**
	 *
	 * @param
	 */
	public void summonrelease() {
	};
}
