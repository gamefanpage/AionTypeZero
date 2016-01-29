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

package org.typezero.gameserver.model.templates.ride;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.templates.Bounds;


/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RideInfo", propOrder = { "bounds" })
public class RideInfo {

	protected Bounds bounds;

	@XmlAttribute(name = "cost_fp")
	protected Integer costFp;

	@XmlAttribute(name = "start_fp")
	protected int startFp;

	@XmlAttribute(name = "sprint_speed")
	protected float sprintSpeed;

	@XmlAttribute(name = "fly_speed")
	protected float flySpeed;

	@XmlAttribute(name = "move_speed")
	protected float moveSpeed;

	@XmlAttribute
	protected Integer type;

	@XmlAttribute(required = true)
	protected int id;

	/**
	 * Gets the value of the bounds property.
	 *
	 * @return possible object is {@link Bounds }
	 */
	public Bounds getBounds() {
		return bounds;
	}

	public Integer getCostFp() {
		return costFp;
	}

	public int getStartFp() {
		return startFp;
	}

	public float getSprintSpeed() {
		return sprintSpeed;
	}

	public float getFlySpeed() {
		return flySpeed;
	}

	public float getMoveSpeed() {
		return moveSpeed;
	}

	public Integer getType() {
		return type;
	}

	public int getNpcId() {
		return id;
	}

	public boolean canSprint() {
		return sprintSpeed != 0;
	}
}
