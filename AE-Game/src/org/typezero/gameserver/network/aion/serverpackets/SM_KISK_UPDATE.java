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


import org.typezero.gameserver.model.gameobjects.Kisk;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author Sarynth 0xB0 for 1.5.1.10 and 1.5.1.15
 */
public class SM_KISK_UPDATE extends AionServerPacket {

	// useMask values determine who can bind to the kisk.
	// 1 ~ race
	// 2 ~ legion
	// 3 ~ solo
	// 4 ~ group
	// 5 ~ alliance
	// of course, we must programmatically check as well.

	private int objId;
	private int useMask;
	private int currentMembers;
	private int maxMembers;
	private int remainingRessurects;
	private int maxRessurects;
	private int remainingLifetime;

	public SM_KISK_UPDATE(Kisk kisk) {
		this.objId = kisk.getObjectId();
		this.useMask = kisk.getUseMask();
		this.currentMembers = kisk.getCurrentMemberCount();
		this.maxMembers = kisk.getMaxMembers();
		this.remainingRessurects = kisk.getRemainingResurrects();
		this.maxRessurects = kisk.getMaxRessurects();
		this.remainingLifetime = kisk.getRemainingLifetime();
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(objId);
		writeD(useMask);
		writeD(currentMembers);
		writeD(maxMembers);
		writeD(remainingRessurects);
		writeD(maxRessurects);
		writeD(remainingLifetime);
	}

}
