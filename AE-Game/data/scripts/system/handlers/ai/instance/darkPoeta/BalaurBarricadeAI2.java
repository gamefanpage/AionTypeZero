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

package ai.instance.darkPoeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;

/**
 * @author Ritsu
 */

@AIName("balaurbarricade")
public class BalaurBarricadeAI2 extends AggressiveNpcAI2
{

	protected List<Integer> percents = new ArrayList<Integer>();

	@Override
	public int modifyDamage(int damage)
	{
		return 1;
	}

	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private synchronized void checkPercentage(int hpPercentage)
	{
		for (Integer percent : percents)
		{
			if (hpPercentage <= percent)
			{
				switch(percent)
				{
					case 60:
					case 10:
						sp();
						break;
				}
				percents.remove(percent);
				break;
			}
		}
	}

	private void sp()
	{
		Npc npc = getOwner();
		float direction = Rnd.get(0, 199) / 100f;
		int distance = Rnd.get(1, 4);
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
			if(npc.getNpcId() == 700517 || npc.getNpcId() == 700556)
			{
				spawn(215262,  npc.getX() + x1,  npc.getY() + y1,  npc.getZ(),  (byte) 0);
				spawn(215262,  npc.getX() + y1,  npc.getY() + x1,  npc.getZ(),  (byte) 0);
			}
			else if(npc.getNpcId() == 700558)
			{
				spawn(215262,  npc.getX() + x1,  npc.getY() + y1,  npc.getZ(),  (byte) 0);
				spawn(214883,  npc.getX() + y1,  npc.getY() + x1,  npc.getZ(),  (byte) 0);
			}
	}

	private void addPercent()
	{
		percents.clear();
		Collections.addAll(percents, new Integer[]{60, 10});
	}

	@Override
	protected void handleSpawned()
	{
		addPercent();
		super.handleDespawned();
	}

	@Override
	protected void handleBackHome()
	{
		addPercent();
		super.handleBackHome();
	}

	@Override
	protected void handleDespawned()
	{
		percents.clear();
		super.handleDespawned();
	}

	@Override
	protected void handleDied()
	{
		percents.clear();
		super.handleDied();
	}
}
