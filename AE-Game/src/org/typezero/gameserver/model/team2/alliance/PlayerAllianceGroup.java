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

package org.typezero.gameserver.model.team2.alliance;

import org.typezero.gameserver.model.team2.TemporaryPlayerTeam;

/**
 * @author ATracer
 */
public class PlayerAllianceGroup extends TemporaryPlayerTeam<PlayerAllianceMember> {

	private final PlayerAlliance alliance;

	public PlayerAllianceGroup(PlayerAlliance alliance, Integer objId) {
		super(objId);
		this.alliance = alliance;
	}

	@Override
	public void addMember(PlayerAllianceMember member) {
		super.addMember(member);
		member.setPlayerAllianceGroup(this);
		member.setAllianceId(getTeamId());
	}

	@Override
	public void removeMember(PlayerAllianceMember member) {
		super.removeMember(member);
		member.setPlayerAllianceGroup(null);
	}

	@Override
	public boolean isFull() {
		return size() == 6;
	}

	@Override
	public int getMinExpPlayerLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxExpPlayerLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	public PlayerAlliance getAlliance() {
		return alliance;
	}

}
