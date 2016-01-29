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

import org.apache.commons.lang.NullArgumentException;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.summons.SummonMode;
import org.typezero.gameserver.model.summons.UnsummonType;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_SUMMON_UPDATE;
import org.typezero.gameserver.restrictions.RestrictionsManager;
import org.typezero.gameserver.services.summons.SummonsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.skillengine.model.Skill;
import org.typezero.gameserver.taskmanager.tasks.PlayerMoveTaskManager;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 * @author RotO (Attack-speed hack protection)
 * modified by Sippolo
 */
public class SummonController extends CreatureController<Summon> {

	private long lastAttackMilis = 0;
	private boolean isAttacked = false;
	private int releaseAfterSkill = -1;

	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		super.notSee(object, isOutOfRange);
		if (getOwner().getMaster() == null)
			return;

		if (object.getObjectId() == getOwner().getMaster().getObjectId()) {
			SummonsService.release(getOwner(), UnsummonType.DISTANCE, isAttacked);
		}
	}

	/**
	 * Release summon
	 */
	public void release(final UnsummonType unsummonType) {
		SummonsService.release(getOwner(), unsummonType, isAttacked);
	}

	@Override
	public Summon getOwner() {
		return (Summon) super.getOwner();
	}

	/**
	 * Change to rest mode
	 */
	public void restMode() {
		SummonsService.restMode(getOwner());
	}

	public void setUnkMode() {
		SummonsService.setUnkMode(getOwner());
	}

	/**
	 * Change to guard mode
	 */
	public void guardMode() {
		SummonsService.guardMode(getOwner());
	}

	/**
	 * Change to attackMode
	 */
	public void attackMode(int targetObjId) {
		VisibleObject obj = getOwner().getKnownList().getObject(targetObjId);
		if (obj != null && obj instanceof Creature) {
			SummonsService.attackMode(getOwner());
		}
	}

	@Override
	public void attackTarget(Creature target, int time) {
		Player master = getOwner().getMaster();

		if (!RestrictionsManager.canAttack(master, target))
			return;

		int attackSpeed = getOwner().getGameStats().getAttackSpeed().getCurrent();
		long milis = System.currentTimeMillis();
		if (milis - lastAttackMilis < attackSpeed) {
			/**
			 * Hack!
			 */
			return;
		}
		lastAttackMilis = milis;
		super.attackTarget(target, time);
	}

	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage, boolean notifyAttack, LOG log) {
		if (getOwner().getLifeStats().isAlreadyDead())
			return;

		// temp
		if (getOwner().getMode() == SummonMode.RELEASE)
			return;

		super.onAttack(creature, skillId, type, damage, notifyAttack, log);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_ATTACK_STATUS(getOwner(), TYPE.REGULAR, 0, damage, log));
		PacketSendUtility.sendPacket(getOwner().getMaster(), new SM_SUMMON_UPDATE(getOwner()));
	}

	@Override
	public void onDie(final Creature lastAttacker) {
		if (lastAttacker == null)
			throw new NullArgumentException("lastAttacker");
		super.onDie(lastAttacker);
		SummonsService.release(getOwner(), UnsummonType.UNSPECIFIED, isAttacked);
		Summon owner = getOwner();
		final Player master = getOwner().getMaster();
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.DIE, 0, lastAttacker.equals(owner) ? 0
			: lastAttacker.getObjectId()));

		if (!master.equals(lastAttacker) && !owner.equals(lastAttacker) && !master.getLifeStats().isAlreadyDead()
			&& !lastAttacker.getLifeStats().isAlreadyDead()) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					lastAttacker.getAggroList().addHate(master, 1);
				}
			}, 1000);
		}
	}

	public void useSkill(int skillId, Creature target) {
		Creature creature = getOwner();
		boolean petHasSkill = DataManager.PET_SKILL_DATA.petHasSkill(getOwner().getObjectTemplate().getTemplateId(),
			skillId);
		if (!petHasSkill) {
			// hackers!)
			return;
		}
		Skill skill = SkillEngine.getInstance().getSkill(creature, skillId, 1, target);
		if (skill != null) {
			// If skill succeeds, handle automatic release if expected
			if (skill.useSkill() && skillId == releaseAfterSkill) {
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						SummonsService.release(getOwner(), UnsummonType.UNSPECIFIED, isAttacked);
					}
				}, 1000);
			}
			setReleaseAfterSkill(-1);
		}
	}

	/**
	 * Handle automatic release if Ultra Skill demands it
	 * @param is the skill commanded by summoner, after which pet is automatically dismissed
	 */
	public void setReleaseAfterSkill(int skillId) {
		this.releaseAfterSkill = skillId;
	}

	@Override
	public void onStartMove() {
		super.onStartMove();
		getOwner().getMoveController().setInMove(true);
		getOwner().getObserveController().notifyMoveObservers();
		PlayerMoveTaskManager.getInstance().addPlayer(getOwner());
	}

	@Override
	public void onStopMove() {
		super.onStopMove();
		PlayerMoveTaskManager.getInstance().removePlayer(getOwner());
		getOwner().getObserveController().notifyMoveObservers();
		getOwner().getMoveController().setInMove(false);
	}

	@Override
	public void onMove() {
		getOwner().getObserveController().notifyMoveObservers();
		super.onMove();
	}

	protected Player getMaster() {
		return getOwner().getMaster();
	}
}
