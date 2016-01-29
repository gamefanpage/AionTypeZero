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

package org.typezero.gameserver.taskmanager.tasks;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.taskmanager.AbstractFIFOPeriodicTaskManager;

/**
 * @author lord_rex and MrPoke
 */
public final class PacketBroadcaster extends AbstractFIFOPeriodicTaskManager<Creature> {

	private static final class SingletonHolder {

		private static final PacketBroadcaster INSTANCE = new PacketBroadcaster();
	}

	public static PacketBroadcaster getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private PacketBroadcaster() {
		super(200);
	}

	public static enum BroadcastMode {
		UPDATE_STATS {

			@Override
			public void sendPacket(Creature creature) {
				creature.getGameStats().updateStatInfo();
			}
		},

		UPDATE_SPEED {

			@Override
			public void sendPacket(Creature creature) {
				creature.getGameStats().updateSpeedInfo();
			}
		},

		UPDATE_PLAYER_HP_STAT {

			@Override
			public void sendPacket(Creature creature) {
				((Player) creature).getLifeStats().sendHpPacketUpdateImpl();
			}
		},
		UPDATE_PLAYER_MP_STAT {

			@Override
			public void sendPacket(Creature creature) {
				((Player) creature).getLifeStats().sendMpPacketUpdateImpl();
			}
		},
		UPDATE_PLAYER_EFFECT_ICONS {

			@Override
			public void sendPacket(Creature creature) {
				creature.getEffectController().updatePlayerEffectIconsImpl();
			}
		},

		UPDATE_PLAYER_FLY_TIME {

			@Override
			public void sendPacket(Creature creature) {
				((Player) creature).getLifeStats().sendFpPacketUpdateImpl();
			}
		},

		BROAD_CAST_EFFECTS {

			@Override
			public void sendPacket(Creature creature) {
				creature.getEffectController().broadCastEffectsImp();
			}
		};

		private final byte MASK;

		private BroadcastMode() {
			MASK = (byte) (1 << ordinal());
		}

		public byte mask() {
			return MASK;
		}

		protected abstract void sendPacket(Creature creature);

		protected final void trySendPacket(final Creature creature, byte mask) {
			if ((mask & mask()) == mask()) {
				sendPacket(creature);
				creature.removePacketBroadcastMask(this);
			}
		}
	}

	private static final BroadcastMode[] VALUES = BroadcastMode.values();

	@Override
	protected void callTask(Creature creature) {
		for (byte mask; (mask = creature.getPacketBroadcastMask()) != 0;) {
			for (BroadcastMode mode : VALUES) {
				mode.trySendPacket(creature, mask);
			}
		}
	}

	@Override
	protected String getCalledMethodName() {
		return "packetBroadcast()";
	}
}
