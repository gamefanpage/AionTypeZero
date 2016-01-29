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

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.configs.administration.AdminConfig;
import org.typezero.gameserver.configs.main.MembershipConfig;
import org.typezero.gameserver.configs.main.SecurityConfig;
import org.typezero.gameserver.controllers.FlyController;
import org.typezero.gameserver.controllers.PlayerController;
import org.typezero.gameserver.controllers.attack.AggroList;
import org.typezero.gameserver.controllers.attack.AttackStatus;
import org.typezero.gameserver.controllers.attack.PlayerAggroList;
import org.typezero.gameserver.controllers.effect.PlayerEffectController;
import org.typezero.gameserver.controllers.movement.PlayerMoveController;
import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.dao.PlayerVarsDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.CreatureType;
import org.typezero.gameserver.model.Gender;
import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TribeClass;
import org.typezero.gameserver.model.account.Account;
import org.typezero.gameserver.model.actions.PlayerActions;
import org.typezero.gameserver.model.actions.PlayerMode;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Kisk;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.Pet;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.gameobjects.SummonedObject;
import org.typezero.gameserver.model.gameobjects.Trap;
import org.typezero.gameserver.model.gameobjects.player.AbyssRank.AbyssRankUpdateType;
import org.typezero.gameserver.model.gameobjects.player.FriendList.Status;
import org.typezero.gameserver.model.gameobjects.player.emotion.EmotionList;
import org.typezero.gameserver.model.gameobjects.player.motion.MotionList;
import org.typezero.gameserver.model.gameobjects.player.npcFaction.NpcFactions;
import org.typezero.gameserver.model.gameobjects.player.title.TitleList;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.model.gameobjects.state.CreatureVisualState;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.house.HouseRegistry;
import org.typezero.gameserver.model.house.HouseStatus;
import org.typezero.gameserver.model.ingameshop.InGameShop;
import org.typezero.gameserver.model.items.ItemCooldown;
import org.typezero.gameserver.model.items.storage.IStorage;
import org.typezero.gameserver.model.items.storage.LegionStorageProxy;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.items.storage.StorageType;
import org.typezero.gameserver.model.skill.PlayerSkillList;
import org.typezero.gameserver.model.stats.container.PlayerGameStats;
import org.typezero.gameserver.model.stats.container.PlayerLifeStats;
import org.typezero.gameserver.model.team.legion.Legion;
import org.typezero.gameserver.model.team.legion.LegionMember;
import org.typezero.gameserver.model.team2.TeamMember;
import org.typezero.gameserver.model.team2.TemporaryPlayerTeam;
import org.typezero.gameserver.model.team2.alliance.PlayerAlliance;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceGroup;
import org.typezero.gameserver.model.team2.common.legacy.LootGroupRules;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.model.templates.BoundRadius;
import org.typezero.gameserver.model.templates.flypath.FlyPathEntry;
import org.typezero.gameserver.model.templates.item.ExtractedItemsCollection;
import org.typezero.gameserver.model.templates.item.ItemAttackType;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.item.ItemUseLimits;
import org.typezero.gameserver.model.templates.ride.RideInfo;
import org.typezero.gameserver.model.templates.stats.PlayerStatsTemplate;
import org.typezero.gameserver.model.templates.windstreams.WindstreamPath;
import org.typezero.gameserver.model.templates.zone.ZoneClassName;
import org.typezero.gameserver.model.templates.zone.ZoneType;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.HousingService;
import org.typezero.gameserver.services.serialkillers.SerialKiller;
import org.typezero.gameserver.skillengine.condition.ChainCondition;
import org.typezero.gameserver.skillengine.effect.AbnormalState;
import org.typezero.gameserver.skillengine.effect.EffectTemplate;
import org.typezero.gameserver.skillengine.effect.RebirthEffect;
import org.typezero.gameserver.skillengine.effect.ResurrectBaseEffect;
import org.typezero.gameserver.skillengine.model.ChainSkills;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.SkillTemplate;
import org.typezero.gameserver.skillengine.task.CraftingTask;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.rates.Rates;
import org.typezero.gameserver.utils.rates.RegularRates;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldPosition;
import org.typezero.gameserver.world.zone.ZoneInstance;
import java.sql.Timestamp;
import java.util.*;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * This class is representing Player object, it contains all needed data.
 *
 * @author -Nemesiss-
 * @author SoulKeeper
 * @author alexa026
 * @author cura
 */
public class Player extends Creature {

	public RideInfo ride;
	public InRoll inRoll;
	public InGameShop inGameShop;
	public WindstreamPath windstreamPath;
	private PlayerAppearance playerAppearance;
	private PlayerAppearance savedPlayerAppearance;
	private PlayerCommonData playerCommonData;
	private Account playerAccount;
	private LegionMember legionMember;

	private MacroList macroList;
	private PlayerSkillList skillList;
	private FriendList friendList;
	private BlockList blockList;
	private PetList toyPetList;
	private Mailbox mailbox;
	private PrivateStore store;
	private TitleList titleList;
	private QuestStateList questStateList;
	private RecipeList recipeList;
	private List<House> houses;

	private ResponseRequester requester;
	private boolean lookingForGroup = false;
	private Storage inventory;
	private Storage[] petBag = new Storage[StorageType.PET_BAG_MAX - StorageType.PET_BAG_MIN + 1];
	private Storage[] cabinets = new Storage[StorageType.HOUSE_WH_MAX - StorageType.HOUSE_WH_MIN + 1];
	private Storage regularWarehouse;
	private Storage accountWarehouse;
	private Equipment equipment;
	private HouseRegistry houseRegistry;

	private PlayerStatsTemplate playerStatsTemplate;
	private final AbsoluteStatOwner absStatsHolder;
	private PlayerSettings playerSettings;

	private org.typezero.gameserver.model.team2.group.PlayerGroup playerGroup2;
	private PlayerAllianceGroup playerAllianceGroup;

	private AbyssRank abyssRank;
	private NpcFactions npcFactions;
	private Rates rates;

	private int flyState = 0;
	private boolean isTrading;
	private long prisonTimer = 0;
	private long startPrison;
	private boolean invul;
	private FlyController flyController;
	private CraftingTask craftingTask;
	private int flightTeleportId;
	private int flightDistance;
	private Summon summon;
	private SummonedObject<?> summonedObj;
	private Pet toyPet;
	private Kisk kisk;
	private boolean isResByPlayer = false;
	private int resurrectionSkill = 0;
	private boolean isFlyingBeforeDeath = false;
	private boolean isGagged = false;
	private boolean edit_mode = false;
	private Npc postman = null;
	private boolean isInResurrectPosState = false;
	private float resPosX = 0;
	private float resPosY = 0;
	private float resPosZ = 0;
	private boolean underNoFPConsum = false;
	private boolean isAdminTeleportation = false;
	private boolean cooldownZero = false;
	private boolean isUnderInvulnerableWing = false;
	private boolean isFlying = false;
	private boolean isWispable = true;
	private boolean isCommandUsed = false;

	private int abyssRankListUpdateMask = 0;

	private BindPointPosition bindPoint;

	private Map<Integer, ItemCooldown> itemCoolDowns;
	private PortalCooldownList portalCooldownList;
	private CraftCooldownList craftCooldownList;
	private HouseObjectCooldownList houseObjectCooldownList;
	private long nextSkillUse;
	private long nextSummonSkillUse;
	private ChainSkills chainSkills;
	private Map<AttackStatus, Long> lastCounterSkill = new HashMap<AttackStatus, Long>();

	private int dualEffectValue = 0;
	/**
	 * Static information for players
	 */
	private static final int CUBE_SPACE = 9;
	private static final int WAREHOUSE_SPACE = 8;
	private boolean isAttackMode = false;

	private long gatherableTimer = 0;
	private long stopGatherable;
	private String captchaWord;
	private byte[] captchaImage;

	private float instanceStartPosX, instanceStartPosY, instanceStartPosZ;
	private int rebirthResurrectPercent = 1;
	private int rebirthSkill = 0;

	/**
	 * Connection of this Player.
	 */
	private AionConnection clientConnection;
	private FlyPathEntry flyLocationId;
	private long flyStartTime;

	private EmotionList emotions;
	private MotionList motions;
	private int partnerId;
    private String partnerName;
	private long flyReuseTime;

	private boolean isMentor;

	private long lastMsgTime = 0;
	private int floodMsgCount = 0;

	private long onlineTime = 0;

	private int lootingNpcOid;
	private boolean rebirthRevive;

	// Needed to remove supplements queue
	private int subtractedSupplementsCount;
	private int subtractedSupplementId;
	private int portAnimation;
	private boolean isInSprintMode;
	private List<ActionObserver> rideObservers;
	private SerialKiller skList;

	byte housingStatus = HousingFlags.BUY_STUDIO_ALLOWED.getId();
	private int battleReturnMap;
	private float[] battleReturnCoords;
	private FastMap<Integer, Object> tempStorage = FastMap.newInstance();
	//robot
	private int robotId = 0;
    private boolean isInRobotForm;
    private int itemEffectId = 1;

	/**
	 * Used for JUnit tests
	 */
	private Player(PlayerCommonData plCommonData) {
		super(plCommonData.getPlayerObjId(), new PlayerController(), null, plCommonData, null);
		this.playerCommonData = plCommonData;
		this.playerAccount = new Account(0);
		this.absStatsHolder = new AbsoluteStatOwner(this, 0);
	}

	public Player(PlayerController controller, PlayerCommonData plCommonData, PlayerAppearance appereance, Account account) {
		super(plCommonData.getPlayerObjId(), controller, null, plCommonData, plCommonData.getPosition());
		this.daoVars = DAOManager.getDAO(PlayerVarsDAO.class);
		this.playerCommonData = plCommonData;
		this.playerAppearance = appereance;
		this.playerAccount = account;

		this.requester = new ResponseRequester(this);
		this.questStateList = new QuestStateList();
		this.titleList = new TitleList();
		this.portalCooldownList = new PortalCooldownList(this);
		this.craftCooldownList = new CraftCooldownList(this);
		this.houseObjectCooldownList = new HouseObjectCooldownList(this);
		this.toyPetList = new PetList(this);
		controller.setOwner(this);
		moveController = new PlayerMoveController(this);
		plCommonData.setBoundingRadius(new BoundRadius(0.5f, 0.5f, getPlayerAppearance().getHeight()));

		setPlayerStatsTemplate(DataManager.PLAYER_STATS_DATA.getTemplate(this));
		setGameStats(new PlayerGameStats(this));
		setLifeStats(new PlayerLifeStats(this));
		inGameShop = new InGameShop();
		skList = new SerialKiller(this);
		absStatsHolder = new AbsoluteStatOwner(this, 0);
	}

	public boolean isInPlayerMode(PlayerMode mode) {
		return PlayerActions.isInPlayerMode(this, mode);
	}

	public void setPlayerMode(PlayerMode mode, Object obj) {
		PlayerActions.setPlayerMode(this, mode, obj);
	}

	public void unsetPlayerMode(PlayerMode mode) {
		PlayerActions.unsetPlayerMode(this, mode);
	}

	@Override
	public PlayerMoveController getMoveController() {
		return (PlayerMoveController) super.getMoveController();
	}

	@Override
	protected final AggroList createAggroList() {
		return new PlayerAggroList(this);
	}

	public PlayerCommonData getCommonData() {
		return playerCommonData;
	}

	@Override
	public String getName() {
		return playerCommonData.getName();
	}

	public PlayerAppearance getPlayerAppearance() {
		return playerAppearance;
	}

	public void setPlayerAppearance(PlayerAppearance playerAppearance) {
		this.playerAppearance = playerAppearance;
	}

	/**
	 * Only use for the Size admin command
	 *
	 * @return PlayerAppearance : The saved player's appearance, to rollback his appearance
	 */
	public PlayerAppearance getSavedPlayerAppearance() {
		return savedPlayerAppearance;
	}

	/**
	 * Only use for the Size admin command
	 *
	 * @param playerAppearance
	 *          PlayerAppearance : The saved player's appearance, to rollback his appearance
	 */
	public void setSavedPlayerAppearance(PlayerAppearance savedPlayerAppearance) {
		this.savedPlayerAppearance = savedPlayerAppearance;
	}

	/**
	 * Set connection of this player.
	 *
	 * @param clientConnection
	 */
	public void setClientConnection(AionConnection clientConnection) {
		this.clientConnection = clientConnection;
	}

	/**
	 * Get connection of this player.
	 *
	 * @return AionConnection of this player.
	 */
	public AionConnection getClientConnection() {
		return this.clientConnection;
	}

	public MacroList getMacroList() {
		return macroList;
	}

	public void setMacroList(MacroList macroList) {
		this.macroList = macroList;
	}

	public PlayerSkillList getSkillList() {
		return skillList;
	}

	public void setSkillList(PlayerSkillList skillList) {
		this.skillList = skillList;
	}

	/**
	 * @return the toyPet
	 */
	public Pet getPet() {
		return toyPet;
	}

	/**
	 * @param toyPet
	 *          the toyPet to set
	 */
	public void setToyPet(Pet toyPet) {
		this.toyPet = toyPet;
	}

	/**
	 * Gets this players Friend List
	 *
	 * @return FriendList
	 */
	public FriendList getFriendList() {
		return friendList;
	}

	/**
	 * Is this player looking for a group
	 *
	 * @return true or false
	 */
	public boolean isLookingForGroup() {
		return lookingForGroup;
	}

	/**
	 * Sets whether or not this player is looking for a group
	 *
	 * @param lookingForGroup
	 */
	public void setLookingForGroup(boolean lookingForGroup) {
		this.lookingForGroup = lookingForGroup;
	}

	public boolean isAttackMode() {
		return isAttackMode;
	}

	public void setAttackMode(boolean isAttackMode) {
		this.isAttackMode = isAttackMode;
	}

	public boolean isNotGatherable() {
		return gatherableTimer != 0;
	}

	public void setGatherableTimer(long gatherableTimer) {
		if (gatherableTimer < 0)
			gatherableTimer = 0;

		this.gatherableTimer = gatherableTimer;
	}

	public long getGatherableTimer() {
		return gatherableTimer;
	}

	public long getStopGatherable() {
		return stopGatherable;
	}

	public void setStopGatherable(long stopGatherable) {
		this.stopGatherable = stopGatherable;
	}

	public String getCaptchaWord() {
		return captchaWord;
	}

	public void setCaptchaWord(String captchaWord) {
		this.captchaWord = captchaWord;
	}

	public byte[] getCaptchaImage() {
		return captchaImage;
	}

	public void setCaptchaImage(byte[] captchaImage) {
		this.captchaImage = captchaImage;
	}

	/**
	 * Sets this players friend list. <br />
	 * Remember to send the player the <tt>SM_FRIEND_LIST</tt> packet.
	 *
	 * @param list
	 */
	public void setFriendList(FriendList list) {
		this.friendList = list;
	}

	public BlockList getBlockList() {
		return blockList;
	}

	public void setBlockList(BlockList list) {
		this.blockList = list;
	}

	public final PetList getPetList() {
		return toyPetList;
	}

	@Override
	public PlayerLifeStats getLifeStats() {
		return (PlayerLifeStats) super.getLifeStats();
	}

	@Override
	public PlayerGameStats getGameStats() {
		return (PlayerGameStats) super.getGameStats();
	}

	/**
	 * Gets the ResponseRequester for this player
	 *
	 * @return ResponseRequester
	 */
	public ResponseRequester getResponseRequester() {
		return requester;
	}

	public boolean isOnline() {
		return getClientConnection() != null;
	}

	public void setQuestExpands(int questExpands) {
		this.playerCommonData.setQuestExpands(questExpands);
		getInventory().setLimit(getInventory().getLimit() + (questExpands + getNpcExpands()) * CUBE_SPACE);
	}

	public int getQuestExpands() {
		return this.playerCommonData.getQuestExpands();
	}

	public void setNpcExpands(int npcExpands) {
		this.playerCommonData.setNpcExpands(npcExpands);
		getInventory().setLimit(getInventory().getLimit() + (npcExpands + getQuestExpands()) * CUBE_SPACE);
	}

	public int getNpcExpands() {
		return this.playerCommonData.getNpcExpands();
	}

	public PlayerClass getPlayerClass() {
		return playerCommonData.getPlayerClass();
	}

	public Gender getGender() {
		return playerCommonData.getGender();
	}

	/**
	 * Return PlayerController of this Player Object.
	 *
	 * @return PlayerController.
	 */
	@Override
	public PlayerController getController() {
		return (PlayerController) super.getController();
	}

	@Override
	public byte getLevel() {
		return (byte) playerCommonData.getLevel();
	}

	/**
	 * @return the inventory
	 */

	public Equipment getEquipment() {
		return equipment;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	/**
	 * @return the player private store
	 */
	public PrivateStore getStore() {
		return store;
	}

	/**
	 * @param store
	 *          the store that needs to be set
	 */
	public void setStore(PrivateStore store) {
		this.store = store;
	}

	/**
	 * @return the questStatesList
	 */
	public QuestStateList getQuestStateList() {
		return questStateList;
	}

	/**
	 * @param questStateList
	 *          the QuestStateList to set
	 */
	public void setQuestStateList(QuestStateList questStateList) {
		this.questStateList = questStateList;
	}

	/**
	 * @return the playerStatsTemplate
	 */
	public PlayerStatsTemplate getPlayerStatsTemplate() {
		return playerStatsTemplate;
	}

	/**
	 * @param playerStatsTemplate
	 *          the playerStatsTemplate to set
	 */
	public void setPlayerStatsTemplate(PlayerStatsTemplate playerStatsTemplate) {
		this.playerStatsTemplate = playerStatsTemplate;
	}

	public RecipeList getRecipeList() {
		return recipeList;
	}

	public void setRecipeList(RecipeList recipeList) {
		this.recipeList = recipeList;
	}

	/**
	 * @param inventory
	 *          the inventory to set Inventory should be set right after player object is created
	 */
	public void setStorage(Storage storage, StorageType storageType) {
		if (storageType == StorageType.CUBE) {
			this.inventory = storage;
		}
		if (storageType.getId() >= StorageType.PET_BAG_MIN && storageType.getId() <= StorageType.PET_BAG_MAX) {
			this.petBag[storageType.getId() - StorageType.PET_BAG_MIN] = storage;
		}
		if (storageType.getId() >= StorageType.HOUSE_WH_MIN && storageType.getId() <= StorageType.HOUSE_WH_MAX) {
			this.cabinets[storageType.getId() - StorageType.HOUSE_WH_MIN] = storage;
		}
		if (storageType == StorageType.REGULAR_WAREHOUSE) {
			this.regularWarehouse = storage;
		}
		if (storageType == StorageType.ACCOUNT_WAREHOUSE) {
			this.accountWarehouse = storage;
		}
		storage.setOwner(this);
	}

	/**
	 * @param storageType
	 * @return
	 */
	public IStorage getStorage(int storageType) {
		if (storageType == StorageType.REGULAR_WAREHOUSE.getId())
			return regularWarehouse;

		if (storageType == StorageType.ACCOUNT_WAREHOUSE.getId())
			return accountWarehouse;

		if (storageType == StorageType.LEGION_WAREHOUSE.getId() && getLegion() != null) {
			return new LegionStorageProxy(getLegion().getLegionWarehouse(), this);
		}

		if (storageType >= StorageType.PET_BAG_MIN && storageType <= StorageType.PET_BAG_MAX)
			return petBag[storageType - StorageType.PET_BAG_MIN];

		if (storageType >= StorageType.HOUSE_WH_MIN && storageType <= StorageType.HOUSE_WH_MAX)
			return cabinets[storageType - StorageType.HOUSE_WH_MIN];

		if (storageType == StorageType.CUBE.getId())
			return inventory;
		return null;
	}

	/**
	 * Items from UPDATE_REQUIRED storages and equipment
	 *
	 * @return
	 */
	public List<Item> getDirtyItemsToUpdate() {
		List<Item> dirtyItems = new ArrayList<Item>();

		IStorage cubeStorage = getStorage(StorageType.CUBE.getId());
		if (cubeStorage.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
			dirtyItems.addAll(cubeStorage.getItemsWithKinah());
			dirtyItems.addAll(cubeStorage.getDeletedItems());
			cubeStorage.setPersistentState(PersistentState.UPDATED);
		}

		IStorage regularWhStorage = getStorage(StorageType.REGULAR_WAREHOUSE.getId());
		if (regularWhStorage.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
			dirtyItems.addAll(regularWhStorage.getItemsWithKinah());
			dirtyItems.addAll(regularWhStorage.getDeletedItems());
			regularWhStorage.setPersistentState(PersistentState.UPDATED);
		}

		IStorage accountWhStorage = getStorage(StorageType.ACCOUNT_WAREHOUSE.getId());
		if (accountWhStorage.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
			dirtyItems.addAll(accountWhStorage.getItemsWithKinah());
			dirtyItems.addAll(accountWhStorage.getDeletedItems());
			accountWhStorage.setPersistentState(PersistentState.UPDATED);
		}

		IStorage legionWhStorage = getStorage(StorageType.LEGION_WAREHOUSE.getId());
		if (legionWhStorage != null) {
			if (legionWhStorage.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
				dirtyItems.addAll(legionWhStorage.getItemsWithKinah());
				dirtyItems.addAll(legionWhStorage.getDeletedItems());
				legionWhStorage.setPersistentState(PersistentState.UPDATED);
			}
		}

		for (int petBagId = StorageType.PET_BAG_MIN; petBagId <= StorageType.PET_BAG_MAX; petBagId++) {
			IStorage petBag = getStorage(petBagId);
			if (petBag != null && petBag.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
				dirtyItems.addAll(petBag.getItemsWithKinah());
				dirtyItems.addAll(petBag.getDeletedItems());
				petBag.setPersistentState(PersistentState.UPDATED);
			}
		}

		for (int houseWhId = StorageType.HOUSE_WH_MIN; houseWhId <= StorageType.HOUSE_WH_MAX; houseWhId++) {
			IStorage cabinet = getStorage(houseWhId);
			if (cabinet != null && cabinet.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
				dirtyItems.addAll(cabinet.getItemsWithKinah());
				dirtyItems.addAll(cabinet.getDeletedItems());
				cabinet.setPersistentState(PersistentState.UPDATED);
			}
		}

		Equipment equipment = getEquipment();
		if (equipment.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
			dirtyItems.addAll(equipment.getEquippedItems());
			equipment.setPersistentState(PersistentState.UPDATED);
		}

		return dirtyItems;
	}

	/**
	 * //TODO probably need to optimize here
	 *
	 * @return
	 */
	public FastList<Item> getAllItems() {
		FastList<Item> items = FastList.newInstance();
		items.addAll(this.inventory.getItemsWithKinah());
		if (this.regularWarehouse != null)
			items.addAll(this.regularWarehouse.getItemsWithKinah());
		if (this.accountWarehouse != null)
			items.addAll(this.accountWarehouse.getItemsWithKinah());

		for (int petBagId = StorageType.PET_BAG_MIN; petBagId <= StorageType.PET_BAG_MAX; petBagId++) {
			IStorage petBag = getStorage(petBagId);
			if (petBag != null)
				items.addAll(petBag.getItemsWithKinah());
		}

		for (int houseWhId = StorageType.HOUSE_WH_MIN; houseWhId <= StorageType.HOUSE_WH_MAX; houseWhId++) {
			IStorage cabinet = getStorage(houseWhId);
			if (cabinet != null)
				items.addAll(cabinet.getItemsWithKinah());
		}

		items.addAll(getEquipment().getEquippedItems());
		return items;
	}

	public Storage getInventory() {
		return inventory;
	}

	/**
	 * @return the playerSettings
	 */
	public PlayerSettings getPlayerSettings() {
		return playerSettings;
	}

	/**
	 * @param playerSettings
	 *          the playerSettings to set
	 */
	public void setPlayerSettings(PlayerSettings playerSettings) {
		this.playerSettings = playerSettings;
	}

	public TitleList getTitleList() {
		return titleList;
	}

	public void setTitleList(TitleList titleList) {
		if (havePermission(MembershipConfig.TITLES_ADDITIONAL_ENABLE)) {
			titleList.addEntry(102, 0);
			titleList.addEntry(103, 0);
			titleList.addEntry(104, 0);
			titleList.addEntry(105, 0);
			titleList.addEntry(106, 0);
			titleList.addEntry(146, 0);
			titleList.addEntry(151, 0);
			titleList.addEntry(152, 0);
			titleList.addEntry(160, 0);
			titleList.addEntry(161, 0);
		}
		this.titleList = titleList;
		titleList.setOwner(this);
	}

	public org.typezero.gameserver.model.team2.group.PlayerGroup getPlayerGroup2() {
		return playerGroup2;
	}

	public void setPlayerGroup2(org.typezero.gameserver.model.team2.group.PlayerGroup playerGroup) {
		this.playerGroup2 = playerGroup;
	}

	/**
	 * @return the abyssRank
	 */
	public AbyssRank getAbyssRank() {
		return abyssRank;
	}

	/**
	 * @param abyssRank
	 *          the abyssRank to set
	 */
	public void setAbyssRank(AbyssRank abyssRank) {
		this.abyssRank = abyssRank;
	}

	@Override
	public PlayerEffectController getEffectController() {
		return (PlayerEffectController) super.getEffectController();
	}

	public void onLoggedIn() {
		friendList.setStatus(Status.ONLINE, getCommonData());
	}

	public void onLoggedOut() {
		requester.denyAll();
		friendList.setStatus(FriendList.Status.OFFLINE, getCommonData());
	}

	/**
	 * Returns true if has valid LegionMember
	 */
	public boolean isLegionMember() {
		return legionMember != null;
	}

	/**
	 * @param legionMember
	 *          the legionMember to set
	 */
	public void setLegionMember(LegionMember legionMember) {
		this.legionMember = legionMember;
	}

	/**
	 * @return the legionMember
	 */
	public LegionMember getLegionMember() {
		return legionMember;
	}

	/**
	 * @return the legion
	 */
	public Legion getLegion() {
		return legionMember != null ? legionMember.getLegion() : null;
	}

	/**
	 * Checks if object id's are the same
	 *
	 * @return true if the object id is the same
	 */
	public boolean sameObjectId(int objectId) {
		return this.getObjectId() == objectId;
	}

	/**
	 * @return true if a player has a store opened
	 */
	public boolean hasStore() {
		if (getStore() != null)
			return true;
		return false;
	}

	/**
	 * Removes legion from player
	 */
	public void resetLegionMember() {
		setLegionMember(null);
	}

	public boolean isInGroup2() {
		return playerGroup2 != null;
	}

	/**
	 * Access level of this player
	 *
	 * @return byte
	 */
	public byte getAccessLevel() {
		return playerAccount.getAccessLevel();
	}

	/**
	 * accountName of this player
	 *
	 * @return int
	 */
	public String getAcountName() {
		return playerAccount.getName();
	}

	/**
	 * @return the rates
	 */
	public Rates getRates() {
		if (rates == null)
			rates = new RegularRates();
		return rates;
	}

	/**
	 * @param rates
	 *          the rates to set
	 */
	public void setRates(Rates rates) {
		this.rates = rates;
	}

	/**
	 * @return warehouse size
	 */
	public int getWarehouseSize() {
		return this.playerCommonData.getWarehouseSize();
	}

	/**
	 * @param warehouseSize
	 */
	public void setWarehouseSize(int warehouseSize) {
		this.playerCommonData.setWarehouseSize(warehouseSize);
		getWarehouse().setLimit(getWarehouse().getLimit() + (warehouseSize * WAREHOUSE_SPACE));
	}

	/**
	 * @return regularWarehouse
	 */
	public Storage getWarehouse() {
		return regularWarehouse;
	}

	/**
	 * 0: regular, 1: fly, 2: glide
	 */
	public int getFlyState() {
		return this.flyState;
	}

	public void setFlyState(int flyState) {
		this.flyState = flyState;
		if (flyState == 1)
			setFlyingMode(true);
		else if (flyState == 0)
			setFlyingMode(false);
	}

	/**
	 * @return the isTrading
	 */
	public boolean isTrading() {
		return isTrading;
	}

	/**
	 * @param isTrading
	 *          the isTrading to set
	 */
	public void setTrading(boolean isTrading) {
		this.isTrading = isTrading;
	}

	/**
	 * @return the isInPrison
	 */
	public boolean isInPrison() {
		return prisonTimer != 0;
	}

	/**
	 * @param prisonTimer
	 *          the prisonTimer to set
	 */
	public void setPrisonTimer(long prisonTimer) {
		if (prisonTimer < 0)
			prisonTimer = 0;

		this.prisonTimer = prisonTimer;
	}

	/**
	 * @return the prisonTimer
	 */
	public long getPrisonTimer() {
		return prisonTimer;
	}

	/**
	 * @return the time in ms of start prison
	 */
	public long getStartPrison() {
		return startPrison;
	}

	/**
	 * @param start
	 *          : The time in ms of start prison
	 */
	public void setStartPrison(long start) {
		this.startPrison = start;
	}

	/**
	 * @return
	 */
	public boolean isProtectionActive() {
		return isInVisualState(CreatureVisualState.BLINKING);
	}

	/**
	 * Check is player is invul
	 *
	 * @return boolean
	 **/
	public boolean isInvul() {
		return invul;
	}

	/**
	 * Sets invul on player
	 *
	 * @param invul
	 *          - boolean
	 **/
	public void setInvul(boolean invul) {
		this.invul = invul;
	}

	public void setMailbox(Mailbox mailbox) {
		this.mailbox = mailbox;
	}

	public Mailbox getMailbox() {
		return mailbox;
	}

	/**
	 * @return the flyController
	 */
	public FlyController getFlyController() {
		return flyController;
	}

	/**
	 * @param flyController
	 *          the flyController to set
	 */
	public void setFlyController(FlyController flyController) {
		this.flyController = flyController;
	}

	public int getLastOnline() {
		Timestamp lastOnline = playerCommonData.getLastOnline();
		if (lastOnline == null || isOnline())
			return 0;

		return (int) (lastOnline.getTime() / 1000);
	}

	/**
	 * @param craftingTask
	 */
	public void setCraftingTask(CraftingTask craftingTask) {
		this.craftingTask = craftingTask;
	}

	/**
	 * @return
	 */
	public CraftingTask getCraftingTask() {
		return craftingTask;
	}

	/**
	 * @param flightTeleportId
	 */
	public void setFlightTeleportId(int flightTeleportId) {
		this.flightTeleportId = flightTeleportId;
	}

	/**
	 * @return flightTeleportId
	 */
	public int getFlightTeleportId() {
		return flightTeleportId;
	}

	/**
	 * @param flightDistance
	 */
	public void setFlightDistance(int flightDistance) {
		this.flightDistance = flightDistance;

	}

	/**
	 * @param path
	 */
	public void setCurrentFlypath(FlyPathEntry path) {
		this.flyLocationId = path;
		if (path != null)
			this.flyStartTime = System.currentTimeMillis();
		else
			this.flyStartTime = 0;
	}

	/**
	 * @return flightDistance
	 */
	public int getFlightDistance() {
		return flightDistance;
	}

	/**
	 * @return
	 */
	public boolean isUsingFlyTeleport() {
		return isInState(CreatureState.FLIGHT_TELEPORT) && flightTeleportId != 0;
	}

	public boolean isGM() {
		return getAccessLevel() >= AdminConfig.GM_LEVEL;
	}

	@Override
	public boolean isEnemy(Creature creature) {
		return creature.isEnemyFrom(this) || this.isEnemyFrom(creature);
	}

	@Override
	public boolean isEnemyFrom(Npc enemy) {
		switch (CreatureType.getCreatureType(enemy.getType(this))) {
			case AGGRESSIVE:
			case ATTACKABLE:
				return true;
		}
		return false;
	}

	/**
	 * Player enemies:<br>
	 * - different race<br>
	 * - duel partner<br>
	 * - in pvp zone
	 *
	 * @param enemy
	 * @return
	 */
	@Override
	public boolean isEnemyFrom(Player enemy) {
			if(this.getObjectId() == enemy.getObjectId())
				return false;
			else if((this.getAdminEnmity() > 1 || enemy.getAdminEnmity() > 1))
				return false;
			else if(canPvP(enemy) || this.getController().isDueling(enemy))
				return true;
			else
				return false;
	}

	public boolean isAggroIconTo(Player player) {
		if (getAdminEnmity() > 1 || player.getAdminEnmity() > 1)
			return true;
		return !player.getRace().equals(getRace());
	}

	private boolean canPvP(Player enemy) {
		int worldId = enemy.getWorldId();
		if (!enemy.getRace().equals(getRace())) {
		   if (World.getInstance().getWorldMap(getWorldId()).isPvpAllowed()) {
			  return (!this.isInDisablePvPZone() && !enemy.isInDisablePvPZone());
		   }
		   else
			  return (this.isInPvPZone() && enemy.isInPvPZone());
		}
		else {
			if (worldId != 600020000 && worldId != 600030000 && worldId != 600050000 && worldId != 600060000) {
				return (this.isInsideZoneType(ZoneType.PVP) && enemy.isInsideZoneType(ZoneType.PVP) && !isInSameTeam(enemy));
			}
		}

		return false;
	}

	public boolean isInDisablePvPZone() {
        if (this.getPosition().getMapRegion() != null) {
            List<ZoneInstance> zones = this.getPosition().getMapRegion().getZones(this);
            for (ZoneInstance zone : zones) {
                if (!zone.isPvpAllowed()) {
                    return true;
                }
            }
        }
		return false;
	}

    public boolean isInLimitArea() {
        if (this.getPosition().getMapRegion() != null) {
            List<ZoneInstance> zones = this.getPosition().getMapRegion().getZones(this);
            for (ZoneInstance zone : zones) {
                if (zone.getZoneTemplate().getZoneType().equals(ZoneClassName.LIMIT)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInArtifactArea() {
        if (this.getPosition().getMapRegion() != null) {
            List<ZoneInstance> zones = this.getPosition().getMapRegion().getZones(this);
            for (ZoneInstance zone : zones) {
                if (zone.getZoneTemplate().getZoneType().equals(ZoneClassName.ARTIFACT)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInFortArea() {
        if (this.getPosition().getMapRegion() != null) {
            List<ZoneInstance> zones = this.getPosition().getMapRegion().getZones(this);
            for (ZoneInstance zone : zones) {
                if (zone.getZoneTemplate().getZoneType().equals(ZoneClassName.FORT)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInPvPZone() {
		List<ZoneInstance> zones = this.getPosition().getMapRegion().getZones(this);
		for (ZoneInstance zone : zones) {
			if (zone.isPvpAllowed()) {
				return true;
			}
		}

		return false;
	}

	public boolean isInSameTeam(Player player) {
		if (isInGroup2() && player.isInGroup2()) {
			return getPlayerGroup2().getTeamId().equals(player.getPlayerGroup2().getTeamId());
		}
		else if (isInAlliance2() && player.isInAlliance2()) {
			return getPlayerAlliance2().getObjectId().equals(player.getPlayerAlliance2().getObjectId());
		}
		else if (isInLeague() && player.isInLeague()) {
			return getPlayerAllianceGroup2().getObjectId().equals(player.getPlayerAllianceGroup2().getObjectId());
		}
		return false;
	}

	@Override
	public boolean canSee(Creature creature) {
		if (creature.isInVisualState(CreatureVisualState.BLINKING))
			return true;

		if (creature instanceof Player && isInSameTeam((Player) creature))
			return true;

		if (creature instanceof Trap && ((Trap) creature).getCreator().getObjectId() == this.getObjectId())
			return true;

		return creature.getVisualState() <= getSeeState();
	}

	@Override
	public TribeClass getTribe() {
		TribeClass transformTribe = getTransformModel().getTribe();
		if (transformTribe != null) {
			return transformTribe;
		}
		return getRace() == Race.ELYOS ? TribeClass.PC : TribeClass.PC_DARK;
	}

	@Override
	public TribeClass getBaseTribe() {
		TribeClass transformTribe = getTransformModel().getTribe();
		if (transformTribe != null) {
			 return DataManager.TRIBE_RELATIONS_DATA.getBaseTribe(transformTribe);
		}
		return getTribe();
	}

	/**
	 * @return the summon
	 */
	public Summon getSummon() {
		return summon;
	}

	/**
	 * @param summon
	 *          the summon to set
	 */
	public void setSummon(Summon summon) {
		this.summon = summon;
	}

	/**
	 * @return the summoned object
	 */
	public SummonedObject<?> getSummonedObj() {
		return summonedObj;
	}

	/**
	 * @param summonedObj
	 *          the summoned object to set
	 */
	public void setSummonedObj(SummonedObject<?> summonedObj) {
		this.summonedObj = summonedObj;
	}

	/**
	 * @param new kisk to bind to (null if unbinding)
	 */
	public void setKisk(Kisk newKisk) {
		this.kisk = newKisk;
	}

	/**
	 * @return
	 */
	public Kisk getKisk() {
		return this.kisk;
	}

	/**
	 * @param delayId
	 * @return
	 */
	public boolean isItemUseDisabled(ItemUseLimits limits) {
		if (limits == null)
			return false;

		if (itemCoolDowns == null || !itemCoolDowns.containsKey(limits.getDelayId()))
			return false;

		Long coolDown = itemCoolDowns.get(limits.getDelayId()).getReuseTime();
		if (coolDown == null)
			return false;

		if (coolDown < System.currentTimeMillis()) {
			itemCoolDowns.remove(limits.getDelayId());
			return false;
		}

		return true;
	}

	/**
	 * @param delayId
	 * @return
	 */
	public long getItemCoolDown(int delayId) {
		if (itemCoolDowns == null || !itemCoolDowns.containsKey(delayId))
			return 0;

		return itemCoolDowns.get(delayId).getReuseTime();
	}

	/**
	 * @return the itemCoolDowns
	 */
	public Map<Integer, ItemCooldown> getItemCoolDowns() {
		return itemCoolDowns;
	}

	/**
	 * @param delayId
	 * @param time
	 * @param useDelay
	 */
	public void addItemCoolDown(int delayId, long time, int useDelay) {
		if (itemCoolDowns == null)
			itemCoolDowns = new FastMap<Integer, ItemCooldown>().shared();

		itemCoolDowns.put(delayId, new ItemCooldown(time, useDelay));
	}

	/**
	 * @param itemMask
	 */
	public void removeItemCoolDown(int itemMask) {
		if (itemCoolDowns == null)
			return;
		itemCoolDowns.remove(itemMask);
	}

	/**
	 * @param isGagged
	 *          the isGagged to set
	 */
	public void setGagged(boolean isGagged) {
		this.isGagged = isGagged;
	}

	/**
	 * @return the isGagged
	 */
	public boolean isGagged() {
		if (getVar("chatgag") != null) {
			if (Long.parseLong(getVar("chatgag").toString()) != 0 && System.currentTimeMillis() <= Long.parseLong(getVar("chatgag").toString())) {
				isGagged = true;
				PacketSendUtility.sendMessage(this, "Time to chat ban ends: " + new Date(Long.parseLong(getVar("chatgag").toString())));
				return isGagged;
			}
			if (Long.parseLong(getVar("chatgag").toString()) != 0 && System.currentTimeMillis() >= Long.parseLong(getVar("chatgag").toString())) {
				isGagged = false;
				delVar("chatgag", true);
			}
		}
		return isGagged;
	}

	/**
	 * @return isAdminTeleportation
	 */
	public boolean getAdminTeleportation() {
		return isAdminTeleportation;
	}

	/**
	 * @param isAdminTeleportation
	 */
	public void setAdminTeleportation(boolean isAdminTeleportation) {
		this.isAdminTeleportation = isAdminTeleportation;
	}

	public final boolean isCoolDownZero() {
		return cooldownZero;
	}

	public final void setCoolDownZero(boolean cooldownZero) {
		this.cooldownZero = cooldownZero;
	}

	public void setPlayerResActivate(boolean isActivated) {
		this.isResByPlayer = isActivated;
	}

	public boolean getResStatus() {
		return isResByPlayer;
	}

	public int getResurrectionSkill() {
		return resurrectionSkill;
	}

	public void setResurrectionSkill(int resurrectionSkill) {
		this.resurrectionSkill = resurrectionSkill;
	}

	public void setIsFlyingBeforeDeath(boolean isActivated) {
		this.isFlyingBeforeDeath = isActivated;
	}

	public boolean getIsFlyingBeforeDeath() {
		return isFlyingBeforeDeath;
	}

	public org.typezero.gameserver.model.team2.alliance.PlayerAlliance getPlayerAlliance2() {
		return playerAllianceGroup != null ? playerAllianceGroup.getAlliance() : null;
	}

	public PlayerAllianceGroup getPlayerAllianceGroup2() {
		return playerAllianceGroup;
	}

	public boolean isInAlliance2() {
		return playerAllianceGroup != null;
	}

	public void setPlayerAllianceGroup2(PlayerAllianceGroup playerAllianceGroup) {
		this.playerAllianceGroup = playerAllianceGroup;
	}

	public final boolean isInLeague() {
		return isInAlliance2() && getPlayerAlliance2().isInLeague();
	}

	public final boolean isInTeam() {
		return isInGroup2() || isInAlliance2();
	}

	/**
	 * @return current {@link PlayerGroup}, {@link PlayerAlliance} or null
	 */
	public final TemporaryPlayerTeam<? extends TeamMember<Player>> getCurrentTeam() {
		return isInGroup2() ? getPlayerGroup2() : getPlayerAlliance2();
	}

	/**
	 * @return current {@link PlayerGroup}, {@link PlayerAllianceGroup} or null
	 */
	public final TemporaryPlayerTeam<? extends TeamMember<Player>> getCurrentGroup() {
		return isInGroup2() ? getPlayerGroup2() : getPlayerAllianceGroup2();
	}

	/**
	 * @return current team id
	 */
	public final int getCurrentTeamId() {
		return isInTeam() ? getCurrentTeam().getTeamId() : 0;
	}

	/**
	 * @param worldId
	 * @return
	 */
	public PortalCooldownList getPortalCooldownList() {
		return portalCooldownList;
	}

	public CraftCooldownList getCraftCooldownList() {
		return craftCooldownList;
	}

	public HouseObjectCooldownList getHouseObjectCooldownList() {
		return houseObjectCooldownList;
	}

	public SerialKiller getSKInfo() {
		return skList;
	}

	public void setSKInfo(SerialKiller serialKiller) {
		skList = serialKiller;
	}

	/**
	 * @author IlBuono
	 */
	public void setEditMode(boolean edit_mode) {
		this.edit_mode = edit_mode;
	}

	/**
	 * @author IlBuono
	 */
	public boolean isInEditMode() {
		return edit_mode;
	}

	public Npc getPostman() {
		return postman;
	}

	public void setPostman(Npc postman) {
		this.postman = postman;
	}

	public Account getPlayerAccount() {
		return playerAccount;
	}

	/**
	 * Quest completion
	 *
	 * @param questId
	 * @return
	 */
	public boolean isCompleteQuest(int questId) {
		QuestState qs = getQuestStateList().getQuestState(questId);

		if (qs == null)
			return false;

		return qs.getStatus() == QuestStatus.COMPLETE;
	}

	public long getNextSkillUse() {
		return nextSkillUse;
	}

	public void setNextSkillUse(long nextSkillUse) {
		this.nextSkillUse = nextSkillUse;
	}

	public long getNextSummonSkillUse() {
		return nextSummonSkillUse;
	}

	public void setNextSummonSkillUse(long nextSummonSkillUse) {
		this.nextSummonSkillUse = nextSummonSkillUse;
	}

	/**
	 * chain skills
	 */
	public ChainSkills getChainSkills() {
		if (this.chainSkills == null)
			this.chainSkills = new ChainSkills();
		return this.chainSkills;
	}

	public void setLastCounterSkill(AttackStatus status) {
		long time = System.currentTimeMillis();
		if (AttackStatus.getBaseStatus(status) == AttackStatus.DODGE && PlayerClass.getStartingClassFor(getPlayerClass()) == PlayerClass.SCOUT) {
			this.lastCounterSkill.put(AttackStatus.DODGE, time);
		}
		else if (AttackStatus.getBaseStatus(status) == AttackStatus.PARRY
			&& (getPlayerClass() == PlayerClass.GLADIATOR || getPlayerClass() == PlayerClass.CHANTER)) {
			this.lastCounterSkill.put(AttackStatus.PARRY, time);
		}
		else if (AttackStatus.getBaseStatus(status) == AttackStatus.BLOCK
			&& PlayerClass.getStartingClassFor(getPlayerClass()) == PlayerClass.WARRIOR) {
			this.lastCounterSkill.put(AttackStatus.BLOCK, time);
		}
	}

	public long getLastCounterSkill(AttackStatus status) {
		if (this.lastCounterSkill.get(status) == null)
			return 0;

		return this.lastCounterSkill.get(status);
	}

	/**
	 * @return the dualEffectValue
	 */
	public int getDualEffectValue() {
		return dualEffectValue;
	}

	/**
	 * @param dualEffectValue
	 *          the dualEffectValue to set
	 */
	public void setDualEffectValue(int dualEffectValue) {
		this.dualEffectValue = dualEffectValue;
	}

	/**
	 * @return the Resurrection Positional State
	 */
	public boolean isInResPostState() {
		return this.isInResurrectPosState;
	}

	/**
	 * @param the
	 *          Resurrection Positional State to set
	 */
	public void setResPosState(boolean value) {
		this.isInResurrectPosState = value;
	}

	/**
	 * @param the
	 *          Resurrection Positional X value to set
	 */
	public void setResPosX(float value) {
		this.resPosX = value;
	}

	/**
	 * @return the Resurrection Positional X value
	 */
	public float getResPosX() {
		return this.resPosX;
	}

	/**
	 * @param the
	 *          Resurrection Positional Y value to set
	 */
	public void setResPosY(float value) {
		this.resPosY = value;
	}

	/**
	 * @return the Resurrection Positional Y value
	 */
	public float getResPosY() {
		return this.resPosY;
	}

	/**
	 * @param the
	 *          Resurrection Positional Z value to set
	 */
	public void setResPosZ(float value) {
		this.resPosZ = value;
	}

	/**
	 * @return the Resurrection Positional Z value
	 */
	public float getResPosZ() {
		return this.resPosZ;
	}

	public boolean isInSiegeWorld() {
		switch (getWorldId()) {
			case 210050000:
			case 220070000:
			case 400010000:
			case 600030000:
			case 600050000:
			case 600060000:
				return true;
			default:
				return false;
		}
	}

	/**
	 * @return true if player is under NoFly Effect
	 */
	public boolean isUnderNoFly() {
		return this.getEffectController().isAbnormalSet(AbnormalState.NOFLY);
	}

	/**
	 * @param the
	 *          status of NoFpConsum Effect
	 */
	public void setUnderNoFPConsum(boolean value) {
		this.underNoFPConsum = value;
	}

	/**
	 * @return true if player is under NoFpConsumEffect
	 */
	public boolean isUnderNoFPConsum() {
		return this.underNoFPConsum;
	}

	public void setInstanceStartPos(float instanceStartPosX, float instanceStartPosY, float instanceStartPosZ) {
		this.instanceStartPosX = instanceStartPosX;
		this.instanceStartPosY = instanceStartPosY;
		this.instanceStartPosZ = instanceStartPosZ;
	}

	public float getInstanceStartPosX() {
		return instanceStartPosX;
	}

	public float getInstanceStartPosY() {
		return instanceStartPosY;
	}

	public float getInstanceStartPosZ() {
		return instanceStartPosZ;
	}

	public boolean havePermission(byte perm) {
		return playerAccount.getMembership() >= perm;
	}

	/**
	 * @return Returns the emotions.
	 */
	public EmotionList getEmotions() {
		return emotions;
	}

	/**
	 * @param emotions
	 *          The emotions to set.
	 */
	public void setEmotions(EmotionList emotions) {
		this.emotions = emotions;
	}

	public int getRebirthResurrectPercent() {
		return rebirthResurrectPercent;
	}

	public void setRebirthResurrectPercent(int rebirthResurrectPercent) {
		this.rebirthResurrectPercent = rebirthResurrectPercent;
	}

	public int getRebirthSkill() {
		return rebirthSkill;
	}

	public void setRebirthSkill(int rebirthSkill) {
		this.rebirthSkill = rebirthSkill;
	}

	public BindPointPosition getBindPoint() {
		return bindPoint;
	}

	public void setBindPoint(BindPointPosition bindPoint) {
		this.bindPoint = bindPoint;
	}

	public int speedHackCounter;
	public int abnormalHackCounter;
	public WorldPosition prevPos = new WorldPosition();
	public long prevPosUT;
	public byte prevMoveType;

	@Override
	public ItemAttackType getAttackType() {
		Item weapon = getEquipment().getMainHandWeapon();
		if (weapon != null)
			return weapon.getItemTemplate().getAttackType();
		return ItemAttackType.PHYSICAL;
	}

	public long getFlyStartTime() {
		return this.flyStartTime;
	}

	public FlyPathEntry getCurrentFlyPath() {
		return flyLocationId;
	}

	public void setUnWispable() {
		this.isWispable = false;
	}

	public void setWispable() {
		this.isWispable = true;
	}

	public boolean isWispable() {
		return isWispable;
	}

	public boolean isInvulnerableWing() {
		return this.isUnderInvulnerableWing;
	}

	public void setInvulnerableWing(boolean value) {
		this.isUnderInvulnerableWing = value;
	}

	public void resetAbyssRankListUpdated() {
		this.abyssRankListUpdateMask = 0;
	}

	public void setAbyssRankListUpdated(AbyssRankUpdateType type) {
		this.abyssRankListUpdateMask |= type.value();
	}

	public boolean isAbyssRankListUpdated(AbyssRankUpdateType type) {
		return (this.abyssRankListUpdateMask & type.value()) == type.value();
	}

	public void addSalvationPoints(long points) {
		this.playerCommonData.addSalvationPoints(points);
		PacketSendUtility.sendPacket(this, new SM_STATS_INFO(this));
	}

	@Override
	public byte isPlayer() {
		if (this.isGM())
			return 2;
		else
			return 1;
	}

	/**
	 * @return the motions
	 */
	public MotionList getMotions() {
		return motions;
	}

	/**
	 * @param motions
	 *          the motions to set
	 */
	public void setMotions(MotionList motions) {
		this.motions = motions;
	}

	public void setTransformed(boolean value) {
		this.getTransformModel().setActive(value);
	}

	public boolean isTransformed() {
		return this.getTransformModel().isActive();
	}

	/**
	 * @return the npcFactions
	 */
	public NpcFactions getNpcFactions() {
		return npcFactions;
	}

	/**
	 * @param npcFactions
	 *          the npcFactions to set
	 */
	public void setNpcFactions(NpcFactions npcFactions) {
		this.npcFactions = npcFactions;
	}

	/**
	 * @return the flyReuseTime
	 */
	public long getFlyReuseTime() {
		return flyReuseTime;
	}

	/**
	 * @param flyReuseTime
	 *          the flyReuseTime to set
	 */
	public void setFlyReuseTime(long flyReuseTime) {
		this.flyReuseTime = flyReuseTime;
	}

	/**
	 * @param the
	 *          flying mode flag to set
	 */
	public void setFlyingMode(boolean value) {
		this.isFlying = value;
	}

	/**
	 * @return true if player is in Flying mode
	 */
	public boolean isInFlyingMode() {
		return this.isFlying;
	}

	/**
	 * Stone Use Order determined by highest inventory slot. :( If player has two types, wrong one might be used.
	 *
	 * @param player
	 * @return selfRezItem
	 */
	public Item getSelfRezStone() {
		Item item = null;
		item = getReviveStone(161001001);
		if (item == null)
			item = getReviveStone(161000003);
		if (item == null)
			item = getReviveStone(161000004);
		if (item == null)
			item = getReviveStone(161000001);
		return item;
	}

	/**
	 * @param stoneItemId
	 * @return stoneItem or null
	 */
	private Item getReviveStone(int stoneId) {
		Item item = getInventory().getFirstItemByItemId(stoneId);
		if (item != null && isItemUseDisabled(item.getItemTemplate().getUseLimits()))
			item = null;
		return item;
	}

	/**
	 * Need to find how an item is determined as able to self-rez.
	 *
	 * @return boolean can self rez with item
	 */
	public boolean haveSelfRezItem() {
		return (getSelfRezStone() != null);
	}

	/**
	 * Rebirth Effect is id 160.
	 *
	 * @return
	 */
	public boolean haveSelfRezEffect() {
		if (getAccessLevel() >= AdminConfig.ADMIN_AUTO_RES)
			return true;

		// Store the effect info.
		List<Effect> effects = getEffectController().getAbnormalEffects();
		for (Effect effect : effects) {
			for (EffectTemplate template : effect.getEffectTemplates()) {
				if (template.getEffectid() == 160 && template instanceof RebirthEffect) {
					RebirthEffect rebirthEffect = (RebirthEffect) template;
					setRebirthResurrectPercent(rebirthEffect.getResurrectPercent());
					setRebirthSkill(rebirthEffect.getSkillId());
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasResurrectBase() {
		List<Effect> effects = getEffectController().getAbnormalEffects();
		for (Effect effect : effects) {
			for (EffectTemplate template : effect.getEffectTemplates()) {
				if (template.getEffectid() == 160 && template instanceof ResurrectBaseEffect) {
					return true;
				}
			}
		}
		return false;
	}

	public void unsetResPosState() {
		if (isInResPostState()) {
			setResPosState(false);
			setResPosX(0);
			setResPosY(0);
			setResPosZ(0);
		}
	}

	public LootGroupRules getLootGroupRules() {
		if (isInGroup2()) {
			return getPlayerGroup2().getLootGroupRules();
		}
		if (isInAlliance2()) {
			return getPlayerAlliance2().getLootGroupRules();
		}
		return null;
	}

	public boolean isLooting() {
		return lootingNpcOid != 0;
	}

	public void setLootingNpcOid(int lootingNpcOid) {
		this.lootingNpcOid = lootingNpcOid;
	}

	public int getLootingNpcOid() {
		return lootingNpcOid;
	}

	public final boolean isMentor() {
		return isMentor;
	}

	public final void setMentor(boolean isMentor) {
		this.isMentor = isMentor;
	}

	@Override
	public Race getRace() {
		return playerCommonData.getRace();
	}

	public Player findPartner() {
		return World.getInstance().findPlayer(partnerId);
	}

	private PlayerVarsDAO daoVars;
	private Map<String, Object> vars = FastMap.newInstance();

	public boolean hasVar(String key) {
		return vars.containsKey(key);
	}

	public void setVar(String key, Object value, boolean sql) {
		vars.put(key, value);
		if (sql)
			daoVars.set(this.getObjectId(), key, value);
	}

	public void setVarLong(String key, long value, boolean sql) {
		vars.put(key, value);
		if (sql)
			daoVars.set(this.getObjectId(), key, value);
	}

    public void delVar(String key, boolean sql) {
        vars.remove(key);
        if (sql)
            daoVars.remove(this.getObjectId(), key);
    }

    public void updVar(String key, Object value, boolean sql) {
        vars.put(key, value);
        if (sql)
            daoVars.upd(this.getObjectId(), key, value);
    }
	public Object getVar(String key) {
		return this.vars.get(key);
	}

	public Object getVarLong(String key) {
		Object o = this.vars.get(key);
		if (o != null)
			return Long.parseLong(o.toString());
		return 0;
	}

	public int getVarInt(String key) {
		Object o = this.vars.get(key);
		if (o != null)
			return Integer.parseInt(o.toString());
		return 0;
	}

	public String getVarStr(String key) {
		Object o = this.vars.get(key);
		if (o != null)
			return o.toString();
		return null;
	}

	public void setVars(Map<String, Object> map) {
		this.vars = map;
	}

	public boolean isMarried() {
		return partnerId != 0;
	}

	public void setPartnerId(int partnerId) {
		this.partnerId = partnerId;
	}
    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }
    public int getPartnerId() {
        return partnerId;
    }
    public String getPartnerName() {
        return partnerName;
	}

	@Override
	public int getSkillCooldown(SkillTemplate template) {
		return isCoolDownZero() ? 0 : template.getCooldown();
	}

	@Override
	public int getItemCooldown(ItemTemplate template) {
		return isCoolDownZero() ? 0 : template.getUseLimits().getDelayTime();
	}

	public void setLastMessageTime() {
		if ((System.currentTimeMillis() - lastMsgTime) / 1000 < SecurityConfig.FLOOD_DELAY)
			floodMsgCount++;
		else
			floodMsgCount = 0;
		lastMsgTime = System.currentTimeMillis();
	}

	public int floodMsgCount() {
		return floodMsgCount;
	}

	public void setOnlineTime() {
		onlineTime = System.currentTimeMillis();
	}

	/*
	 * return online time in sec
	 */
	public long getOnlineTime() {
		return (System.currentTimeMillis() - onlineTime) / 1000;
	}

	/**
	 * Set Player command Used
	 */
	public void setCommandUsed(boolean value) {
		this.isCommandUsed = value;
	}

	/**
	 * @return true if player has used command
	 */
	public boolean isCommandInUse() {
		return this.isCommandUsed;
	}

	public void setRebirthRevive(boolean result) {
		rebirthRevive = result;
	}

	public boolean canUseRebirthRevive() {
		return rebirthRevive;
	}

	/**
	 * Put up supplements to subtraction queue, so that when moving they would not decrease, need update as confirmation
	 * To update use updateSupplements()
	 */
	public void subtractSupplements(int count, int supplementId) {
		subtractedSupplementsCount = count;
		subtractedSupplementId = supplementId;
	}

	/**
	 * Update supplements in queue and clear the queue
	 */
	public void updateSupplements() {
		if (subtractedSupplementId == 0 || subtractedSupplementsCount == 0)
			return;
		this.getInventory().decreaseByItemId(subtractedSupplementId, subtractedSupplementsCount);
		subtractedSupplementsCount = 0;
		subtractedSupplementId = 0;
	}

	public int getPortAnimation() {
		return portAnimation;
	}

	public void setPortAnimation(int portAnimation) {
		this.portAnimation = portAnimation;
	}

	@Override
	public boolean isSkillDisabled(SkillTemplate template) {
		ChainCondition cond = template.getChainCondition();
		if (cond != null && cond.getSelfCount() > 0) {// exception for multicast
			int chainCount = this.getChainSkills().getChainCount(this, template, cond.getCategory());
			if (chainCount > 0 && chainCount < cond.getSelfCount() && this.getChainSkills().chainSkillEnabled(cond.getCategory(), cond.getTime())) {
				return false;
			}
		}
		return super.isSkillDisabled(template);
	}

	/**
	 * @return the houses
	 */
	public List<House> getHouses() {
		if (houses == null) {
			List<House> found = HousingService.getInstance().searchPlayerHouses(this.getObjectId());
			if (found.size() > 0)
				houses = found;
			else
				return found;
		}
		return houses;
	}

	public void resetHouses() {
		if (houses != null) {
			houses.clear();
			houses = null;
		}
	}

	public House getActiveHouse() {
		for (House house : getHouses())
			if (house.getStatus() == HouseStatus.ACTIVE || house.getStatus() == HouseStatus.SELL_WAIT)
				return house;

		return null;
	}

	public int getHouseOwnerId() {
		House house = getActiveHouse();
		if (house != null)
			return house.getAddress().getId();

		return 0;
	}

	public HouseRegistry getHouseRegistry() {
		return houseRegistry;
	}

	public void setHouseRegistry(HouseRegistry houseRegistry) {
		this.houseRegistry = houseRegistry;
	}

	public byte getHousingStatus() {
		return housingStatus;
	}

	public boolean isInHousingStatus(HousingFlags status) {
		return (housingStatus & status.getId()) != 0;
	}

	public void setHousingStatus(byte status) {
		housingStatus |= status;
		House house = getActiveHouse();
		if (house != null) {
			house.fixHousingFlags();
		}
	}

	public void unsetHousingStatus(byte status) {
		housingStatus &= ~status;
		House house = getActiveHouse();
		if (house != null) {
			house.fixHousingFlags();
		}
	}

	public float[] getBattleReturnCoords() {
		return this.battleReturnCoords;
	}

	public void setBattleReturnCoords(int mapId, float[] coords) {
		this.battleReturnMap = mapId;
		this.battleReturnCoords = coords;
	}

	public int getBattleReturnMap() {
		return battleReturnMap;
	}

	public boolean isInSprintMode() {
		return isInSprintMode;
	}

	public void setSprintMode(boolean isInSprintMode) {
		this.isInSprintMode = isInSprintMode;
	}

	public void setRideObservers(ActionObserver observer) {
		if (rideObservers == null)
			rideObservers = new ArrayList<ActionObserver>(3);

		rideObservers.add(observer);
	}

	public List<ActionObserver> getRideObservers() {
		return rideObservers;
	}

	public AbsoluteStatOwner getAbsoluteStats() {
		return absStatsHolder;
	}

    public void putTempStorage(int index, Object obj)
    {
        tempStorage.put(index, obj);
    }

    public Object getTempStorage(int index) {
        return tempStorage.get(index);
    }

    public void clearTempStorage(int index) {
        tempStorage.remove(index);
    }

    public void setRobotForm(boolean isInRobotForm) {
        this.isInRobotForm = isInRobotForm;
    }

    public boolean isRobotForm() {
        return isInRobotForm;
    }

	public int getRobotId() {
        return robotId;
    }

    public void setRobotId(int robotId) {
        this.robotId = robotId;
    }

    public int getItemEffectId()
    {
        return itemEffectId;
    }

    public void setItemEffectId(int itemEffectId) {
        this.itemEffectId = itemEffectId;
    }

    public void updataItemEffectId() {
        int[] itemids = { 186000219, 186000220, 186000224, 186000225, 186000226, 186000227, 186000228, 186000229, 186000338, 186000339, 186000340, 186000341, 186000342, 186000343 };
        Item item = null;
        for (int itemId : itemids) {
            if (inventory.getItemCountByItemId(itemId) > 0) {
                item = inventory.getFirstItemByItemId(itemId);
                if (getItemEffectId() < 2) {
                    getGameStats().addEffect(item.getItemEffect(), item.getItemEffect().getModifiers());
                    setItemEffectId(item.getItemEffect().getItemEffectId());
                    break;
                }
            }
        }
        if (item == null)
            setItemEffectId(1);
    }
}
