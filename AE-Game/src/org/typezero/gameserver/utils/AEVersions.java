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

package org.typezero.gameserver.utils;

import com.aionemu.commons.utils.AEInfos;
import com.aionemu.commons.versionning.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.GameServer;

/**
 * @author lord_rex
 */
public class AEVersions {

	private static final Logger log = LoggerFactory.getLogger(AEVersions.class);
	private static final Version commons = new Version(AEInfos.class);
	private static final Version gameserver = new Version(GameServer.class);

	private static String getRevisionInfo(Version version) {
		return String.format("%-6s", version.getRevision());
	}

	private static String getBranchInfo(Version version) {
		return String.format("%-6s", version.getBranch());
	}

	private static String getBranchCommitTimeInfo(Version version) {
		return String.format("%-6s", version.getCommitTime());
	}

	private static String getDateInfo(Version version) {
		return String.format("[ %4s ]", version.getDate());
	}

	public static String[] getFullVersionInfo() {
		return new String[] { "Commons Revision: " + getRevisionInfo(commons),
			"Commons Build Date: " + getDateInfo(commons), "GS Revision: " + getRevisionInfo(gameserver),
			"GS Branch: " + getBranchInfo(gameserver), "GS Branch Commit Date: " + getBranchCommitTimeInfo(gameserver),
			"GS Build Date: " + getDateInfo(gameserver), "..................................................",
			".................................................." };
	}

	public static void printFullVersionInfo() {
		for (String line : getFullVersionInfo())
			log.info(line);
	}
}
