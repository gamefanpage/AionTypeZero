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

package org.typezero.gameserver.model.gameobjects.player;

import javolution.util.FastMap;

/**
 * @author synchro2
 */
public class CraftCooldownList {

	private FastMap<Integer, Long> craftCooldowns;

	CraftCooldownList(Player owner) {
	}

	public boolean isCanCraft(int delayId) {
		if (craftCooldowns == null || !craftCooldowns.containsKey(delayId))
			return true;

		Long coolDown = craftCooldowns.get(delayId);
		if (coolDown == null)
			return true;

		if (coolDown < System.currentTimeMillis()) {
			craftCooldowns.remove(delayId);
			return true;
		}

		return false;
	}

	public long getCraftCooldown(int delayId) {
		if (craftCooldowns == null || !craftCooldowns.containsKey(delayId))
			return 0;

		return craftCooldowns.get(delayId);
	}

	public FastMap<Integer, Long> getCraftCoolDowns() {
		return craftCooldowns;
	}

	public void setCraftCoolDowns(FastMap<Integer, Long> craftCoolDowns) {
		this.craftCooldowns = craftCoolDowns;
	}

	public void addCraftCooldown(int delayId, int delay) {
		if (craftCooldowns == null) {
			craftCooldowns = new FastMap<Integer, Long>();
		}

		long nextUseTime = System.currentTimeMillis() + (delay * 1000);
		craftCooldowns.put(delayId, nextUseTime);
	}
}
