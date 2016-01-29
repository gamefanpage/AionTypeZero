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

import org.typezero.gameserver.configs.main.AutoGroupConfig;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.autogroup.AutoGroupType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.HousingFlags;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RequestResponseHandler;
import org.typezero.gameserver.model.team.legion.Legion;
import org.typezero.gameserver.model.team.legion.LegionWarehouse;
import org.typezero.gameserver.model.templates.portal.PortalPath;
import org.typezero.gameserver.model.templates.teleport.TeleportLocation;
import org.typezero.gameserver.model.templates.teleport.TeleporterTemplate;
import org.typezero.gameserver.model.templates.tradelist.TradeListTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_AUTO_GROUP;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_PET;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLASTIC_SURGERY;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_REPURCHASE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SELL_ITEM;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.network.aion.serverpackets.SM_TRADELIST;
import org.typezero.gameserver.network.aion.serverpackets.SM_TRADE_IN_LIST;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.restrictions.RestrictionsManager;
import org.typezero.gameserver.services.craft.CraftSkillUpdateService;
import org.typezero.gameserver.services.craft.RelinquishCraftStatus;
import org.typezero.gameserver.services.instance.DredgionService2;
import org.typezero.gameserver.services.item.ItemChargeService;
import org.typezero.gameserver.services.teleport.PortalService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.services.trade.PricesService;
import org.typezero.gameserver.skillengine.model.SkillTargetSlot;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author VladimirZ
 */
public class DialogService {

	public static void onCloseDialog(Npc npc, Player player) {
		switch (npc.getObjectTemplate().getTitleId()) {
			case 350409:
			case 315073:
			case 463212:
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npc.getObjectId(), 0));
				Legion legion = player.getLegion();
				if (legion != null) {
					LegionWarehouse lwh = player.getLegion().getLegionWarehouse();
					if (lwh.getWhUser() == player.getObjectId()) {
						lwh.setWhUser(0);
					}
				}
				break;
		}
	}

	public static void onDialogSelect(int dialogId, final Player player, Npc npc, int questId, int extendedRewardIndex) {

		QuestEnv env = new QuestEnv(npc, player, questId, dialogId);
		env.setExtendedRewardIndex(extendedRewardIndex);
		int targetObjectId = npc.getObjectId();
		int titleId = npc.getObjectTemplate().getTitleId();

		if (player.getAccessLevel() >= 3 && CustomConfig.ENABLE_SHOW_DIALOGID) {
			PacketSendUtility.sendMessage(player, "dialogId: " + dialogId);
			PacketSendUtility.sendMessage(player, "questId: " + questId);
		}

		if (questId == 0) {
			switch (DialogAction.getActionByDialogId(dialogId)) {
				case BUY: {
					TradeListTemplate tradeListTemplate = DataManager.TRADE_LIST_DATA.getTradeListTemplate(npc.getNpcId());
					if (tradeListTemplate == null) {
						PacketSendUtility.sendMessage(player, "Buy list is missing!!");
						break;
					}
					int tradeModifier = tradeListTemplate.getSellPriceRate();
					PacketSendUtility.sendPacket(player, new SM_TRADELIST(player, npc, tradeListTemplate, PricesService.getVendorBuyModifier()
						* tradeModifier / 100));
					break;
				}
				case OPEN_STIGMA_WINDOW: { // stigma
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 1));
					//PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 0));
                                        //PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401259));
					break;
				}
				case OPEN_STIGMA_ENCHANT: { // stigma enchant
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 53));
					//PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 0));
                                        //PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401259));
					break;
				}
				case CREATE_LEGION: { // create legion
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 2));
					break;
				}
				case DISPERSE_LEGION: { // disband legion
					LegionService.getInstance().requestDisbandLegion(npc, player);
					break;
				}
				case RECREATE_LEGION: { // recreate legion
					LegionService.getInstance().recreateLegion(npc, player);
					break;
				}
				case DEPOSIT_CHAR_WAREHOUSE: { // warehouse (2.5)
					switch (titleId) {
						case 315008:
						case 350417:
						case 462878:
						case 0:
							if (!RestrictionsManager.canUseWarehouse(player))
								return;
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 26));
							WarehouseService.sendWarehouseInfo(player, true);
						break;
					}
					break;
				}
				case OPEN_VENDOR: { // Consign trade?? npc karinerk, koorunerk (2.5)
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 13));
					break;
				}
				case RECOVERY: { // soul healing (2.5)
					final long expLost = player.getCommonData().getExpRecoverable();
					if (expLost == 0) {
						player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
						player.getCommonData().setDeathCount(0);
					}
					final double factor = (expLost < 1000000 ? 0.25 - (0.00000015 * expLost) : 0.1);
					final int price = (int) (expLost * factor);

					RequestResponseHandler responseHandler = new RequestResponseHandler(npc) {

						@Override
						public void acceptRequest(Creature requester, Player responder) {
							if (player.getInventory().getKinah() >= price) {
								PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GET_EXP2(expLost));
								PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SUCCESS_RECOVER_EXPERIENCE);
								player.getCommonData().resetRecoverableExp();
								player.getInventory().decreaseKinah(price);
								player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
								player.getCommonData().setDeathCount(0);
							}
							else {
								PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_KINA(price));
							}
						}

						@Override
						public void denyRequest(Creature requester, Player responder) {
							// no message
						}
					};
					if (player.getCommonData().getExpRecoverable() > 0) {
						boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_RECOVER_EXPERIENCE, responseHandler);
						if (result) {
							PacketSendUtility.sendPacket(player,
								new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_RECOVER_EXPERIENCE, 0, 0, String.valueOf(price)));
						}
					}
					else {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DONOT_HAVE_RECOVER_EXPERIENCE);
					}
					break;
				}
				case ENTER_PVP: { // (2.5)
					switch (npc.getNpcId()) {
						case 204089: // pvp arena in pandaemonium.
							TeleportService2.teleportTo(player, 120010000, 1, 984f, 1543f, 222.1f, (byte) 45, TeleportAnimation.JUMP_AIMATION);
							break;
						case 203764: // pvp arena in sanctum.
							TeleportService2.teleportTo(player, 110010000, 1, 1462.5f, 1326.1f, 564.1f, (byte) 81, TeleportAnimation.JUMP_AIMATION);
							break;
						case 203981:
							TeleportService2.teleportTo(player, 210020000, 1, 439.3f, 422.2f, 274.3f, (byte) 74, TeleportAnimation.JUMP_AIMATION);
							break;
					}
					break;
				}
				case LEAVE_PVP: { // (2.5)
					switch (npc.getNpcId()) {
						case 204087:
							TeleportService2.teleportTo(player, 120010000, 1, 1005.1f, 1528.9f, 222.1f, (byte) 104, TeleportAnimation.JUMP_AIMATION);
							break;
						case 203875:
							TeleportService2.teleportTo(player, 110010000, 1, 1470.3f, 1343.5f, 563.7f, (byte) 21, TeleportAnimation.JUMP_AIMATION);
							break;
						case 203982:
							TeleportService2.teleportTo(player, 210020000, 1, 446.2f, 431.1f, 274.5f, (byte) 15, TeleportAnimation.JUMP_AIMATION);
							break;
					}
					break;
				}
				case GIVE_ITEM_PROC: { // Godstone socketing (2.5)
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 21));
					break;
				}
				case REMOVE_MANASTONE: { // remove mana stone (2.5)
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 20));
					break;
				}
				case CHANGE_ITEM_SKIN: { // modify appearance (2.5)
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 19));
					break;
				}
				case AIRLINE_SERVICE: { // flight and teleport (2.5)
					if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
						int level = player.getLevel();
						if (level < 9) {
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 27));
						}
						else {
							TeleportService2.showMap(player, targetObjectId, npc.getNpcId());
						}
					}
					else {
						switch (npc.getNpcId()) {
							case 203194: {
								if (player.getRace() == Race.ELYOS) {
									QuestState qs = player.getQuestStateList().getQuestState(1006);
									if (qs == null || qs.getStatus() != QuestStatus.COMPLETE)
										PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 27));
									else
										TeleportService2.showMap(player, targetObjectId, npc.getNpcId());
								}
								break;
							}
							case 203679: {
								if (player.getRace() == Race.ASMODIANS) {
									QuestState qs = player.getQuestStateList().getQuestState(2008);
									if (qs == null || qs.getStatus() != QuestStatus.COMPLETE)
										PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 27));
									else
										TeleportService2.showMap(player, targetObjectId, npc.getNpcId());
								}
								break;
							}
							default: {
								TeleportService2.showMap(player, targetObjectId, npc.getNpcId());
							}
						}
					}
					break;
				}
				case GATHER_SKILL_LEVELUP: // improve extraction (2.5)
				case COMBINE_SKILL_LEVELUP: { // learn tailoring armor smithing etc. (2.5)
					CraftSkillUpdateService.getInstance().learnSkill(player, npc);
					break;
				}
				case EXTEND_INVENTORY: { // expand cube (2.5)
					CubeExpandService.expandCube(player, npc);
					break;
				}
				case EXTEND_CHAR_WAREHOUSE: { // (2.5)
					WarehouseService.expandWarehouse(player, npc);
					break;
				}
				case OPEN_LEGION_WAREHOUSE: { // legion warehouse (2.5)
					switch (titleId) {
						case 350409:
						case 315073:
                                                case 463212:
                                                case 358046:
                                                case 358047:
                                                case 358048:
                                                case 358049:
							LegionService.getInstance().openLegionWarehouse(player, npc);
							break;
					}
					break;
				}
				case CLOSE_LEGION_WAREHOUSE: { // WTF??? Quest dialog packet (2.5)
					break;
				}
				case CRAFT: { // (2.5)
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 28));
					break;
				}
				case EDIT_CHARACTER:
				case EDIT_GENDER: { // (2.5)
					byte changesex = 0; // 0 plastic surgery, 1 gender switch
					byte check_ticket = 2; // 2 no ticket, 1 have ticket
					if (dialogId == DialogAction.EDIT_GENDER.id()) {
						// Gender Switch
						changesex = 1;
                        if (player.getInventory().getItemCountByItemId(169660000) > 0 || player.getInventory().getItemCountByItemId(169660001) > 0 || player.getInventory().getItemCountByItemId(169660002) > 0 || player.getInventory().getItemCountByItemId(169660003) > 0) {
                            check_ticket = 1;
                        }
					}
					else {
						// Plastic Surgery
                    	if (player.getInventory().getItemCountByItemId(169650000) > 0 || player.getInventory().getItemCountByItemId(169650001) > 0 || player.getInventory().getItemCountByItemId(169650002) > 0 || player.getInventory().getItemCountByItemId(169650003) > 0
						|| player.getInventory().getItemCountByItemId(169650004) > 0 || player.getInventory().getItemCountByItemId(169650005) > 0 || player.getInventory().getItemCountByItemId(169650006) > 0 || player.getInventory().getItemCountByItemId(169650007) > 0) {
                            check_ticket = 1;
                        }
					}
					PacketSendUtility.sendPacket(player, new SM_PLASTIC_SURGERY(player, check_ticket, changesex));
					player.setEditMode(true);
					break;
				}
				case MATCH_MAKER: // dredgion
					if (AutoGroupConfig.AUTO_GROUP_ENABLE && DredgionService2.getInstance().isDredgionAvialable()) {
						AutoGroupType agt = AutoGroupType.getAutoGroup(npc.getNpcId());
						if (agt != null && agt.isDredgion()) {
							PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(agt.getInstanceMaskId()));
						}
						else {
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 0));
						}
					}
					else {
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 1011));
					}
					break;
				case INSTANCE_ENTRY: { // (2.5)
					break;
				}
				case COMPOUND_WEAPON: { // armsfusion (2.5)
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 29));
					break;
				}
				case DECOMPOUND_WEAPON: { // armsbreaking (2.5)
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 30));
					break;
				}
				case FACTION_JOIN: { // join npcFaction (2.5)
					player.getNpcFactions().enterGuild(npc);
					break;
				}
				case FACTION_SEPARATE: { // leave npcFaction (2.5)
					player.getNpcFactions().leaveNpcFaction(npc);
					break;
				}
				case BUY_AGAIN: { // repurchase (2.5)
					PacketSendUtility.sendPacket(player, new SM_REPURCHASE(player, npc.getObjectId()));
					break;
				}
				case PET_ADOPT: { // adopt pet (2.5)
					PacketSendUtility.sendPacket(player, new SM_PET(6));
					break;
				}
				case PET_ABANDON: { // surrender pet (2.5)
					PacketSendUtility.sendPacket(player, new SM_PET(7));
					break;
				}
				case HOUSING_BUILD: { // housing build
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 32));
					break;
				}
				case HOUSING_DESTRUCT: { // housing destruct
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 33));
					break;
				}
				case CHARGE_ITEM_SINGLE: { // condition an individual item
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 35));
					break;
				}
				case CHARGE_ITEM_MULTI: { // condition all equiped items
					ItemChargeService.startChargingEquippedItems(player, targetObjectId, 1);
					break;
				}
				case TRADE_IN: {
					TradeListTemplate tradeListTemplate = DataManager.TRADE_LIST_DATA.getTradeInListTemplate(npc.getNpcId());
					if (tradeListTemplate == null) {
						PacketSendUtility.sendMessage(player, "Buy list is missing!!");
						break;
					}
					PacketSendUtility.sendPacket(player, new SM_TRADE_IN_LIST(npc, tradeListTemplate, 100));
					break;
				}
				case SELL:
				case TRADE_SELL_LIST: {
					TradeListTemplate tradeListTemplate = DataManager.TRADE_LIST_DATA.getPurchaseTemplate(npc.getNpcId());
					PacketSendUtility.sendPacket(player, new SM_SELL_ITEM(targetObjectId, tradeListTemplate, 100));
					break;
				}
				case GIVEUP_CRAFT_EXPERT: { // relinquish Expert Status
					RelinquishCraftStatus.relinquishExpertStatus(player, npc);
					break;
				}
				case GIVEUP_CRAFT_MASTER: { // relinquish Master Status
					RelinquishCraftStatus.relinquishMasterStatus(player, npc);
					break;
				}
				case HOUSING_PERSONAL_AUCTION: { // housing auction
					if ((player.getHousingStatus() & HousingFlags.BIDDING_ALLOWED.getId()) == 0) {
						if (player.getRace() == Race.ELYOS)
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_OWN_NOT_COMPLETE_QUEST(18802));
						else
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CANT_OWN_NOT_COMPLETE_QUEST(28802));
						return;
					}
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 38));
					break;
				}
				case FUNC_PET_H_ADOPT:
					PacketSendUtility.sendPacket(player, new SM_PET(16));
					break;
				case FUNC_PET_H_ABANDON:
					PacketSendUtility.sendPacket(player, new SM_PET(17));
					break;
				case CHARGE_ITEM_SINGLE2: // augmenting an individual item
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 42));
					break;
				case CHARGE_ITEM_MULTI2: // augmenting all equiped items
					ItemChargeService.startChargingEquippedItems(player, targetObjectId, 2);
					break;
				case HOUSING_RECREATE_PERSONAL_INS: // recreate personal house instance (studio)
					HousingService.getInstance().recreatePlayerStudio(player);
					break;
				case TOWN_CHALLENGE: // town improvement
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 43));
					break;
				case TELEPORT_SIMPLE:
				case SETPRO1:
				case SETPRO2:
				case SETPRO3:
				case SETPRO4:
				case SETPRO5:
					if (QuestEngine.getInstance().onDialog(env)) { //remove this shit after assigning AI portal_dialog
						return;
					}
					TeleporterTemplate template = DataManager.TELEPORTER_DATA.getTeleporterTemplateByNpcId(npc.getNpcId());
					PortalPath portalPath = DataManager.PORTAL2_DATA.getPortalDialog(npc.getNpcId(), dialogId, player.getRace());
					if (portalPath != null) {
						PortalService.port(portalPath, player, targetObjectId);
					}
					else if (template != null) {
						TeleportLocation loc = template.getTeleLocIdData().getTelelocations().get(0);
						if (loc != null) {
							TeleportService2.teleport(template, loc.getLocId(), player, npc,
								npc.getAi2().getName().equals("general") ? TeleportAnimation.JUMP_AIMATION : TeleportAnimation.BEAM_ANIMATION);
						}
					}
					break;
				case ITEM_UPGRADE:
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 52));
					break;
				default:
					if (QuestEngine.getInstance().onDialog(env)) {
						return;
					}
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId));
					break;
			}
		}

		else {
			if (QuestEngine.getInstance().onDialog(env)) {
				return;
			}
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId, questId));
		}
	}
}



