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

package org.typezero.gameserver.services.summons;

import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.controllers.SummonController;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.summons.SummonMode;
import org.typezero.gameserver.model.summons.UnsummonType;
import org.typezero.gameserver.network.aion.serverpackets.*;
import org.typezero.gameserver.spawnengine.VisibleObjectSpawner;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 *
 * @author xTz
 */
public class SummonsService {

	/**
	 * create summon
	 */
	public static final void createSummon(Player master, int npcId, int skillId, int skillLevel, int time) {
		if (master.getSummon() != null) {
			PacketSendUtility.sendPacket(master, new SM_SYSTEM_MESSAGE(1300072));
			return;
		}
		Summon summon = VisibleObjectSpawner.spawnSummon(master, npcId, skillId, skillLevel, time);
		if (summon.getAi2().getName().equals("siege_weapon")) {
			summon.getAi2().onGeneralEvent(AIEventType.SPAWNED);
		}
		master.setSummon(summon);
		PacketSendUtility.sendPacket(master, new SM_SUMMON_PANEL(summon));
		PacketSendUtility.broadcastPacket(summon, new SM_EMOTION(summon, EmotionType.START_EMOTE2));
		PacketSendUtility.broadcastPacket(summon, new SM_SUMMON_UPDATE(summon));
	}

	/**
	 * Release summon
	 */
	public static final void release(final Summon summon, final UnsummonType unsummonType, final boolean isAttacked) {
		if (summon.getMode() == SummonMode.RELEASE)
			return;
		summon.getController().cancelCurrentSkill();
		summon.setMode(SummonMode.RELEASE);
		final Player master = summon.getMaster();
		switch (unsummonType) {
			case COMMAND:
				PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_UNSUMMON_FOLLOWER(summon.getNameId()));
				PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(summon));
				break;
			case DISTANCE:
				PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_UNSUMMON_BY_TOO_DISTANCE);
				PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(summon));
				break;
			case LOGOUT:
			case UNSPECIFIED:
				break;
		}
		summon.getObserveController().notifySummonReleaseObservers();
		summon.setReleaseTask(ThreadPoolManager.getInstance().schedule(new ReleaseSummonTask(summon, unsummonType, isAttacked), 5000));
	}

	public static class ReleaseSummonTask implements Runnable {

		private Summon owner;
		private UnsummonType unsummonType;
		private Player master;
		private VisibleObject target;
		private boolean isAttacked;

		public ReleaseSummonTask(Summon owner, UnsummonType unsummonType, boolean isAttacked) {
			this.owner = owner;
			this.unsummonType = unsummonType;
			this.master = owner.getMaster();
			this.target = master.getTarget();
			this.isAttacked = isAttacked;
		}

		@Override
		public void run() {

			owner.getController().delete();
			owner.setMaster(null);
			master.setSummon(null);

			switch (unsummonType) {
				case COMMAND:
				case DISTANCE:
				case UNSPECIFIED:
					PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_UNSUMMONED(owner.getNameId()));
					PacketSendUtility.sendPacket(master, new SM_SUMMON_OWNER_REMOVE(owner.getObjectId()));

					// TODO temp till found on retail
					PacketSendUtility.sendPacket(master, new SM_SUMMON_PANEL_REMOVE());
					if (target instanceof Creature) {
						final Creature lastAttacker = (Creature) target;
						if (!master.getLifeStats().isAlreadyDead() && !lastAttacker.getLifeStats().isAlreadyDead() && isAttacked) {
							ThreadPoolManager.getInstance().schedule(new Runnable() {

								@Override
								public void run() {
									lastAttacker.getAggroList().addHate(master, 1);
								}

							}, 1000);
						}
					}
					break;
				case LOGOUT:
					break;
			}
		}

	}

	/**
	 * Change to rest mode
	 */
	public static final void restMode(final Summon summon) {
		summon.getController().cancelCurrentSkill();
		summon.setMode(SummonMode.REST);
		Player master = summon.getMaster();
		PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_REST_MODE(summon.getNameId()));
		PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(summon));
		summon.getLifeStats().triggerRestoreTask();
	}

	public static final void setUnkMode(final Summon summon) {
		summon.setMode(SummonMode.UNK);
		Player master = summon.getMaster();
		PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(summon));
	}

	/**
	 * Change to guard mode
	 */
	public static final void guardMode(final Summon summon) {
		summon.getController().cancelCurrentSkill();
		summon.setMode(SummonMode.GUARD);
		Player master = summon.getMaster();
		PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_GUARD_MODE(summon.getNameId()));
		PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(summon));
		summon.getLifeStats().triggerRestoreTask();
	}

	/**
	 * Change to attackMode
	 */
	public static final void attackMode(final Summon summon) {
		summon.setMode(SummonMode.ATTACK);
		Player master = summon.getMaster();
		PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_ATTACK_MODE(summon.getNameId()));
		PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(summon));
		summon.getLifeStats().cancelRestoreTask();
	}

	public static final void doMode(SummonMode summonMode, Summon summon) {
		doMode(summonMode, summon, 0, null);
	}

	public static final void doMode(SummonMode summonMode, Summon summon, UnsummonType unsummonType) {
		doMode(summonMode, summon, 0, unsummonType);
	}

	public static final void doMode(SummonMode summonMode, Summon summon, int targetObjId, UnsummonType unsummonType) {
		if (summon.getLifeStats().isAlreadyDead()) {
			return;
		}
		if (unsummonType != null && unsummonType.equals(UnsummonType.COMMAND) && !summonMode.equals(SummonMode.RELEASE)) {
			summon.cancelReleaseTask();
		}
		SummonController summonController = summon.getController();
		if (summonController == null) {
			return;
		}
		if (summon.getMaster() == null) {
			summon.getController().onDelete();
			return;
		}
		switch (summonMode) {
			case REST:
				summonController.restMode();
				break;
			case ATTACK:
				summonController.attackMode(targetObjId);
				break;
			case GUARD:
				summonController.guardMode();
				break;
			case RELEASE:
				if (unsummonType != null) {
					summonController.release(unsummonType);
				}
				break;
			case UNK:
				break;
		}
	}
}
