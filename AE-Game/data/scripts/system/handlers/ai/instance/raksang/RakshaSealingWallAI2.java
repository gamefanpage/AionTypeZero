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

package ai.instance.raksang;

import ai.GeneralNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.world.WorldMapInstance;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("raksha_sealing_wall")
public class RakshaSealingWallAI2 extends GeneralNpcAI2 {

	private AtomicBoolean startedEvent = new AtomicBoolean(false);

	@Override
	public boolean canThink() {
		return false;
	}

	@Override
	protected void handleCreatureMoved(Creature creature) {
		if (creature instanceof Player) {
			final Player player = (Player) creature;
			if (MathUtil.getDistance(getOwner(), player) <= 35) {
				if (startedEvent.compareAndSet(false, true)) {
					WorldMapInstance instance = getPosition().getWorldMapInstance();
					Npc sharik = instance.getNpc(217425);
					Npc flamelord = instance.getNpc(217451);
					Npc sealguard = instance.getNpc(217456);
					int bossId;
					if ((sharik == null || NpcActions.isAlreadyDead(sharik))
							&& (flamelord == null || NpcActions.isAlreadyDead(flamelord))
							&& (sealguard == null || NpcActions.isAlreadyDead(sealguard))) {
						bossId = 217475;
					}
					else {
						bossId = 217647;
					}
					spawn(bossId, 1063.08f, 903.13f, 138.744f, (byte) 29);
					AI2Actions.deleteOwner(this);
				}
			}
		}
	}

}
