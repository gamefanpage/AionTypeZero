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

package org.typezero.gameserver.controllers;

import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.ai2.follow.FollowStartService;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.summons.UnsummonType;
import org.typezero.gameserver.model.templates.npcskill.NpcSkillTemplates;

/**
 *
 * @author xTz
 */
public class SiegeWeaponController extends SummonController {

	private NpcSkillTemplates skills;

	public SiegeWeaponController(int npcId) {
		skills = DataManager.NPC_SKILL_DATA.getNpcSkillList(npcId);
	}

	@Override
	public void release(final UnsummonType unsummonType) {
		getMaster().getController().cancelTask(TaskId.SUMMON_FOLLOW);
		getOwner().getMoveController().abortMove();
		super.release(unsummonType);
	}

	@Override
	public void restMode() {
		getMaster().getController().cancelTask(TaskId.SUMMON_FOLLOW);
		super.restMode();
		getOwner().getAi2().onCreatureEvent(AIEventType.STOP_FOLLOW_ME, getMaster());
	}

	@Override
	public void setUnkMode() {
		super.setUnkMode();
		getMaster().getController().cancelTask(TaskId.SUMMON_FOLLOW);
	}

	@Override
	public final void guardMode() {
		super.guardMode();
		getMaster().getController().cancelTask(TaskId.SUMMON_FOLLOW);
		getOwner().setTarget(getMaster());
		getOwner().getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, getMaster());
		getOwner().getMoveController().moveToTargetObject();
		getMaster().getController().addTask(TaskId.SUMMON_FOLLOW, FollowStartService.newFollowingToTargetCheckTask(getOwner(), getMaster()));
	}

	@Override
	public void attackMode(int targetObjId) {
		super.attackMode(targetObjId);
		Creature target = (Creature) getOwner().getKnownList().getObject(targetObjId);
		if (target == null) {
			return;
		}
		getOwner().setTarget(target);
		getOwner().getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, target);
		getOwner().getMoveController().moveToTargetObject();
		getMaster().getController().addTask(TaskId.SUMMON_FOLLOW, FollowStartService.newFollowingToTargetCheckTask(getOwner(), target));
	}

	@Override
	public void onDie(final Creature lastAttacker) {
		getMaster().getController().cancelTask(TaskId.SUMMON_FOLLOW);
		super.onDie(lastAttacker);
	}

	public NpcSkillTemplates getNpcSkillTemplates() {
		return skills;
	}
}
