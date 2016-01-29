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

package org.typezero.gameserver.model.gameobjects.player.motion;

import java.util.HashMap;
import java.util.Map;

import org.typezero.gameserver.model.IExpirable;
import org.typezero.gameserver.model.gameobjects.player.Player;


/**
 * @author MrPoke
 *
 */
public class Motion implements IExpirable{

	static final Map<Integer, Integer> motionType = new HashMap<Integer, Integer>();
	static{
		motionType.put(1, 1);
		motionType.put(2, 2);
		motionType.put(3, 3);
		motionType.put(4, 4);
		motionType.put(5, 1);
		motionType.put(6, 2);
		motionType.put(7, 3);
		motionType.put(8, 4);
		motionType.put(9, 5);
		motionType.put(10, 5);
		motionType.put(11, 1);
		motionType.put(12, 2);
		motionType.put(13, 3);
		motionType.put(14, 4);
		motionType.put(15, 1);
		motionType.put(16, 2);
		motionType.put(17, 3);
		motionType.put(18, 4);
		motionType.put(19, 5);
		motionType.put(20, 1);
		motionType.put(21, 2);
		motionType.put(22, 3);
		motionType.put(23, 4);
		motionType.put(24, 5);
		motionType.put(25, 1);
		motionType.put(26, 2);
		motionType.put(27, 3);
		motionType.put(28, 4);
		motionType.put(29, 5);
	}
	private int id;
	private int deletionTime = 0;
	private boolean active = false;

	/**
	 * @param id
	 * @param deletionTime
	 */
	public Motion(int id, int deletionTime, boolean isActive) {
		this.id = id;
		this.deletionTime = deletionTime;
		this.active = isActive;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	public int getRemainingTime(){
		if (deletionTime == 0)
			return 0;
		return deletionTime-(int)(System.currentTimeMillis()/1000);
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public int getExpireTime() {
		return deletionTime;
	}

	@Override
	public void expireEnd(Player player) {
		player.getMotions().remove(id);
	}

	@Override
	public void expireMessage(Player player, int time) {
	}

	@Override
	public boolean canExpireNow() {
		return true;
	}
}
