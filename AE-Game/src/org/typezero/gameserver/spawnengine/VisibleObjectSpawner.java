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

package org.typezero.gameserver.spawnengine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.configs.main.SiegeConfig;
import org.typezero.gameserver.controllers.*;
import org.typezero.gameserver.controllers.effect.EffectController;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.dataholders.NpcData;
import org.typezero.gameserver.geoEngine.collision.CollisionIntention;
import org.typezero.gameserver.geoEngine.math.Vector3f;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.base.BaseLocation;
import org.typezero.gameserver.model.gameobjects.*;
import org.typezero.gameserver.model.gameobjects.player.PetCommonData;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.siege.SiegeNpc;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.model.gameobjects.state.CreatureVisualState;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.rift.RiftLocation;
import org.typezero.gameserver.model.siege.SiegeLocation;
import org.typezero.gameserver.model.siege.SiegeRace;
import org.typezero.gameserver.model.templates.VisibleObjectTemplate;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.model.templates.pet.PetTemplate;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.basespawns.BaseSpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.riftspawns.RiftSpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.vortexspawns.VortexSpawnTemplate;
import org.typezero.gameserver.model.vortex.VortexLocation;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import org.typezero.gameserver.services.*;
import org.typezero.gameserver.skillengine.effect.SummonOwner;
import org.typezero.gameserver.skillengine.model.SkillTemplate;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.idfactory.IDFactory;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.geo.GeoService;
import org.typezero.gameserver.world.knownlist.CreatureAwareKnownList;
import org.typezero.gameserver.world.knownlist.NpcKnownList;
import org.typezero.gameserver.world.knownlist.PlayerAwareKnownList;

/**
 * @author ATracer
 */
public class VisibleObjectSpawner {

	private static final Logger log = LoggerFactory.getLogger(VisibleObjectSpawner.class);

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @return
	 */
	protected static VisibleObject spawnNpc(SpawnTemplate spawn, int instanceIndex) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		if (npcTemplate == null) {
			log.error("No template for NPC " + String.valueOf(objectId));
			return null;
		}
		IDFactory iDFactory = IDFactory.getInstance();
		Npc npc = new Npc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
		npc.setCreatorId(spawn.getCreatorId());
		npc.setMasterName(spawn.getMasterName());
		npc.setKnownlist(new NpcKnownList(npc));
		npc.setEffectController(new EffectController(npc));

		if (WalkerFormator.getInstance().processClusteredNpc(npc, instanceIndex))
			return npc;

		try {
			SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		}
		catch (Exception ex) {
			log.error("Error during spawn of npc {}, world {}, x-y {}-{}",
					new Object[]{npcTemplate.getTemplateId(), spawn.getWorldId(), spawn.getX(), spawn.getY()});
			log.error("Npc {} will be despawned", npcTemplate.getTemplateId(), ex);
			World.getInstance().despawn(npc);
		}
		return npc;
	}

	public static SummonedHouseNpc spawnHouseNpc(SpawnTemplate spawn, int instanceIndex, House creator, String masterName) {
		int npcId = spawn.getNpcId();
		NpcTemplate template = DataManager.NPC_DATA.getNpcTemplate(npcId);
		SummonedHouseNpc npc = new SummonedHouseNpc(IDFactory.getInstance().nextId(), new NpcController(), spawn, template, creator, masterName);
		npc.setKnownlist(new PlayerAwareKnownList(npc));
		npc.setEffectController(new EffectController(npc));
		SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		return npc;
	}

	protected static VisibleObject spawnBaseNpc(BaseSpawnTemplate spawn, int instanceIndex) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);

		if (npcTemplate == null) {
			log.error("No template for Base / Base Katalam NPC " + String.valueOf(objectId));
			return null;
		}

		boolean isActive = BaseService.getInstance().isActive(spawn.getId());
		BaseLocation base = BaseService.getInstance().getBaseLocation(spawn.getId());

		if (!isActive && spawn.getBaseRace() != base.getRace()) {
			return null;
		}

		IDFactory iDFactory = IDFactory.getInstance();
		Npc npc = new Npc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
		npc.setKnownlist(new NpcKnownList(npc));
		npc.setEffectController(new EffectController(npc));
		SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		return npc;
	}

	protected static VisibleObject spawnRiftNpc(RiftSpawnTemplate spawn, int instanceIndex) {
		if (!CustomConfig.RIFT_ENABLED) {
			return null;
		}

		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		if (npcTemplate == null) {
			log.error("No template for NPC " + String.valueOf(objectId));
			return null;
		}
		IDFactory iDFactory = IDFactory.getInstance();
		Npc npc;

		int spawnId = spawn.getId();
		RiftLocation loc = RiftService.getInstance().getRiftLocation(spawnId);
		if (loc.isOpened() && spawnId == loc.getId()) {
			npc = new Npc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		}
		else {
			return null;
		}
		npc.setEffectController(new EffectController(npc));
		SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		return npc;
	}

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @return
	 */
	protected static VisibleObject spawnSiegeNpc(SiegeSpawnTemplate spawn, int instanceIndex) {
		if (!SiegeConfig.SIEGE_ENABLED)
			return null;

		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		if (npcTemplate == null) {
			log.error("No template for NPC " + String.valueOf(objectId));
			return null;
		}
		IDFactory iDFactory = IDFactory.getInstance();
		Npc npc = null;

		int spawnSiegeId = spawn.getSiegeId();
		SiegeLocation loc = SiegeService.getInstance().getSiegeLocation(spawnSiegeId);
		if ((spawn.isPeace() || loc.isVulnerable()) && spawnSiegeId == loc.getLocationId() && spawn.getSiegeRace() == loc.getRace()) {
			// default: GUARD
			npc = new SiegeNpc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		}
		else if (spawn.isAssault() && loc.isVulnerable() && spawn.getSiegeRace().equals(SiegeRace.BALAUR)) {
			// attakers
			npc = new SiegeNpc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		}
		else {
			return null;
		}
		npc.setEffectController(new EffectController(npc));
		SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		return npc;
	}

	protected static VisibleObject spawnInvasionNpc(VortexSpawnTemplate spawn, int instanceIndex) {
		if (!CustomConfig.VORTEX_ENABLED) {
			return null;
		}

		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		if (npcTemplate == null) {
			log.error("No template for NPC " + String.valueOf(objectId));
			return null;
		}
		IDFactory iDFactory = IDFactory.getInstance();
		Npc npc;

		int spawnId = spawn.getId();
		VortexLocation loc = VortexService.getInstance().getVortexLocation(spawnId);
		if (loc.isActive() && spawnId == loc.getId() && spawn.isInvasion()) {
			npc = new Npc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		}
		else if (!loc.isActive() && spawnId == loc.getId() && spawn.isPeace()) {
			npc = new Npc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		}
		else {
			return null;
		}
		npc.setEffectController(new EffectController(npc));
		SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		return npc;
	}

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @return
	 */
	protected static VisibleObject spawnGatherable(SpawnTemplate spawn, int instanceIndex) {
		int objectId = spawn.getNpcId();
		VisibleObjectTemplate template = DataManager.GATHERABLE_DATA.getGatherableTemplate(objectId);
		Gatherable gatherable = new Gatherable(spawn, template, IDFactory.getInstance().nextId(),
				new GatherableController());
		gatherable.setKnownlist(new PlayerAwareKnownList(gatherable));
		SpawnEngine.bringIntoWorld(gatherable, spawn, instanceIndex);
		return gatherable;
	}

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @param creator
	 * @return
	 */
	public static Trap spawnTrap(SpawnTemplate spawn, int instanceIndex, Creature creator, int skillId) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		Trap trap = new Trap(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate);
		trap.setKnownlist(new NpcKnownList(trap));
		trap.setEffectController(new EffectController(trap));
		trap.setCreator(creator);
		trap.getSkillList().addSkill(trap, skillId, 1);
		trap.setVisualState(CreatureVisualState.HIDE1);
		//set proper trap range
		trap.getAi2().onCustomEvent(1, DataManager.SKILL_DATA.getSkillTemplate(skillId).getProperties().getEffectiveRange());
		SpawnEngine.bringIntoWorld(trap, spawn, instanceIndex);
		PacketSendUtility.broadcastPacket(trap, new SM_PLAYER_STATE(trap));
		return trap;
	}

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @param creator
	 * @return
	 */
	public static GroupGate spawnGroupGate(SpawnTemplate spawn, int instanceIndex, Creature creator) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		GroupGate groupgate = new GroupGate(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate);
		groupgate.setKnownlist(new PlayerAwareKnownList(groupgate));
		groupgate.setEffectController(new EffectController(groupgate));
		groupgate.setCreator(creator);
		SpawnEngine.bringIntoWorld(groupgate, spawn, instanceIndex);
		return groupgate;
	}

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @param creator
	 * @return
	 */
	public static Kisk spawnKisk(SpawnTemplate spawn, int instanceIndex, Player creator) {
		int npcId = spawn.getNpcId();
		NpcTemplate template = DataManager.NPC_DATA.getNpcTemplate(npcId);
		Kisk kisk = new Kisk(IDFactory.getInstance().nextId(), new NpcController(), spawn, template, creator);
		kisk.setKnownlist(new PlayerAwareKnownList(kisk));
		kisk.setCreator(creator);
		kisk.setEffectController(new EffectController(kisk));
		SpawnEngine.bringIntoWorld(kisk, spawn, instanceIndex);
		return kisk;
	}

	/**
	 * @param owner
	 * @author ViAl Spawns postman for express mail
	 */
	public static Npc spawnPostman(final Player owner) {
		int npcId = owner.getRace() == Race.ELYOS ? 798100 : 798101;
		NpcData npcData = DataManager.NPC_DATA;
		NpcTemplate template = npcData.getNpcTemplate(npcId);
		IDFactory iDFactory = IDFactory.getInstance();
		int worldId = owner.getWorldId();
		int instanceId = owner.getInstanceId();
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(owner.getHeading()));
		Vector3f pos = GeoService.getInstance().getClosestCollision(owner,
				owner.getX() + (float) (Math.cos(radian) * 5),
				owner.getY() + (float) (Math.sin(radian) * 5),
				owner.getZ(), false, CollisionIntention.PHYSICAL.getId());
		SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, pos.getX(), pos.getY(), pos.getZ(), (byte) 0);
		final Npc postman = new Npc(iDFactory.nextId(), new NpcController(), spawn, template);
		postman.setKnownlist(new PlayerAwareKnownList(postman));
		postman.setEffectController(new EffectController(postman));
		postman.getAi2().onCustomEvent(1, owner);
		SpawnEngine.bringIntoWorld(postman, spawn, instanceId);
		owner.setPostman(postman);
		return postman;
	}


	public static Npc spawnFunctionalNpc(final Player owner, int npcId, SummonOwner summonOwner) {
		NpcData npcData = DataManager.NPC_DATA;
		NpcTemplate template = npcData.getNpcTemplate(npcId);
		IDFactory iDFactory = IDFactory.getInstance();
		int worldId = owner.getWorldId();
		int instanceId = owner.getInstanceId();
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(owner.getHeading()));
		Vector3f pos = GeoService.getInstance().getClosestCollision(owner,
				owner.getX() + (float) (Math.cos(radian) * 1),
				owner.getY() + (float) (Math.sin(radian) * 1),
				owner.getZ(), false, CollisionIntention.PHYSICAL.getId());
		SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, pos.getX(), pos.getY(), pos.getZ(), (byte) 0);
		final Npc functionalNpc = new Npc(iDFactory.nextId(), new NpcController(), spawn, template);
		functionalNpc.setKnownlist(new PlayerAwareKnownList(functionalNpc));
		functionalNpc.setEffectController(new EffectController(functionalNpc));
		functionalNpc.getAi2().onCustomEvent(1, owner);
		SpawnEngine.bringIntoWorld(functionalNpc, spawn, instanceId);
		return functionalNpc;
	}

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @param creator
	 * @param skillId
	 * @param level
	 * @return
	 */
	public static Servant spawnServant(SpawnTemplate spawn, int instanceIndex, Creature creator, int skillId, int level,
			NpcObjectType objectType) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		int creatureLevel = creator.getLevel();
		level = SkillLearnService.getSkillLearnLevel(skillId, creatureLevel, level);
		byte servantLevel = (byte) SkillLearnService.getSkillMinLevel(skillId, creatureLevel, level);

		Servant servant = new Servant(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate,
				servantLevel);
		servant.setKnownlist(new NpcKnownList(servant));
		servant.setEffectController(new EffectController(servant));
		servant.setCreator(creator);
		servant.setNpcObjectType(objectType);
		servant.getSkillList().addSkill(servant, skillId, 1);
		SpawnEngine.bringIntoWorld(servant, spawn, instanceIndex);
		SkillTemplate st = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (st.getStartconditions() != null && st.getHpCondition() != null) {
			int hp = (st.getHpCondition().getHpValue() * 3);
			servant.getLifeStats().setCurrentHp(hp);
		}
		return servant;
	}

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @param creator
	 * @param skillId
	 * @param level
	 * @return
	 */
	public static Servant spawnEnemyServant(SpawnTemplate spawn, int instanceIndex, Creature creator, byte servantLvl) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		Servant servant = new Servant(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate,
			servantLvl);
		servant.setKnownlist(new NpcKnownList(servant));
		servant.setEffectController(new EffectController(servant));
		servant.setCreator(creator);
		servant.setNpcObjectType(NpcObjectType.SERVANT);
		SpawnEngine.bringIntoWorld(servant, spawn, instanceIndex);
		return servant;
	}

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @param creator
	 * @param attackCount
	 * @return
	 */
	public static Homing spawnHoming(SpawnTemplate spawn, int instanceIndex, Creature creator, int attackCount,
			int skillId, int level, int homingSkillId) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		int creatureLevel = creator.getLevel();
		level = SkillLearnService.getSkillLearnLevel(skillId, creatureLevel, level);
		byte homingLevel = (byte) SkillLearnService.getSkillMinLevel(skillId, creatureLevel, level);
		Homing homing = new Homing(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate, homingLevel, skillId);
		homing.setState(CreatureState.WEAPON_EQUIPPED);
		homing.setKnownlist(new NpcKnownList(homing));
		homing.setEffectController(new EffectController(homing));
		homing.setCreator(creator);
		if (homingSkillId != 0)
			homing.getSkillList().addSkill(homing, homingSkillId, 1);
		homing.setActiveSkillId(homingSkillId);
		homing.setAttackCount(attackCount);
		SpawnEngine.bringIntoWorld(homing, spawn, instanceIndex);
		return homing;
	}

	/**
	 * @param creator
	 * @param npcId
	 * @param skillLevel
	 * @return
	 */
	public static Summon spawnSummon(Player creator, int npcId, int skillId, int skillLevel, int time) {
		float x = creator.getX();
		float y = creator.getY();
		float z = creator.getZ();
		byte heading = creator.getHeading();
		int worldId = creator.getWorldId();
		int instanceId = creator.getInstanceId();

		SpawnTemplate spawn = SpawnEngine.createSpawnTemplate(worldId, npcId, x, y, z, heading);
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);

		skillLevel = SkillLearnService.getSkillLearnLevel(skillId, creator.getCommonData().getLevel(), skillLevel);
		byte level = (byte) SkillLearnService.getSkillMinLevel(skillId, creator.getCommonData().getLevel(), skillLevel);
		boolean isSiegeWeapon = npcTemplate.getAi().equals("siege_weapon");
		Summon summon = new Summon(IDFactory.getInstance().nextId(), isSiegeWeapon ? new SiegeWeaponController(npcId)
				: new SummonController(), spawn, npcTemplate, isSiegeWeapon ? npcTemplate.getLevel() : level, time);
		summon.setKnownlist(new CreatureAwareKnownList(summon));
		summon.setEffectController(new EffectController(summon));
		summon.setMaster(creator);
		summon.getLifeStats().synchronizeWithMaxStats();

		SpawnEngine.bringIntoWorld(summon, spawn, instanceId);
		return summon;
	}

	/**
	 * @param player
	 * @param petId
	 * @return
	 */
	public static Pet spawnPet(Player player, int petId) {

		PetCommonData petCommonData = player.getPetList().getPet(petId);
		if (petCommonData == null) {
			return null;
		}
		PetTemplate petTemplate = DataManager.PET_DATA.getPetTemplate(petId);
		if (petTemplate == null)
			return null;

		PetController controller = new PetController();
		Pet pet = new Pet(petTemplate, controller, petCommonData, player);
		pet.setKnownlist(new PlayerAwareKnownList(pet));
		player.setToyPet(pet);

		float x = player.getX();
		float y = player.getY();
		float z = player.getZ();
		byte heading = player.getHeading();
		int worldId = player.getWorldId();
		int instanceId = player.getInstanceId();
		SpawnTemplate spawn = SpawnEngine.createSpawnTemplate(worldId, petId, x, y, z, heading);

		SpawnEngine.bringIntoWorld(pet, spawn, instanceId);
		return pet;
	}

}
