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

package instance.pvparenas;

import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.flyring.FlyRing;
import org.typezero.gameserver.model.gameobjects.Gatherable;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import org.typezero.gameserver.model.templates.flyring.FlyRingTemplate;
import org.typezero.gameserver.model.utils3d.Point3D;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 *
 * @author xTz
 */
@InstanceID(300420000)
public class ChaosTrainingGroundsInstance extends PvPArenaInstance {

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		killBonus = 1000;
		deathFine = -125;
		super.onInstanceCreate(instance);
	}

	@Override
	public void onGather(Player player, Gatherable gatherable) {
		if (!instanceReward.isStartProgress()) {
			return;
		}
		getPlayerReward(player.getObjectId()).addPoints(1250);
		sendPacket();
		int nameId = gatherable.getObjectTemplate().getNameId();
		DescriptionId name = new DescriptionId(nameId * 2 + 1);
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400237, name, 1250));
	}

	@Override
	protected void spawnRings() {
		FlyRing f1 = new FlyRing(new FlyRingTemplate("PVP_ARENA_1", mapId,
				new Point3D(674.66974, 1792.8499, 149.77501),
				new Point3D(674.66974, 1792.8499, 155.77501),
				new Point3D(678.83636, 1788.5325, 149.77501), 6), instanceId);
		f1.spawn();
		FlyRing f2 = new FlyRing(new FlyRingTemplate("PVP_ARENA_2", mapId,
				new Point3D(688.30615, 1769.7937, 149.88556),
				new Point3D(688.30615, 1769.7937, 155.88556),
				new Point3D(689.42096, 1763.8982, 149.88556), 6), instanceId);
		f2.spawn();
		FlyRing f3 = new FlyRing(new FlyRingTemplate("PVP_ARENA_3", mapId,
				new Point3D(664.2252, 1761.671, 170.95732),
				new Point3D(664.2252, 1761.671, 176.95732),
				new Point3D(669.2843, 1764.8967, 170.95732), 6), instanceId);
		f3.spawn();
		FlyRing fv1 = new FlyRing(new FlyRingTemplate("PVP_ARENA_VOID_1", mapId,
				new Point3D(690.28625, 1753.8561, 192.07726),
				new Point3D(690.28625, 1753.8561, 198.07726),
				new Point3D(689.4365, 1747.9165, 192.07726), 6), instanceId);
		fv1.spawn();
		FlyRing fv2 = new FlyRing(new FlyRingTemplate("PVP_ARENA_VOID_2", mapId,
				new Point3D(690.1935, 1797.0029, 203.79236),
				new Point3D(690.1935, 1797.0029, 209.79236),
				new Point3D(692.8295, 1802.3928, 203.79236), 6), instanceId);
		fv2.spawn();
		FlyRing fv3 = new FlyRing(new FlyRingTemplate("PVP_ARENA_VOID_3", mapId,
				new Point3D(659.2784, 1766.0273, 207.25465),
				new Point3D(659.2784, 1766.0273, 213.25465),
				new Point3D(665.2619, 1766.4718, 207.25465), 6), instanceId);
		fv3.spawn();
	}

	@Override
	public boolean onPassFlyingRing(Player player, String flyingRing) {
		PvPArenaPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (playerReward == null || !instanceReward.isStartProgress()) {
			return false;
		}
		Npc npc;
		if (flyingRing.equals("PVP_ARENA_1")) {
			npc = getNpc(674.841f, 1793.065f, 150.964f);
			if (npc != null && npc.isSpawned()) {
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
				sendSystemMsg(player, npc, 250);
				sendPacket();
			}
		}
		else if (flyingRing.equals("PVP_ARENA_2")) {
			npc = getNpc(688.410f, 1769.611f, 150.964f);
			if (npc != null && npc.isSpawned()) {
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
				playerReward.addPoints(250);
				sendSystemMsg(player, npc, 250);
				sendPacket();
			}
		}
		else if (flyingRing.equals("PVP_ARENA_3")) {
			npc = getNpc(664.160f, 1761.933f, 171.504f);
			if (npc != null && npc.isSpawned()) {
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
				playerReward.addPoints(250);
				sendSystemMsg(player, npc, 250);
				sendPacket();
			}
		}
		else if (flyingRing.equals("PVP_ARENA_VOID_1")) {
			npc = getNpc(693.061f, 1752.479f, 186.750f);
			if (npc != null && npc.isSpawned()) {
				useSkill(npc, player, 20059, 1);
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
			}
		}
		else if (flyingRing.equals("PVP_ARENA_VOID_2")) {
			npc = getNpc(688.061f, 1798.229f, 198.500f);
			if (npc != null && npc.isSpawned()) {
				useSkill(npc, player, 20059, 1);
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
			}
		}
		else if (flyingRing.equals("PVP_ARENA_VOID_3")) {
			npc = getNpc(659.311f, 1768.979f, 201.500f);
			if (npc != null && npc.isSpawned()) {
				useSkill(npc, player, 20059, 1);
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
			}
		}
		return false;
	}

}
