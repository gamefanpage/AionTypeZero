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

package ai.instance.elementisForest;

import ai.GeneralNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.world.WorldPosition;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xTz
 */
@AIName("jurdins_illusion")
public class JurdinsIllusionAI2 extends GeneralNpcAI2 {

	private AtomicBoolean isSpawned = new AtomicBoolean(false);

	@Override
	protected void handleDialogStart(Player player) {
		if (isSpawned.compareAndSet(false, true)) {
			WorldPosition p = getPosition();
			final int instanceId = p.getInstanceId();
			final int worldId = p.getMapId();
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							spawn(worldId, 217238, 472.989f, 798.109f, 130.072f, (byte) 90, 0, instanceId);
							Npc smoke = (Npc) spawn(282465, 472.989f, 798.109f, 130.072f, (byte) 0);
							NpcActions.delete(smoke);
						}

					}, 4000);
					AI2Actions.deleteOwner(JurdinsIllusionAI2.this);
				}

			}, 3000);
		}
		super.handleDialogStart(player);
	}
}
