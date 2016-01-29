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

package org.typezero.gameserver.model.instance;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.stats.calc.StatOwner;
import org.typezero.gameserver.model.stats.calc.functions.IStatFunction;
import org.typezero.gameserver.model.stats.calc.functions.StatAddFunction;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.model.templates.instance_bonusatrr.InstanceBonusAttr;
import org.typezero.gameserver.model.templates.instance_bonusatrr.InstancePenaltyAttr;
import org.typezero.gameserver.skillengine.change.Func;
import org.typezero.gameserver.utils.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 *
 * @author xTz
 */
public class InstanceBuff implements StatOwner {

	private Future<?> task;
	private List<IStatFunction> functions = new ArrayList<IStatFunction>();
	private InstanceBonusAttr instanceBonusAttr;
	private long startTime;

	public InstanceBuff(int buffId) {
		instanceBonusAttr = DataManager.INSTANCE_BUFF_DATA.getInstanceBonusattr(buffId);
	}

	public void applyEffect(Player player, int time) {

		if (hasInstanceBuff() || instanceBonusAttr == null) {
			return;
		}
		if (time != 0) {
			task = ThreadPoolManager.getInstance().schedule(new InstanceBuffTask(player), time);
		}
		startTime = System.currentTimeMillis();
		for (InstancePenaltyAttr instancePenaltyAttr : instanceBonusAttr.getPenaltyAttr()) {
			StatEnum stat = instancePenaltyAttr.getStat();
			int statToModified = player.getGameStats().getStat(stat, 0).getBase();
			int value = instancePenaltyAttr.getValue();
			int valueModified = instancePenaltyAttr.getFunc().equals(Func.PERCENT) ? (statToModified * value / 100) : (value);
			functions.add(new StatAddFunction(stat, valueModified, true));
		}
		player.getGameStats().addEffect(this, functions);
	}

	public void endEffect(Player player) {
		functions.clear();
		if (hasInstanceBuff()) {
			task.cancel(true);
		}
		player.getGameStats().endEffect(this);
	}

	public int getRemaningTime() {
		return (int) ((System.currentTimeMillis() - startTime) / 1000);
	}

	private class InstanceBuffTask implements Runnable {

		private Player player;

		public InstanceBuffTask(Player player) {
			this.player = player;
		}

		@Override
		public void run() {
			endEffect(player);
		}

	}

	public boolean hasInstanceBuff() {
		return task != null && !task.isDone();
	}

}
