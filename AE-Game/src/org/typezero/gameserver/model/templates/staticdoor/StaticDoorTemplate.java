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


package org.typezero.gameserver.model.templates.staticdoor;

import java.util.EnumSet;

import javax.xml.bind.annotation.*;

import org.typezero.gameserver.geoEngine.bounding.BoundingBox;
import org.typezero.gameserver.model.templates.VisibleObjectTemplate;

/**
 * @author Wakizashi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StaticDoor")
public class StaticDoorTemplate extends VisibleObjectTemplate {

	@XmlAttribute
	protected DoorType type = DoorType.DOOR;
	@XmlAttribute
	protected Float x;
	@XmlAttribute
	protected Float y;
	@XmlAttribute
	protected Float z;
	@XmlAttribute(name = "doorid")
	protected int doorId;
	@XmlAttribute(name = "keyid")
	protected int keyId;
	@XmlAttribute(name = "state")
	protected String statesHex;
	@XmlAttribute(name = "mesh")
	private String meshFile;
	@XmlElement(name = "box")
	private StaticDoorBounds box;

	@XmlTransient
	EnumSet<StaticDoorState> states = EnumSet.noneOf(StaticDoorState.class);

	public Float getX() {
		return x;
	}

	public Float getY() {
		return y;
	}

	public Float getZ() {
		return z;
	}

	/**
	 * @return the doorId
	 */
	public int getDoorId() {
		return doorId;
	}

	/**
	 * @return the keyItem
	 */
	public int getKeyId() {
		return keyId;
	}

	@Override
	public int getTemplateId() {
		return 300001;
	}

	@Override
	public String getName() {
		return "door";
	}

	@Override
	public int getNameId() {
		return 0;
	}

	public EnumSet<StaticDoorState> getInitialStates() {
		if (statesHex != null) {
			int radix = 16;
			if (statesHex.startsWith("0x")) {
				statesHex = statesHex.replace("0x", "");
			}
			else
				radix = 10;
			try {
				StaticDoorState.setStates(Integer.parseInt(statesHex, radix), states);
			}
			catch (NumberFormatException ex) {
			}
			finally {
				statesHex = null;
			}
		}
		return states;
	}

	public String getMeshFile() {
		return meshFile;
	}

	public BoundingBox getBoundingBox() {
		if (box == null)
			return null;
		return box.getBoundingBox();
	}

	public DoorType getDoorType() {
		return type;
	}

}
