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

package org.typezero.gameserver.model.autogroup;

import static ch.lambdaj.Lambda.*;
import org.typezero.gameserver.model.gameobjects.player.Player;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author xTz
 */
public class SearchInstance {

	private long registrationTime = System.currentTimeMillis();
	private int instanceMaskId;
	private EntryRequestType ert;
	private List<Integer> members;

	public SearchInstance(int instanceMaskId, EntryRequestType ert, Collection<Player> members) {
		this.instanceMaskId = instanceMaskId;
		this.ert = ert;
		if (members != null) {
			this.members = extract(members, on(Player.class).getObjectId());
		}
	}

	public List<Integer> getMembers() {
		return members;
	}

	public int getInstanceMaskId() {
		return instanceMaskId;
	}

	public int getRemainingTime() {
		return (int) (System.currentTimeMillis() - registrationTime) / 1000 * 256;
	}

	public EntryRequestType getEntryRequestType() {
		return ert;
	}

	public boolean isDredgion() {
		return instanceMaskId == 1 || instanceMaskId == 2 || instanceMaskId == 3;
	}
}
