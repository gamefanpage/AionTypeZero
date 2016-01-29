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

package ai.worlds.tiamaranta.ativasCristalline;

import java.util.concurrent.atomic.AtomicBoolean;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;

/**
 * @author Ritsu
 */

@AIName("ativascristalline")
public class AtivasCristallineAI2 extends AggressiveNpcAI2
{

	private AtomicBoolean isStart90Event = new AtomicBoolean(false);
	private AtomicBoolean isStart60Event = new AtomicBoolean(false);
	private AtomicBoolean isStart30Event = new AtomicBoolean(false);
	private AtomicBoolean isStart10Event = new AtomicBoolean(false);

	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	@Override
	protected void handleBackHome()
	{
		isStart90Event.set(false);
		isStart60Event.set(false);
		isStart30Event.set(false);
		isStart10Event.set(false);
		super.handleBackHome();
	}

	private void checkPercentage(int hpPercentage)
	{
		if (hpPercentage <= 90)
		{
			if (isStart90Event.compareAndSet(false, true)) {
				topazKomad();
			}
		}
		else if (hpPercentage <= 60)
		{
			if (isStart60Event.compareAndSet(false, true)) {
				garnetKomad();
			}
		}
		else if (hpPercentage <= 30)
		{
			if (isStart30Event.compareAndSet(false, true)) {
				topazKomad();
			}
		}
		else if (hpPercentage <= 10)
		{
			if (isStart10Event.compareAndSet(false, true)) {
				garnetKomad();
			}
		}
	}

	private void garnetKomad() {
		if (getPosition().isSpawned() && !isAlreadyDead())
		{
			for (int i = 0; i < 1; i++) {
				int distance = Rnd.get(3, 5);
				int nrNpc = Rnd.get(1, 0);
				switch (nrNpc) {
					case 1:
						nrNpc = 282708; //Garnet Komad.
						break;
				}
				rndSpawnInRange(nrNpc, distance);
			}
		}
	}

	private void topazKomad() {
		if (getPosition().isSpawned() && !isAlreadyDead())
		{
			for (int i = 0; i < 1; i++) {
				int distance = Rnd.get(3, 5);
				int nrNpc = Rnd.get(1, 0);
				switch (nrNpc) {
					case 1:
						nrNpc = 282709; //Topaz Komad.
						break;
				}
				rndSpawnInRange(nrNpc, distance);
			}
		}
	}

	private void rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		spawn(npcId, getPosition().getX() + x1, getPosition().getY() + y1, getPosition().getZ(), (byte) 0);
	}
}
