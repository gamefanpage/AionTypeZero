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


package org.typezero.gameserver.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.skill.PlayerSkillEntry;
import org.typezero.gameserver.model.templates.HiddenStigmasTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import org.typezero.gameserver.skillengine.model.SkillLearnTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.configs.main.MembershipConfig;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Equipment;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.ItemSlot;
import org.typezero.gameserver.model.templates.item.RequireSkill;
import org.typezero.gameserver.model.templates.item.Stigma;
import org.typezero.gameserver.model.templates.item.Stigma.StigmaSkill;
import org.typezero.gameserver.network.aion.serverpackets.SM_CUBE_UPDATE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.audit.AuditLogger;

/**
 * @author ATracer
 * @modified cura
 */
public class StigmaService {

	private static final Logger log = LoggerFactory.getLogger(StigmaService.class);

	public static boolean extendAdvancedStigmaSlots(Player player) {
		int newAdvancedSlotSize = player.getCommonData().getAdvencedStigmaSlotSize() + 1;
		if (newAdvancedSlotSize <= 6) { // maximum
			player.getCommonData().setAdvencedStigmaSlotSize(newAdvancedSlotSize);
			PacketSendUtility.sendPacket(player, SM_CUBE_UPDATE.stigmaSlots(player.getCommonData()
				.getAdvencedStigmaSlotSize()));
			return true;
		}
		return false;
	}

	/**
	 * @param player
	 * @param resultItem
	 * @param slot
	 * @return
	 */
	public static boolean notifyEquipAction(Player player, Item resultItem, long slot) {
		if (resultItem.getItemTemplate().isStigma()) {
			if (ItemSlot.isRegularStigma(slot)) {
				// check the number of stigma wearing
				if (getPossibleStigmaCount(player) <= player.getEquipment().getEquippedItemsRegularStigma().size()) {
					AuditLogger.info(player, "Possible client hack stigma count big :O");
					return false;
				}
			}
			else if (ItemSlot.isAdvancedStigma(slot)) {
				// check the number of advanced stigma wearing
				if (getPossibleAdvencedStigmaCount(player) <= player.getEquipment().getEquippedItemsAdvencedStigma().size()) {
					AuditLogger.info(player,"Possible client hack advanced stigma count big :O");
					return false;
				}
			}

			if (resultItem.getItemTemplate().isClassSpecific(player.getCommonData().getPlayerClass()) == false) {
				AuditLogger.info(player,"Possible client hack not valid for class.");
				return false;
			}

			Stigma stigmaInfo = resultItem.getItemTemplate().getStigma();

			if (stigmaInfo == null) {
				log.warn("Stigma info missing for item: " + resultItem.getItemTemplate().getTemplateId());
				return false;
			}

			int kinahCount = stigmaInfo.getKinah();
			if (player.getInventory().getKinah() < kinahCount) {
				AuditLogger.info(player,"Possible client hack stigma kinah count low.");
				return false;
			}
			int neededSkillsCount = stigmaInfo.getRequireSkill().size();
			for (RequireSkill rs : stigmaInfo.getRequireSkill()) {
				for (int id : rs.getSkillIds()) {
					if (player.getSkillList().isSkillPresent(id)) {
						neededSkillsCount--;
						break;
					}
				}
			}
			if (neededSkillsCount != 0) {
				AuditLogger.info(player, "Possible client hack advenced stigma skill.");
				return false;
			}

			if (!player.getInventory().tryDecreaseKinah(kinahCount))
				return false;

            Integer realSkillId = DataManager.SKILL_TREE_DATA.getStigmaTree().get(player.getRace()).
                        get(DataManager.SKILL_DATA.getSkillTemplate(stigmaInfo.getSkills().get(0).getSkillId()).getStack()).get(resultItem.getEnchantLevel() + 1);
            if (realSkillId != null)
                player.getSkillList().addStigmaSkill(player, realSkillId, 1);
            else {
                log.error("No have Stigma skill for enchanted stigma item.");
            }
		}
		return true;
	}

	/**
	 * @param player
	 * @param resultItem
	 * @return
	 */
	public static boolean notifyUnequipAction(Player player, Item resultItem) {
		if (resultItem.getItemTemplate().isStigma()) {
			Stigma stigmaInfo = resultItem.getItemTemplate().getStigma();
			int itemId = resultItem.getItemId();
			Equipment equipment = player.getEquipment();
			if (itemId == 140000007 || itemId == 140000005) {
				if (equipment.hasDualWeaponEquipped(ItemSlot.LEFT_HAND)) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_STIGMA_CANNT_UNEQUIP_STONE_FIRST_UNEQUIP_CURRENT_EQUIPPED_ITEM);
					return false;
				}
			}
			for (Item item : player.getEquipment().getEquippedItemsAllStigma()) {
				Stigma si = item.getItemTemplate().getStigma();
				if (resultItem == item || si == null)
					continue;

				for (StigmaSkill sSkill : stigmaInfo.getSkills()) {
					for (RequireSkill rs : si.getRequireSkill()) {
						if (rs.getSkillIds().contains(sSkill.getSkillId())) {
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300410, new DescriptionId(resultItem
								.getItemTemplate().getNameId()), new DescriptionId(item.getItemTemplate().getNameId())));
							return false;
						}
					}
				}
			}
            for (StigmaSkill sSkill : stigmaInfo.getSkills()) {
                String sSkillStack = DataManager.SKILL_DATA.getSkillTemplate(sSkill.getSkillId()).getStack();

                for (PlayerSkillEntry psSkill : player.getSkillList().getStigmaSkills()){
                    if (psSkill.getSkillTemplate().getStack().equals(sSkillStack)) {
                        int nameId = DataManager.SKILL_DATA.getSkillTemplate(psSkill.getSkillId()).getNameId();
                        PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300403, new DescriptionId(nameId)));
                        SkillLearnService.removeSkill(player, psSkill.getSkillId());
                        player.getEffectController().removeEffect(psSkill.getSkillId());
                        player.getSkillList().deleteHiddenStigma(player);
                    }
                }
            }
		}
		return true;
	}

    public static void recheckHiddenStigma(Player player) {
        if (player.getLevel() >= 55 && player.getEquipment().getEquippedItemsAllStigma().size() >= 6) {
            for (Item stigma : player.getEquipment().getEquippedItemsAllStigma()) {
                if (stigma.getItemTemplate().isNoEnchant())
                    return;
            }
            int hiddenStigmaSkillId = DataManager.HIDDEN_STIGMA_DATA.getHiddenStigmaSkill(player);

            if (hiddenStigmaSkillId != 0)
                if (!player.getSkillList().isHaveHiddenStigma(player) || (player.getSkillList().isHaveHiddenStigma(player) && player.getSkillList().getStigmaSkillEntry(hiddenStigmaSkillId) == null)) {
                    player.getSkillList().addHiddenStigmaSkill(player, hiddenStigmaSkillId, 1);
                }
        }
    }

	/**
	 * @param player
	 */
	public static void onPlayerLogin(Player player) {
		List<Item> equippedItems = player.getEquipment().getEquippedItemsAllStigma();
		for (Item item : equippedItems) {
			if (item.getItemTemplate().isStigma()) {
				Stigma stigmaInfo = item.getItemTemplate().getStigma();

				if (stigmaInfo == null) {
					//log.warn("Stigma info missing for item: " + item.getItemTemplate().getTemplateId());
					//return;
                                    //4.8 Fix unequip stigma
					AuditLogger.info(player,"Stigma: " + item.getItemTemplate().getTemplateId() + " moved in inventory");//4.8
					player.getEquipment().unEquipItem(item.getObjectId(), 0);//4.8
					continue;//4.8
				}
				player.getSkillList().addStigmaSkill(player, stigmaInfo.getSkills(), false);
			}
		}

        recheckHiddenStigma(player);

		for (Item item : equippedItems) {
			if (item.getItemTemplate().isStigma()) {
				if (!isPossibleEquippedStigma(player, item)) {
					AuditLogger.info(player,"Possible client hack stigma count big :O");
					player.getEquipment().unEquipItem(item.getObjectId(), 0);
					continue;
				}

				Stigma stigmaInfo = item.getItemTemplate().getStigma();

				if (stigmaInfo == null) {
					log.warn("Stigma info missing for item: " + item.getItemTemplate().getTemplateId());
					player.getEquipment().unEquipItem(item.getObjectId(), 0);
					continue;
				}

				int needSkill = stigmaInfo.getRequireSkill().size();
				for (RequireSkill rs : stigmaInfo.getRequireSkill()) {
					for (int id : rs.getSkillIds()) {
						if (player.getSkillList().isSkillPresent(id)) {
							needSkill--;
							break;
						}
					}
				}
				if (needSkill != 0) {
					AuditLogger.info(player,"Possible client hack advenced stigma skill.");
					player.getEquipment().unEquipItem(item.getObjectId(), 0);
					continue;
				}
				if (item.getItemTemplate().isClassSpecific(player.getCommonData().getPlayerClass()) == false) {
					AuditLogger.info(player,"Possible client hack not valid for class.");
					player.getEquipment().unEquipItem(item.getObjectId(), 0);
					continue;
				}
			}
		}
	}

	/**
	 * Get the number of available Stigma
	 *
	 * @param player
	 * @return
	 */
	private static int getPossibleStigmaCount(Player player) {
		if (player == null || player.getLevel() < 20)
			return 0;

		if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST)) {
			return 6;
		}

		/*
		 * Stigma Quest Elyos: 1929, Asmodians: 2900
		 */
		boolean isCompleteQuest = false;

		if (player.getRace() == Race.ELYOS) {
			isCompleteQuest = player.isCompleteQuest(1929)
				|| (player.getQuestStateList().getQuestState(1929).getStatus() == QuestStatus.START && player
					.getQuestStateList().getQuestState(1929).getQuestVars().getQuestVars() == 98);
		}
		else {
			isCompleteQuest = player.isCompleteQuest(2900)
				|| (player.getQuestStateList().getQuestState(2900).getStatus() == QuestStatus.START && player
					.getQuestStateList().getQuestState(2900).getQuestVars().getQuestVars() == 99);
		}

		int playerLevel = player.getLevel();

		if (isCompleteQuest) {
			if (playerLevel < 30)
				return 2;
			else if (playerLevel < 40)
				return 3;
			else if (playerLevel < 50)
				return 4;
			else if (playerLevel < 55)
				return 5;
			else
				return 6;
		}
		return 0;
	}

	/**
	 * Get the number of available Advenced Stigma
	 *
	 * @param player
	 * @return
	 */
	private static int getPossibleAdvencedStigmaCount(Player player) {
		if (player == null || player.getLevel() < 45)
			return 0;

		if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST)) {
			return 6;
		}

		/*
		 * Advenced Stigma Quest 1st - Elyos: 3930, Asmodians: 4934 2nd - Elyos: 3931, Asmodians: 4935 3rd- Elyos: 3932,
		 * Asmodians: 4936 4th - Elyos: 11049, Asmodians: 21049 5th - Elyos: 30217, Asmodians: 30317
		 */
		if (player.getRace() == Race.ELYOS) {
			// Check whether Stigma Quests
			if (!player.isCompleteQuest(1929))
				return 0;
			if (player.isCompleteQuest(11550))
				return 6;
			else if (player.isCompleteQuest(30217) || player.isCompleteQuest(11276))
				return 5;
			else if (player.isCompleteQuest(11049))
				return 4;
			else if (player.isCompleteQuest(3932))
				return 3;
			else if (player.isCompleteQuest(3931))
				return 2;
			else if (player.isCompleteQuest(3930))
				return 1;
		}
		else {
			// Check whether Stigma Quests
			if (!player.isCompleteQuest(2900))
				return 0;
			if (player.isCompleteQuest(21550))
				return 6;
			else if (player.isCompleteQuest(30317) || player.isCompleteQuest(21278))
				return 5;
			else if (player.isCompleteQuest(21049))
				return 4;
			else if (player.isCompleteQuest(4936))
				return 3;
			else if (player.isCompleteQuest(4935))
				return 2;
			else if (player.isCompleteQuest(4934))
				return 1;
		}
		return 0;
	}

	/**
	 * Stigma is a worn check available slots
	 *
	 * @param player
	 * @param item
	 * @return
	 */
	private static boolean isPossibleEquippedStigma(Player player, Item item) {
		if (player == null || (item == null || !item.getItemTemplate().isStigma()))
			return false;

		long itemSlotToEquip = item.getEquipmentSlot();

		// Stigma
		if (ItemSlot.isRegularStigma(itemSlotToEquip)) {
			int stigmaCount = getPossibleStigmaCount(player);

			if (stigmaCount > 0) {
				if (stigmaCount == 1) {
					if (itemSlotToEquip == ItemSlot.STIGMA1.getSlotIdMask())
						return true;
				}
				else if (stigmaCount == 2) {
					if (itemSlotToEquip == ItemSlot.STIGMA1.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA2.getSlotIdMask())
						return true;
				}
				else if (stigmaCount == 3) {
					if (itemSlotToEquip == ItemSlot.STIGMA1.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA2.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA3.getSlotIdMask())
						return true;
				}
				else if (stigmaCount == 4) {
					if (itemSlotToEquip == ItemSlot.STIGMA1.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA2.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA3.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA4.getSlotIdMask())
						return true;
				}
				else if (stigmaCount == 5) {
					if (itemSlotToEquip == ItemSlot.STIGMA1.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA2.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA3.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA4.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA5.getSlotIdMask())
						return true;
				}
				else if (stigmaCount == 6) {
					return true;
				}
			}
		}
		// Advenced Stigma
		else if (ItemSlot.isAdvancedStigma(itemSlotToEquip)) {
			int advStigmaCount = getPossibleAdvencedStigmaCount(player);

			if (advStigmaCount > 0) {
				if (advStigmaCount == 1) {
					if (itemSlotToEquip == ItemSlot.ADV_STIGMA1.getSlotIdMask())
						return true;
				}
				else if (advStigmaCount == 2) {
					if (itemSlotToEquip == ItemSlot.ADV_STIGMA1.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.ADV_STIGMA2.getSlotIdMask())
						return true;
				}
				else if (advStigmaCount == 3) {
					if (itemSlotToEquip == ItemSlot.ADV_STIGMA1.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.ADV_STIGMA2.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.ADV_STIGMA3.getSlotIdMask())
						return true;
				}
				else if (advStigmaCount == 4) {
					if (itemSlotToEquip == ItemSlot.ADV_STIGMA1.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.ADV_STIGMA2.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.ADV_STIGMA3.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.ADV_STIGMA4.getSlotIdMask())
						return true;
				}
				else if (advStigmaCount == 5) {
					if (itemSlotToEquip == ItemSlot.ADV_STIGMA1.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.ADV_STIGMA2.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.ADV_STIGMA3.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.ADV_STIGMA4.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.ADV_STIGMA5.getSlotIdMask())
					return true;
				}
				else if (advStigmaCount == 6) {
					return true;
				}
			}
		}
		return false;
	}

    public static void reparseHiddenStigmas() {
        for (HiddenStigmasTemplate classStigmas : DataManager.HIDDEN_STIGMA_DATA.getHiddenStigmasByClass()) {
            for (HiddenStigmasTemplate.HiddenStigmaTemplate hst : classStigmas.getHiddenStigmas()) {
                hst.dataProcessing();
            }
        }
    }
}
