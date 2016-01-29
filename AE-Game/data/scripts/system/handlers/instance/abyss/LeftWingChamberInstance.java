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

import com.aionemu.commons.utils.Rnd;
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
 * @author xTz
 */
@InstanceID(300080000)
public class LeftWingChamberInstance extends GeneralInstanceHandler {

	private boolean isStartTimer = false;
	private long startTime;
	private boolean isInstanceDestroyed = false;
	private Race instanceRace;

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		spawnRings();
		//spawn Treasurer Nabatma (pool=1)
		switch ((int)Rnd.get(1, 2)) {
			case 1:
				spawn(215424, 502.1326f, 502.89673f, 352.94437f, (byte) 20);
				break;
			case 2:
				spawn(215424, 508.71814f, 660.93994f, 352.94638f, (byte) 60);
				break;
		}
	}

	private void spawnRings() {
		FlyRing f1 = new FlyRing(new FlyRingTemplate("LEFT_WING_1", mapId,
				new Point3D(576.2102, 585.4146, 353.90677),
				new Point3D(576.2102, 585.4146, 359.90677),
				new Point3D(575.18384, 596.36664, 353.90677), 10), instanceId);
		f1.spawn();
	}

	@Override
	public boolean onPassFlyingRing(Player player, String flyingRing) {
		if (flyingRing.equals("LEFT_WING_1")) {
			if (!isStartTimer) {
				PacketSendUtility.sendPacket(player, STR_MSG_INSTANCE_START_IDABRE);
				isStartTimer = true;
				startTime = System.currentTimeMillis();
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 900));
					spawn(700466, 196.038f, 552.028f, 364.604f, (byte) 1);
					spawn(700466, 232.866f, 745.98f, 364.604f, (byte) 110);
					spawn(700466, 208.59f, 684.088f, 364.604f, (byte) 115);
					spawn(700466, 196.132f, 619.546f, 364.604f, (byte) 117);
					spawn(700466, 210.714f, 486.842f, 364.604f, (byte) 4);
					spawn(700466, 233.327f, 425.466f, 364.604f, (byte) 7);
					spawnGoldChest();
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						despawnNpcs(getNpcs(700466));
						despawnNpcs(getNpcs(701481));
						despawnNpcs(getNpcs(701486));
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
		final int chestId = instanceRace == Race.ELYOS ? 701481 : 701486;
		spawn(chestId, 496.87f, 664.07f, 352.94f, (byte) 90);
	}

	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
	}
}
