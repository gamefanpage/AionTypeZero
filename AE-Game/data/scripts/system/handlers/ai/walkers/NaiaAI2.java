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

package ai.walkers;

import java.util.List;

import ai.GeneralNpcAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.handler.MoveEventHandler;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.templates.npcshout.NpcShout;
import org.typezero.gameserver.model.templates.npcshout.ShoutEventType;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.utils.MathUtil;

/**
 * @author Rolandas
 */
@AIName("naia")
public class NaiaAI2 extends GeneralNpcAI2 {

	boolean saidCannon = false;
	boolean saidQydro = false;

	@Override
	protected void handleMoveArrived() {
		MoveEventHandler.onMoveArrived(this);

		Npc npc2 = null;
		Npc cannon = getPosition().getWorldMapInstance().getNpc(203145);
		Npc qydro = getPosition().getWorldMapInstance().getNpc(203125);
		boolean isCannonNear = MathUtil.isIn3dRange(getOwner(), cannon, getOwner().getAggroRange());
		boolean isQydroNear = MathUtil.isIn3dRange(getOwner(), qydro, getOwner().getAggroRange());
		int delay = 0;

		List<NpcShout> shouts = null;
		if (!saidCannon && isCannonNear) {
			saidCannon = true;
			npc2 = cannon;
			delay = 10;
			// TODO: she should get closer and turn to Cannon
			// getOwner().getPosition().setH((byte)60);
			shouts = DataManager.NPC_SHOUT_DATA.getNpcShouts(getPosition().getMapId(), getNpcId(), ShoutEventType.WALK_WAYPOINT, "2",
				0);
		}
		else if (saidCannon && !isCannonNear) {
			saidCannon = false;
		}
		if (!saidQydro && isQydroNear) {
			saidQydro = true;
			npc2 = qydro;
			shouts = DataManager.NPC_SHOUT_DATA.getNpcShouts(getPosition().getMapId(), getNpcId(), ShoutEventType.WALK_WAYPOINT, "1",
				0);
		}
		else if (saidQydro && !isQydroNear) {
			saidQydro = false;
		}

		if (shouts != null) {
			NpcShoutsService.getInstance().shout(getOwner(), npc2, shouts, delay, false);
			shouts.clear();
		}
	}
}
