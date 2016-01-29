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

package org.typezero.gameserver.model.team2;

import com.aionemu.commons.utils.internal.chmv8.PlatformDependent;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.gameobjects.AionObject;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

/**
 * @author ATracer
 */
public abstract class GeneralTeam<M extends AionObject, TM extends TeamMember<M>> extends AionObject implements
	Team<M, TM> {

	private final static Logger log = LoggerFactory.getLogger(GeneralTeam.class);
	protected final Map<Integer, TM> members = PlatformDependent.newConcurrentHashMap();
	protected final Lock teamLock = new ReentrantLock();
	private TM leader;

	private final MemberTransformFunction<TM, M> TRANSFORM_FUNCTION = new MemberTransformFunction<TM, M>();

	public GeneralTeam(Integer objId) {
		super(objId);
	}

	@Override
	public void onEvent(TeamEvent event) {
		lock();
		try {
			if (event.checkCondition()) {
				event.handleEvent();
			}
			else {
				log.warn("[TEAM2] skipped event: {} group: {}", event, this);
			}
		}
		finally {
			unlock();
		}
	}

	@Override
	public TM getMember(Integer objectId) {
		return members.get(objectId);
	}

	@Override
	public boolean hasMember(Integer objectId) {
		return members.get(objectId) != null;
	}

	@Override
	public void addMember(TM member) {
		Preconditions.checkNotNull(member, "Team member should be not null");
		Preconditions.checkState(members.get(member.getObjectId()) == null, "Team member is already added");
		members.put(member.getObjectId(), member);
	}

	@Override
	public void removeMember(TM member) {
		Preconditions.checkNotNull(member, "Team member should be not null");
		Preconditions.checkState(members.get(member.getObjectId()) != null, "Team member is already removed");
		members.remove(member.getObjectId());
	}

	@Override
	public final void removeMember(Integer objectId) {
		removeMember(members.get(objectId));
	}

	/**
	 * Apply some predicate on all group members<br>
	 * Should be used only to change state of the group or its members
	 */
	public void apply(Predicate<TM> predicate) {
		lock();
		try {
			for (TM member : members.values()) {
				if (!predicate.apply(member)) {
					return;
				}
			}
		}
		finally {
			unlock();
		}
	}

	/**
	 * Apply some predicate on all group member's objects<br>
	 * Should be used only to change state of the group or its members
	 */
	public void applyOnMembers(Predicate<M> predicate) {
		lock();
		try {
			for (TM member : members.values()) {
				if (!predicate.apply(member.getObject())) {
					return;
				}
			}
		}
		finally {
			unlock();
		}
	}

	@Override
	public Collection<TM> filter(Predicate<TM> predicate) {
		return Collections2.filter(members.values(), predicate);
	}

	@Override
	public Collection<M> filterMembers(Predicate<M> predicate) {
		return Collections2.filter(Collections2.transform(members.values(), TRANSFORM_FUNCTION), predicate);
	}

	@Override
	public Collection<M> getMembers() {
		return filterMembers(Predicates.<M> alwaysTrue());
	}

	@Override
	public int size() {
		return members.size();
	}

	@Override
	public final Integer getTeamId() {
		return getObjectId();
	}

	@Override
	public String getName() {
		return GeneralTeam.class.getName();
	}

	public final TM getLeader() {
		return leader;
	}

	public final M getLeaderObject() {
		return leader.getObject();
	}

	public final boolean isLeader(M member) {
		return leader.getObject().getObjectId().equals(member.getObjectId());
	}

	public final void changeLeader(TM member) {
		Preconditions.checkNotNull(leader, "Leader should already be set");
		Preconditions.checkNotNull(member, "New leader should not be null");
		this.leader = member;
	}

	protected final void setLeader(TM member) {
		Preconditions.checkState(leader == null, "Leader should be not initialized");
		Preconditions.checkNotNull(member, "Leader should not be null");
		this.leader = member;
	}

	protected final void lock() {
		teamLock.lock();
	}

	protected final void unlock() {
		teamLock.unlock();
	}

	private static final class MemberTransformFunction<TM extends TeamMember<M>, M> implements Function<TM, M> {

		@Override
		public M apply(TM member) {
			return member.getObject();
		}

	}

}
