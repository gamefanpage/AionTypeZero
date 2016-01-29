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

package org.typezero.gameserver.model.gameobjects;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.TemporaryPlayerTeam;
import org.typezero.gameserver.model.team2.alliance.PlayerAlliance;
import org.typezero.gameserver.model.team2.group.PlayerGroup;

/**
 * Find Group
 * 
 * @author MrPoke
 */
public class FindGroup {

	private AionObject object;
	private String message;
	private int groupType;
	private int lastUpdate = (int) (System.currentTimeMillis() / 1000);

	public FindGroup(AionObject object, String message, int groupType) {
		this.object = object;
		this.message = message;
		this.groupType = groupType;
	}

	public String getMessage() {
		return message;
	}

	public int getGroupType() {
		return groupType;
	}

	public int getObjectId() {
		return object.getObjectId();
	}

	public int getClassId() {
		if (object instanceof Player)
			return ((Player) (object)).getPlayerClass().getClassId();
		else if (object instanceof PlayerAlliance)
			((PlayerAlliance) (object)).getLeaderObject().getCommonData().getPlayerClass();
		else if (object instanceof PlayerGroup) {
			((PlayerGroup) object).getLeaderObject().getPlayerClass();
		}
		return 0;
	}

	public int getMinLevel() {
		if (object instanceof Player)
			return ((Player) (object)).getLevel();
		else if (object instanceof PlayerAlliance) {
			int minLvl = 99;
			for (Player member : ((PlayerAlliance) (object)).getMembers()) {
				int memberLvl = member.getCommonData().getLevel();
				if (memberLvl < minLvl)
					minLvl = memberLvl;
			}
			return minLvl;
		}
		else if (object instanceof PlayerGroup) {
			return ((PlayerGroup) object).getMinExpPlayerLevel();
		}
		else if (object instanceof TemporaryPlayerTeam) {
			return ((TemporaryPlayerTeam<?>) object).getMinExpPlayerLevel();
		}
		return 1;
	}

	public int getMaxLevel() {
		if (object instanceof Player)
			return ((Player) (object)).getLevel();
		else if (object instanceof PlayerAlliance) {
			int maxLvl = 1;
			for (Player member : ((PlayerAlliance) (object)).getMembers()) {
				int memberLvl = member.getCommonData().getLevel();
				if (memberLvl > maxLvl)
					maxLvl = memberLvl;
			}
			return maxLvl;
		}
		else if (object instanceof PlayerGroup) {
			return ((PlayerGroup) object).getMaxExpPlayerLevel();
		}
		else if (object instanceof TemporaryPlayerTeam) {
			return ((TemporaryPlayerTeam<?>) object).getMaxExpPlayerLevel();
		}
		return 1;
	}

	public int getUnk() {
		if (object instanceof Player)
			return 65557;
		else
			return 0;
	}

	/**
	 * @return the lastUpdate
	 */
	public int getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		if (object instanceof Player)
			return ((Player) object).getName();
		else if (object instanceof PlayerAlliance)
			return ((PlayerAlliance) object).getLeaderObject().getCommonData().getName();
		else if (object instanceof PlayerGroup) {
			return ((PlayerGroup) object).getLeaderObject().getName();
		}
		return "";
	}

	public int getSize() {
		if (object instanceof Player)
			return 1;
		else if (object instanceof PlayerAlliance)
			return ((PlayerAlliance) object).size();
		else if (object instanceof PlayerGroup) {
			return ((PlayerGroup) object).size();
		}
		return 1;
	}

	public void setMessage(String message) {
		lastUpdate = (int) (System.currentTimeMillis() / 1000);
		this.message = message;
	}
}
