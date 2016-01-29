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
package ai.siege;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.utils.ThreadPoolManager;

/*
 * @author Luzien
 */
@AIName("dredgionCommander")
public class DredgionCommanderAI2 extends SiegeNpcAI2 {

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		scheduleOneShot();
	}

	private int getSkill() {
		switch (getNpcId()) {
			case 276649:
				return 17572;
			case 276871:
			case 276872:
				return 18411;
			case 258236:
				return 21312;
			case 272294:
			case 272794:
			case 273342:
				return 18428;
			default:
				return 0;
		}
	}
	private void scheduleOneShot() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if(getSkill() != 0) {
					if (getTarget() instanceof Npc) {
						Npc target = (Npc) getTarget();
						Race race = target.getRace();
						if ((race.equals(Race.GCHIEF_DARK) || race.equals(Race.GCHIEF_LIGHT)) && !target.getLifeStats().isAlreadyDead()) {
							AI2Actions.useSkill(DredgionCommanderAI2.this, getSkill());
							getAggroList().addHate(target, 10000);
						}
					}
					scheduleOneShot();
				}
			}
		}, 45 * 1000);
	}
}
