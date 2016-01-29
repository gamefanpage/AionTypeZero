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

package com.aionengine.loginserver;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.AEInfos;
import com.aionemu.commons.utils.ExitCode;
import com.aionengine.loginserver.configs.Config;
import com.aionengine.loginserver.controller.BannedIpController;
import com.aionengine.loginserver.controller.PremiumController;
import com.aionengine.loginserver.dao.BannedMacDAO;
import com.aionengine.loginserver.network.NetConnector;
import com.aionengine.loginserver.network.ncrypt.KeyGen;
import com.aionengine.loginserver.service.PlayerTransferService;
import com.aionengine.loginserver.taskmanager.TaskFromDBManager;
import com.aionengine.loginserver.utils.DeadLockDetector;
import com.aionengine.loginserver.utils.ThreadPoolManager;
import com.aionengine.loginserver.utils.cron.ThreadPoolManagerRunnableRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author -Nemesiss-
 */
public class LoginServer {

    /**
     * Logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(LoginServer.class);

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
     * @param args
     */
    public static void main(final String[] args) {
        long start = System.currentTimeMillis();

        initalizeLoggger();
        CronService.initSingleton(ThreadPoolManagerRunnableRunner.class);

        //write a timestamp that can be used by TruncateToZipFileAppender
        log.info("\f" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(System.currentTimeMillis())) + "\f");
        Config.load();
        DatabaseFactory.init();
        DAOManager.init();

        /**
         * Start deadlock detector that will restart server if deadlock happened
         */
        new DeadLockDetector(60, DeadLockDetector.RESTART).start();
        ThreadPoolManager.getInstance();

        /**
         * Initialize Key Generator
         */
        try {
            KeyGen.init();
        } catch (Exception e) {
            log.error("Failed initializing Key Generator. Reason: " + e.getMessage(), e);
            System.exit(ExitCode.CODE_ERROR);
        }

        GameServerTable.load();
        BannedIpController.start();
        DAOManager.getDAO(BannedMacDAO.class).cleanExpiredBans();

        NetConnector.getInstance().connect();
        PlayerTransferService.getInstance();
        TaskFromDBManager.getInstance();

        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

        AEInfos.printAllInfos();

        PremiumController.getController();
        log.info("Aion Engine Emu Login Server started in " + (System.currentTimeMillis() - start) / 1000 + " seconds.");
    }
}
