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

package ai.worlds.sarpan;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.StaticDoor;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.windstreams.Location2D;
import org.typezero.gameserver.model.templates.windstreams.WindstreamTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.network.aion.serverpackets.SM_WINDSTREAM_ANNOUNCE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author xTz
 */
@AIName("ancient_windstream_activator")
public class AncientWindStreamActivatorAI2 extends NpcAI2 {

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		StaticDoor door = getPosition().getMapRegion().getDoors().get(146);
				door.setOpen(false);
		windStreamAnnounce(getOwner(), 0);
		PacketSendUtility.broadcastPacket(door, new SM_SYSTEM_MESSAGE(1401332));
		despawnNpc(207089);
		spawn(207081, 162.31667f, 2210.9192f, 555.0005f, (byte) 0, 2964);
	}

	private void startTask(final Npc npc) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				Npc npc2 = (Npc) spawn(600020000, 207088, 158.58449f, 2204.0615f, 556.51917f, (byte) 0, 0, 1);
				windStreamAnnounce(npc2, 1);
				PacketSendUtility.broadcastPacket(npc2, new SM_SYSTEM_MESSAGE(1401331));
				spawn(207089, 158.58449f, 2204.0615f, 556.51917f, (byte) 0);
				PacketSendUtility.broadcastPacket(npc2, new SM_WINDSTREAM_ANNOUNCE(1, 600020000, 163, 1));

				if (npc2 != null) {
					npc2.getController().onDelete();
				}
				if (npc != null) {
					npc.getController().onDelete();
				}
			}

		}, 15000);
	}

	private void windStreamAnnounce(final Npc npc, final int state) {
		WindstreamTemplate template = DataManager.WINDSTREAM_DATA.getStreamTemplate(npc.getPosition().getMapId());
		for (Location2D wind : template.getLocations().getLocation()) {
			if (wind.getId() == 163) {
				wind.setState(state);
				break;
			}
		}
		npc.getPosition().getWorld().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_WINDSTREAM_ANNOUNCE(1, 600020000, 163, state));
			}

		});
	}

	private void despawnNpc(final int npcId) {
		getKnownList().doOnAllNpcs(new Visitor<Npc>() {

			@Override
			public void visit(Npc npc) {
				if (npc.getNpcId() == npcId) {
					npc.getController().onDelete();
				}
			}

		});
	}

	@Override
	protected void handleDied() {
		Npc npc = (Npc) spawn(207087, 158.58449f, 2204.0615f, 556.51917f, (byte) 0);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_SYSTEM_MESSAGE(1401330));
		getPosition().getMapRegion().getDoors().get(146).setOpen(true);
		despawnNpc(207081);
		super.handleDied();
		AI2Actions.deleteOwner(this);
		startTask(npc);
	}

	@Override
	public int modifyDamage(int damage) {
		return super.modifyDamage(1);
	}

}
