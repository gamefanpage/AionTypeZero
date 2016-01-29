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
package ai.instance.dragonLordsRefuge;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.ai2.AI2Actions;

/**
 * @author Bobobear
 */

@AIName("gods")
public class GodsAI2 extends AggressiveNpcAI2 {

   Npc tiamat;

	@Override
	protected  void handleDeactivate() {
	}

	@Override
	public int modifyDamage(int damage) {
		return 6000;
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		tiamat = getPosition().getWorldMapInstance().getNpc(219361);
		if (getNpcId() == 219488 || getNpcId() == 219491) {
			//empyrean lord (god) debuff all players before start attack Tiamat
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					SkillEngine.getInstance().getSkill(getOwner(), (getOwner().getNpcId() == 219488 ? 20932 : 20936), 100, getOwner()).useSkill();
				}
			}, 8000);
			//empyrean lord (god) start attack Tiamat Dragon
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					AI2Actions.targetCreature(GodsAI2.this, tiamat);
					getAggroList().addHate(tiamat, 100000);
					NpcShoutsService.getInstance().sendMsg(getOwner(), 1401550);
					SkillEngine.getInstance().getSkill(getOwner(), (getNpcId() == 219488 ? 20931 : 20935), 60, tiamat).useNoAnimationSkill(); //adds 1mio hate
				}
			}, 12000);
		} else if (getNpcId() == 219489 || getNpcId() == 219492) {
			//empyrean lord (god) start final attack to Tiamat Dragon before became exausted
			NpcShoutsService.getInstance().sendMsg(getOwner(), (getNpcId() == 219489 ? 1401538 : 1401539));
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					SkillEngine.getInstance().getSkill(getOwner(), (getNpcId() == 219489 ? 20929 : 20933), 100, tiamat).useNoAnimationSkill();
				}
			}, 2000);
		}
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	@Override
	protected void handleActivate() {
		super.handleActivate();
		tiamat = getPosition().getWorldMapInstance().getNpc(219361);
		if (getOwner().getNpcId() == 219488 || getOwner().getNpcId() == 219491) {
			AI2Actions.targetCreature(GodsAI2.this, tiamat);
			SkillEngine.getInstance().getSkill(getOwner(), (getNpcId() == 219488 ? 20931 : 20935), 60, tiamat).useSkill();
		}
	}

	private void checkPercentage(int hpPercentage) {
		if (getOwner().getNpcId() == 219488 || getOwner().getNpcId() == 219488) {
			if (hpPercentage == 50) {
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1401548);
			}
			if (hpPercentage == 15) {
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1401549);
			}
			if (hpPercentage < 5) {
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1401548);
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1401549);
			}
		}
	}
}
