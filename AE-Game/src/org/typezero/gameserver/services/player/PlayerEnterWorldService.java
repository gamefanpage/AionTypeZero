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

package org.typezero.gameserver.services.player;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.versionning.Version;
import org.typezero.gameserver.GameServer;
import org.typezero.gameserver.cache.HTMLCache;
import org.typezero.gameserver.configs.administration.AdminConfig;
import org.typezero.gameserver.configs.main.*;
import org.typezero.gameserver.dao.*;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.ChatType;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.account.Account;
import org.typezero.gameserver.model.account.CharacterBanInfo;
import org.typezero.gameserver.model.account.CharacterPasskey.ConnectType;
import org.typezero.gameserver.model.account.PlayerAccountData;
import org.typezero.gameserver.model.gameobjects.HouseObject;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.model.gameobjects.player.emotion.Emotion;
import org.typezero.gameserver.model.gameobjects.player.motion.Motion;
import org.typezero.gameserver.model.gameobjects.player.title.Title;
import org.typezero.gameserver.model.gameobjects.state.CreatureSeeState;
import org.typezero.gameserver.model.gameobjects.state.CreatureVisualState;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.items.storage.IStorage;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.items.storage.StorageType;
import org.typezero.gameserver.model.skill.PlayerSkillEntry;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceService;
import org.typezero.gameserver.model.team2.group.PlayerGroupService;
import org.typezero.gameserver.model.templates.QuestTemplate;
import org.typezero.gameserver.model.templates.quest.QuestExtraCategory;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.serverpackets.*;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.*;
import org.typezero.gameserver.services.PunishmentService.PunishmentType;
import org.typezero.gameserver.services.abyss.AbyssSkillService;
import org.typezero.gameserver.services.craft.RelinquishCraftStatus;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.json.JsonService;
import org.typezero.gameserver.services.mail.MailService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.services.toypet.PetService;
import org.typezero.gameserver.services.transfers.PlayerTransferService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.skillengine.effect.AbnormalState;
import org.typezero.gameserver.taskmanager.tasks.ExpireTimerTask;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.utils.audit.AuditLogger;
import org.typezero.gameserver.utils.audit.GMService;
import org.typezero.gameserver.utils.collections.ListSplitter;
import org.typezero.gameserver.utils.rates.Rates;
import org.typezero.gameserver.world.World;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ATracer
 */
public final class PlayerEnterWorldService {

	private static final Logger log = LoggerFactory.getLogger("GAMECONNECTION_LOG");
	private static final String serverInfo = "=============================";
	private static final String serverName = "Welcome on " + GSConfig.SERVER_NAME + " server!";
	private static final String serverIntro = "Corresponding to your current ranking, a specific number of Honour Points will be deducted from you every day.";
	private static final String alInfo;
	private static final Set<Integer> pendingEnterWorld = new HashSet<Integer>();

	static {
		String alBuffer;

		alBuffer = "==========Web Information==========\n";
		alBuffer += GSConfig.SITE + "\n";
		alBuffer += GSConfig.FORUM + "\n";
		alBuffer += GSConfig.VK + "\n";
		//alBuffer += "=======\u0418\u0433\u0440\u043e\u0432\u043e\u0439 \u0431\u0430\u043b\u0430\u043d\u0441=======\n";
		//alBuffer += GSConfig.MMOTOP + "\n";
		//alBuffer += GSConfig.CASH_POINT + "\n";

		if (GSConfig.SERVER_MOTD_DISPLAYREV) {
			alBuffer += "-----------------------------\n";
			alBuffer += "Server Revision: " + String.format("%-6s", new Version(GameServer.class).getRevision()) + "\n";
		}

		alInfo = alBuffer;
		alBuffer = null;
	}

	/**
	 * @param objectId
	 * @param client
	 */
	public static final void startEnterWorld(final int objectId, final AionConnection client) {
		// check if char is banned
		PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(objectId);
		if (playerAccData == null) {
			log.warn("playerAccData == null " + objectId);
			return;
		}
		if (playerAccData.getPlayerCommonData() == null) {
			log.warn("playerAccData.getPlayerCommonData() == null " + objectId);
			return;
		}
		Timestamp lastOnline = playerAccData.getPlayerCommonData().getLastOnline();
		if (lastOnline != null && client.getAccount().getAccessLevel() < AdminConfig.GM_LEVEL) {
			if (System.currentTimeMillis() - lastOnline.getTime() < (GSConfig.CHARACTER_REENTRY_TIME * 1000)) {
				client.sendPacket(new SM_ENTER_WORLD_CHECK((byte) 6)); // 20 sec time
				return;
			}
		}
		CharacterBanInfo cbi = client.getAccount().getPlayerAccountData(objectId).getCharBanInfo();
		if (cbi != null) {
			if (cbi.getEnd() > System.currentTimeMillis() / 1000) {
				client.close(new SM_QUIT_RESPONSE(), false);
				return;
			}
			else {
				DAOManager.getDAO(PlayerPunishmentsDAO.class).unpunishPlayer(objectId, PunishmentType.CHARBAN);
			}
		}
		// passkey check
		if (SecurityConfig.PASSKEY_ENABLE && !client.getAccount().getCharacterPasskey().isPass()) {
			showPasskey(objectId, client);
		}
		else {
			validateAndEnterWorld(objectId, client);
		}
	}

	/**
	 * @param objectId
	 * @param client
	 */
	private static final void showPasskey(final int objectId, final AionConnection client) {
		client.getAccount().getCharacterPasskey().setConnectType(ConnectType.ENTER);
		client.getAccount().getCharacterPasskey().setObjectId(objectId);
		boolean isExistPasskey = DAOManager.getDAO(PlayerPasskeyDAO.class).existCheckPlayerPasskey(client.getAccount().getId());

		if (!isExistPasskey)
			client.sendPacket(new SM_CHARACTER_SELECT(0));
		else
			client.sendPacket(new SM_CHARACTER_SELECT(1));
	}

	/**
	 * @param objectId
	 * @param client
	 */
	private static final void validateAndEnterWorld(final int objectId, final AionConnection client) {
		synchronized (pendingEnterWorld) {
			if (pendingEnterWorld.contains(objectId)) {
				log.warn("Skipping enter world " + objectId);
				return;
			}
			pendingEnterWorld.add(objectId);
		}
		int delay = 0;
		// double checked enter world
		if (World.getInstance().findPlayer(objectId) != null) {
			delay = 15000;
			log.warn("Postponed enter world " + objectId);
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				try {
					Player player = World.getInstance().findPlayer(objectId);
					if (player != null) {
						AuditLogger.info(player, "Duplicate player in world");
						client.close(new SM_QUIT_RESPONSE(), false);
						return;
					}
					enterWorld(client, objectId);
				}
				catch (Throwable ex) {
					log.error("Error during enter world " + objectId, ex);
				}
				finally {
					synchronized (pendingEnterWorld) {
						pendingEnterWorld.remove(objectId);
					}
				}
			}

		}, delay);
	}

	/**
	 * @param client
	 * @param objectId
	 */
	public static final void enterWorld(AionConnection client, int objectId) {
		Account account = client.getAccount();
		PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(objectId);

		if (playerAccData == null) {
			// Somebody wanted to login on character that is not at his account
			return;
		}
		Player player = PlayerService.getPlayer(objectId, account);

		if (player != null && client.setActivePlayer(player)) {
			player.setClientConnection(client);

			log.info("[MAC_AUDIT] Player " + player.getName() + " (account " + account.getName() + ") has entered world with "
					+ client.getMacAddress() + " MAC.");
			World.getInstance().storeObject(player);

			StigmaService.onPlayerLogin(player);

			/**
			 * Energy of Repose must be calculated before sending SM_STATS_INFO
			 */
			if (playerAccData.getPlayerCommonData().getLastOnline() != null) {
				long lastOnline = playerAccData.getPlayerCommonData().getLastOnline().getTime();
				PlayerCommonData pcd = player.getCommonData();
				long secondsOffline = (System.currentTimeMillis() / 1000) - lastOnline / 1000;
				if (pcd.isReadyForSalvationPoints()) {
					// 10 mins offline = 0 salvation points.
					if (secondsOffline > 10 * 60) {
						player.getCommonData().resetSalvationPoints();
					}
				}
				if (pcd.isReadyForReposteEnergy()) {
					pcd.updateMaxReposte();
					// more than 4 hours offline = start counting Reposte Energy addition.
					if (secondsOffline > 4 * 3600) {
						double hours = secondsOffline / 3600d;
						long maxRespose = player.getCommonData().getMaxReposteEnergy();
						if (hours > 24)
							hours = 24;
						// 24 hours offline = 100% Reposte Energy
						long addResposeEnergy = (long) ((hours / 24) * maxRespose);

						// Additional Energy of Repose bonus
						// TODO: use player house zones
						if (player.getHouseOwnerId() / 10000 * 10000 == player.getWorldId()) {
							switch (player.getActiveHouse().getHouseType()) {
                                case STUDIO:
									addResposeEnergy *= 1.05f;
									break;
								case MANSION:
									addResposeEnergy *= 1.08f;
									break;
								case ESTATE:
									addResposeEnergy *= 1.15f;
									break;
								case PALACE:
									addResposeEnergy *= 1.50f;
                                    break;
                                default:
                                    addResposeEnergy *= 1.10f;
                                    break;
							}
						}

						pcd.addReposteEnergy(addResposeEnergy > maxRespose ? maxRespose : addResposeEnergy);
					}
				}
				if (((System.currentTimeMillis() / 1000) - lastOnline) > 300)
					player.getCommonData().setDp(0);
			}

            long lastOnlineL = playerAccData.getPlayerCommonData().getLastOnline().getTime();

            if (player.getEffectController().hasAbnormalEffect(11885) && ((System.currentTimeMillis() - lastOnlineL )/1000) > 300)
            {
                player.getEffectController().removeEffect(11885);
                player.getEffectController().removeEffect(11912);
            }

            if (player.getEffectController().hasAbnormalEffect(11886) && ((System.currentTimeMillis() - lastOnlineL )/1000) > 300)
            {
                player.getEffectController().removeEffect(11886);
                player.getEffectController().removeEffect(11913);
            }

            if (player.getEffectController().hasAbnormalEffect(11887) && ((System.currentTimeMillis() - lastOnlineL )/1000) > 300)
            {
                player.getEffectController().removeEffect(11887);
                player.getEffectController().removeEffect(11914);
            }

            if (player.getEffectController().hasAbnormalEffect(11888) && ((System.currentTimeMillis() - lastOnlineL )/1000) > 300)
            {
                player.getEffectController().removeEffect(11888);
                player.getEffectController().removeEffect(11915);
            }

            if (player.getEffectController().hasAbnormalEffect(11889) && ((System.currentTimeMillis() - lastOnlineL )/1000) > 300)
            {
                player.getEffectController().removeEffect(11889);
                player.getEffectController().removeEffect(11916);
            }

            if (player.getEffectController().hasAbnormalEffect(11890) && ((System.currentTimeMillis() - lastOnlineL )/1000) > 300)
            {
                player.getEffectController().removeEffect(11890);
            }

            if (player.getEffectController().hasAbnormalEffect(11890) && ((System.currentTimeMillis() - lastOnlineL )/1000) > 300)
            {
                player.getEffectController().removeEffect(11890);
                player.getEffectController().removeEffect(11907);
            }

            if (player.getEffectController().hasAbnormalEffect(11891) && ((System.currentTimeMillis() - lastOnlineL )/1000) > 300)
            {
                player.getEffectController().removeEffect(11891);
                player.getEffectController().removeEffect(11908);
            }

            if (player.getEffectController().hasAbnormalEffect(11892) && ((System.currentTimeMillis() - lastOnlineL )/1000) > 300)
            {
                player.getEffectController().removeEffect(11892);
                player.getEffectController().removeEffect(11909);
            }

            if (player.getEffectController().hasAbnormalEffect(11893) && ((System.currentTimeMillis() - lastOnlineL )/1000) > 300)
            {
                player.getEffectController().removeEffect(11893);
                player.getEffectController().removeEffect(11910);
            }

            if (player.getEffectController().hasAbnormalEffect(11894) && ((System.currentTimeMillis() - lastOnlineL )/1000) > 300)
            {
                player.getEffectController().removeEffect(11894);
                player.getEffectController().removeEffect(11911);
            }

			InstanceService.onPlayerLogin(player);
			client.sendPacket(new SM_UNK_3_5_1());
			if (!player.getSkillList().isSkillPresent(302)) {
				player.getSkillList().addSkill(player, 302, 129);
			}
			// Update player skills first!!!
			AbyssSkillService.onEnterWorld(player);
			// TODO: check the split size
			client.sendPacket(new SM_SKILL_LIST(player, player.getSkillList().getBasicSkills()));
			for (PlayerSkillEntry stigmaSkill : player.getSkillList().getStigmaSkills())
				client.sendPacket(new SM_SKILL_LIST(player, stigmaSkill));

			if (player.getSkillCoolDowns() != null)
				client.sendPacket(new SM_SKILL_COOLDOWN(player.getSkillCoolDowns()));

			if (player.getItemCoolDowns() != null)
				client.sendPacket(new SM_ITEM_COOLDOWN(player.getItemCoolDowns()));

			FastList<QuestState> questList = FastList.newInstance();
			FastList<QuestState> completeQuestList = FastList.newInstance();
			for (QuestState qs : player.getQuestStateList().getAllQuestState()) {
				QuestTemplate questTemplate = DataManager.QUEST_DATA.getQuestById(qs.getQuestId());
				if (questTemplate.getExtraCategory() != QuestExtraCategory.NONE)
					continue;
				if (qs.getStatus() == QuestStatus.NONE && qs.getCompleteCount() == 0)
					continue;
				if (qs.getStatus() != QuestStatus.COMPLETE && qs.getStatus() != QuestStatus.NONE)
					questList.add(qs);
				if (qs.getCompleteCount() > 0)
					completeQuestList.add(qs);
			}
			client.sendPacket(new SM_QUEST_COMPLETED_LIST(completeQuestList));
			client.sendPacket(new SM_QUEST_LIST(questList));
			client.sendPacket(new SM_TITLE_INFO(player.getCommonData().getTitleId()));
			client.sendPacket(new SM_TITLE_INFO(6, player.getCommonData().getBonusTitleId()));
			client.sendPacket(new SM_MOTION(player.getMotions().getMotions().values()));
			client.sendPacket(new SM_ENTER_WORLD_CHECK());

			byte[] uiSettings = player.getPlayerSettings().getUiSettings();
			byte[] shortcuts = player.getPlayerSettings().getShortcuts();
			byte[] houseBuddies = player.getPlayerSettings().getHouseBuddies();

			if (uiSettings != null)
				client.sendPacket(new SM_UI_SETTINGS(uiSettings, 0));

			if (shortcuts != null)
				client.sendPacket(new SM_UI_SETTINGS(shortcuts, 1));

			if (houseBuddies != null)
				client.sendPacket(new SM_UI_SETTINGS(houseBuddies, 2));

			sendItemInfos(client, player);
			playerLoggedIn(player);

			client.sendPacket(new SM_INSTANCE_INFO(player, false));

			client.sendPacket(new SM_CHANNEL_INFO(player.getPosition()));

			KiskService.getInstance().onLogin(player);
			TeleportService2.sendSetBindPoint(player);

			// Without player spawn initialization can't access to his mapRegion for chk below
			World.getInstance().preSpawn(player);
			player.getController().validateLoginZone();
			VortexService.getInstance().validateLoginZone(player);

			client.sendPacket(new SM_PLAYER_SPAWN(player));

			// SM_WEATHER miss on login (but he 'live' in CM_LEVEL_READY.. need invistigate)
			client.sendPacket(new SM_GAME_TIME());

			SerialKillerService.getInstance().onLogin(player);

			if (player.isLegionMember())
				LegionService.getInstance().onLogin(player);

			client.sendPacket(new SM_TITLE_INFO(player));
			client.sendPacket(new SM_EMOTION_LIST((byte) 0, player.getEmotions().getEmotions()));

			// SM_INFLUENCE_RATIO, SM_SIEGE_LOCATION_INFO, SM_RIFT_ANNOUNCE (Balaurea), SM_RIFT_ANNOUNCE (Tiamaranta)
			SiegeService.getInstance().onPlayerLogin(player);

			client.sendPacket(new SM_PRICES());
			DisputeLandService.getInstance().onLogin(player);
			client.sendPacket(new SM_ABYSS_RANK(player.getAbyssRank()));
            if (CustomConfig.FATIGUE_SYSTEM_ENABLED) {
                PlayerFatigueService.getInstance().onPlayerLogin(player);
            }
			// Intro message
			PacketSendUtility.sendBrightYellowMessage(player, serverInfo);
			PacketSendUtility.sendBrightYellowMessageOnCenter(player, serverName);
			PacketSendUtility.sendBrightYellowMessage(player, serverIntro);
			//PacketSendUtility.sendWhiteMessage(player, alInfo);

			player.setRates(Rates.getRatesFor(client.getAccount().getMembership()));

			if (CustomConfig.PREMIUM_NOTIFY) {
				showPremiumAccountInfo(client, account);
                                showToll(player);
			}
          if (player.getEffectController().hasAbnormalEffect(20364))
            {
                player.getEffectController().removeEffect(20364);
            }
            if (player.getEffectController().hasAbnormalEffect(20365))
            {
                player.getEffectController().removeEffect(20365);
            }

            if (CustomConfig.DISPLAY_RATE) {
                PacketSendUtility.sendYellowMessage(player, MuiService.getInstance().getMessage("ANNOUNCE_RATES"));
                PacketSendUtility.sendYellowMessage(player, MuiService.getInstance().getMessage("ANNOUNCE_RATES_XP", player.getRates().getXpRate()));
                PacketSendUtility.sendYellowMessage(player, MuiService.getInstance().getMessage("ANNOUNCE_RATES_QS", player.getRates().getQuestXpRate()));
                PacketSendUtility.sendYellowMessage(player, MuiService.getInstance().getMessage("ANNOUNCE_RATES_DR", player.getRates().getDropRate()));
                PacketSendUtility.sendYellowMessage(player, MuiService.getInstance().getMessage("ANNOUNCE_RATES_AP", player.getRates().getApPlayerGainRate()));
            }

            PacketSendUtility.sendBrightYellowMessage(player, alInfo);

			if (player.isGM()) {
				if (AdminConfig.INVULNERABLE_GM_CONNECTION || AdminConfig.INVISIBLE_GM_CONNECTION
						|| AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Neutral")
						|| AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Enemy") || AdminConfig.VISION_GM_CONNECTION
						|| AdminConfig.WHISPER_GM_CONNECTION) {
					PacketSendUtility.sendMessage(player, "=============================");
					if (AdminConfig.INVULNERABLE_GM_CONNECTION) {
						player.setInvul(true);
						PacketSendUtility.sendMessage(player, ">> Mode enabled: Immortality. <<");
					}
					if (AdminConfig.INVISIBLE_GM_CONNECTION) {
						player.getEffectController().setAbnormal(AbnormalState.HIDE.getId());
						player.setVisualState(CreatureVisualState.HIDE20);
						PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
						PacketSendUtility.sendMessage(player, ">> Mode enabled: Invisible. <<");
					}
					if (AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Neutral")) {
						player.setAdminNeutral(3);
						player.setAdminEnmity(0);
						PacketSendUtility.sendMessage(player, ">> Mode enabled: Neutral. <<");
					}
					if (AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Enemy")) {
						player.setAdminNeutral(0);
						player.setAdminEnmity(3);
						PacketSendUtility.sendMessage(player, ">> Mode enabled: Aggresive. <<");
					}
					if (AdminConfig.VISION_GM_CONNECTION) {
						player.setSeeState(CreatureSeeState.SEARCH10);
						PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
						PacketSendUtility.sendMessage(player, ">> Mode enabled: Visible. <<");
					}
					if (AdminConfig.WHISPER_GM_CONNECTION) {
						player.setUnWispable();
						PacketSendUtility.sendMessage(player, ">> Accepting Whisper : OFF <<");
					}
					PacketSendUtility.sendMessage(player, "=============================");
				}
                // Special skill for gm
                if (player.getAccessLevel() >= AdminConfig.COMMAND_SPECIAL_SKILL) {
					for (int al : AccessLevelEnum.getAlType(player.getAccessLevel()).getSkills()) {
                        player.getSkillList().addGMSkill(player, al, 1);
					}
				}
			}

			// Alliance Packet after SetBindPoint
			PlayerAllianceService.onPlayerLogin(player);

			if (player.isInPrison())
				PunishmentService.updatePrisonStatus(player);

			if (player.isNotGatherable())
				PunishmentService.updateGatherableStatus(player);

			PlayerGroupService.onPlayerLogin(player);
			PetService.getInstance().onPlayerLogin(player);

			// ----------------------------- Retail sequence -----------------------------
			MailService.getInstance().onPlayerLogin(player);
            AtreianPassportService.getInstance().onLogin(player);
			HousingService.getInstance().onPlayerLogin(player);
			BrokerService.getInstance().onPlayerLogin(player);
			sendMacroList(client, player);
			client.sendPacket(new SM_RECIPE_LIST(player.getRecipeList().getRecipeList()));
			// ----------------------------- Retail sequence -----------------------------

			PetitionService.getInstance().onPlayerLogin(player);
			if (AutoGroupConfig.AUTO_GROUP_ENABLE) {
				AutoGroupService.getInstance().onPlayerLogin(player);
			}
			ClassChangeService.showClassChangeDialog(player);

			GMService.getInstance().onPlayerLogin(player);
			/**
			 * Trigger restore services on login.
			 */
			player.getLifeStats().updateCurrentStats();

			if (HTMLConfig.ENABLE_HTML_WELCOME)
				HTMLService.showHTML(player, HTMLCache.getInstance().getHTML("welcome.xhtml"));

            JsonService.getInstance().onPlayerLogin(player);

			player.getNpcFactions().sendDailyQuest();

			if (HTMLConfig.ENABLE_GUIDES)
				HTMLService.onPlayerLogin(player);
			if (player.hasVar("chatgag"))
				if(Long.parseLong(player.getVar("chatgag").toString()) != 0 && System.currentTimeMillis() >= Long.parseLong(player.getVar("chatgag").toString()))
				{
					  player.delVar("chatgag", true);
				}

			for (StorageType st : StorageType.values()) {
				if (st == StorageType.LEGION_WAREHOUSE)
					continue;
				IStorage storage = player.getStorage(st.getId());
				if (storage != null) {
					for (Item item : storage.getItemsWithKinah())
						if (item.getExpireTime() > 0)
							ExpireTimerTask.getInstance().addTask(item, player);
				}
			}

			for (Item item : player.getEquipment().getEquippedItems())
				if (item.getExpireTime() > 0)
					ExpireTimerTask.getInstance().addTask(item, player);

			player.getEquipment().checkRankLimitItems(); // Remove items after offline changed rank

			for (Motion motion : player.getMotions().getMotions().values()) {
				if (motion.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(motion, player);
				}
			}

			for (Emotion emotion : player.getEmotions().getEmotions()) {
				if (emotion.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(emotion, player);
				}
			}

			for (Title title : player.getTitleList().getTitles()) {
				if (title.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(title, player);
				}
			}

			if (player.getHouseRegistry() != null) {
				for (HouseObject<?> obj : player.getHouseRegistry().getObjects()) {
					if (obj.getPersistentState() == PersistentState.DELETED)
						continue;
					if (obj.getObjectTemplate().getUseDays() > 0)
						ExpireTimerTask.getInstance().addTask(obj, player);
				}
			}
			// scheduler periodic update
			player.getController().addTask(
					TaskId.PLAYER_UPDATE,
					ThreadPoolManager.getInstance().scheduleAtFixedRate(new GeneralUpdateTask(player.getObjectId()),
					PeriodicSaveConfig.PLAYER_GENERAL * 1000, PeriodicSaveConfig.PLAYER_GENERAL * 1000));
			player.getController().addTask(
					TaskId.INVENTORY_UPDATE,
					ThreadPoolManager.getInstance().scheduleAtFixedRate(new ItemUpdateTask(player.getObjectId()),
					PeriodicSaveConfig.PLAYER_ITEMS * 1000, PeriodicSaveConfig.PLAYER_ITEMS * 1000));

			SurveyService.getInstance().showAvailable(player);

			if (EventsConfig.ENABLE_EVENT_SERVICE)
				EventService.getInstance().onPlayerLogin(player);

			if (CraftConfig.DELETE_EXCESS_CRAFT_ENABLE)
				RelinquishCraftStatus.removeExcessCraftStatus(player, false);

			PlayerTransferService.getInstance().onEnterWorld(player);
            player.setPartnerId(DAOManager.getDAO(WeddingDAO.class).loadPartnerId(player));
            player.setPartnerName(DAOManager.getDAO(WeddingDAO.class).loadPartnerName(player.getPartnerId()));
			if (CustomConfig.SECURITY_BUFF_ENABLE)
                            applySecurityBuff(player);
		}
		else {
			log.info("[DEBUG] enter world" + objectId + ", Player: " + player);
		}
	}

	/**
	 * @param client
	 * @param player
	 */
	// TODO! this method code is really odd [Nemesiss]
	private static void sendItemInfos(AionConnection client, Player player) {
		// Cubesize limit set in inventory.
		int questExpands = player.getQuestExpands();
		int npcExpands = player.getNpcExpands();
		player.getInventory().setLimit(StorageType.CUBE.getLimit() + (questExpands + npcExpands) * 9);
		player.getWarehouse().setLimit(StorageType.REGULAR_WAREHOUSE.getLimit() + player.getWarehouseSize() * 8);

		// items
		Storage inventory = player.getInventory();
		List<Item> allItems = new ArrayList<Item>();
		if (inventory.getKinah() == 0) {
			inventory.increaseKinah(0); // create an empty object with value 0
		}
		allItems.add(inventory.getKinahItem()); // always included even with 0 count, and first in the packet !
		allItems.addAll(player.getEquipment().getEquippedItems());
		allItems.addAll(inventory.getItems());

        boolean isFirst = true;
        ListSplitter<Item> splitter = new ListSplitter<Item>(allItems, 10);
        while (!splitter.isLast()) {
            client.sendPacket(new SM_INVENTORY_INFO(isFirst, splitter.getNext(), npcExpands, questExpands, false, player));
            isFirst = false;
        }

        client.sendPacket(new SM_INVENTORY_INFO(false, new ArrayList<Item>(0), npcExpands, questExpands, false, player));
		client.sendPacket(new SM_STATS_INFO(player));
		client.sendPacket(SM_CUBE_UPDATE.stigmaSlots(player.getCommonData().getAdvencedStigmaSlotSize()));
	}

	private static void sendMacroList(AionConnection client, Player player) {
		client.sendPacket(new SM_MACRO_LIST(player, false));
		if (player.getMacroList().getSize() > 7)
			client.sendPacket(new SM_MACRO_LIST(player, true));
	}

	/**
	 * @param player
	 */
	private static void playerLoggedIn(Player player) {
		log.info("Player logged in: " + player.getName() + " Account: " + player.getClientConnection().getAccount().getName());
		player.getCommonData().setOnline(true);
		DAOManager.getDAO(PlayerDAO.class).onlinePlayer(player, true);
		player.onLoggedIn();
		player.setOnlineTime();
	}

    /**
     * Security Service Buff
     */


    private static void applySecurityBuff(Player player) {
    		if (player.getClientConnection().getAccount().getMembership() == 2) {
    	    	PacketSendUtility.sendPacket(player, new SM_ICON_INFO(2, true));
            	player.getGameStats().getStat(StatEnum.FLY_TIME, 0).setBonusRate(30.0f);
                SkillEngine.getInstance().applyEffectDirectly(3233, player, player, 0);
    	    } else {
    	    	PacketSendUtility.sendPacket(player, new SM_ICON_INFO(2, false));
    	    }

    }

	private static void showToll(Player player) {
            final long tolls = player.getClientConnection().getAccount().getToll();
            PacketSendUtility.sendMessage(player,  MuiService.getInstance().getMessage("ALL_POINTS") + tolls + " Points.");
        }

	private static void showPremiumAccountInfo(AionConnection client, Account account) {
		byte membership = account.getMembership();
		if (membership > 0) {
			String accountType = "";
			switch (account.getMembership()) {
				case 1:
					accountType = "[color:premi;0 1 0][color:um;0 1 0]";
					break;
				case 2:
					accountType = "[color:VIP;1 0 0]";
					break;
			}
			client.sendPacket(new SM_MESSAGE(0, null, "The status of your account :  " + accountType, ChatType.GOLDEN_YELLOW));
		}
	}

}

class GeneralUpdateTask implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(GeneralUpdateTask.class);
	private final int playerId;

	GeneralUpdateTask(int playerId) {
		this.playerId = playerId;
	}

	@Override
	public void run() {
		Player player = World.getInstance().findPlayer(playerId);
		if (player != null) {
			try {
				DAOManager.getDAO(AbyssRankDAO.class).storeAbyssRank(player);
				DAOManager.getDAO(PlayerSkillListDAO.class).storeSkills(player);
				DAOManager.getDAO(PlayerQuestListDAO.class).store(player);
                DAOManager.getDAO(PlayerPassportsDAO.class).store(player);
				DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
				for (House house : player.getHouses())
					house.save();
			}
			catch (Exception ex) {
				log.error("Exception during periodic saving of player " + player.getName(), ex);
			}
		}

	}

}

class ItemUpdateTask implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(ItemUpdateTask.class);
	private final int playerId;

	ItemUpdateTask(int playerId) {
		this.playerId = playerId;
	}

	@Override
	public void run() {
		Player player = World.getInstance().findPlayer(playerId);
		if (player != null) {
			try {
				DAOManager.getDAO(InventoryDAO.class).store(player);
				DAOManager.getDAO(ItemStoneListDAO.class).save(player);
			}
			catch (Exception ex) {
				log.error("Exception during periodic saving of player items " + player.getName(), ex);
			}
		}
	}

}
