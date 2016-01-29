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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.typezero.gameserver.model.templates.event.AtreianPassport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.GameServer;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.configs.main.GSConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.dataholders.StaticData;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.Gender;
import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.templates.BoundRadius;
import org.typezero.gameserver.model.templates.VisibleObjectTemplate;
import org.typezero.gameserver.model.templates.event.AtreianPassport;
import org.typezero.gameserver.network.aion.serverpackets.SM_DP_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_STATUPDATE_DP;
import org.typezero.gameserver.network.aion.serverpackets.SM_STATUPDATE_EXP;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.stats.XPLossEnum;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldPosition;

/**
 * This class is holding base information about player, that may be used even when player itself is not online.
 *
 * @author Luno
 * @modified cura
 */
public class PlayerCommonData extends VisibleObjectTemplate {

	/** Logger used by this class and {@link StaticData} class */
	static Logger log = LoggerFactory.getLogger(PlayerCommonData.class);

	private final int playerObjId;
	private Race race;
	private String name;
	private PlayerClass playerClass;
	/** Should be changed right after character creation **/
	private int level = 0;
	private long exp = 0;
	private long expRecoverable = 0;
	private Gender gender;
	private Timestamp lastOnline = new Timestamp(Calendar.getInstance().getTime().getTime() - 20);
    private Timestamp lastStamp = new Timestamp(Calendar.getInstance().getTime().getTime() - 20);
	private boolean online;
	private String note;
	private WorldPosition position;
	private int questExpands = 0;
	private int npcExpands = 0;
	private int warehouseSize = 0;
	private int advencedStigmaSlotSize = 0;
	private int titleId = -1;
	private int bonusTitleId = -1;
	private int dp = 0;
	private int mailboxLetters;
	private int soulSickness = 0;
	private boolean noExp = false;
	private long reposteCurrent;
	private long reposteMax;
	private long salvationPoint;
	private int mentorFlagTime;
	private int worldOwnerId;
    private int fatigue = 0;
    private int fatigueRecover = 0;
    private int fatigueReset = 0;
    private int stamps = 0;
    private int passportReward = 0;
    public Map<Integer, AtreianPassport> playerPassports = new HashMap<Integer, AtreianPassport>(1);
    private PlayerPassports completedPassports;

	private BoundRadius boundRadius;

	private long lastTransferTime;

	// TODO: Move all function to playerService or Player class.
	public PlayerCommonData(int objId) {
		this.playerObjId = objId;
	}

	public int getPlayerObjId() {
		return playerObjId;
	}

	public long getExp() {
		return this.exp;
	}

	public int getQuestExpands() {
		return this.questExpands;
	}

	public void setQuestExpands(int questExpands) {
		this.questExpands = questExpands;
	}

	public void setNpcExpands(int npcExpands) {
		this.npcExpands = npcExpands;
	}

	public int getNpcExpands() {
		return npcExpands;
	}

	/**
	 * @return the advencedStigmaSlotSize
	 */
	public int getAdvencedStigmaSlotSize() {
		return advencedStigmaSlotSize;
	}

	/**
	 * @param advencedStigmaSlotSize
	 *          the advencedStigmaSlotSize to set
	 */
	public void setAdvencedStigmaSlotSize(int advencedStigmaSlotSize) {
		this.advencedStigmaSlotSize = advencedStigmaSlotSize;
	}

	public long getExpShown() {
		return this.exp - DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(this.level);
	}

	public long getExpNeed() {
		if (this.level == DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel()) {
			return 0;
		}
		return DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(this.level + 1)
			- DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(this.level);
	}

	/**
	 * calculate the lost experience must be called before setexp
	 *
	 * @author Jangan
	 */
	public void calculateExpLoss() {
		long expLost = XPLossEnum.getExpLoss(this.level, this.getExpNeed());

		int unrecoverable = (int) (expLost * 0.33333333);
		int recoverable = (int) expLost - unrecoverable;
		long allExpLost = recoverable + this.expRecoverable;

		if (this.getExpShown() > unrecoverable) {
			this.exp = this.exp - unrecoverable;
		}
		else {
			this.exp = this.exp - this.getExpShown();
		}
		if (this.getExpShown() > recoverable) {
			this.expRecoverable = allExpLost;
			this.exp = this.exp - recoverable;
		}
		else {
			this.expRecoverable = this.expRecoverable + this.getExpShown();
			this.exp = this.exp - this.getExpShown();
		}
		if (this.expRecoverable > getExpNeed() * 0.25) {
			this.expRecoverable = Math.round(getExpNeed() * 0.25);
		}
		if (this.getPlayer() != null)
			PacketSendUtility.sendPacket(getPlayer(), new SM_STATUPDATE_EXP(getExpShown(), getExpRecoverable(), getExpNeed(), this.getCurrentReposteEnergy(), this.getMaxReposteEnergy()));
	}

	public void setRecoverableExp(long expRecoverable) {
		this.expRecoverable = expRecoverable;
	}

	public void resetRecoverableExp() {
        long el = this.expRecoverable;
        this.expRecoverable = 0;
        this.setExp(this.exp + el);
    }

	public long getExpRecoverable() {
		return this.expRecoverable;
	}

	/**
	 * @param value
	 */
	public void addExp(long value, int npcNameId) {
		this.addExp(value, null, npcNameId, "");
	}

	public void addExp(long value, RewardType rewardType) {
		this.addExp(value, rewardType, 0, "");
	}

	public void addExp(long value, RewardType rewardType, int npcNameId) {
		this.addExp(value, rewardType, npcNameId, "");
	}

	public void addExp(long value, RewardType rewardType, String name) {
		this.addExp(value, rewardType, 0, name);
	}

	public void addExp(long value, RewardType rewardType, int npcNameId, String name) {
		if (this.noExp)
			return;

		if (CustomConfig.ENABLE_EXP_CAP)
			value = value > CustomConfig.EXP_CAP_VALUE ? CustomConfig.EXP_CAP_VALUE : value;

		long reward = value;
		if (this.getPlayer() != null && rewardType != null)
			reward = rewardType.calcReward(this.getPlayer(), value);

		long repose = 0;
		if (this.isReadyForReposteEnergy() && this.getCurrentReposteEnergy() > 0) {
			repose = (long) ((reward / 100f) * 40); //40% bonus
			this.addReposteEnergy(-repose);
		}

		long salvation = 0;
		if (this.isReadyForSalvationPoints() && this.getCurrentSalvationPercent() > 0) {
			salvation = (long) ((reward / 100f) * this.getCurrentSalvationPercent());
			//TODO! remove salvation points?
		}

		reward += repose + salvation;
		this.setExp(this.exp + reward);
		if (this.getPlayer() != null) {
			if (rewardType != null) {
				switch (rewardType) {
					case GROUP_HUNTING:
					case HUNTING:
					case QUEST:
						if (npcNameId == 0) //Exeption quest w/o reward npc
							//You have gained %num1 XP.
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP2(reward));
						else if (repose > 0 && salvation > 0)
							//You have gained %num1 XP from %0 (Energy of Repose %num2, Energy of Salvation %num3).
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_VITAL_MAKEUP_BONUS_DESC(new DescriptionId(npcNameId * 2 + 1), reward, repose, salvation));
						else if (repose > 0 && salvation == 0)
							//You have gained %num1 XP from %0 (Energy of Repose %num2).
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_VITAL_BONUS_DESC(new DescriptionId(npcNameId * 2 + 1), reward, repose));
						else if (repose == 0 && salvation > 0)
							//You have gained %num1 XP from %0 (Energy of Salvation %num2).
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_MAKEUP_BONUS_DESC(new DescriptionId(npcNameId * 2 + 1), reward, salvation));
						else
							//You have gained %num1 XP from %0.
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_DESC(new DescriptionId(npcNameId * 2 + 1), reward));
						break;
					case PVP_KILL:
						if (repose > 0 && salvation > 0)
							//You have gained %num1 XP from %0 (Energy of Repose %num2, Energy of Salvation %num3).
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_VITAL_MAKEUP_BONUS(name, reward, repose, salvation));
						else if (repose > 0 && salvation == 0)
							//You have gained %num1 XP from %0 (Energy of Repose %num2).
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_VITAL_BONUS(name, reward, repose));
						else if (repose == 0 && salvation > 0)
							//You have gained %num1 XP from %0 (Energy of Salvation %num2).
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP_MAKEUP_BONUS(name, reward, salvation));
						else
							//You have gained %num1 XP from %0.
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP(name, reward));
						break;
					case CRAFTING:
					case GATHERING:
						if (repose > 0 && salvation > 0)
							//You have gained %num1 XP(Energy of Repose %num2, Energy of Salvation %num3).
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP2_VITAL_MAKEUP_BONUS(reward, repose, salvation));
						else if (repose > 0 && salvation == 0)
							//You have gained %num1 XP(Energy of Repose %num2).
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP2_VITAL_BONUS(reward, repose));
						else if (repose == 0 && salvation > 0)
							//You have gained %num1 XP(Energy of Salvation %num2).
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP2_MAKEUP_BONUS(reward, salvation));
						else
							//You have gained %num1 XP.
							PacketSendUtility.sendPacket(getPlayer(), SM_SYSTEM_MESSAGE.STR_GET_EXP2(reward));
						break;
				}
			}
		}
	}

	public boolean isReadyForSalvationPoints() {
		return level >= 15 && level < GSConfig.PLAYER_MAX_LEVEL + 1;
	}

	public boolean isReadyForReposteEnergy() {
		return level >= 10;
	}

	public void addReposteEnergy(long add) {
		if(!this.isReadyForReposteEnergy())
			return;

		reposteCurrent += add;
		if(reposteCurrent < 0)
			reposteCurrent = 0;
		else if(reposteCurrent > getMaxReposteEnergy())
			reposteCurrent = getMaxReposteEnergy();
	}

	public void updateMaxReposte() {
		if (!isReadyForReposteEnergy()) {
			reposteCurrent = 0;
			reposteMax = 0;
		}
		else
			reposteMax = (long) (getExpNeed() * 0.25f); //Retail 99%
	}

	public void setCurrentReposteEnergy(long value)	{
		reposteCurrent = value;
	}

	public long getCurrentReposteEnergy() {
		return isReadyForReposteEnergy() ? this.reposteCurrent : 0;
	}

	public long getMaxReposteEnergy() {
		return isReadyForReposteEnergy() ? this.reposteMax : 0;
	}

	/**
	 * sets the exp value
	 *
	 * @param exp
	 */
	public void setExp(long exp) {
		// maxLevel is 56 but in game 55 should be shown with full XP bar
		int maxLevel = DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel();

		if (getPlayerClass() != null && getPlayerClass().isStartingClass())
			maxLevel = 10;

		long maxExp = DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(maxLevel);

		if (exp > maxExp)
			exp = maxExp;

		int oldLvl = this.level;
		this.exp = exp;
		// make sure level is never larger than maxLevel-1
		boolean up = false;
		while ((this.level + 1) < maxLevel
			&& (up = exp >= DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(this.level + 1)) || (this.level - 1) >= 0
			&& exp < DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(this.level)) {
			if (up)
				this.level++;
			else
				this.level--;

			upgradePlayerData();
		}

		if (this.getPlayer() != null) {
			if (up && GSConfig.ENABLE_RATIO_LIMITATION) {
				if (this.level >= GSConfig.RATIO_MIN_REQUIRED_LEVEL && getPlayer().getPlayerAccount().getNumberOf(getRace()) == 1)
					GameServer.updateRatio(getRace(), 1);

				if (this.level >= GSConfig.RATIO_MIN_REQUIRED_LEVEL && getPlayer().getPlayerAccount().getNumberOf(getRace()) == 1)
					GameServer.updateRatio(getRace(), -1);
			}
			if(oldLvl != level)
				updateMaxReposte();

			PacketSendUtility.sendPacket(this.getPlayer(), new SM_STATUPDATE_EXP(getExpShown(), getExpRecoverable(), getExpNeed(), this.getCurrentReposteEnergy(), this.getMaxReposteEnergy()));
		}
	}

	private void upgradePlayerData() {
		Player player = getPlayer();
		if (player != null) {
			player.getController().upgradePlayer();
			resetSalvationPoints();
		}
	}

	public void setNoExp(boolean value) {
		this.noExp = value;
	}

	public boolean getNoExp() {
		return noExp;
	}

	/**
	 * @return Race as from template
	 */
	public final Race getRace() {
		return race;
	}

	public Race getOppositeRace() {
		return race == Race.ELYOS ? Race.ASMODIANS : Race.ELYOS;
	}

	/**
	 * @return the mentorFlagTime
	 */
	public int getMentorFlagTime() {
		return mentorFlagTime;
	}

	public boolean isHaveMentorFlag() {
		return mentorFlagTime > System.currentTimeMillis() / 1000;
	}

	/**
	 * @param mentorFlagTime the mentorFlagTime to set
	 */
	public void setMentorFlagTime(int mentorFlagTime) {
		this.mentorFlagTime = mentorFlagTime;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlayerClass getPlayerClass() {
		return playerClass;
	}

	public void setPlayerClass(PlayerClass playerClass) {
		this.playerClass = playerClass;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public WorldPosition getPosition() {
		return position;
	}

	public Timestamp getLastOnline() {
		return lastOnline;
	}

	public void setLastOnline(Timestamp timestamp) {
		lastOnline = timestamp;
	}

    public Timestamp getLastStamp() {
        return lastStamp;
    }

    public void setLastStamp(Timestamp timestamp) {
        this.lastStamp = timestamp;
    }

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		if (level <= DataManager.PLAYER_EXPERIENCE_TABLE.getMaxLevel()) {
			this.setExp(DataManager.PLAYER_EXPERIENCE_TABLE.getStartExpForLevel(level));
		}
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getTitleId() {
		return titleId;
	}

	public void setTitleId(int titleId) {
		this.titleId = titleId;
	}

	public int getBonusTitleId(){
		return bonusTitleId;
	}

	public void setBonusTitleId(int bonusTitleId){
		this.bonusTitleId = bonusTitleId;
	}

	/**
	 * This method should be called exactly once after creating object of this class
	 *
	 * @param position
	 */
	public void setPosition(WorldPosition position) {
		if (this.position != null) {
			throw new IllegalStateException("position already set");
		}
		this.position = position;
	}

	/**
	 * Gets the cooresponding Player for this common data. Returns null if the player is not online
	 *
	 * @return Player or null
	 */
	public Player getPlayer() {
		if (online && getPosition() != null) {
			return World.getInstance().findPlayer(playerObjId);
		}
		return null;
	}

	public void addDp(int dp) {
		setDp(this.dp + dp);
	}

	/**
	 * //TODO move to lifestats -> db save?
	 *
	 * @param dp
	 */
	public void setDp(int dp) {
		if (getPlayer() != null) {
			if (playerClass.isStartingClass())
				return;

			int maxDp = getPlayer().getGameStats().getMaxDp().getCurrent();
			this.dp = dp > maxDp ? maxDp : dp;

			PacketSendUtility.broadcastPacket(getPlayer(), new SM_DP_INFO(playerObjId, this.dp), true);
			getPlayer().getGameStats().updateStatsAndSpeedVisually();
			PacketSendUtility.sendPacket(getPlayer(), new SM_STATUPDATE_DP(this.dp));
		}
		else {
			log.debug("CHECKPOINT : getPlayer in PCD return null for setDP " + isOnline() + " " + getPosition());
		}
	}
    public void setDpOnLogin(int dp) {
        this.dp = dp;
    }

	public int getDp() {
		return this.dp;
	}

	@Override
	public int getTemplateId() {
		return 100000 + race.getRaceId() * 2 + gender.getGenderId();
	}

	@Override
	public int getNameId() {
		return 0;
	}

	/**
	 * @param warehouseSize
	 *          the warehouseSize to set
	 */
	public void setWarehouseSize(int warehouseSize) {
		this.warehouseSize = warehouseSize;
	}

	/**
	 * @return the warehouseSize
	 */
	public int getWarehouseSize() {
		return warehouseSize;
	}

	public void setMailboxLetters(int count) {
		this.mailboxLetters = count;
	}

	public int getMailboxLetters() {
		return mailboxLetters;
	}

	/**
	 * @param boundRadius
	 */
	public void setBoundingRadius(BoundRadius boundRadius) {
		this.boundRadius = boundRadius;
	}

	@Override
	public BoundRadius getBoundRadius() {
		return boundRadius;
	}

	public void setDeathCount(int count) {
		this.soulSickness = count;
	}

	public int getDeathCount() {
		return this.soulSickness;
	}

	/**
	 * Value returned here means % of exp bonus.
	 * @return
	 */
	public byte getCurrentSalvationPercent() {
		if (salvationPoint <= 0)
			return 0;

		long per = salvationPoint / 1000;
		if (per > 30)
			return 30;

		return (byte) per;
	}

	public void addSalvationPoints(long points) {
		salvationPoint += points;
	}

	public void resetSalvationPoints() {
		salvationPoint = 0;
	}

	public void setLastTransferTime(long value) {
		this.lastTransferTime = value;
	}

	public long getLastTransferTime() {
		return this.lastTransferTime;
	}

	public int getWorldOwnerId() {
		return worldOwnerId;
	}

	public void setWorldOwnerId(int worldOwnerId) {
		this.worldOwnerId = worldOwnerId;
	}

    public void setFatigue(int value) {
        this.fatigue = value;
    }

    public void setFatigueRecover(int count) {
        this.fatigueRecover = count;
    }

    public int getFatigue() {
        return fatigue;
    }

    public int getFatigueRecover() {
        return fatigueRecover;
    }

    public void setFatigueReset(int value) {
        this.fatigueReset = value;
    }

    public int getFatigueReset() {
        return fatigueReset;
    }

    public int getPassportStamps() {
        return stamps;
    }

    public void setPassportStamps(int value) {
        this.stamps = value;
    }

    public Map<Integer, AtreianPassport> getPlayerPassports() {
        return playerPassports;
    }

    public PlayerPassports getCompletedPassports() {
        return completedPassports;
    }

    public void addToCompletedPassports(AtreianPassport pp) {
        completedPassports.addPassport(pp.getId(), pp);
    }

    public void setCompletedPassports(PlayerPassports pp) {
        completedPassports = pp;
    }

    public int getPassportReward() {
        return passportReward;
    }

    public void setPassportReward(int value) {
        this.passportReward = value;
    }
}
