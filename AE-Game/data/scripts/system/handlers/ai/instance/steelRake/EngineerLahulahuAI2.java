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

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * @author xTz
 */
@AIName("engineerlahulahu")
public class EngineerLahulahuAI2 extends AggressiveNpcAI2 {

	private boolean isStart = false;
	private boolean isUsedSkill = false;
	private int skill = 18153;
	private Npc npc;
	private Npc npc1;
	private Npc npc2;
	private Npc npc3;
	private Npc npc4;
	private Npc npc5;
	private Npc npc6;
	private Npc npc7;
	private Npc npc8;
	private Npc npc9;
	private Npc npc10;
	private Npc npc11;

	private void registerNpcs() {
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		npc = instance.getNpc(281111);
		npc1 = instance.getNpc(281325);
		npc2 = instance.getNpc(281323);
		npc3 = instance.getNpc(281322);
		npc4 = instance.getNpc(281326);
		npc5 = instance.getNpc(281113);
		npc6 = instance.getNpc(281324);
		npc7 = instance.getNpc(281109);
		npc8 = instance.getNpc(281112);
		npc9 = instance.getNpc(281114);
		npc10 = instance.getNpc(281108);
		npc11 = instance.getNpc(281110);
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage <= 95 && !isStart) {
			registerNpcs();
			isStart = true;
			AI2Actions.useSkill(this, 18131);
			useSkills();
		}
		if (hpPercentage <= 25 && !isUsedSkill) {
			isUsedSkill = true;
			getEffectController().removeEffect(18131);
			AI2Actions.useSkill(this, 18132);
		}
	}

	@Override
	protected void handleBackHome() {
		isStart = false;
		isUsedSkill = false;
		super.handleBackHome();
	}

	private void doSchedule() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				useSkills();
			}

		}, 10000);
	}

	private void useSkills() {
		if (getPosition().isSpawned() && !isAlreadyDead() && isStart) {
			int rnd = Rnd.get(1, 8);
			switch (rnd) {
				case 1:
					if (npc != null) {
						npc.setTarget(npc);
						npc.getController().useSkill(skill);
					}
					if (npc1 != null) {
						npc1.setTarget(npc1);
						npc1.getController().useSkill(skill);
					}
					break;
				case 2:
					if (npc2 != null) {
						npc2.setTarget(npc2);
						npc2.getController().useSkill(skill);
					}
					if (npc3 != null) {
						npc3.setTarget(npc3);
						npc3.getController().useSkill(skill);
					}
					break;
				case 3:
					if (npc4 != null) {
						npc4.setTarget(npc4);
						npc4.getController().useSkill(skill);
					}
					if (npc5 != null) {
						npc5.setTarget(npc5);
						npc5.getController().useSkill(skill);
					}
					break;
				case 4:
					if (npc6 != null) {
						npc6.setTarget(npc6);
						npc6.getController().useSkill(skill);
					}
					if (npc7 != null) {
						npc7.setTarget(npc7);
						npc7.getController().useSkill(skill);
					}
					break;
				case 5:
					if (npc8 != null) {
						npc8.setTarget(npc8);
						npc8.getController().useSkill(skill);
					}
					break;
				case 6:
					if (npc9 != null) {
						npc9.setTarget(npc9);
						npc9.getController().useSkill(skill);
					}
					break;
				case 7:
					if (npc10 != null) {
						npc10.setTarget(npc10);
						npc10.getController().useSkill(skill);
					}
					break;
				case 8:
					if (npc11 != null) {
						npc11.setTarget(npc11);
						npc11.getController().useSkill(skill);
					}
					break;
			}
			doSchedule();
		}
	}

}
