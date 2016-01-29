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

package org.typezero.gameserver.network.aion.serverpackets;


import org.apache.commons.lang.StringUtils;

import org.typezero.gameserver.model.autogroup.AutoGroupType;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author SheppeR, Guapo, nrg
 */
public class SM_AUTO_GROUP extends AionServerPacket {
	private byte windowId;
	private int instanceMaskId;
	private int mapId;
	private int messageId;
	private int titleId;
	private int waitTime;
	private boolean close;
	String name = StringUtils.EMPTY;

	public static final byte wnd_EntryIcon = 6;

	public SM_AUTO_GROUP(int instanceMaskId) {
		AutoGroupType agt = AutoGroupType.getAGTByMaskId(instanceMaskId);
		if (agt == null) {
			throw new IllegalArgumentException("Auto Groups Type no found for Instance MaskId: " + instanceMaskId);
		}

		this.instanceMaskId = instanceMaskId;
		this.messageId = agt.getNameId();
		this.titleId = agt.getTittleId();
		this.mapId = agt.getInstanceMapId();
	}

	public SM_AUTO_GROUP(int instanceMaskId, Number windowId) {
		this(instanceMaskId);
		this.windowId = windowId.byteValue();
	}

	public SM_AUTO_GROUP(int instanceMaskId, Number windowId, boolean close) {
		this(instanceMaskId);
		this.windowId = windowId.byteValue();
		this.close = close;
	}

	public SM_AUTO_GROUP(int instanceMaskId, Number windowId, int waitTime, String name) {
		this(instanceMaskId);
		this.windowId = windowId.byteValue();
		this.waitTime = waitTime;
		this.name = name;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(this.instanceMaskId);
		writeC(this.windowId);
		writeD(this.mapId);
		switch (this.windowId) {
			case 0: // request entry
				writeD(this.messageId);
				writeD(this.titleId);
				writeD(0);
				break;
			case 1: // waiting window
				writeD(0);
				writeD(0);
				writeD(this.waitTime);
				break;
			case 2: // cancel looking
				writeD(0);
				writeD(0);
				writeD(0);
				break;
			case 3: // pass window
				writeD(0);
				writeD(0);
				writeD(this.waitTime);
				break;
			case 4: // enter window
				writeD(0);
				writeD(0);
				writeD(0);
				break;
			case 5: // after you click enter
				writeD(0);
				writeD(0);
				writeD(0);
				break;
			case wnd_EntryIcon: // entry icon
				writeD(this.messageId);
				writeD(this.titleId);
				writeD(this.close ? 0 : 1);
				break;
			case 7: // failed window
				writeD(this.messageId);
				writeD(this.titleId);
				writeD(0);
				break;
			case 8: // on login
				writeD(0);
				writeD(0);
				writeD(this.waitTime);
				break;
		}
		writeC(0);
		writeS(this.name);
	}
}
