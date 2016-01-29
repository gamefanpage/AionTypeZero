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

package org.typezero.gameserver.model.templates.item;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.ItemId;
import org.typezero.gameserver.model.items.ItemMask;
import org.typezero.gameserver.model.stats.calc.functions.StatFunction;
import org.typezero.gameserver.model.templates.VisibleObjectTemplate;
import org.typezero.gameserver.model.templates.item.actions.ItemActions;
import org.typezero.gameserver.model.templates.itemset.ItemSetTemplate;
import org.typezero.gameserver.model.templates.stats.ModifiersTemplate;
import org.typezero.gameserver.world.zone.ZoneName;
import org.junit.experimental.categories.Category;

/**
 * @author Luno modified by ATracer
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(namespace = "", name = "ItemTemplate")
public class ItemTemplate extends VisibleObjectTemplate {

	@XmlAttribute(name = "id", required = true)
	@XmlID
	private String id;

	@XmlElement(name = "modifiers", required = false)
	protected ModifiersTemplate modifiers;

    @XmlAttribute(name = "name_desc")
    private String namedesc;

	@XmlElement(name = "actions", required = false)
	protected ItemActions actions;

	@XmlAttribute(name = "mask")
	private int mask;

	@XmlAttribute(name = "category")
	private ItemCategory category = ItemCategory.NONE;

	@XmlAttribute(name = "slot")
	private int itemSlot;

	@XmlAttribute(name = "equipment_type")
	private EquipType equipmentType = EquipType.NONE;

	@XmlAttribute(name = "weapon_boost")
	private int weaponBoost;

	@XmlAttribute(name = "price")
	private int price;

	@XmlAttribute(name = "max_stack_count")
	private int maxStackCount = 1;

	@XmlAttribute(name = "level")
	private int level;

	@XmlAttribute(name = "quality")
	private ItemQuality itemQuality;

	@XmlAttribute(name = "item_type")
	private ItemType itemType;

	@XmlAttribute(name = "weapon_type")
	private WeaponType weaponType;

	@XmlAttribute(name = "armor_type")
	private ArmorType armorType;

	@XmlAttribute(name = "attack_type")
	private ItemAttackType attackType;

	@XmlAttribute(name = "attack_gap")
	private float attackGap;

	@XmlAttribute(name = "desc")
	private String description;

	@XmlAttribute(name = "option_slot_bonus")
	private int optionSlotBonus;

	@XmlAttribute(name = "rnd_bonus")
	private int rnd_bonus = 0;

	@XmlAttribute(name = "rnd_count")
	private int rnd_count = 0;

	@XmlAttribute(name = "bonus_apply")
	private String bonusApply;// enum

	@XmlAttribute(name = "race")
	private Race race = Race.PC_ALL;

	private int itemId;

	@XmlAttribute(name = "return_world")
	private int returnWorldId;

	@XmlAttribute(name = "return_alias")
	private String returnAlias;

	@XmlElement(name = "godstone")
	private GodstoneInfo godstoneInfo;

	@XmlElement(name = "stigma")
	private Stigma stigma;

	@XmlAttribute(name = "name")
	private String name;

	@XmlAttribute(name = "restrict")
	private String restrict;

	@XmlAttribute(name = "restrict_max")
	private String restrictMax;

	@XmlTransient
	private int[] restricts;

	@XmlTransient
	private byte[] restrictsMax;

	@XmlAttribute(name = "m_slots")
	private int manastoneSlots;

	@XmlAttribute(name = "s_slots")
	private int specialSlots;

	@XmlAttribute(name = "max_enchant")
	private int maxEnchant;

	@XmlAttribute(name = "temp_exchange_time")
	protected int temExchangeTime;

	@XmlAttribute(name = "expire_time")
	protected int expireTime;

	@XmlElement(name = "weapon_stats")
	protected WeaponStats weaponStats;

	@XmlAttribute(name = "activate_count")
	private int activationCount;

	@XmlElement(name = "tradein_list")
	protected TradeinList tradeinList;

	@XmlElement(name = "acquisition")
	private Acquisition acquisition;

	@XmlElement(name = "disposition")
	private Disposition disposition;

	@XmlElement(name = "improve")
	private Improvement improvement;

	@XmlElement(name = "uselimits")
	private ItemUseLimits useLimits = new ItemUseLimits();

	@XmlElement(name = "inventory")
	private ExtraInventory extraInventory;

	@XmlElement(name = "idian")
	private Idian idianAction;

	@XmlAttribute(name = "robot_id")
	private int robotId;

    @XmlAttribute(name="auth_id")
    private int auth_id;

    @XmlAttribute(name="authorize")
    private int authorize;

    @XmlAttribute(name = "authorize_condition")
    private int authorize_condition;
    @XmlAttribute(name = "authorize_name")
    private int authorize_name;
    @XmlAttribute(name = "oversea_only")
    private int oversea_only;
    @XmlAttribute(name = "exceed_enchant")
    private boolean exceedEnchant = false;
    @XmlAttribute(name = "enchant_skill")
    private String enchantSkillSet;

	private static final WeaponStats emptyWeaponStats = new WeaponStats();

	@XmlTransient
	private boolean isQuestUpdateItem;

	/**
	 * @param u
	 * @param parent
	 */
	void afterUnmarshal(Unmarshaller u, Object parent) {
		setItemId(Integer.parseInt(id));
		String[] parts = restrict.split(",");
		restricts = new int[17];
		for (int i = 0; i < parts.length; i++) {
			restricts[i] = Integer.parseInt(parts[i]);
		}
		if (restrictMax != null) {
			String[] partsMax = restrictMax.split(",");
			restrictsMax = new byte[17];
			for (int i = 0; i < partsMax.length; i++) {
				restrictsMax[i] = Byte.parseByte(partsMax[i]);
			}
		}
		if (weaponStats == null)
			weaponStats = emptyWeaponStats;
	}

	public byte getMaxLevelRestrict(Player player) {
		if (restrictMax != null) {
			byte restrictId = player.getPlayerClass().getClassId();
			byte restrictLevel = restrictsMax[restrictId];
			return player.getLevel() <= restrictLevel ? 0 : restrictLevel;
		}
		return 0;
	}

	public int getMask() {
		return mask;
	}

	public ItemCategory getCategory() {
		return category;
	}

	public int getItemSlot() {
		return itemSlot;
	}

	/**
	 * @param playerClass
	 * @return
	 */
	public boolean isClassSpecific(PlayerClass playerClass) {
		boolean related = restricts[playerClass.ordinal()] > 0;
		if (!related && !playerClass.isStartingClass()) {
			related = restricts[PlayerClass.getStartingClassFor(playerClass).ordinal()] > 0;
		}
		return related;
	}

	/**
	 * @param playerClass
	 * @param level
	 * @return
	 */
	public int getRequiredLevel(PlayerClass playerClass) {
		int requiredLevel = restricts[playerClass.ordinal()];
		if (requiredLevel == 0)
			return -1;
		else
			return requiredLevel;
	}

	public List<StatFunction> getModifiers() {
		if (modifiers != null) {
			return modifiers.getModifiers();
		}
		return null;
	}

	public ItemActions getActions() {
		return actions;
	}

	public EquipType getEquipmentType() {
		return equipmentType;
	}

	public int getPrice() {
		return price;
	}

	public int getLevel() {
		return level;
	}

	public ItemQuality getItemQuality() {
		return itemQuality;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public WeaponType getWeaponType() {
		return weaponType;
	}

    public ArmorType getArmorType() {
        if (isPlume())
            return ArmorType.PLUME;
        return armorType;
    }

	@Override
	public int getNameId() {
		try {
			int val = Integer.parseInt(description);
			return val;
		}
		catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public long getMaxStackCount() {
		if (isKinah()) {
			if (CustomConfig.ENABLE_KINAH_CAP) {
				return CustomConfig.KINAH_CAP_VALUE;
			}
			else {
				return Long.MAX_VALUE;
			}
		}
		return maxStackCount;
	}

	public ItemAttackType getAttackType() {
		return attackType;
	}

	public float getAttackGap() {
		return attackGap;
	}

	public int getOptionSlotBonus() {
		return optionSlotBonus;
	}

	public String getBonusApply() {
		return bonusApply;
	}

	public boolean isNoEnchant() {
		return (getMask() & ItemMask.NO_ENCHANT) == ItemMask.NO_ENCHANT;
	}

	public boolean isItemDyePermitted() {
		return (getMask() & ItemMask.DYEABLE) == ItemMask.DYEABLE;
	}

	public Race getRace() {
		return race;
	}

	public int getWeaponBoost() {
		return weaponBoost;
	}

	public boolean isWeapon() {
		return equipmentType == EquipType.WEAPON;
	}

	public boolean isArmor() {
		return equipmentType == EquipType.ARMOR;
	}

	public boolean isKinah() {
		return itemId == ItemId.KINAH.value();
	}

	public boolean isStigma() {
		return itemId > 140000000 && itemId < 140001500;
	}

    public boolean isPlume() {
        return category == ItemCategory.PLUME; // return itemId >= 187100015 && itemId <= 187100018;
    }

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return id of the associated ItemSetTemplate or null if none
	 */
	public ItemSetTemplate getItemSet() {
		return DataManager.ITEM_SET_DATA.getItemSetTemplateByItemId(itemId);
	}

	/**
	 * Checks if the ItemTemplate belongs to an item set
	 */
	public boolean isItemSet() {
		return getItemSet() != null;
	}

	public GodstoneInfo getGodstoneInfo() {
		return godstoneInfo;
	}

	@Override
	public String getName() {
		return name != null ? name : StringUtils.EMPTY;
	}

    public String getNamedesc() {
		return namedesc;
	}

	@Override
	public int getTemplateId() {
		return itemId;
	}

	public int getReturnWorldId() {
		return returnWorldId;
	}

	public String getReturnAlias() {
		return returnAlias;
	}

	public Stigma getStigma() {
		return stigma;
	}

	public int getManastoneSlots() {
		return manastoneSlots;
	}

	public int getSpecialSlots() {
		return specialSlots;
	}

	public int getMaxEnchantLevel() {
		return maxEnchant;
	}

	public boolean hasLimitOne() {
		return (getMask() & ItemMask.LIMIT_ONE) == ItemMask.LIMIT_ONE;
	}

	public boolean isTradeable() {
		return (getMask() & ItemMask.TRADEABLE) == ItemMask.TRADEABLE;
	}

	public boolean isCanFuse() {
		return (getMask() & ItemMask.CAN_COMPOSITE_WEAPON) == ItemMask.CAN_COMPOSITE_WEAPON;
	}

	public boolean canExtract() {
		return (getMask() & ItemMask.CAN_SPLIT) == ItemMask.CAN_SPLIT;
	}

	public boolean isSoulBound() {
		return (getMask() & ItemMask.SOUL_BOUND) == ItemMask.SOUL_BOUND;
	}

	public boolean isBreakable() {
		return (getMask() & ItemMask.BREAKABLE) == ItemMask.BREAKABLE;
	}

	public boolean isDeletable() {
		return (getMask() & ItemMask.DELETABLE) == ItemMask.DELETABLE;
	}

	public boolean isCanPolish() {
		return (getMask() & ItemMask.CAN_POLISH) == ItemMask.CAN_POLISH;
	}

	public boolean isTwoHandWeapon() {
		if (!isWeapon())
			return false;
		return weaponType.getRequiredSlots() == 2;
	}

	public int getTempExchangeTime() {
		return temExchangeTime;
	}

	public int getExpireTime() {
		return expireTime;
	}

	public final WeaponStats getWeaponStats() {
		return weaponStats;
	}

	public int getActivationCount() {
		return activationCount;
	}

	/**
	 * Null if no id, can be values 0, 1, 2
	 */
	public int getExtraInventoryId() {
		if (extraInventory == null) {
			return -1;
		}
		return extraInventory.getId();
	}

	public void modifyMask(boolean apply, int filter) {
		if (apply)
			mask |= filter;
		else
			mask &= ~filter;
	}

	public boolean isStackable() {
		return this.maxStackCount > 1;
	}

	public boolean hasAreaRestriction() {
		return useLimits.getUseArea() != null;
	}

	public ZoneName getUseArea() {
		return useLimits.getUseArea();
	}

	/**
	 * @return the tradeinList
	 */
	public TradeinList getTradeinList() {
		return tradeinList;
	}

	/**
	 * @return the acquisition
	 */
	public Acquisition getAcquisition() {
		return acquisition;
	}

	/**
	 * @return the rnd_bonus, 0 if no bonus exists
	 */
	public int getRandomBonusId() {
		return rnd_bonus;
	}

	/**
	 * @return the rnd_count, 0 if no randomization is limited (JUST A GUESS!)
	 */
	public int getRandomBonusCount() {
		return rnd_count;
	}

	/**
	 * @return the conditioning
	 */
	public Improvement getImprovement() {
		return improvement;
	}

	/**
	 * @return the useLimits
	 */
	public ItemUseLimits getUseLimits() {
		return useLimits;
	}

	public Disposition getDisposition() {
		return disposition;
	}

	public int getOwnershipWorld() {
		return useLimits.getOwnershipWorld();
	}

	public boolean isCloth() {
		return armorType != null && armorType != ArmorType.ARROW && equipmentType == EquipType.ARMOR;
	}

	public boolean isQuestUpdateItem() {
		return isQuestUpdateItem;
	}

	public void setQuestUpdateItem(boolean value) {
		this.isQuestUpdateItem = value;
	}

	public Idian getIdianAction() {
		return idianAction;
	}

    public boolean isCombinationItem() {
        return category == ItemCategory.COMBINATION;
    }

    public boolean isEnchantmentStone()
    {
        return category == ItemCategory.ENCHANTMENT;
    }

	public int getRobotId() {
		return robotId;
	}

    public boolean isAccessory()
    {
        return category == ItemCategory.EARRINGS || category == ItemCategory.RINGS || category == ItemCategory.NECKLACE || category == ItemCategory.PLUME || category == ItemCategory.HELMET || category == ItemCategory.BELT;
    }

    public int authorizeId()
    {
        return auth_id;
    }

    public int getAuthorize()
    {
        return authorize;
    }

    public int getAuthorizeName() {
        return authorize_name;
    }

    public int getAuthorizeCondition() {
        return authorize_condition;
    }

    public int getOverseaOnly() {
        return oversea_only;
    }

    public boolean getExceedEnchant() {
        return exceedEnchant;
    }

    public String getEnchantSkillSet() {
    	return enchantSkillSet;
    }

    public boolean isNewPlayerBuffItem()
    {
        return (itemId == 186000219) || ((itemId >= 186000224) && (itemId <= 186000226));
    }

    public boolean isOldPlayerBuffItem() {
        return (itemId == 186000220) || ((itemId >= 186000227) && (itemId <= 186000229));
    }

    public boolean isEventBuffItem() {
        return (itemId >= 186000338) && (itemId <= 186000343);
    }
}
