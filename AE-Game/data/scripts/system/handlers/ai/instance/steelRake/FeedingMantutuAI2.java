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

package ai.instance.steelRake;

import ai.ShifterAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * @author xTz
 */
@AIName("feeding_mantutu")
public class FeedingMantutuAI2 extends ShifterAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		if (instance.getNpc(281128) == null && instance.getNpc(281129) == null) {
			super.handleDialogStart(player);
		}
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		super.handleUseItemFinish(player);
		Npc boss = getPosition().getWorldMapInstance().getNpc(219033);
		if (boss != null && boss.isSpawned() && !NpcActions.isAlreadyDead(boss)) {
			Npc npc = null;
			switch (getNpcId()) {
				case 701387: // water supply
					npc = (Npc) spawn(281129, 712.042f, 490.5559f, 939.7027f, (byte) 0);
					break;
				case 701386: // feed supply
					npc = (Npc) spawn(281128, 714.62634f, 504.4552f, 939.60675f, (byte) 0);
					break;
			}
			boss.getAi2().onCustomEvent(1, npc);
			AI2Actions.deleteOwner(this);
		}
	}

}
