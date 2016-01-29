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

package org.typezero.gameserver.restrictions;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.model.Skill;

/**
 * @author lord_rex
 */
public abstract class AbstractRestrictions implements Restrictions {

	public void activate() {
		RestrictionsManager.activate(this);
	}

	public void deactivate() {
		RestrictionsManager.deactivate(this);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	/**
	 * To avoid accidentally multiple times activated restrictions.
	 */
	@Override
	public boolean equals(Object obj) {
		return getClass().equals(obj.getClass());
	}

	@Override
	@DisabledRestriction
	public boolean isRestricted(Player player, Class<? extends Restrictions> callingRestriction) {
		throw new AbstractMethodError();
	}

	@Override
	@DisabledRestriction
	public boolean canAttack(Player player, VisibleObject target) {
		throw new AbstractMethodError();
	}

	@Override
	@DisabledRestriction
	public boolean canAffectBySkill(Player player, VisibleObject target, Skill skill) {
		throw new AbstractMethodError();
	}

	@Override
	@DisabledRestriction
	public boolean canUseSkill(Player player, Skill skill) {
		throw new AbstractMethodError();
	}

	@Override
	@DisabledRestriction
	public boolean canChat(Player player) {
		throw new AbstractMethodError();
	}

	@Override
	@DisabledRestriction
	public boolean canInviteToGroup(Player player, Player target) {
		throw new AbstractMethodError();
	}

	@Override
	@DisabledRestriction
	public boolean canChangeEquip(Player player) {
		throw new AbstractMethodError();
	}

	@Override
	@DisabledRestriction
	public boolean canUseWarehouse(Player player) {
		throw new AbstractMethodError();
	}

	@Override
	@DisabledRestriction
	public boolean canTrade(Player player) {
		throw new AbstractMethodError();
	}

	@Override
	@DisabledRestriction
	public boolean canUseItem(Player player, Item item) {
		throw new AbstractMethodError();
	}

}
