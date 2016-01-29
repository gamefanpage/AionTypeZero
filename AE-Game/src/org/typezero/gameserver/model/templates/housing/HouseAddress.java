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

package org.typezero.gameserver.model.templates.housing;

import javax.xml.bind.annotation.*;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "address")
public class HouseAddress {

	@XmlAttribute(name = "exit_z")
	protected Float exitZ;

	@XmlAttribute(name = "exit_y")
	protected Float exitY;

	@XmlAttribute(name = "exit_x")
	protected Float exitX;

	@XmlAttribute(name = "exit_map")
	protected Integer exitMap;

	@XmlAttribute(required = true)
	protected float z;

	@XmlAttribute(required = true)
	protected float y;

	@XmlAttribute(required = true)
	protected float x;

	@XmlAttribute(name="town", required = true)
	private int townId;

	@XmlAttribute(required = true)
	protected int map;

	@XmlAttribute(required = true)
	protected int id;

	public Float getExitZ() {
		return exitZ;
	}

	public Float getExitY() {
		return exitY;
	}

	public Float getExitX() {
		return exitX;
	}

	public Integer getExitMapId() {
		return exitMap;
	}

	public float getZ() {
		return z;
	}

	public float getY() {
		return y;
	}

	public float getX() {
		return x;
	}

	public int getMapId() {
		return map;
	}

	public int getId() {
		return id;
	}

	public int getTownId() {
		return townId;
	}

}
