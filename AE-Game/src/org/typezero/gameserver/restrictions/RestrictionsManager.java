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

import org.apache.commons.lang.ArrayUtils;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.model.Skill;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author lord_rex This class is a Manager for restrictions. This is a very useful system, you can use that for: game
 *         restrictions, events restrictions, quests restrictions, instances restrictions, customs restrictions.
 * @Example: Open a new class like this: public class AppleRestrictions extends AbstractRestrictions {
 * @Override public boolean canEatApple(Player player) { if(player.getController().isInEvent()) { // only example, don't
 *           forget! return true; // player can eat apple in event. } return false; // player cannot eat apple in normal
 *           game. } }
 */
public final class RestrictionsManager {

	private RestrictionsManager() {

	}

	private static enum RestrictionMode implements Comparator<Restrictions> {
		isRestricted,
		canAttack,
		canAffectBySkill,
		canUseSkill,
		canChat,
		canInviteToGroup,
		canInviteToAlliance,
		canChangeEquip,
		canTrade,
		canUseWarehouse,
		canUseItem,
		// TODO
		;

		private final Method METHOD;

		private RestrictionMode() {
			for (Method method : Restrictions.class.getMethods()) {
				if (name().equals(method.getName())) {
					METHOD = method;
					return;
				}
			}

			throw new InternalError();
		}

		private boolean equalsMethod(Method method) {
			if (!METHOD.getName().equals(method.getName()))
				return false;

			if (!METHOD.getReturnType().equals(method.getReturnType()))
				return false;

			return Arrays.equals(METHOD.getParameterTypes(), method.getParameterTypes());
		}

		private static final RestrictionMode[] VALUES = RestrictionMode.values();

		private static RestrictionMode parse(Method method) {
			for (RestrictionMode mode : VALUES) {
				if (mode.equalsMethod(method))
					return mode;
			}

			return null;
		}

		@Override
		public int compare(Restrictions o1, Restrictions o2) {
			return Double.compare(getPriority(o2), getPriority(o1));
		}

		private double getPriority(Restrictions restriction) {
			RestrictionPriority a1 = getMatchingMethod(restriction.getClass()).getAnnotation(RestrictionPriority.class);
			if (a1 != null)
				return a1.value();

			RestrictionPriority a2 = restriction.getClass().getAnnotation(RestrictionPriority.class);
			if (a2 != null)
				return a2.value();

			return RestrictionPriority.DEFAULT_PRIORITY;
		}

		private Method getMatchingMethod(Class<? extends Restrictions> clazz) {
			for (Method method : clazz.getMethods()) {
				if (equalsMethod(method))
					return method;
			}

			throw new InternalError();
		}

	}

	private static final Restrictions[][] RESTRICTIONS = new Restrictions[RestrictionMode.VALUES.length][0];

	public synchronized static void activate(Restrictions restriction) {
		for (Method method : restriction.getClass().getMethods()) {
			RestrictionMode mode = RestrictionMode.parse(method);

			if (mode == null)
				continue;

			if (method.getAnnotation(DisabledRestriction.class) != null)
				continue;

			Restrictions[] restrictions = RESTRICTIONS[mode.ordinal()];

			if (!ArrayUtils.contains(restrictions, restriction))
				restrictions = (Restrictions[]) ArrayUtils.add(restrictions, restriction);

			Arrays.sort(restrictions, mode);

			RESTRICTIONS[mode.ordinal()] = restrictions;
		}
	}

	public synchronized static void deactivate(Restrictions restriction) {
		for (RestrictionMode mode : RestrictionMode.VALUES) {
			Restrictions[] restrictions = RESTRICTIONS[mode.ordinal()];

			for (int index; (index = ArrayUtils.indexOf(restrictions, restriction)) != -1;)
				restrictions = (Restrictions[]) ArrayUtils.remove(restrictions, index);

			RESTRICTIONS[mode.ordinal()] = restrictions;
		}
	}

	static {
		// This is the Restrictions when player is in normal game.
		activate(new PlayerRestrictions());
		// This is the Restrictions when player is in shutdown.
		activate(new ShutdownRestrictions());
		// This is the Restrictions when player is in prison.
		activate(new PrisonRestrictions());
	}

	/**
	 * This function can be used for activate one restriction. Example: public static boolean startAppleEatingEvent(Player
	 * player) { if(RestrictionsManager.isRestricted(player, AppleEatingEventRestriction.class)) return false; return
	 * true; }
	 */
	public static boolean isRestricted(Player player, Class<? extends Restrictions> callingRestriction) {
		if (player == null)
			return true;

		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.isRestricted.ordinal()]) {
			if (!restrictions.isRestricted(player, callingRestriction))
				return false;
		}

		return false;
	}

	/**
	 * This function created for enable/disable attack.
	 *
	 * @param player
	 * @param target
	 */
	public static boolean canAttack(Player player, VisibleObject target) {
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canAttack.ordinal()]) {
			if (!restrictions.canAttack(player, target))
				return false;
		}

		return true;
	}

	/**
	 * This function is created for enable/disable on specific target.
	 *
	 * @param player
	 * @param target
	 */
	public static boolean canAffectBySkill(Player player, VisibleObject target, Skill skill) {
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canAffectBySkill.ordinal()]) {
			if (!restrictions.canAffectBySkill(player, target, skill))
				return false;
		}

		return true;
	}

	/**
	 * Check whether player can use such skill
	 *
	 * @param player
	 * @param skill
	 * @return
	 */
	public static boolean canUseSkill(Player player, Skill skill) {
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canUseSkill.ordinal()]) {
			if (!restrictions.canUseSkill(player, skill))
				return false;
		}

		return true;
	}

	/**
	 * This function is created for enable/disable chat.
	 *
	 * @param player
	 */
	public static boolean canChat(Player player) {
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canChat.ordinal()]) {
			if (!restrictions.canChat(player))
				return false;
		}

		return true;
	}

	/**
	 * This function is created for enable/disable invite to group.
	 *
	 * @param player
	 * @param target
	 */
	public static boolean canInviteToGroup(Player player, Player target) {
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canInviteToGroup.ordinal()]) {
			if (!restrictions.canInviteToGroup(player, target))
				return false;
		}

		return true;
	}

	/**
	 * This function is created for enable/disable invite to alliance.
	 *
	 * @param player
	 * @param target
	 */
	public static boolean canInviteToAlliance(Player player, Player target) {
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canInviteToAlliance.ordinal()]) {
			if (!restrictions.canInviteToAlliance(player, target))
				return false;
		}

		return true;
	}

	/**
	 * This function is created for enable/disable equip change.
	 *
	 * @param player
	 */
	public static boolean canChangeEquip(Player player) {
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canChangeEquip.ordinal()]) {
			if (!restrictions.canChangeEquip(player))
				return false;
		}

		return true;
	}

	/**
	 * Check whether player can perform trade
	 *
	 * @param player
	 * @return true or false
	 */
	public static boolean canTrade(Player player) {
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canTrade.ordinal()]) {
			if (!restrictions.canTrade(player))
				return false;
		}

		if (player.getLifeStats().isAlreadyDead())
			return false;

		return true;
	}

	/**
	 * Check whether player can use warehouse
	 *
	 * @param player
	 * @return true or false
	 */
	public static boolean canUseWarehouse(Player player) {
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canUseWarehouse.ordinal()]) {
			if (!restrictions.canUseWarehouse(player))
				return false;
		}

		return true;
	}

	/**
	 * Check whether player can use an item
	 *
	 * @param player
	 * @return
	 */
	public static boolean canUseItem(Player player, Item item) {
		for (Restrictions restrictions : RESTRICTIONS[RestrictionMode.canUseItem.ordinal()]) {
			if (!restrictions.canUseItem(player, item))
				return false;
		}
		return true;
	}
}
