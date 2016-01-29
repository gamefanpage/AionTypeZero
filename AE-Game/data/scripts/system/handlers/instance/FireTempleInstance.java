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


package instance;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * @author Gigi
 */
@InstanceID(320100000)
public class FireTempleInstance extends GeneralInstanceHandler {

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		// Random spawns of bosses
		if (Rnd.get(1, 100) > 25) { // Blue Crystal Molgat
			spawn(212839, 127.1218f, 176.1912f, 99.67548f, (byte) 15);
		}
		else { // elite mob spawns
			spawn(212790, 127.1218f, 176.1912f, 99.67548f, (byte) 15);
		}

		if (Rnd.get(1, 100) > 25) { // Black Smoke Asparn
			spawn(212842, 322.3193f, 431.2696f, 134.5296f, (byte) 80);
		}
		else { // elite mob spawns
			spawn(212799, 322.3193f, 431.2696f, 134.5296f, (byte) 80);
		}

		if (Rnd.get(1, 100) > 25) { // Lava Gatneri
			spawn(212840, 153.0038f, 299.7786f, 123.0186f, (byte) 30);
		}
		else { // elite mob spawns
			spawn(212794, 153.0038f, 299.7786f, 123.0186f, (byte) 30);
		}

		if (Rnd.get(1, 100) > 25) { // Tough Sipus
			spawn(212843, 296.6911f, 201.9092f, 119.3652f, (byte) 30);
		}
		else { // elite mob spawns
			spawn(212803, 296.6911f, 201.9092f, 119.3652f, (byte) 15);
		}

		if (Rnd.get(1, 100) > 25) { // Flame Branch Flavi
			spawn(212841, 350.9276f, 351.7389f, 146.8498f, (byte) 45);
		}
		else { // elite mob spawns
			spawn(212799, 350.9276f, 351.7389f, 146.8498f, (byte) 45);
		}

		if (Rnd.get(1, 100) > 25) { // Broken Wing Kutisen
			spawn(212845, 298.7095f, 89.42245f, 128.7143f, (byte) 15);
		}
		else { // elite mob spawns
			spawn(214094, 298.7095f, 89.42245f, 128.7143f, (byte) 15);
		}

		if (Rnd.get(1, 100) > 90) {// stronger kromede
			spawn(214621, 421.9935f, 93.18915f, 117.3053f, (byte) 46);
		}
		else { // normal kromede
			spawn(212846, 421.9935f, 93.18915f, 117.3053f, (byte) 46);
		}
		//spawn Silver Blade Rotan (pool=1)
		switch ((int)Rnd.get(1, 3)) {
			case 1:
				spawn(212844, 216.35815f, 264.34009f, 120.931f, (byte) 90);
				break;
			case 2:
				spawn(212844, 277.70825f, 248.30695f, 121.067665f, (byte) 90);
				break;
			case 3:
				spawn(212844, 290.94812f, 178.18243f, 119.29246f, (byte) 90);
				break;
		}

	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0
				: lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}

}
