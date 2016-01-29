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

package org.typezero.gameserver.utils;

import com.aionemu.commons.objects.filter.ObjectFilter;
import org.typezero.gameserver.model.ChatType;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team.legion.Legion;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.network.aion.serverpackets.SM_MESSAGE;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;
import org.typezero.gameserver.world.zone.SiegeZoneInstance;

/**
 * This class contains static methods, which are utility methods, all of them are interacting only with objects passed
 * as parameters.<br>
 * These methods could be placed directly into Player class, but we want to keep Player class as a pure data holder.<br>
 *
 * @author Luno
 */
public class PacketSendUtility {

	/**
	 * Global message sending
	 */
	public static void sendMessage(Player player, String msg) {
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.GOLDEN_YELLOW));
	}

	public static void sendWhiteMessage(Player player, String msg) {
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.WHITE));
	}
	public static void sendWhiteMessageOnCenter(Player player, String msg) {
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.WHITE_CENTER));
	}

	public static void sendYellowMessage(Player player, String msg) {
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.YELLOW));
	}
	public static void sendYellowMessageOnCenter(Player player, String msg) {
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.YELLOW_CENTER));
	}

	public static void sendBrightYellowMessage(Player player, String msg) {
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.BRIGHT_YELLOW));
	}
	public static void sendBrightYellowMessageOnCenter(Player player, String msg) {
		sendPacket(player, new SM_MESSAGE(0, null, msg, ChatType.BRIGHT_YELLOW_CENTER));
	}

	/**
	 * Send packet to this player
	 */
	public static void sendPacket(Player player, AionServerPacket packet) {
		if (player.getClientConnection() != null) {
			player.getClientConnection().sendPacket(packet);
		}
	}

	/**
	 * Broadcast packet to all visible players.
	 *
	 * @param player
	 * @param packet
	 *          ServerPacket that will be broadcast
	 * @param toSelf
	 *          true if packet should also be sent to this player
	 */
	public static void broadcastPacket(Player player, AionServerPacket packet, boolean toSelf) {
		if (toSelf)
			sendPacket(player, packet);

		broadcastPacket(player, packet);
	}

	/**
	 * Broadcast packet to all visible players.
	 *
	 * @param visibleObject
	 * @param packet
	 */
	public static void broadcastPacketAndReceive(VisibleObject visibleObject, AionServerPacket packet) {
		if (visibleObject instanceof Player)
			sendPacket((Player) visibleObject, packet);

		broadcastPacket(visibleObject, packet);
	}

	/**
	 * Broadcast packet to all Players from knownList of the given visible object.
	 *
	 * @param visibleObject
	 * @param packet
	 */
	public static void broadcastPacket(VisibleObject visibleObject, final AionServerPacket packet) {
		visibleObject.getKnownList().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					sendPacket(player, packet);
				}
			}
		});
	}

	/**
	 * Broadcasts packet to all visible players matching a filter
	 *
	 * @param player
	 * @param packet
	 *          ServerPacket to be broadcast
	 * @param toSelf
	 *          true if packet should also be sent to this player
	 * @param filter
	 *          filter determining who should be messaged
	 */
	public static void broadcastPacket(Player player, final AionServerPacket packet, boolean toSelf,
		final ObjectFilter<Player> filter) {
		if (toSelf) {
			sendPacket(player, packet);
		}

		player.getKnownList().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player object) {
				if (filter.acceptObject(object))
					sendPacket(object, packet);
			}
		});
	}

	/**
	 * Broadcasts packet to all Players from knownList of the given visible object within the specified distance in meters
	 *
	 * @param visibleObject
	 * @param packet
	 * @param distance
	 */
	public static void broadcastPacket(final VisibleObject visibleObject, final AionServerPacket packet, final int distance)
	{
		visibleObject.getKnownList().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player p)
			{
				if (MathUtil.isIn3dRange(visibleObject, p, distance))
					sendPacket(p, packet);
			}
		});
	}

	/**
	 * Broadcasts packet to ALL players matching a filter
	 *
	 * @param player
	 * @param packet
	 *          ServerPacket to be broadcast
	 * @param filter
	 *          filter determining who should be messaged
	 */
	public static void broadcastFilteredPacket(final AionServerPacket packet,
		final ObjectFilter<Player> filter) {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player object) {
				if (filter.acceptObject(object))
					sendPacket(object, packet);
			}
		});
	}

	/**
	 * Broadcasts packet to all legion members of a legion
	 *
	 * @param legion
	 *          Legion to broadcast packet to
	 * @param packet
	 *          ServerPacket to be broadcast
	 */
	public static void broadcastPacketToLegion(Legion legion, AionServerPacket packet) {
		for (Player onlineLegionMember : legion.getOnlineLegionMembers()) {
			sendPacket(onlineLegionMember, packet);
		}
	}

	public static void broadcastPacketToLegion(Legion legion, AionServerPacket packet, int playerObjId) {
		for (Player onlineLegionMember : legion.getOnlineLegionMembers()) {
			if (onlineLegionMember.getObjectId() != playerObjId)
				sendPacket(onlineLegionMember, packet);
		}
	}

	public static void broadcastPacketToZone(SiegeZoneInstance zone, final AionServerPacket packet) {
		zone.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				sendPacket(player, packet);

			}
		});
	}
}
