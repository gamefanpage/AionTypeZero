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

package instance.abyss;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.flyring.FlyRing;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.flyring.FlyRingTemplate;
import org.typezero.gameserver.model.utils3d.Point3D;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import static org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE.*;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import java.util.List;

/**
 *
 * @author xTz
 */
@InstanceID(300090000)
public class RightWingChamberInstance extends GeneralInstanceHandler {

	private boolean isStartTimer = false;
	private long startTime;
	private boolean isInstanceDestroyed = false;
	private Race instanceRace;

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		spawnRings();
	}

	private void spawnRings() {
		FlyRing f1 = new FlyRing(new FlyRingTemplate("RIGHT_WING_1", mapId,
				new Point3D(262.87686, 361.04962,107.83435),
				new Point3D(262.87686, 361.04962,113.83435),
				new Point3D(254.22054, 358.58627, 107.83435), 8), instanceId);
		f1.spawn();
	}

	@Override
	public boolean onPassFlyingRing(Player player, String flyingRing) {
		if (flyingRing.equals("RIGHT_WING_1")) {
			if (!isStartTimer) {
				isStartTimer = true;
				PacketSendUtility.sendPacket(player, STR_MSG_INSTANCE_START_IDABRE);
				startTime = System.currentTimeMillis();
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 900));
					spawn(700471, 279.97f, 285.31f, 101.84085f, (byte) 30);
					spawn(700471, 257.35f, 153.45f, 101.84089f, (byte) 30);
					spawn(700471, 260.4574f, 266.0f, 102.33314f, (byte) 30);
					spawn(700471, 260.65f, 215.79f, 102.332985f, (byte) 30);
					spawn(700471, 257.9f, 184.9f, 102.33297f, (byte) 30);
					spawn(700471, 318.03f, 192.07f, 101.84086f, (byte) 30);
					spawn(700471, 291.55f, 168.34f, 101.84086f, (byte) 30);
					spawn(700471, 268.38f, 175.52f, 102.33297f, (byte) 30);
					spawn(700471, 277.21f, 153.45f, 101.84086f, (byte) 30);
					spawn(700471, 235.28f, 209.72f, 102.33297f, (byte) 30);
					spawn(700471, 212.66f, 269.86f, 101.84082f, (byte) 70);
					spawn(700471, 307.55f, 209.72f, 102.33296f, (byte) 70);
					spawn(700471, 276.1f, 204.76f, 102.33298f, (byte) 70);
					spawn(700471, 250.72f, 212.48f, 102.332985f, (byte) 70);
					spawn(700471, 249.62f, 255.52f, 102.09304f, (byte) 70);
					spawnGoldChest();
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						despawnNpcs(getNpcs(700471));
						despawnNpcs(getNpcs(701482));
						despawnNpcs(getNpcs(701487));
					}
				}, 900000);
			}
		}
		return false;
	}

	@Override
	public void onEnterInstance(Player player) {
		if (isStartTimer) {
			long time = System.currentTimeMillis() - startTime;
			if (time < 900000) {
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 900 - (int) time / 1000));
			}
		}
	}

	private List<Npc> getNpcs(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpcs(npcId);
		}
		return null;
	}

	private void despawnNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			npc.getController().onDelete();
		}
	}

	private void spawnGoldChest() {
		final int chestId = instanceRace == Race.ELYOS ? 701482 : 701487;
		spawn(chestId, 261.69f, 206.11f, 102.33f, (byte) 0);
	}

	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
	}
}
