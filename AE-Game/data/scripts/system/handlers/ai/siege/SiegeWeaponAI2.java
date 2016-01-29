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

package ai.siege;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.AISummon;
import org.typezero.gameserver.controllers.SiegeWeaponController;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.summons.SummonMode;
import org.typezero.gameserver.model.templates.npcskill.NpcSkillTemplate;
import org.typezero.gameserver.model.templates.npcskill.NpcSkillTemplates;
import org.typezero.gameserver.services.summons.SummonsService;

/**
 *
 * @author xTz
 */
@AIName("siege_weapon")
public class SiegeWeaponAI2 extends AISummon {

	private long lastAttackTime;
	private int skill;
	private int skillLvl;
	private int duration;

	@Override
	protected void handleSpawned() {
		this.setStateIfNot(AIState.IDLE);
		SummonsService.doMode(SummonMode.GUARD, getOwner());
		NpcSkillTemplate skillTemplate = getNpcSkillTemplates().getNpcSkills().get(0);
		skill = skillTemplate.getSkillid();
		skillLvl = skillTemplate.getSkillLevel();
		duration = DataManager.SKILL_DATA.getSkillTemplate(this.skill).getDuration();
	}

	@Override
	protected void handleFollowMe(Creature creature) {
		this.setStateIfNot(AIState.FOLLOWING);
	}

	@Override
	protected void handleCreatureMoved(Creature creature) {
	}

	@Override
	protected void handleStopFollowMe(Creature creature) {
		this.setStateIfNot(AIState.IDLE);
		this.getOwner().getMoveController().abortMove();
	}

	@Override
	protected void handleTargetTooFar() {
		getOwner().getMoveController().moveToDestination();
	}

	@Override
	protected void handleMoveArrived() {
		this.getOwner().getController().onMove();
		this.getOwner().getMoveController().abortMove();
	}

	@Override
	protected void handleMoveValidate() {
		this.getOwner().getController().onMove();
		getMoveController().moveToTargetObject();
	}

	@Override
	protected SiegeWeaponController getController() {
		return (SiegeWeaponController) super.getController();
	}

	private NpcSkillTemplates getNpcSkillTemplates() {
		return getController().getNpcSkillTemplates();
	}

	@Override
	protected void handleAttack(Creature creature) {
		if (creature == null) {
			return;
		}
		Race race = creature.getRace();
		Player master = getOwner().getMaster();
		if (master == null) {
			return;
		}
		Race masterRace = master.getRace();
		if (masterRace.equals(Race.ASMODIANS) && !race.equals(Race.PC_LIGHT_CASTLE_DOOR) && !race.equals(Race.DRAGON_CASTLE_DOOR)) {
			return;
		}
		else if (masterRace.equals(Race.ELYOS) && !race.equals(Race.PC_DARK_CASTLE_DOOR) && !race.equals(Race.DRAGON_CASTLE_DOOR)) {
			return;
		}
		if (!getOwner().getMode().equals(SummonMode.ATTACK)) {
			return;
		}

		if (System.currentTimeMillis() - lastAttackTime > duration + 2000) {
			lastAttackTime = System.currentTimeMillis();
			getOwner().getController().useSkill(skill, skillLvl);
		}
	}

}
