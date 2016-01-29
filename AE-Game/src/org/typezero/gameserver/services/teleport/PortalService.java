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

package org.typezero.gameserver.services.teleport;

import org.typezero.gameserver.configs.administration.AdminConfig;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.configs.main.SecurityConfig;
import org.typezero.gameserver.configs.main.MembershipConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.siege.FortressLocation;
import org.typezero.gameserver.model.team2.alliance.PlayerAlliance;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.model.team2.league.League;
import org.typezero.gameserver.model.templates.InstanceCooltime;
import org.typezero.gameserver.model.templates.portal.*;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMapInstance;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ATracer, xTz
 */
public class PortalService {

	private static Logger log = LoggerFactory.getLogger(PortalService.class);
    private static final Logger debuglog = LoggerFactory.getLogger("INSTANCEDEBUG_LOG");

	public static void port(final PortalPath portalPath, final Player player, int npcObjectId) {

		if (!CustomConfig.ENABLE_INSTANCES) {
			return;
		}

		PortalLoc loc = DataManager.PORTAL_LOC_DATA.getPortalLoc(portalPath.getLocId());
		if (loc == null) {
			log.warn("No portal loc for locId" + portalPath.getLocId());
			return;
		}

		boolean instanceTitleReq = false;
		boolean instanceLevelReq = false;
		boolean instanceRaceReq = false;
		boolean instanceQuestReq = false;
		boolean instanceGroupReq = false;
		int instanceCooldownRate = 0;
		int mapId = loc.getWorldId();
		int playerSize = portalPath.getPlayerCount();
		boolean isInstance = portalPath.isInstance();

		if (player.getAccessLevel() < AdminConfig.INSTANCE_REQ) {
			instanceTitleReq = !player.havePermission(MembershipConfig.INSTANCES_TITLE_REQ);
			instanceLevelReq = !player.havePermission(MembershipConfig.INSTANCES_LEVEL_REQ);
			instanceRaceReq = !player.havePermission(MembershipConfig.INSTANCES_RACE_REQ);
			instanceQuestReq = !player.havePermission(MembershipConfig.INSTANCES_QUEST_REQ);
			instanceGroupReq = !player.havePermission(MembershipConfig.INSTANCES_GROUP_REQ);
			instanceCooldownRate = InstanceService.getInstanceRate(player, loc.getWorldId());
		}

		if (instanceRaceReq && !checkRace(player, portalPath.getRace())) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MOVE_PORTAL_ERROR_INVALID_RACE);
			return;
		}
		if (instanceGroupReq && !checkPlayerSize(player, portalPath, npcObjectId)) {
			return;
		}
		int sigeId = portalPath.getSigeId();
		if (instanceRaceReq && sigeId != 0) {
			if (!checkSigeId(player, sigeId)) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MOVE_PORTAL_ERROR_INVALID_RACE);
				return;
			}
		}
		PortalReq portalReq = portalPath.getPortalReq();
		if (portalReq != null) {
			if (instanceLevelReq && !checkEnterLevel(player, mapId, portalReq, npcObjectId)) {
				return;
			}
			if (instanceQuestReq && !checkQuestsReq(player, npcObjectId, portalReq.getQuestReq())) {
				return;
			}
			int titleId = portalReq.getTitleId();
			if (instanceTitleReq && titleId != 0) {
				if (!checkTitle(player, titleId)) {
					PacketSendUtility.sendMessage(player, "You must have correct title.");
					return;
				}
			}
			if (!checkKinah(player, portalReq.getKinahReq())) {
				return;
			}
			if (SecurityConfig.INSTANCE_KEYCHECK && !checkItemReq(player, portalReq.getItemReq())) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_CANT_ENTER_WITHOUT_ITEM);
				return;
			}
		}

		boolean reenter = false;
		int useDelay = 0;
		int instanceCooldown = 0;
		InstanceCooltime clt = DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(mapId);
		if (clt != null) {
			instanceCooldown = clt.getEntCoolTime();
		}
		if (instanceCooldownRate > 0) {
			useDelay = instanceCooldown / instanceCooldownRate;
		}
		WorldMapInstance instance = null;
		if (player.getPortalCooldownList().isPortalUseDisabled(mapId) && useDelay > 0) {
			switch (playerSize) {
				case 0: // solo
					instance = InstanceService.getRegisteredInstance(mapId, player.getObjectId());
					break;
				case 6: // group
					if (player.getPlayerGroup2() != null) {
						instance = InstanceService.getRegisteredInstance(mapId, player.getPlayerGroup2().getTeamId());
					}
					break;
				default:  // alliance
					if (player.isInAlliance2()) {
						if (player.isInLeague()) {
							instance = InstanceService.getRegisteredInstance(mapId, player.getPlayerAlliance2().getLeague().getObjectId());
                        } else {
                            instance = InstanceService.getRegisteredInstance(mapId, player.getPlayerAlliance2().getObjectId());
                        }
                    }
                    break;
            }

            if (instance == null) {
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANNOT_MAKE_INSTANCE_COOL_TIME);
                return;
            } else {
                if (!instance.isRegistered(player.getObjectId())) {
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANNOT_MAKE_INSTANCE_COOL_TIME);
                    return;
                } else {
                    reenter = true;
                    log.info(player.getName() + "has been in intance and also have cd, can reenter.");
                }
            }
        } else {
            log.warn(player.getName() + " doesn't have cd of this instance, can enter and will be registed to this intance");
        }
        PlayerGroup group = player.getPlayerGroup2();
        switch (playerSize) {
            case 0:
                // If there is a group (whatever group requirement exists or not)...
                if (group != null && !instanceGroupReq) {
                    instance = InstanceService.getRegisteredInstance(mapId, group.getTeamId());
                }
                // But if there is no group, go to solo
                else {
                    instance = InstanceService.getRegisteredInstance(mapId, player.getObjectId());
                }

                // No group instance, group on and default requirement off
                if (instance == null && group != null && !instanceGroupReq) {
                    // For each player from group
                    for (Player member : group.getMembers()) {
                        // Get his instance
                        instance = InstanceService.getRegisteredInstance(mapId, member.getObjectId());

                        // If some player is soloing and I found no one else yet, I get his instance
                        if (instance != null) {
                            break;
                        }

                    }

                   // No solo instance found
                    if (instance == null && isInstance) {
                        for (Player member : group.getMembers()) {
                            if (member.getPortalCooldownList().isPortalUseDisabled(mapId)) {
                                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
                                return;
                            }
                        }
                        instance = registerGroup(group, mapId);
                    }
                }
                // if already registered - just teleport
                if (instance != null) {
                    if (loc.getWorldId() != player.getWorldId()) {
                        reenter = true;
                        transfer(player, loc, instance, reenter);
                        return;
                    }
                }
                port(player, loc, reenter, isInstance);
                break;
            case 6:
                if (group != null || !instanceGroupReq) {
                    // If there is a group (whatever group requirement exists or not)...
                    if (group != null) {
                        if (instance != null && instance.getPlayersInside() != null && clt != null && clt.getMaxMemberDark() != null && clt.getMaxMemberLight() != null && instance.getPlayersInside().size() > (player.getRace() != Race.ELYOS ? clt.getMaxMemberDark() : clt.getMaxMemberLight())) {
                            debuglog.info("[FULL] Player " + player.getName() + "try port to FULL instance MapID " + (instance != null ? instance.getMapId() : "null") + ". ReflectionID: " + (instance != null ? instance.getInstanceId() : "null"));
                            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LEAVE_INSTANCE_NOT_PARTY);
                            return;
                        }
                        instance = InstanceService.getRegisteredInstance(mapId, group.getTeamId());
                        debuglog.info("Prepare to port player(already registered) " + player.getName() + ". Group: " + player.getPlayerGroup2().getMembers() + ". MapID: " + (instance != null ? instance.getMapId() : "null") + ". ReflectionID: " + (instance != null ? instance.getInstanceId() : "null"));
                    }
                    // But if there is no group (and solo is enabled, of course)
                    else {
                        instance = InstanceService.getRegisteredInstance(mapId, player.getObjectId());
                    }

                    // No instance (for group), group on and default requirement off
                    if (instance == null && group != null && !instanceGroupReq) {
                        // For each player from group
                        for (Player member : group.getMembers()) {
                            // Get his instance
                            instance = InstanceService.getRegisteredInstance(mapId, member.getObjectId());

                            // If some player is soloing and I found no one else yet, I get his instance
                            if (instance != null) {
                                break;
                            }
                        }

                        if (instance == null && isInstance) {
                            for (Player member : group.getMembers()) {
                                if (member.getPortalCooldownList().isPortalUseDisabled(mapId)) {
                                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
                                    break;
                                }
                            }
                        }

                        // No solo instance found
                        if (instance == null) {
                            for (Player member : group.getMembers()) {
                                if (member.getPortalCooldownList().isPortalUseDisabled(mapId)) {
                                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
                                    return;
                                }
                            }
                            instance = registerGroup(group, mapId);
                            debuglog.info("Prepare to port player(new group register) " + player.getName() + ". Group: " + player.getPlayerGroup2().getMembers() + ". MapID: " + (instance != null ? instance.getMapId() : "null") + ". ReflectionID: " + (instance != null ? instance.getInstanceId() : "null"));
                        }
                    }
                    // No instance and default requirement on = Group on
                    else if (instance == null && instanceGroupReq) {
                        for (Player member : group.getMembers()) {
                            if (member.getPortalCooldownList().isPortalUseDisabled(mapId)) {
                                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
                                return;
                            }
                        }
                        instance = registerGroup(group, mapId);
                    }
                    // No instance, default requirement off, no group = Register new instance with player ID
                    else if (instance == null && !instanceGroupReq && group == null) {
                        instance = InstanceService.getNextAvailableInstance(mapId);
                    }

                    transfer(player, loc, instance, reenter);
                }
                break;
            default:
                PlayerAlliance allianceGroup = player.getPlayerAlliance2();
                if (allianceGroup != null || !instanceGroupReq) {
                    Integer allianceId = player.getObjectId();
                    League league = null;
                    if (allianceGroup != null) {
                        league = allianceGroup.getLeague();
                        if (player.isInLeague()) {
                            allianceId = league.getObjectId();
                        } else {
                            allianceId = allianceGroup.getObjectId();

                        }
                        instance = InstanceService.getRegisteredInstance(mapId, allianceId);
                    } else {
                        instance = InstanceService.getRegisteredInstance(mapId, allianceId);
                    }

                    if (instance == null && allianceGroup != null && !instanceGroupReq) {
                        if (league != null) {
                            for (PlayerAlliance alliance : allianceGroup.getLeague().getMembers()) {
                                for (Player member : alliance.getMembers()) {
                                    instance = InstanceService.getRegisteredInstance(mapId, member.getObjectId());
                                    if (instance != null) {
                                        break;
                                    }
                                }
                            }
                        } else {
                            for (Player member : allianceGroup.getMembers()) {
                                instance = InstanceService.getRegisteredInstance(mapId, member.getObjectId());
                                if (instance != null) {
                                    break;
                                }
                            }
                        }
                        if (instance == null) {
                            if (league != null) {
                                instance = registerLeague(league, mapId);
                            } else {
                                instance = registerAlliance(allianceGroup, mapId);
                            }
                        }
                    } else if (instance == null && instanceGroupReq) {
                        if (league != null) {
                            if(league.getMembers().size() > playerSize)
                            {
                                PacketSendUtility.sendMessage(player, "To many party members for this instance, MAX = " +playerSize);
                                return;
                            }
                            instance = registerLeague(league, mapId);
                            debuglog.info("Prepare to port player(new group register) " + player.getName() + ". Group: " + league.getMembers() + ". MapID: " + (instance != null ? instance.getMapId() : "null") + ". ReflectionID: " + (instance != null ? instance.getInstanceId() : "null"));
                        } else {
                            if(allianceGroup.getMembers().size() > playerSize)
                            {
                                PacketSendUtility.sendMessage(player, "To many party members for this instance, MAX = " +playerSize);
                                return;
                            }
                            instance = registerAlliance(allianceGroup, mapId);
                            debuglog.info("Prepare to port player(new group register) " + player.getName() + ". Group: " + allianceGroup.getMembers() + ". MapID: " + (instance != null ? instance.getMapId() : "null") + ". ReflectionID: " + (instance != null ? instance.getInstanceId() : "null"));
                        }
                    } else if (instance == null && !instanceGroupReq && allianceGroup == null) {
                        instance = InstanceService.getNextAvailableInstance(mapId);
                    }
                    if (instance.getPlayersInside().size() < playerSize) {
                       transfer(player, loc, instance, reenter);
                    }
                }
                break;
        }
    }

    private static boolean checkKinah(Player player, int kinah) {
        Storage inventory = player.getInventory();
        if (!inventory.tryDecreaseKinah(kinah)) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_KINA(kinah));
            return false;
        }
        return true;
    }

    private static boolean checkEnterLevel(Player player, int mapId, PortalReq portalReq, int npcObjectId) {
        int enterMinLvl = portalReq.getMinLevel();
        int enterMaxLvl = portalReq.getMaxLevel();
        int lvl = player.getLevel();
        InstanceCooltime instancecooltime = DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(mapId);
        if (instancecooltime != null && player.isMentor()) {
            if (!instancecooltime.getCanEnterMentor()) {
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_MENTOR_CANT_ENTER(World.getInstance().getWorldMap(mapId).getName()));
                return false;
            }
        }
        if (lvl > enterMaxLvl || lvl < enterMinLvl) {
            int errDialog = portalReq.getErrLevel();
            if (errDialog != 0) {
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcObjectId, errDialog));
            } else {
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_LEVEL);
            }
            return false;
        }
        return true;
    }

    private static boolean checkPlayerSize(Player player, PortalPath portalPath, int npcObjectId) {
        int playerSize = portalPath.getPlayerCount();
        if (playerSize == 6) { // group
            if (!player.isInGroup2()) {
                int errDialog = portalPath.getErrGroup();
                if (errDialog != 0) {
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcObjectId, errDialog));
                } else {
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ENTER_ONLY_PARTY_DON);
                }
                return false;
            }
        } else if (playerSize > 6 && playerSize <= 24) { // alliance
            if (!player.isInAlliance2()) {
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ENTER_ONLY_FORCE_DON);
                return false;
            }
        } else if (playerSize > 24) { // league
            if (!player.isInLeague()) {
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401251));
                return false;
            }
        }
        return true;
    }

    private static boolean checkRace(Player player, Race portalRace) {
        return player.getRace().equals(portalRace) || portalRace.equals(Race.PC_ALL);
    }

    private static boolean checkSigeId(Player player, int sigeId) {
        FortressLocation loc = SiegeService.getInstance().getFortress(sigeId);
        if (loc != null) {
            if (loc.getRace().getRaceId() != player.getRace().getRaceId()) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkTitle(Player player, int titleId) {
        return player.getCommonData().getTitleId() == titleId;
    }

    private static boolean checkQuestsReq(Player player, int npcObjectId, List<QuestReq> questReq) {
        if (questReq != null) {
            for (QuestReq quest : questReq) {
                int questId = quest.getQuestId();
                int questStep = quest.getQuestStep();
                final QuestState qs = player.getQuestStateList().getQuestState(questId);
                if (qs == null || (questStep == 0 && qs.getStatus() != QuestStatus.COMPLETE
                        || (qs.getQuestVarById(0) < quest.getQuestStep() && qs.getStatus() != QuestStatus.COMPLETE))) {
                    int errDialog = quest.getErrQuest();
                    if (errDialog != 0) {
                        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npcObjectId, errDialog));
                    } else {
                        PacketSendUtility.sendMessage(player, "You must complete the entrance quest.");
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkItemReq(Player player, List<ItemReq> itemReq) {
        if (itemReq != null) {
            Storage inventory = player.getInventory();
            for (ItemReq item : itemReq) {
                if (inventory.getItemCountByItemId(item.getItemId()) < item.getItemCount()) {
                    return false;
                }
            }
            for (ItemReq item : itemReq) {
                inventory.decreaseByItemId(item.getItemId(), item.getItemCount());
            }
        }
        return true;
    }

    private static void port(Player requester, PortalLoc loc, boolean reenter, boolean isInstance) {
        WorldMapInstance instance = null;

        if (isInstance) {
            instance = InstanceService.getNextAvailableInstance(loc.getWorldId(), requester.getObjectId());
            //InstanceService.registerPlayerWithInstance(instance, requester);
            transfer(requester, loc, instance, reenter);
        } else {
            /*WorldMap worldMap = World.getInstance().getWorldMap(worldId);
			if (worldMap == null) {
				log.warn("There is no registered map with id " + worldId);
				return;
			}
			instance = worldMap.getWorldMapInstance();*/
            easyTransfer(requester, loc);
        }
    }

    private static WorldMapInstance registerGroup(PlayerGroup group, int mapId) {
        WorldMapInstance instance = InstanceService.getNextAvailableInstance(mapId);
        InstanceService.registerGroupWithInstance(instance, group);
        return instance;
    }

    private static WorldMapInstance registerAlliance(PlayerAlliance group, int mapId) {
        WorldMapInstance instance = InstanceService.getNextAvailableInstance(mapId);
        InstanceService.registerAllianceWithInstance(instance, group);
        return instance;
    }

    private static WorldMapInstance registerLeague(League group, int mapId) {
        WorldMapInstance instance = InstanceService.getNextAvailableInstance(mapId);
        InstanceService.registerLeagueWithInstance(instance, group);
        return instance;
    }

    private static void transfer(Player player, PortalLoc loc, WorldMapInstance instance, boolean reenter) {
        long useDelay = DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, instance.getMapId());
        player.getPortalCooldownList().addPortalCooldown(instance.getMapId(), useDelay);
        player.setInstanceStartPos(loc.getX(), loc.getY(), loc.getZ());
        InstanceService.registerPlayerWithInstance(instance, player);
        TeleportService2.teleportTo(player, loc.getWorldId(), instance.getInstanceId(), loc.getX(),
                loc.getY(), loc.getZ(), loc.getH(), TeleportAnimation.BEAM_ANIMATION);
    }

    private static void easyTransfer(Player player, PortalLoc loc) {
        TeleportService2.teleportTo(player, loc.getWorldId(), loc.getX(),
                loc.getY(), loc.getZ(), loc.getH(), TeleportAnimation.BEAM_ANIMATION);
    }
}
