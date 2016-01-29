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

package org.typezero.gameserver;

import ch.lambdaj.Lambda;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.network.NioServer;
import com.aionemu.commons.network.ServerCfg;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.AEInfos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.ai2.AI2Engine;
import org.typezero.gameserver.cache.HTMLCache;
import org.typezero.gameserver.configs.Config;
import org.typezero.gameserver.configs.main.*;
import org.typezero.gameserver.configs.network.NetworkConfig;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.instance.InstanceEngine;
import org.typezero.gameserver.model.GameEngine;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.house.MaintenanceTask;
import org.typezero.gameserver.model.siege.Influence;
import org.typezero.gameserver.network.BannedMacManager;
import org.typezero.gameserver.network.aion.GameConnectionFactoryImpl;
import org.typezero.gameserver.network.chatserver.ChatServer;
import org.typezero.gameserver.network.loginserver.LoginServer;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.services.*;
import org.typezero.gameserver.services.abyss.AbyssRankUpdateService;
import org.typezero.gameserver.services.drop.DropRegistrationService;
import org.typezero.gameserver.services.event.InvasionBritra;
import org.typezero.gameserver.services.event.L2EventMg;
import org.typezero.gameserver.services.event.TreasureAbyss;
import org.typezero.gameserver.services.instance.DredgionService2;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.instance.KamarBattlefieldService;
import org.typezero.gameserver.services.json.JsonService;
import org.typezero.gameserver.services.player.*;
import org.typezero.gameserver.services.reward.RewardService;
import org.typezero.gameserver.services.transfers.PlayerTransferService;
import org.typezero.gameserver.spawnengine.InstanceRiftSpawnManager;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.spawnengine.TemporarySpawnEngine;
import org.typezero.gameserver.taskmanager.fromdb.TaskFromDBManager;
import org.typezero.gameserver.taskmanager.tasks.PacketBroadcaster;
import org.typezero.gameserver.utils.AEVersions;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.utils.ThreadUncaughtExceptionHandler;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.ChatProcessor;
import org.typezero.gameserver.utils.cron.ThreadPoolManagerRunnableRunner;
import org.typezero.gameserver.utils.gametime.DateTimeUtil;
import org.typezero.gameserver.utils.gametime.GameTimeManager;
import org.typezero.gameserver.utils.idfactory.IDFactory;
import org.typezero.gameserver.utils.javaagent.JavaAgentUtils;
import org.typezero.gameserver.utils.mui.MuiEngine;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.geo.GeoService;
import org.typezero.gameserver.world.zone.ZoneService;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <tt>GameServer </tt> is the main class of the application and represents the whole game server.<br>
 * This class is also an entry point with main() method.
 *
 * @author -Nemesiss-
 * @author SoulKeeper
 * @author cura
 */
public class GameServer {

	private static final Logger log = LoggerFactory.getLogger(GameServer.class);

	//TODO remove all this shit
	private static int ELYOS_COUNT = 0;
	private static int ASMOS_COUNT = 0;
	private static double ELYOS_RATIO = 0.0;
	private static double ASMOS_RATIO = 0.0;
	private static final ReentrantLock lock = new ReentrantLock();

	private static void initalizeLoggger() {
		new File("./log/backup/").mkdirs();
		File[] files = new File("log").listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".log");
			}
		});

		if (files != null && files.length > 0) {
			byte[] buf = new byte[1024];
			try {
				String outFilename = "./log/backup/" + new SimpleDateFormat("yyyy-MM-dd HHmmss").format(new Date()) + ".zip";
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
				out.setMethod(ZipOutputStream.DEFLATED);
				out.setLevel(Deflater.BEST_COMPRESSION);

				for (File logFile : files) {
					FileInputStream in = new FileInputStream(logFile);
					out.putNextEntry(new ZipEntry(logFile.getName()));
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					out.closeEntry();
					in.close();
					logFile.delete();
				}
				out.close();
			} catch (IOException e) {
			}
		}
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc);
			lc.reset();
			configurator.doConfigure("config/slf4j-logback.xml");
		} catch (JoranException je) {
			throw new RuntimeException("Failed to configure loggers, shutting down...", je);
		}
	}

	/**
	 * Launching method for GameServer
	 *
	 * @param args arguments, not used
	 */
	public static void main(String[] args) {
		long start = System.currentTimeMillis();

		Lambda.enableJitting(true);
		final GameEngine[] parallelEngines = new GameEngine[] {
				QuestEngine.getInstance(), InstanceEngine.getInstance(),
				AI2Engine.getInstance(), ChatProcessor.getInstance(),
				MuiEngine.getInstance()
		};

		final CountDownLatch progressLatch = new CountDownLatch(parallelEngines.length);

		initalizeLoggger();
		initUtilityServicesAndConfig();
		DataManager.getInstance();
		Util.printSection("Stigma stack tree");
		DataManager.SKILL_TREE_DATA.setStigmaTree();
		Util.printSection("Hidden Stigma");
		StigmaService.reparseHiddenStigmas();
		Util.printSection("IDFactory");
		IDFactory.getInstance();

		Util.printSection("Zone");
		ZoneService.getInstance().load(null);

		Util.printSection("Geodata");
		GeoService.getInstance().initializeGeo();
		// ZoneService.getInstance().saveMaterialZones();
		System.gc();

		Util.printSection("World");
		World.getInstance();

		Util.printSection("Drops");
		DropRegistrationService.getInstance();

		GameServer gs = new GameServer();
		// Set all players is offline
		DAOManager.getDAO(PlayerDAO.class).setPlayersOffline(false);
		DatabaseCleaningService.getInstance();

		BannedMacManager.getInstance();

		for (int i = 0; i < parallelEngines.length; i++) {
			final int index = i;
			ThreadPoolManager.getInstance().execute(new Runnable() {
				@Override
				public void run() {
					parallelEngines[index].load(progressLatch);
				}
			});
		}

		try {
			progressLatch.await();
		}
		catch (InterruptedException e1) {
		}

		// This is loading only siege location data
		// No Siege schedule or spawns
		Util.printSection("Location Data");
		SiegeService.getInstance().initSiegeLocations();
		BaseService.getInstance().initBaseLocations();
		BeritraService.getInstance().initBeritraLocations();
		VortexService.getInstance().initVortexLocations();
		RiftService.getInstance().initRiftLocations();

		Util.printSection("Spawns");
		SpawnEngine.spawnAll();
		RiftService.getInstance().initRifts();
		InstanceRiftSpawnManager.spawnAll();
		TemporarySpawnEngine.spawnAll();
		if (SiegeConfig.SIEGE_ENABLED)
			ShieldService.getInstance().spawnAll();

		Util.printSection("Limits");
		LimitedItemTradeService.getInstance().start();
		if (CustomConfig.LIMITS_ENABLED)
			PlayerLimitService.getInstance().scheduleUpdate();
		GameTimeManager.startClock();

		// Init Sieges... It's separated due to spawn engine.
		// It should not spawn siege NPCs
		Util.printSection("Siege Schedule initialization");
		SiegeService.getInstance().initSieges();

		Util.printSection("World Bases and The Katalam initialization");
		BaseService.getInstance().initBases();

		Util.printSection("Serial Killers initialization");
		SerialKillerService.getInstance().initSerialKillers();

		Util.printSection("Dispute Lands initialization");
		DisputeLandService.getInstance().init();

		Util.printSection("TaskManagers");
		PacketBroadcaster.getInstance();

		GameTimeService.getInstance();
		AnnouncementService.getInstance();
		DebugService.getInstance();
		WeatherService.getInstance();
		BrokerService.getInstance();
		Influence.getInstance();
		ExchangeService.getInstance();
		PeriodicSaveService.getInstance();
		PetitionService.getInstance();
		PlayerFatigueService.getInstance();
		AtreianPassportService.getInstance().onStart();

		if (AIConfig.SHOUTS_ENABLE)
			NpcShoutsService.getInstance();
		InstanceService.load();

		FlyRingService.getInstance();
		if (!GeoDataConfig.GEO_MATERIALS_ENABLE)
			CuringZoneService.getInstance();
		RoadService.getInstance();
		HTMLCache.getInstance();
		WordFilterService.getInstance();
		AbyssRankUpdateService.getInstance().scheduleUpdate();
		AbyssRankUpdateService.getInstance().GpointUpdata();
		PlayerService.scheduleCoolDownCountUpdate();
		TaskFromDBManager.getInstance();
		if (AutoGroupConfig.AUTO_GROUP_ENABLE && AutoGroupConfig.DREDGION2_ENABLE) {
			Util.printSection("Dredgion");
			DredgionService2.getInstance().start();
		}
		if (AutoGroupConfig.AUTO_GROUP_ENABLE && AutoGroupConfig.KAMAR_ENABLE) {
			KamarBattlefieldService.getInstance().start();
		}
		Util.printSection("Pig Event");
		L2EventMg.ScheduleCron();
		Util.printSection("Treasure Abyss Event");
		TreasureAbyss.ScheduleCron();
		Util.printSection("Invasion Britra System start");
		InvasionBritra.getInstance().initStart();
		Util.printSection("------");
		if (CustomConfig.ENABLE_REWARD_SERVICE)
			RewardService.getInstance();
		if (EventsConfig.EVENT_ENABLED)
			PlayerEventService.getInstance();
		if (EventsConfig.EVENT_ENABLED2)
			PlayerEventService2.getInstance();
		if (EventsConfig.EVENT_ENABLED3)
			PlayerEventService3.getInstance();
		if (EventsConfig.ENABLE_EVENT_SERVICE)
			EventService.getInstance().start();
		if (WeddingsConfig.WEDDINGS_ENABLE)
			WeddingService.getInstance();

		AdminService.getInstance();
		PlayerTransferService.getInstance();
		HousingBidService.getInstance().start();
		MaintenanceTask.getInstance();
		TownService.getInstance();

		Util.printSection("MultiLanguage Interface");
		MuiService.getInstance().load();
		Util.printSection("VK service is loaded");
		JsonService.getInstance().load();
		ChallengeTaskService.getInstance();

		Util.printSection("System");
		AEVersions.printFullVersionInfo();
		System.gc();
		AEInfos.printAllInfos();

		Util.printSection("GameServerLog");
		log.info("Awesome World Game Server started in " + (System.currentTimeMillis() - start) / 1000 + " seconds.");

		gs.startServers();

		Runtime.getRuntime().addShutdownHook(ShutdownHook.getInstance());

		if (GSConfig.ENABLE_RATIO_LIMITATION) {
			addStartupHook(new StartupHook() {

				@Override
				public void onStartup() {
					lock.lock();
					try {
						ASMOS_COUNT = DAOManager.getDAO(PlayerDAO.class).getCharacterCountForRace(Race.ASMODIANS);
						ELYOS_COUNT = DAOManager.getDAO(PlayerDAO.class).getCharacterCountForRace(Race.ELYOS);
						computeRatios();
					} catch (Exception e) {
					} finally {
						lock.unlock();
					}
					displayRatios(false);
				}
			});
		}

		onStartup();
	}

	/**
	 * Starts servers for connection with aion client and login\chat server.
	 */
	private void startServers() {
		Util.printSection("Starting Network");
		NioServer nioServer = new NioServer(NetworkConfig.NIO_READ_WRITE_THREADS, new ServerCfg(NetworkConfig.GAME_BIND_ADDRESS, NetworkConfig.GAME_PORT, "Game Connections", new GameConnectionFactoryImpl()));

		LoginServer ls = LoginServer.getInstance();
		ChatServer cs = ChatServer.getInstance();

		ls.setNioServer(nioServer);
		cs.setNioServer(nioServer);

		// Nio must go first
		nioServer.connect();
		ls.connect();

		if (GSConfig.ENABLE_CHAT_SERVER)
			cs.connect();
	}

	/**
	 * Initialize all helper services, that are not directly related to aion gs, which includes:
	 * <ul>
	 * <li>Logging</li>
	 * <li>Database factory</li>
	 * <li>Thread pool</li>
	 * </ul>
	 * This method also initializes {@link Config}
	 */
	private static void initUtilityServicesAndConfig() {
		// Set default uncaught exception handler
		Thread.setDefaultUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());

		// make sure that callback code was initialized
		if (JavaAgentUtils.isConfigured())
			log.info("JavaAgent [Callback Support] is configured.");

		// Initialize cron service
		CronService.initSingleton(ThreadPoolManagerRunnableRunner.class);

		// init config
		Config.load();
		// DateTime zone override from configs
		DateTimeUtil.init();
		// Second should be database factory
		Util.printSection("DataBase");
		DatabaseFactory.init();
		// Initialize DAOs
		DAOManager.init();
		// Initialize thread pools
		Util.printSection("Threads");
		ThreadConfig.load();
		ThreadPoolManager.getInstance();
	}

	private static Set<StartupHook> startUpHooks = new HashSet<StartupHook>();

	public synchronized static void addStartupHook(StartupHook hook) {
		if (startUpHooks != null)
			startUpHooks.add(hook);
		else
			hook.onStartup();
	}

	private synchronized static void onStartup() {
		final Set<StartupHook> startupHooks = startUpHooks;

		startUpHooks = null;

		for (StartupHook hook : startupHooks)
			hook.onStartup();
	}

	public interface StartupHook {

		public void onStartup();
	}

	/**
	 * @param race
	 * @param i
	 */
	public static void updateRatio(Race race, int i) {
		lock.lock();
		try {
			switch (race) {
				case ASMODIANS:
					GameServer.ASMOS_COUNT += i;
					break;
				case ELYOS:
					GameServer.ELYOS_COUNT += i;
					break;
				default:
					break;
			}

			computeRatios();
		} catch (Exception e) {
		} finally {
			lock.unlock();
		}

		displayRatios(true);
	}

	private static void computeRatios() {
		if ((GameServer.ASMOS_COUNT <= GSConfig.RATIO_MIN_CHARACTERS_COUNT) && (GameServer.ELYOS_COUNT <= GSConfig.RATIO_MIN_CHARACTERS_COUNT)) {
			GameServer.ASMOS_RATIO = GameServer.ELYOS_RATIO = 50.0;
		} else {
			GameServer.ASMOS_RATIO = GameServer.ASMOS_COUNT * 100.0 / (GameServer.ASMOS_COUNT + GameServer.ELYOS_COUNT);
			GameServer.ELYOS_RATIO = GameServer.ELYOS_COUNT * 100.0 / (GameServer.ASMOS_COUNT + GameServer.ELYOS_COUNT);
		}
	}

	private static void displayRatios(boolean updated) {
		log.info("FACTIONS RATIO " + (updated ? "UPDATED " : "") + ": E " + String.format("%.1f", GameServer.ELYOS_RATIO)
				+ " % / A " + String.format("%.1f", GameServer.ASMOS_RATIO) + " %");
	}

	public static double getRatiosFor(Race race) {
		switch (race) {
			case ASMODIANS:
				return GameServer.ASMOS_RATIO;
			case ELYOS:
				return GameServer.ELYOS_RATIO;
			default:
				return 0.0;
		}
	}

	public static int getCountFor(Race race) {
		switch (race) {
			case ASMODIANS:
				return GameServer.ASMOS_COUNT;
			case ELYOS:
				return GameServer.ELYOS_COUNT;
			default:
				return 0;
		}
	}

}
