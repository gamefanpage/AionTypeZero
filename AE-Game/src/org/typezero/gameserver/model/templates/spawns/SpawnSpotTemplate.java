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

package org.typezero.gameserver.model.templates.spawns;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.*;

/**
 * @author xTz
 * @modified Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpawnSpotTemplate")
public class SpawnSpotTemplate {

	@XmlAttribute(name = "state")
	private Integer state = 0;

	@XmlAttribute(name = "anchor")
	private String anchor;

	@XmlAttribute(name = "fly")
	private Integer fly = 0;

	@XmlAttribute(name = "walker_index")
	private Integer walkerIdx;

	@XmlAttribute(name = "walker_id")
	private String walkerId;

	@XmlAttribute(name = "random_walk")
	private Integer randomWalk = 0;

	@XmlAttribute(name = "static_id")
	private Integer staticId = 0;

	@XmlAttribute(name = "h", required = true)
	private byte h;

	@XmlAttribute(name = "z", required = true)
	private float z;

	@XmlAttribute(name = "y", required = true)
	private float y;

	@XmlAttribute(name = "x", required = true)
	private float x;

	@XmlElement(name = "temporary_spawn")
	private TemporarySpawn temporaySpawn;

	@XmlElement(name = "model")
	private SpawnModel model;

	public SpawnSpotTemplate() {
	}

	private static final Integer ZERO = new Integer(0);

	void beforeMarshal(Marshaller marshaller) {
		if (ZERO.equals(staticId))
			staticId = null;
		if (ZERO.equals(fly))
			fly = null;
		if (ZERO.equals(randomWalk))
			randomWalk = null;
		if (ZERO.equals(state))
			state = null;
		if (ZERO.equals(walkerIdx))
			walkerIdx = null;
	}

	void afterMarshal(Marshaller marshaller) {
		if (staticId == null)
			staticId = 0;
		if (fly == null)
			fly = 0;
		if (randomWalk == null)
			randomWalk = 0;
		if (state == null)
			state = 0;
		if (walkerIdx == null)
			walkerIdx = 0;
	}

	public SpawnSpotTemplate(float x, float y, float z, byte h, int randomWalk, String walkerId, Integer walkerIndex) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.h = h;
		if (randomWalk > 0)
			this.randomWalk = randomWalk;
		this.walkerId = walkerId;
		this.walkerIdx = walkerIndex;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public byte getHeading() {
		return h;
	}

	public int getStaticId() {
		return staticId;
	}

	public void setStaticId(int staticId) {
		this.staticId = staticId;
	}

	public String getWalkerId() {
		return walkerId;
	}

	public void setWalkerId(String walkerId) {
		this.walkerId = walkerId;
	}

	public int getWalkerIndex() {
		if (walkerIdx == null)
			return 0;
		return walkerIdx;
	}

	public int getRandomWalk() {
		return randomWalk;
	}

	public int getFly() {
		return fly;
	}

	public String getAnchor() {
		return anchor;
	}

	public SpawnModel getModel() {
		return model;
	}

	public int getState() {
		if (state == null)
			return 0;
		return state;
	}

	public TemporarySpawn getTemporarySpawn() {
		return temporaySpawn;
	}
}
