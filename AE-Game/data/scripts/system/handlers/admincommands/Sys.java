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

package admincommands;

import java.util.List;

import com.aionemu.commons.utils.AEInfos;
import org.typezero.gameserver.ShutdownHook;
import org.typezero.gameserver.ShutdownHook.ShutdownMode;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.AEVersions;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author lord_rex //sys info - System Informations //sys memory - Memory Informations //sys gc - Garbage Collector
 *         //sys shutdown <seconds> <announceInterval> - Call shutdown //sys restart <seconds> <announceInterval> - Call
 *         restart //sys threadpool - Thread pools info
 */
public class Sys extends AdminCommand {

	public Sys() {
		super("sys");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility
				.sendMessage(
					player,
					"Usage: //sys info | //sys memory | //sys gc | //sys restart <countdown time> <announce delay> | //sys shutdown <countdown time> <announce delay>");
			return;
		}

		if (params[0].equals("info")) {
			// Time
			PacketSendUtility.sendMessage(player, "System Informations at: " + AEInfos.getRealTime().toString());

			// Version Infos
			for (String line : AEVersions.getFullVersionInfo())
				PacketSendUtility.sendMessage(player, line);

			// OS Infos
			for (String line : AEInfos.getOSInfo())
				PacketSendUtility.sendMessage(player, line);

			// CPU Infos
			for (String line : AEInfos.getCPUInfo())
				PacketSendUtility.sendMessage(player, line);

			// JRE Infos
			for (String line : AEInfos.getJREInfo())
				PacketSendUtility.sendMessage(player, line);

			// JVM Infos
			for (String line : AEInfos.getJVMInfo())
				PacketSendUtility.sendMessage(player, line);
		}

		else if (params[0].equals("memory")) {
			// Memory Infos
			for (String line : AEInfos.getMemoryInfo())
				PacketSendUtility.sendMessage(player, line);
		}

		else if (params[0].equals("gc")) {
			long time = System.currentTimeMillis();
			PacketSendUtility.sendMessage(player, "RAM Used (Before): "
				+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576));
			System.gc();
			PacketSendUtility.sendMessage(player, "RAM Used (After): "
				+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576));
			System.runFinalization();
			PacketSendUtility.sendMessage(player, "RAM Used (Final): "
				+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576));
			PacketSendUtility.sendMessage(player,
				"Garbage Collection and Finalization finished in: " + (System.currentTimeMillis() - time) + " milliseconds...");
		}
		else if (params[0].equals("shutdown")) {
			try {
				int val = Integer.parseInt(params[1]);
				int announceInterval = Integer.parseInt(params[2]);
				ShutdownHook.getInstance().doShutdown(val, announceInterval, ShutdownMode.SHUTDOWN);
				PacketSendUtility.sendMessage(player, "Server will shutdown in " + val + " seconds.");
			}
			catch (ArrayIndexOutOfBoundsException e) {
				PacketSendUtility.sendMessage(player, "Numbers only!");
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(player, "Numbers only!");
			}
		}
		else if (params[0].equals("restart")) {
			try {
				int val = Integer.parseInt(params[1]);
				int announceInterval = Integer.parseInt(params[2]);
				ShutdownHook.getInstance().doShutdown(val, announceInterval, ShutdownMode.RESTART);
				PacketSendUtility.sendMessage(player, "Server will restart in " + val + " seconds.");
			}
			catch (ArrayIndexOutOfBoundsException e) {
				PacketSendUtility.sendMessage(player, "Numbers only!");
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(player, "Numbers only!");
			}
		}
		else if (params[0].equals("threadpool")) {
			List<String> stats = ThreadPoolManager.getInstance().getStats();
			for (String stat : stats) {
				PacketSendUtility.sendMessage(player, stat.replaceAll("\t", ""));
			}
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility
			.sendMessage(
				player,
				"Usage: //sys info | //sys memory | //sys gc | //sys restart <countdown time> <announce delay> | //sys shutdown <countdown time> <announce delay>");
	}

}
