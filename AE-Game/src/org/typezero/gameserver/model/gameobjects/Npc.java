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

package org.typezero.gameserver.model.gameobjects;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import org.typezero.gameserver.ai2.AI2Engine;
import org.typezero.gameserver.ai2.AITemplate;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.configs.main.AIConfig;
import org.typezero.gameserver.controllers.NpcController;
import org.typezero.gameserver.controllers.movement.NpcMoveController;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.CreatureType;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TribeClass;
import org.typezero.gameserver.model.drop.NpcDrop;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.siege.SiegeNpc;
import org.typezero.gameserver.model.skill.NpcSkillList;
import org.typezero.gameserver.model.stats.container.NpcGameStats;
import org.typezero.gameserver.model.stats.container.NpcLifeStats;
import org.typezero.gameserver.model.templates.item.ItemAttackType;
import org.typezero.gameserver.model.templates.npc.NpcRating;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.model.templates.npc.NpcTemplateType;
import org.typezero.gameserver.model.templates.npcshout.NpcShout;
import org.typezero.gameserver.model.templates.npcshout.ShoutEventType;
import org.typezero.gameserver.model.templates.npcshout.ShoutType;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_LOOKATOBJECT;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.TribeRelationService;
import org.typezero.gameserver.spawnengine.WalkerGroup;
import org.typezero.gameserver.spawnengine.WalkerGroupShift;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.WorldPosition;
import org.typezero.gameserver.world.WorldType;
import com.google.common.base.Preconditions;

/**
 * This class is a base class for all in-game NPCs, what includes: monsters and npcs that player can talk to (aka
 * Citizens)
 *
 * @author Luno
 */
public class Npc extends Creature {

	private WalkerGroup walkerGroup;
	private boolean isQuestBusy = false;
	private NpcSkillList skillList;
	private WalkerGroupShift walkerGroupShift;
	private long lastShoutedSeconds;
	private String masterName = StringUtils.EMPTY;
	private int creatorId = 0;
	private int townId;
	private ItemAttackType attacktype = ItemAttackType.PHYSICAL;
	private NpcTemplateType npcTemplateType;

	public Npc(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate) {
		this(objId, controller, spawnTemplate, objectTemplate, objectTemplate.getLevel());
	}

	public Npc(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate, byte level) {
		super(objId, controller, spawnTemplate, objectTemplate, new WorldPosition());
		Preconditions.checkNotNull(objectTemplate, "Npcs should be based on template");
		controller.setOwner(this);
		moveController = new NpcMoveController(this);
		skillList = new NpcSkillList(this);
		setupStatContainers(level);

		boolean aiOverride = false;
		if (spawnTemplate.getModel() != null) {
			if (spawnTemplate.getModel().getAi() != null) {
				aiOverride = true;
				AI2Engine.getInstance().setupAI(spawnTemplate.getModel().getAi(), this);
			}
		}

		if (!aiOverride)
			AI2Engine.getInstance().setupAI(objectTemplate.getAi(), this);

		lastShoutedSeconds = System.currentTimeMillis() / 1000;
	}

	@Override
	public NpcMoveController getMoveController() {
		return (NpcMoveController) super.getMoveController();
	}

	/**
	 * @param level
	 */
	protected void setupStatContainers(byte level) {
		setGameStats(new NpcGameStats(this));
		setLifeStats(new NpcLifeStats(this));
	}

	@Override
	public NpcTemplate getObjectTemplate() {
		return (NpcTemplate) objectTemplate;
	}

	@Override
	public String getName() {
		return getObjectTemplate().getName();
	}

	public int getNpcId() {
		return getObjectTemplate().getTemplateId();
	}

	@Override
	public byte getLevel() {
		return getObjectTemplate().getLevel();
	}

	@Override
	public NpcLifeStats getLifeStats() {
		return (NpcLifeStats) super.getLifeStats();
	}

	@Override
	public NpcGameStats getGameStats() {
		return (NpcGameStats) super.getGameStats();
	}

	@Override
	public NpcController getController() {
		return (NpcController) super.getController();
	}

	@Override
	public ItemAttackType getAttackType() {
		return this.ai2.modifyAttackType(attacktype);
	}

	public NpcSkillList getSkillList() {
		return this.skillList;
	}

	public boolean hasWalkRoutes() {
		return getSpawn().getWalkerId() != null || (getSpawn().hasRandomWalk() && AIConfig.ACTIVE_NPC_MOVEMENT);
	}

	@Override
	public TribeClass getTribe() {
		TribeClass transformTribe = getTransformModel().getTribe();
		if (transformTribe != null) {
			return transformTribe;
		}
		return this.getObjectTemplate().getTribe();
	}

	@Override
	public TribeClass getBaseTribe() {
		return DataManager.TRIBE_RELATIONS_DATA.getBaseTribe(getTribe());
	}

	public int getAggroRange() {
		return getObjectTemplate().getAggroRange();
	}

	/**
	 * Check whether npc located near initial spawn location
	 *
	 * @return true or false
	 */
	public boolean isAtSpawnLocation() {
		return getDistanceToSpawnLocation() < 3;
	}

	@Override
	public boolean isEnemy(Creature creature) {
		return creature.isEnemyFrom(this) || this.isEnemyFrom(creature);
	}

	@Override
	public boolean isEnemyFrom(Creature creature) {
		return TribeRelationService.isAggressive(creature, this) || TribeRelationService.isHostile(creature, this);
	}

	@Override
	public boolean isEnemyFrom(Npc npc) {
		return TribeRelationService.isAggressive(this, npc) || TribeRelationService.isHostile(this, npc);
	}

	@Override
	public boolean isEnemyFrom(Player player) {
		return player.isEnemyFrom(this);
	}

	@Override
	public int getType(Creature creature) {
		int typeForPlayer = -1;
		if (TribeRelationService.isNone(this, creature))
			typeForPlayer = CreatureType.PEACE.getId();
		else if (TribeRelationService.isAggressive(this, creature))
			typeForPlayer = CreatureType.AGGRESSIVE.getId();
		else if (TribeRelationService.isHostile(this, creature))
			typeForPlayer = CreatureType.ATTACKABLE.getId();
		else if (TribeRelationService.isFriend(this, creature) || TribeRelationService.isNeutral(this, creature))
			typeForPlayer = CreatureType.FRIEND.getId();
		else if (TribeRelationService.isSupport(this, creature))
			typeForPlayer = CreatureType.SUPPORT.getId();
		return typeForPlayer;
	}

	/**
	 * @return distance to spawn location
	 */
	public double getDistanceToSpawnLocation() {
		return MathUtil.getDistance(getSpawn().getX(), getSpawn().getY(), getSpawn().getZ(), getX(), getY(), getZ());
	}

	@Override
	public int getSeeState() {
		int skillSeeState = super.getSeeState();
		int congenitalSeeState = getObjectTemplate().getRating().getCongenitalSeeState().getId();
		return Math.max(skillSeeState, congenitalSeeState);
	}

	public boolean getIsQuestBusy() {
		return isQuestBusy;
	}

	public void setIsQuestBusy(boolean busy) {
		isQuestBusy = busy;
	}

	/**
	 * @return Name of the Master
	 */
	public String getMasterName() {
		return masterName;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	/**
	 * @return UniqueId of the VisibleObject which created this Npc (could be player or house)
	 */
	public int getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	public int getTownId() {
		return townId;
	}

	public void setTownId(int townId) {
		this.townId = townId;
	}

	public VisibleObject getCreator() {
		return null;
	}

	@Override
	public void setTarget(VisibleObject creature) {
		if (getTarget() != creature) {
			super.setTarget(creature);
			super.clearAttackedCount();
			getGameStats().renewLastChangeTargetTime();
			if (!getLifeStats().isAlreadyDead()) {
				PacketSendUtility.broadcastPacket(this, new SM_LOOKATOBJECT(this));
			}
		}
	}

	public void setWalkerGroup(WalkerGroup wg) {
		this.walkerGroup = wg;
	}

	public WalkerGroup getWalkerGroup() {
		return walkerGroup;
	}

	public void setWalkerGroupShift(WalkerGroupShift shift) {
		this.walkerGroupShift = shift;
	}

	public WalkerGroupShift getWalkerGroupShift() {
		return walkerGroupShift;
	}

	@Override
	public boolean isFlag() {
		return getObjectTemplate().getNpcTemplateType().equals(NpcTemplateType.FLAG);
	}

	public boolean isBoss() {
		return getObjectTemplate().getRating() == NpcRating.HERO || getObjectTemplate().getRating() == NpcRating.LEGENDARY;
	}

	public boolean hasStatic() {
		return getSpawn().getStaticId() != 0;
	}

	@Override
	public Race getRace() {
		return this.getObjectTemplate().getRace();
	}

	public NpcDrop getNpcDrop() {
		return getObjectTemplate().getNpcDrop();
	}

	public void setNpcType(int newType) {
	   type = newType;
	}

	public NpcTemplateType getNpcTemplateType() {
		return npcTemplateType;
	}

	public void setNpcTemplateType(NpcTemplateType newType) {
		npcTemplateType = newType;
	}

	public boolean isRewardAP() {
		if (this instanceof SiegeNpc) {
			return true;
		}
		else if (this.getWorldType() == WorldType.ABYSS) {
			return true;
		}
		else if (this.getAi2().ask(AIQuestion.SHOULD_REWARD_AP).isPositive()) {
			return true;
		}
		else if (this.getWorldType() == WorldType.BALAUREA) {
			return getRace() == Race.DRAKAN || getRace() == Race.LIZARDMAN;
		}

		return false;
	}

	public boolean isRewardGP() {
		if (this.getWorldId() == 600040000) {
			return getRace() == Race.DRAKAN;
		}
		return false;
	}

	public boolean mayShout(int delaySeconds) {
		if (!DataManager.NPC_SHOUT_DATA.hasAnyShout(getPosition().getMapId(), getNpcId()))
			return false;
		return (System.currentTimeMillis() - lastShoutedSeconds) / 1000 >= delaySeconds;
	}

	public void shout(final NpcShout shout, final Creature target, final Object param, int delaySeconds) {
		if (shout.getWhen() != ShoutEventType.DIED && shout.getWhen() != ShoutEventType.BEFORE_DESPAWN
			&& getLifeStats().isAlreadyDead() || !mayShout(delaySeconds))
			return;

		if (shout.getPattern() != null
			&& !((AITemplate) getAi2()).onPatternShout(shout.getWhen(), shout.getPattern(), shout.getSkillNo()))
			return;

		final int shoutRange = getObjectTemplate().getMinimumShoutRange();
		if (shout.getShoutType() == ShoutType.SAY && !(target instanceof Player) || target != null
			&& !MathUtil.isIn3dRange(target, this, shoutRange))
			return;

		final Npc thisNpc = this;
		final SM_SYSTEM_MESSAGE message = new SM_SYSTEM_MESSAGE(true, shout.getStringId(), getObjectId(), 1, param);
		lastShoutedSeconds = System.currentTimeMillis() / 1000;

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (thisNpc.getLifeStats().isAlreadyDead() && shout.getWhen() != ShoutEventType.DIED
					&& shout.getWhen() != ShoutEventType.BEFORE_DESPAWN)
					return;

				// message for the specific player (when IDLE we are already broadcasting!!!)
				if (shout.getShoutType() == ShoutType.SAY || shout.getWhen() == ShoutEventType.IDLE) {
					// [RR] Should we have lastShoutedSeconds separated from broadcasts (??)
					PacketSendUtility.sendPacket((Player) target, message);
				}
				else {
					Iterator<Player> iter = thisNpc.getKnownList().getKnownPlayers().values().iterator();
					while (iter.hasNext()) {
						Player kObj = iter.next();
						if (kObj.getLifeStats().isAlreadyDead() || !kObj.isOnline())
							continue;
						if (MathUtil.isIn3dRange(kObj, thisNpc, shoutRange))
							PacketSendUtility.sendPacket(kObj, message);
					}
				}
			}
		}, delaySeconds * 1000);
	}

}
