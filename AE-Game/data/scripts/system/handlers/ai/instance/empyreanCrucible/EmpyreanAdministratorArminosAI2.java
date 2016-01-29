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

package ai.instance.empyreanCrucible;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.services.NpcShoutsService;

/**
 * @author xTz
 */
@AIName("empadministratorarminos")
public class EmpyreanAdministratorArminosAI2 extends NpcAI2 {

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		startEvent();
	}

	private void startEvent() {
		switch (getNpcId()) {
			case 217744:
				sendMsg(1500247, getObjectId(), false, 8000);
				sendMsg(1500250, getObjectId(), false, 20000);
				sendMsg(1500251, getObjectId(), false, 60000);
				break;
			case 217749:
				sendMsg(1500252, getObjectId(), false, 8000);
				sendMsg(1500253, getObjectId(), false, 16000);
				sendMsg(1400982, 0, false, 25000);
				sendMsg(1400988, 0, false, 27000);
				sendMsg(1400989, 0, false, 29000);
				sendMsg(1400990, 0, false, 31000);
				sendMsg(1401013, 0, false, 93000);
				sendMsg(1401014, 0, false, 113000);
				sendMsg(1401015, 0, false, 118000);
				sendMsg(1500255, getObjectId(), true, 118000);
				break;
			//case
				//despawn after 1min
		}
	}

	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}
