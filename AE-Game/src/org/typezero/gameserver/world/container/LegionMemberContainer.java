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

package org.typezero.gameserver.world.container;

import javolution.util.FastMap;
import org.typezero.gameserver.model.team.legion.LegionMember;
import org.typezero.gameserver.model.team.legion.LegionMemberEx;
import org.typezero.gameserver.world.exceptions.DuplicateAionObjectException;

/**
 * Container for storing Legion members by Id and name.
 *
 * @author Simple
 */
public class LegionMemberContainer {

	private final FastMap<Integer, LegionMember> legionMemberById = new FastMap<Integer, LegionMember>().shared();

	private final FastMap<Integer, LegionMemberEx> legionMemberExById = new FastMap<Integer, LegionMemberEx>().shared();
	private final FastMap<String, LegionMemberEx> legionMemberExByName = new FastMap<String, LegionMemberEx>().shared();

	/**
	 * Add LegionMember to this Container.
	 *
	 * @param legionMember
	 */
	public void addMember(LegionMember legionMember) {
		if (!legionMemberById.containsKey(legionMember.getObjectId()))
			legionMemberById.put(legionMember.getObjectId(), legionMember);
	}

	/**
	 * This method will return a member from cache
	 *
	 * @param memberObjId
	 */
	public LegionMember getMember(int memberObjId) {
		return legionMemberById.get(memberObjId);
	}

	/**
	 * Add LegionMemberEx to this Container.
	 *
	 * @param legionMember
	 */
	public void addMemberEx(LegionMemberEx legionMember) {
		if (legionMemberExById.containsKey(legionMember.getObjectId())
			|| legionMemberExByName.containsKey(legionMember.getName()))
			throw new DuplicateAionObjectException();
		legionMemberExById.put(legionMember.getObjectId(), legionMember);
		legionMemberExByName.put(legionMember.getName(), legionMember);
	}

	/**
	 * This method will return a memberEx from cache
	 *
	 * @param memberObjId
	 */
	public LegionMemberEx getMemberEx(int memberObjId) {
		return legionMemberExById.get(memberObjId);
	}

	/**
	 * This method will return a memberEx from cache
	 *
	 * @param memberName
	 */
	public LegionMemberEx getMemberEx(String memberName) {
		return legionMemberExByName.get(memberName);
	}

	/**
	 * Remove LegionMember from this Container.
	 *
	 * @param legionMember
	 */
	public void remove(LegionMemberEx legionMember) {
		legionMemberById.remove(legionMember.getObjectId());
		legionMemberExById.remove(legionMember.getObjectId());
		legionMemberExByName.remove(legionMember.getName());
	}

	/**
	 * Returns true if legion is in cached by id
	 *
	 * @param memberObjId
	 * @return true or false
	 */
	public boolean contains(int memberObjId) {
		return legionMemberById.containsKey(memberObjId);
	}

	/**
	 * Returns true if legion is in cached by id
	 *
	 * @param memberObjId
	 * @return true or false
	 */
	public boolean containsEx(int memberObjId) {
		return legionMemberExById.containsKey(memberObjId);
	}

	/**
	 * Returns true if legion is in cached by id
	 *
	 * @param memberName
	 * @return true or false
	 */
	public boolean containsEx(String memberName) {
		return legionMemberExByName.containsKey(memberName);
	}

	public void clear() {
		legionMemberById.clear();
		legionMemberExById.clear();
		legionMemberExByName.clear();
	}
}
