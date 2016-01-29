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

package ai.worlds.heiron;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import ai.AggressiveFirstSkillAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * @author Ritsu
 */

@AIName("bollvig")
public class BollvigAI2 extends AggressiveFirstSkillAI2
{
	protected List<Integer> percents = new ArrayList<Integer>();
	private Future<?> firstTask;
	private Future<?> secondTask;
	private Future<?> thirdTask;
	private Future<?> lastTask;

	@Override
	protected void handleSpawned()
	{
		addPercent();
		super.handleSpawned();
		Npc npc = getPosition().getWorldMapInstance().getNpc(204655);
		if(npc != null)
			npc.getController().onDelete();
	}

	@Override
	protected void handleRespawned()
	{
		addPercent();
		super.handleRespawned();
		Npc npc = getPosition().getWorldMapInstance().getNpc(204655);
		if(npc != null)
			npc.getController().onDelete();
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
					case 75:
					case 50:
						cancelTask();
						useFirstSkillTree();
						break;
					case 25:
						cancelTask();
						firstSkill();
						break;
				}
				percents.remove(percent);
				break;
			}
		}
	}

	private void useFirstSkillTree()
	{
		useSkill(17861);//Sleep of Death
		rndSpawnInRange(280802);
		rndSpawnInRange(280802);
		rndSpawnInRange(280803);
		rndSpawnInRange(280803);
		firstSkill();
	}

	private void firstSkill()
	{
		int hpPercent = getLifeStats().getHpPercentage();
		if(50 >= hpPercent && hpPercent > 25)
		{
			firstTask = ThreadPoolManager.getInstance().schedule(new Runnable()
			{

				@Override
				public void run()
				{
					useSkill(18034);//Nerve Absorption
					rndSpawnInRange(280804);
				}
			}, 10000);
		}
		else if(hpPercent <= 25)
		{
			useSkill(18037);//Blood Cell Destruction
		}
		secondTask = ThreadPoolManager.getInstance().schedule(new Runnable()
		{

			@Override
			public void run()
			{
				skillThree();
			}
		}, 31000);
	}

	private void skillThree()
	{
		useSkill(17899);//Charming Attraction
		thirdTask = ThreadPoolManager.getInstance().schedule(new Runnable()
		{

			@Override
			public void run()
			{
				int hpPercent = getLifeStats().getHpPercentage();
				if(75 >= hpPercent && hpPercent > 50)
				{
					useSkill(18025);//Curse of Soul
					firstSkill();
				}
				else if(50 >= hpPercent)
				{
					useSkill(18025);//Curse of Soul
					firstSkill();
				}
				else if(25 >= hpPercent)
				{
					useSkill(18027);//Mortal Cutting
					lastTask = ThreadPoolManager.getInstance().schedule(new Runnable()
					{

						@Override
						public void run()
						{
							skillThree();
						}
					}, 11000);
				}
			}
		}, 5000);
	}

	private void cancelTask()
	{
		if(firstTask != null && !firstTask.isDone())
			firstTask.cancel(true);
		else if(secondTask != null && !secondTask.isDone())
			secondTask.cancel(true);
		else if(thirdTask != null && !thirdTask.isDone())
			thirdTask.cancel(true);
		else if(lastTask != null && !lastTask.isDone())
			lastTask.cancel(true);
	}

	private void rndSpawnInRange(int npcId) {
		float direction = Rnd.get(0, 199) / 100f;
		float x = (float) (Math.cos(Math.PI * direction) * 10);
		float y = (float) (Math.sin(Math.PI * direction) * 10);
		spawn(npcId,  1001 + x,  2828 + y,  235.66f, (byte) 0);
	}

	private void useSkill(int skillId)
	{
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 50, getTarget()).useSkill();
	}

	private void addPercent()
	{
		percents.clear();
		Collections.addAll(percents, new Integer[]{75, 50, 25});
	}

	@Override
	protected void handleBackHome() {
		addPercent();
		cancelTask();
		super.handleBackHome();
	}

	@Override
	protected void handleDespawned() {
		percents.clear();
		cancelTask();
		deleteSummons(280802);
		deleteSummons(280803);
		deleteSummons(280804);
		super.handleDespawned();
		if(checkNpc())
			spawn(204655,  1001f,  2828f,  235.66f, (byte) 0);
	}

	@Override
	protected void handleDied() {
		percents.clear();
		cancelTask();
		deleteSummons(280802);
		deleteSummons(280803);
		deleteSummons(280804);
		super.handleDied();
		if(checkNpc())
			spawn(204655,  1001f,  2828f,  235.66f, (byte) 0);
	}

	private void deleteSummons(int npcId)
	{
		if(getPosition().getWorldMapInstance().getNpcs(npcId) != null)
		{
			List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(npcId);
			for (Npc npc : npcs)
			{
				npc.getController().onDelete();
			}
		}
	}

	private boolean checkNpc()
	{
		WorldMapInstance map = getPosition().getWorldMapInstance();
		if(map.getNpc(204655) == null && (map.getNpc(212314) == null ||
				map.getNpc(212314).getLifeStats().isAlreadyDead()))
			return true;

		return false;
	}
}
