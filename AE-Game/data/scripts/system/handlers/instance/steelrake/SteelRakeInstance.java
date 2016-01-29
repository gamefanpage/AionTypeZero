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

package instance.steelrake;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * @author xTz
 */
@InstanceID(300100000)
public class SteelRakeInstance extends GeneralInstanceHandler {

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		if (Rnd.get(1, 2) == 1) { // Collector memekin
			spawn(215064, 747.065f, 458.293f, 942.354f, (byte) 60);
		}
		else { // Discerner werikiki
			spawn(215065, 728.008f, 541.524f, 942.354f, (byte) 59);
		}
		if (Rnd.get(1, 100) > 25) { // Pegureronerk
			spawn(798376, 516.198364f, 489.708008f, 885.760315f, (byte) 60);
		}
		if (Rnd.get(1, 2) == 1) { // Madame Bovariki
			spawn(215078, 460.904999f, 512.684998f, 952.549011f, (byte) 0);
		}
		else {
			spawn(215078, 477.534210f, 478.140991f, 951.703674f, (byte) 0);
		}
		int npcId = 0;
		switch (Rnd.get(1, 6)) {  // Special Delivery
			case 1:
				npcId = 215074;
				break;
			case 2:
				npcId = 215075;
				break;
			case 3:
				npcId = 215076;
				break;
			case 4:
				npcId = 215077;
				break;
			case 5:
				npcId = 215054;
				break;
			case 6:
				npcId = 215055;
				break;
		}
		spawn(npcId, 461.933350f, 510.545654f, 877.618103f, (byte) 90);
	}
}
