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

import com.aionemu.commons.callbacks.EnhancedObject;
import org.typezero.gameserver.configs.main.SecurityConfig;
import org.typezero.gameserver.configs.main.HTMLConfig;
import org.typezero.gameserver.configs.main.MembershipConfig;
import org.typezero.gameserver.controllers.attack.AttackUtil;
import org.typezero.gameserver.controllers.observer.SkillCastListener;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.dataholders.PlayerInitialData;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.actions.PlayerMode;
import org.typezero.gameserver.model.gameobjects.*;
import org.typezero.gameserver.model.gameobjects.player.AbyssRank;
import org.typezero.gameserver.model.gameobjects.player.BindPointPosition;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.model.gameobjects.state.CreatureVisualState;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.skill.PlayerSkillEntry;
import org.typezero.gameserver.model.stats.container.PlayerGameStats;
import org.typezero.gameserver.model.summons.SummonMode;
import org.typezero.gameserver.model.summons.UnsummonType;
import org.typezero.gameserver.model.team2.group.PlayerFilters.ExcludePlayerFilter;
import org.typezero.gameserver.model.templates.flypath.FlyPathEntry;
import org.typezero.gameserver.model.templates.panels.SkillPanel;
import org.typezero.gameserver.model.templates.quest.QuestItems;
import org.typezero.gameserver.model.templates.stats.PlayerStatsTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import org.typezero.gameserver.network.aion.serverpackets.*;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.restrictions.RestrictionsManager;
import org.typezero.gameserver.services.*;
import org.typezero.gameserver.services.craft.CraftSkillUpdateService;
import org.typezero.gameserver.services.abyss.AbyssService;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.services.player.PlayerService;
import org.typezero.gameserver.services.summons.SummonsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.skillengine.model.Skill.SkillMethod;
import org.typezero.gameserver.skillengine.model.*;
import org.typezero.gameserver.taskmanager.tasks.PlayerMoveTaskManager;
import org.typezero.gameserver.taskmanager.tasks.TeamEffectUpdater;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.utils.audit.AuditLogger;
import org.typezero.gameserver.world.MapRegion;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldType;
import org.typezero.gameserver.world.geo.GeoService;
import org.typezero.gameserver.world.zone.ZoneInstance;
import org.typezero.gameserver.world.zone.ZoneName;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Future;
import java.util.Set;
import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is for controlling players.
 *
 * @author -Nemesiss-, ATracer, xavier, Sarynth, RotO, xTz, KID, Sippolo
 */
public class PlayerController extends CreatureController<Player> {

	private Logger log = LoggerFactory.getLogger(PlayerController.class);
	private boolean isInShutdownProgress;
	private long lastAttackMilis = 0;
	private long lastAttackedMilis = 0;
	private int stance = 0;

	@Override
	public void see(VisibleObject object) {
		super.see(object);
		if (object instanceof Player) {
			Player player = (Player) object;
			PacketSendUtility.sendPacket(getOwner(), new SM_PLAYER_INFO(player, getOwner().isAggroIconTo(player)));
			PacketSendUtility.sendPacket(getOwner(), new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
			if (player.isInPlayerMode(PlayerMode.RIDE)) {
				PacketSendUtility.sendPacket(getOwner(), new SM_EMOTION(player, EmotionType.RIDE, 0, player.ride.getNpcId()));
			}
			if (player.getPet() != null) {
				LoggerFactory.getLogger(PlayerController.class).debug(
						"Player " + getOwner().getName() + " sees " + object.getName() + " that has toypet");
				PacketSendUtility.sendPacket(getOwner(), new SM_PET(3, player.getPet()));
			}
			player.getEffectController().sendEffectIconsTo(getOwner());
		}
		else if (object instanceof Kisk) {
			Kisk kisk = ((Kisk) object);
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(kisk, getOwner()));
			if (getOwner().getRace() == kisk.getOwnerRace())
				PacketSendUtility.sendPacket(getOwner(), new SM_KISK_UPDATE(kisk));
		}
		else if (object instanceof Npc) {
			Npc npc = ((Npc) object);

			/*if (npc.isFlag()) {
				return;
			}*/

			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(npc, getOwner()));
			if (!npc.getEffectController().isEmpty())
				npc.getEffectController().sendEffectIconsTo(getOwner());
			QuestEngine.getInstance().onAtDistance(new QuestEnv(object, getOwner(), 0, 0));
		}
		else if (object instanceof Summon) {
			Summon npc = ((Summon) object);
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(npc, getOwner()));
			if (!npc.getEffectController().isEmpty())
				npc.getEffectController().sendEffectIconsTo(getOwner());
		}
		else if (object instanceof Gatherable || object instanceof StaticObject) {
			PacketSendUtility.sendPacket(getOwner(), new SM_GATHERABLE_INFO(object));
		}
		else if (object instanceof Pet) {
			PacketSendUtility.sendPacket(getOwner(), new SM_PET(3, (Pet) object));
		}
	}

	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		super.notSee(object, isOutOfRange);
		if (object instanceof Pet) {
			PacketSendUtility.sendPacket(getOwner(), new SM_PET(4, (Pet) object));
		}
		else {
			PacketSendUtility.sendPacket(getOwner(), new SM_DELETE(object, isOutOfRange ? 0 : 15));
		}
	}

	public void updateNearbyQuests() {
		Set<Integer> nearbyQuestList = new HashSet<Integer>();
		for (int questId : getOwner().getPosition().getMapRegion().getParent().getQuestIds()) {
			if (QuestService.checkStartConditions(new QuestEnv(null, getOwner(), questId, 0), false)
					&& QuestService.checkLevelRequirement(questId, getOwner().getCommonData().getLevel())) {
				nearbyQuestList.add(questId);
			}
		}
		PacketSendUtility.sendPacket(getOwner(), new SM_NEARBY_QUESTS(nearbyQuestList));
	}

	@Override
	public void onEnterZone(ZoneInstance zone) {
		Player player = getOwner();
		if (!zone.canRide() && player.isInPlayerMode(PlayerMode.RIDE)) {
			player.unsetPlayerMode(PlayerMode.RIDE);
		}
		InstanceService.onEnterZone(player, zone);
		if (zone.getAreaTemplate().getZoneName() == null) {
			log.error("No name found for a Zone in the map " + zone.getAreaTemplate().getWorldId());
		}
		else {
			QuestEngine.getInstance().onEnterZone(new QuestEnv(null, player, 0, 0), zone.getAreaTemplate().getZoneName());
		}
	}

	@Override
	public void onLeaveZone(ZoneInstance zone) {
		Player player = getOwner();
		InstanceService.onLeaveZone(player, zone);
		ZoneName zoneName = zone.getAreaTemplate().getZoneName();
		if (zoneName == null) {
			log.warn("No name for zone template in " + zone.getAreaTemplate().getWorldId());
			return;
		}
		QuestEngine.getInstance().onLeaveZone(new QuestEnv(null, player, 0, 0), zoneName);
	}

	/**
	 * {@inheritDoc} Should only be triggered from one place (life stats)
	 */
	// TODO [AT] move
	public void onEnterWorld() {

		InstanceService.onEnterInstance(getOwner());
		if (getOwner().getPosition().getWorldMapInstance().getParent().isExceptBuff()) {
			getOwner().getEffectController().removeAllEffects();
		}

            if ((getOwner().getEffectController().hasAbnormalEffect(21521)) || (getOwner().getEffectController().hasAbnormalEffect(21522)))
            {
                getOwner().getEffectController().removeEffect(21521);
                getOwner().getEffectController().removeEffect(21522);
            }

		for (Effect ef : getOwner().getEffectController().getAbnormalEffects()) {
			if (ef.isDeityAvatar()) {
				// remove abyss transformation if worldtype != abyss && worldtype != balaurea
				if (getOwner().getWorldType() != WorldType.ABYSS && getOwner().getWorldType() != WorldType.BALAUREA
						|| getOwner().isInInstance()) {
					ef.endEffect();
					getOwner().getEffectController().clearEffect(ef);
				}
			}
			else if (ef.getSkillTemplate().getDispelCategory() == DispelCategoryType.NPC_BUFF) {
				ef.endEffect();
				getOwner().getEffectController().clearEffect(ef);
			}
		}
	}

	// TODO [AT] move
	public void onLeaveWorld() {
		SerialKillerService.getInstance().onLeaveMap(getOwner());
		InstanceService.onLeaveInstance(getOwner());
	}

	public void validateLoginZone() {
		int mapId;
		float x, y, z;
		byte h;
		boolean moveToBind = false;

		BindPointPosition bind = getOwner().getBindPoint();

		if (bind != null) {
			mapId = bind.getMapId();
			x = bind.getX();
			y = bind.getY();
			z = bind.getZ();
			h = bind.getHeading();
		}
		else {
			PlayerInitialData.LocationData start = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(getOwner().getRace());

			mapId = start.getMapId();
			x = start.getX();
			y = start.getY();
			z = start.getZ();
			h = start.getHeading();
		}
		if (!SiegeService.getInstance().validateLoginZone(getOwner())) {
			moveToBind = true;
		}
		else {
			long lastOnline = getOwner().getCommonData().getLastOnline().getTime();
			long secondsOffline = (System.currentTimeMillis() / 1000) - lastOnline / 1000;
			if (secondsOffline > 10 * 60) { //Logout in no-recall zone sends you to bindpoint after 10 (??) minutes
				for (ZoneInstance zone : getOwner().getPosition().getMapRegion().getZones(getOwner())) {
					if (!zone.canRecall()) {
						moveToBind = true;
						break;
					}
				}
			}
		}

		if (moveToBind)
			World.getInstance().setPosition(getOwner(), mapId, x, y, z, h);
	}

	public void onDie(@Nonnull Creature lastAttacker, boolean showPacket) {
		Player player = this.getOwner();
		player.getController().cancelCurrentSkill();
		player.setRebirthRevive(getOwner().haveSelfRezEffect());
		showPacket = player.hasResurrectBase() ? false : showPacket;
		Creature master = lastAttacker.getMaster();

		// High ranked kill announce
		AbyssRank ar = player.getAbyssRank();
		if (AbyssService.isOnPvpMap(player) && ar != null) {
			if (ar.getRank().getId() >= 10)
				AbyssService.rankedKillAnnounce(player);
		}

		if (DuelService.getInstance().isDueling(player.getObjectId())) {
			if (master != null && DuelService.getInstance().isDueling(player.getObjectId(), master.getObjectId())) {
				DuelService.getInstance().loseDuel(player);
				player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
				player.getLifeStats().setCurrentHpPercent(33);
				player.getLifeStats().setCurrentMpPercent(33);
				return;
			}
			DuelService.getInstance().loseDuel(player);
		}

		/**
		 * Release summon
		 */
		Summon summon = player.getSummon();
		if (summon != null) {
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.UNSPECIFIED);
		}

		// setIsFlyingBeforeDead for PlayerReviveService
		if (player.isInState(CreatureState.FLYING)) {
			player.setIsFlyingBeforeDeath(true);
		}

		// ride
		player.setPlayerMode(PlayerMode.RIDE, null);
		player.unsetState(CreatureState.RESTING);
		player.unsetState(CreatureState.FLOATING_CORPSE);

		// unsetflying
		player.unsetState(CreatureState.FLYING);
		player.unsetState(CreatureState.GLIDING);
		player.setFlyState(0);

		if (player.isInInstance()) {
			if (player.getPosition().getWorldMapInstance().getInstanceHandler().onDie(player, lastAttacker)) {
				super.onDie(lastAttacker);
				return;
			}
		}

		MapRegion mapRegion = player.getPosition().getMapRegion();
		if (mapRegion != null && mapRegion.onDie(lastAttacker, getOwner())) {
			return;
		}

		this.doReward();

		if (master instanceof Npc || master == player) {
			if (player.getLevel() > 4 && !isNoDeathPenaltyInEffect())
				player.getCommonData().calculateExpLoss();
		}

		// Effects removed with super.onDie()
		super.onDie(lastAttacker);

		// send sm_emotion with DIE
		// have to be send after state is updated!
		sendDieFromCreature(lastAttacker, showPacket);

		QuestEngine.getInstance().onDie(new QuestEnv(null, player, 0, 0));

		if (player.isInGroup2()) {
			player.getPlayerGroup2().sendPacket(SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_FRIENDLY_DEATH(player.getName()),
					new ExcludePlayerFilter(player));
		}
	}

	@Override
	public void onDie(@Nonnull Creature lastAttacker) {
		this.onDie(lastAttacker, true);
	}

	public void sendDie() {
		sendDieFromCreature(getOwner(), true);
	}

	private void sendDieFromCreature(@Nonnull Creature lastAttacker, boolean showPacket) {
		Player player = this.getOwner();

		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0,
				player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);

		if (showPacket) {
			int kiskTimeRemaining = (player.getKisk() != null ? player.getKisk().getRemainingLifetime() : 0);
			PacketSendUtility.sendPacket(player, new SM_DIE(player.canUseRebirthRevive(), player.haveSelfRezItem(),
					kiskTimeRemaining, 0, isInvader(player)));
		}

		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_MY_DEATH);
	}

	private boolean isInvader(Player player) {
		if (player.getRace().equals(Race.ASMODIANS)) {
			return player.getWorldId() == 210060000;
		}
		else {
			return player.getWorldId() == 220050000;
		}
	}

	@Override
	public void doReward() {
		PvpService.getInstance().doReward(getOwner());
	}

	@Override
	public void onBeforeSpawn() {
		super.onBeforeSpawn();
		startProtectionActiveTask();
		if (getOwner().getIsFlyingBeforeDeath())
			getOwner().unsetState(CreatureState.FLOATING_CORPSE);
		else
			getOwner().unsetState(CreatureState.DEAD);
		getOwner().setState(CreatureState.ACTIVE);
	}

	@Override
	public void attackTarget(Creature target, int time) {

		PlayerGameStats gameStats = getOwner().getGameStats();

		if (!RestrictionsManager.canAttack(getOwner(), target))
			return;

		// Normal attack is already limited client side (ex. Press C and attacker approaches target)
		// but need a check server side too also for Z axis issue

		if (!MathUtil.isInAttackRange(getOwner(), target,
				(float) (getOwner().getGameStats().getAttackRange().getCurrent() / 1000f) + 1))
			return;

		if (!GeoService.getInstance().canSee(getOwner(), target)) {
			PacketSendUtility.sendPacket(getOwner(), SM_SYSTEM_MESSAGE.STR_ATTACK_OBSTACLE_EXIST);
			return;
		}

		if (target instanceof Npc) {
			QuestEngine.getInstance().onAttack(new QuestEnv(target, getOwner(), 0, 0));
		}

		int attackSpeed = gameStats.getAttackSpeed().getCurrent();

		long milis = System.currentTimeMillis();
		// network ping..
		if (milis - lastAttackMilis + 300 < attackSpeed) {
			// hack
			return;
		}
		lastAttackMilis = milis;

		/**
		 * notify attack observers
		 */
		super.attackTarget(target, time);

	}

	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage, boolean notifyAttack, LOG log) {
		if (getOwner().getLifeStats().isAlreadyDead())
			return;

		if (getOwner().isInvul() || getOwner().isProtectionActive() || creature instanceof Player && getOwner().isInDisablePvPZone() && !getOwner().getController().isDueling((Player) creature))
			damage = 0;

		cancelUseItem();
		cancelGathering();
		super.onAttack(creature, skillId, type, damage, notifyAttack, log);

		PacketSendUtility.broadcastPacket(getOwner(), new SM_ATTACK_STATUS(getOwner(), type, skillId, damage, log), true);

		if (creature instanceof Npc) {
			QuestEngine.getInstance().onAttack(new QuestEnv(creature, getOwner(), 0, 0));
		}

		lastAttackedMilis = System.currentTimeMillis();
	}

	/**
	 * @param skillId
	 * @param targetType
	 * @param x
	 * @param y
	 * @param z
	 */
	public void useSkill(int skillId, int targetType, float x, float y, float z, int time) {
		Player player = getOwner();

		Skill skill = SkillEngine.getInstance().getSkillFor(player, skillId, player.getTarget());

		if (skill != null) {
			if (!RestrictionsManager.canUseSkill(player, skill))
				return;

			skill.setTargetType(targetType, x, y, z);
			skill.setHitTime(time);
			skill.useSkill();
		}
	}

	/**
	 * @param template
	 * @param targetType
	 * @param x
	 * @param y
	 * @param z
	 * @param clientHitTime
	 */
	public void useSkill(SkillTemplate template, int targetType, float x, float y, float z, int clientHitTime,
			int skillLevel) {
		Player player = getOwner();
		Skill skill = null;
		skill = SkillEngine.getInstance().getSkillFor(player, template, player.getTarget());
		if (skill == null && player.isTransformed()) {
			SkillPanel panel = DataManager.PANEL_SKILL_DATA.getSkillPanel(player.getTransformModel().getPanelId());
			if (panel != null && panel.canUseSkill(template.getSkillId(), skillLevel)) {
				skill = SkillEngine.getInstance().getSkillFor(player, template, player.getTarget(), skillLevel);
			}
		}

		if (skill != null) {
			if (!RestrictionsManager.canUseSkill(player, skill))
				return;

			skill.setTargetType(targetType, x, y, z);
			skill.setHitTime(clientHitTime);
			skill.useSkill();
		}
	}

	@Override
	public void onMove() {
		getOwner().getObserveController().notifyMoveObservers();
		super.onMove();
	}

	@Override
	public void onStopMove() {
		PlayerMoveTaskManager.getInstance().removePlayer(getOwner());
		getOwner().getObserveController().notifyMoveObservers();
		getOwner().getMoveController().setInMove(false);
		cancelCurrentSkill();
		updateZone();
		super.onStopMove();
	}

	@Override
	public void onStartMove() {
		getOwner().getMoveController().setInMove(true);
		PlayerMoveTaskManager.getInstance().addPlayer(getOwner());
		cancelUseItem();
		cancelCurrentSkill();
		super.onStartMove();
	}

	@Override
	public void cancelCurrentSkill() {
		if (getOwner().getCastingSkill() == null) {
			return;
		}

		Player player = getOwner();
		Skill castingSkill = player.getCastingSkill();
		castingSkill.cancelCast();
		player.removeSkillCoolDown(castingSkill.getSkillTemplate().getCooldownId());
		player.setCasting(null);
		player.setNextSkillUse(0);
		if (castingSkill.getSkillMethod() == SkillMethod.CAST || castingSkill.getSkillMethod() == SkillMethod.CHARGE) {
			PacketSendUtility.broadcastPacket(player, new SM_SKILL_CANCEL(player, castingSkill.getSkillTemplate().getSkillId()), true);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SKILL_CANCELED);
		}
		else if (castingSkill.getSkillMethod() == SkillMethod.ITEM) {
			PacketSendUtility.sendPacket(player,
					SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(castingSkill.getItemTemplate().getNameId())));
			player.removeItemCoolDown(castingSkill.getItemTemplate().getUseLimits().getDelayId());
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), castingSkill.getFirstTarget().getObjectId(), castingSkill.getItemObjectId(),
					castingSkill.getItemTemplate().getTemplateId(), 0, 3, 0), true);
		}
	}

	@Override
	public void cancelUseItem() {
		Player player = getOwner();
		Item usingItem = player.getUsingItem();
		player.setUsingItem(null);
		if (hasTask(TaskId.ITEM_USE)) {
			cancelTask(TaskId.ITEM_USE);
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), usingItem == null ? 0
					: usingItem.getObjectId(), usingItem == null ? 0 : usingItem.getItemTemplate().getTemplateId(), 0, 3, 0), true);
		}
	}

	public void cancelGathering() {
		Player player = getOwner();
		if (player.getTarget() instanceof Gatherable) {
			Gatherable g = (Gatherable) player.getTarget();
			g.getController().finishGathering(player);
		}
	}

	public void updatePassiveStats() {
		Player player = getOwner();
		for (PlayerSkillEntry skillEntry : player.getSkillList().getAllSkills()) {
			Skill skill = SkillEngine.getInstance().getSkillFor(player, skillEntry.getSkillId(), player.getTarget());
			if (skill != null && skill.isPassive()) {
				skill.useSkill();
			}
		}
	}

	@Override
	public Player getOwner() {
		return (Player) super.getOwner();
	}

	@Override
	public void onRestore(HealType healType, int value) {
		super.onRestore(healType, value);
		switch (healType) {
			case DP:
				getOwner().getCommonData().addDp(value);
				break;
			default:
				break;
		}
	}

	/**
	 * @param player
	 * @return
	 */
	// TODO [AT] move to Player
	public boolean isDueling(Player player) {
		return DuelService.getInstance().isDueling(player.getObjectId(), getOwner().getObjectId());
	}

	// TODO [AT] rename or remove
	public boolean isInShutdownProgress() {
		return isInShutdownProgress;
	}

	// TODO [AT] rename or remove
	public void setInShutdownProgress(boolean isInShutdownProgress) {
		this.isInShutdownProgress = isInShutdownProgress;
	}

	@Override
	public void onDialogSelect(int dialogId, Player player, int questId, int extendedRewardIndex) {
		switch (dialogId) {
			case 2:
				PacketSendUtility.sendPacket(player, new SM_PRIVATE_STORE(getOwner().getStore(), player));
				break;
		}
	}

	public void upgradePlayer() {
		Player player = getOwner();
		byte level = player.getLevel();

		PlayerStatsTemplate statsTemplate = DataManager.PLAYER_STATS_DATA.getTemplate(player);
		player.setPlayerStatsTemplate(statsTemplate);

		player.getLifeStats().synchronizeWithMaxStats();
		player.getLifeStats().updateCurrentStats();

		PacketSendUtility.broadcastPacket(player, new SM_LEVEL_UPDATE(player.getObjectId(), 0, level), true);

		// Guides Html on level up
		if (HTMLConfig.ENABLE_GUIDES)
			HTMLService.sendGuideHtml(player);

		// Temporal
		ClassChangeService.showClassChangeDialog(player);

		QuestEngine.getInstance().onLvlUp(new QuestEnv(null, player, 0, 0));
		updateNearbyQuests();

		// add new skills
		SkillLearnService.addNewSkills(player);

		player.getController().updatePassiveStats();

		// add recipe for morph
		if (level == 10)
			CraftSkillUpdateService.getInstance().setMorphRecipe(player);

		if (player.isInTeam()) {
			TeamEffectUpdater.getInstance().startTask(player);
		}
		if (player.isLegionMember())
			LegionService.getInstance().updateMemberInfo(player);
		player.getNpcFactions().onLevelUp();
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
	}

	/**
	 * After entering game player char is "blinking" which means that it's in
	 * under some protection, after making an action char stops blinking. -
	 * Starts protection active - Schedules task to end protection
	 */
	public void startProtectionActiveTask() {
		if (!getOwner().isProtectionActive()) {
			getOwner().setVisualState(CreatureVisualState.BLINKING);
			AttackUtil.cancelCastOn((Creature)getOwner());
			AttackUtil.removeTargetFrom((Creature)getOwner());
			PacketSendUtility.broadcastPacket(getOwner(), new SM_PLAYER_STATE(getOwner()), true);
			Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					stopProtectionActiveTask();
				}

			}, 60000);
			addTask(TaskId.PROTECTION_ACTIVE, task);
		}
	}

	/**
	 * Stops protection active task after first move or use skill
	 */
	public void stopProtectionActiveTask() {
		cancelTask(TaskId.PROTECTION_ACTIVE);
		Player player = getOwner();
		if (player != null && player.isSpawned()) {
			player.unsetVisualState(CreatureVisualState.BLINKING);
			PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
			notifyAIOnMove();
		}
	}

	/**
	 * When player arrives at destination point of flying teleport
	 */
	public void onFlyTeleportEnd() {
		Player player = getOwner();
		if (player.isInPlayerMode(PlayerMode.WINDSTREAM)) {
			player.unsetPlayerMode(PlayerMode.WINDSTREAM);
			player.getLifeStats().triggerFpReduce();
			player.unsetState(CreatureState.FLYING);
			player.setState(CreatureState.ACTIVE);
			player.setState(CreatureState.GLIDING);
			player.getGameStats().updateStatsAndSpeedVisually();
		}
		else {
			player.unsetState(CreatureState.FLIGHT_TELEPORT);
			player.setFlightTeleportId(0);

			if (SecurityConfig.ENABLE_FLYPATH_VALIDATOR) {
				long diff = (System.currentTimeMillis() - player.getFlyStartTime());
				FlyPathEntry path = player.getCurrentFlyPath();

				if (player.getWorldId() != path.getEndWorldId()) {
					AuditLogger.info(player, "Player tried to use flyPath #" + path.getId() + " from not native start world "
							+ player.getWorldId() + ". expected " + path.getEndWorldId());
				}

				if (diff < path.getTimeInMs()) {
					AuditLogger.info(player,
							"Player " + player.getName() + " used flypath bug " + diff + " instead of " + path.getTimeInMs());
					/*
					 * todo if works teleport player to start_* xyz, or even ban
					 */
				}

				player.setCurrentFlypath(null);
			}

			player.setFlightDistance(0);
			player.setState(CreatureState.ACTIVE);
			updateZone();
		}
	}

	public boolean addItems(int itemId, int count) {
		return ItemService.addQuestItems(getOwner(), Collections.singletonList(new QuestItems(itemId, count)));
	}

	public void startStance(final int skillId) {
		stance = skillId;
	}

	public void stopStance() {
		getOwner().getEffectController().removeEffect(stance);
		PacketSendUtility.sendPacket(getOwner(), new SM_PLAYER_STANCE(getOwner(), 0));
		stance = 0;
	}

	public int getStanceSkillId() {
		return stance;
	}

	public boolean isUnderStance() {
		return stance != 0;
	}

	public void updateSoulSickness(int skillId) {
		Player player = getOwner();
		House house = player.getActiveHouse();
		if (house != null)
			switch (house.getHouseType()) {
				case MANSION:
				case ESTATE:
				case PALACE:
					return;
			default:
				break;
			}

		if (!player.havePermission(MembershipConfig.DISABLE_SOULSICKNESS)) {
			int deathCount = player.getCommonData().getDeathCount();
			if (deathCount < 10) {
				deathCount++;
				player.getCommonData().setDeathCount(deathCount);
			}

			if (skillId == 0)
				skillId = 8291;
			SkillEngine.getInstance().getSkill(player, skillId, deathCount, player).useSkill();
		}
	}

	/**
	 * Player is considered in combat if he's been attacked or has attacked less
	 * or equal 10s before
	 *
	 * @return true if the player is actively in combat
	 */
	public boolean isInCombat() {
		return (((System.currentTimeMillis() - lastAttackedMilis) <= 10000) || ((System.currentTimeMillis() - lastAttackMilis) <= 10000));
	}

	/**
	 * Check if NoDeathPenalty is active
	 *
	 * @param player
	 * @return boolean
	 */
	public boolean isNoDeathPenaltyInEffect() {
		// Check if NoDeathPenalty is active
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = iterator.next();
			if (effect.isNoDeathPenalty())
				return true;
		}
		return false;
	}

	/**
	 * Check if NoResurrectPenalty is active
	 *
	 * @param player
	 * @return boolean
	 */
	public boolean isNoResurrectPenaltyInEffect() {
		// Check if NoResurrectPenalty is active
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = iterator.next();
			if (effect.isNoResurrectPenalty())
				return true;
		}
		return false;
	}

    /**
	 * Check if HiPass is active
	 *
	 * @param player
	 * @return boolean
	 */
	public boolean isHiPassInEffect() {
		// Check if HiPass is active
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = iterator.next();
			if (effect.isHiPass())
				return true;
		}
		return false;
	}

}
