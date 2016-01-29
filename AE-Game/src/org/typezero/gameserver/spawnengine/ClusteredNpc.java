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

package org.typezero.gameserver.spawnengine;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.templates.walker.WalkerTemplate;

/**
 * Stores for the spawn needed information, used for forming walker groups and spawning NPCs
 *
 * @author vlog
 * @modified Rolandas
 */
public class ClusteredNpc {

	private Npc npc;
	private int instance;
	private WalkerTemplate walkTemplate;
	private float x;
	private float y;
	private int walkerIdx;

	public ClusteredNpc(Npc npc, int instance, WalkerTemplate walkTemplate) {
		this.npc = npc;
		this.instance = instance;
		this.walkTemplate = walkTemplate;
		this.x = npc.getSpawn().getX();
		this.y = npc.getSpawn().getY();
		this.walkerIdx = npc.getSpawn().getWalkerIndex();
	}

	public Npc getNpc() {
		return npc;
	}

	public int getInstance() {
		return instance;
	}

	public void spawn(float z) {
		SpawnEngine.bringIntoWorld(npc, npc.getSpawn().getWorldId(), instance, x, y, z, npc.getSpawn().getHeading());
	}

	public void setNpc(Npc npc) {
		npc.setWalkerGroupShift(this.npc.getWalkerGroupShift());
		this.npc = npc;
		this.x = npc.getSpawn().getX();
		this.y = npc.getSpawn().getY();
	}

	public boolean hasSamePosition(ClusteredNpc other) {
    if (this == other)
      return true;
		if (other == null)
			return false;
		return this.x == other.x && this.y == other.y;
	}

  public int getPositionHash() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits(x);
    result = prime * result + Float.floatToIntBits(y);
    return result;
  }

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	public float getXDelta() {
		return walkTemplate.getRouteStep(1).getX() - x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(float x) {
		this.x = x;
		this.getNpc().getSpawn().setX(x);
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	public float getYDelta() {
		return walkTemplate.getRouteStep(1).getY() - y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(float y) {
		this.y = y;
		this.getNpc().getSpawn().setY(y);
	}

	/**
	 * @return the walkTemplate
	 */
	public WalkerTemplate getWalkTemplate() {
		return walkTemplate;
	}

	public int getWalkerIndex() {
		return walkerIdx;
	}

}
