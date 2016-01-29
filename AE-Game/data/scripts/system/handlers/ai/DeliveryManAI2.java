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

package ai;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.handler.FollowEventHandler;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.npcshout.NpcShout;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author -Nemesiss-
 */
@AIName("deliveryman")
public class DeliveryManAI2 extends FollowingNpcAI2 {

	public static int EVENT_SET_CREATOR = 1;
	private static int SERVICE_TIME = 5 * 60 * 1000;
	private static int SPAWN_ACTION_DELAY = 1500;
	private Player owner;

	@Override
	protected void handleSpawned() {
		ThreadPoolManager.getInstance().schedule(new DeleteDeliveryMan(), SERVICE_TIME);
		ThreadPoolManager.getInstance().schedule(new DeliveryManSpawnAction(), SPAWN_ACTION_DELAY);

		super.handleSpawned();
	}

	@Override
	protected void handleDespawned() {
		PacketSendUtility.broadcastPacket(getOwner(), new SM_SYSTEM_MESSAGE(true, 390267, getObjectId(), 1, new NpcShout().getParam()));

		super.handleDespawned();
	}

	@Override
	protected void handleDialogStart(Player player) {
		if (player.equals(owner)) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 18));
			player.getMailbox().sendMailList(true);
		}
	}

	@Override
	protected void handleCreatureMoved(Creature creature) {
		if (creature == owner)
			FollowEventHandler.creatureMoved(this, creature);
	}

	@Override
	protected void handleCustomEvent(int eventId, Object... args) {
		if (eventId == EVENT_SET_CREATOR)
			owner = (Player) args[0];
	}

	private final class DeleteDeliveryMan implements Runnable {

		@Override
		public void run() {
			AI2Actions.deleteOwner(DeliveryManAI2.this);
		}

	}

	private final class DeliveryManSpawnAction implements Runnable {

		@Override
		public void run() {
			PacketSendUtility.broadcastPacket(getOwner(), new SM_SYSTEM_MESSAGE(true, 390266, getObjectId(), 1, new NpcShout().getParam()));
			handleFollowMe(owner);
			handleCreatureMoved(owner);
		}

	}

}
