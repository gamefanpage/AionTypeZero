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

package ai.instance.theobomosLab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.AggressiveNpcAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 *
 * @author Ritsu
 */
@AIName("triroan")
public class UnstableTriroanAI2 extends AggressiveNpcAI2
{

	protected List<Integer> percents = new ArrayList<Integer>();

	@Override
	protected void handleSpawned()
	{
		addPercent();
		super.handleSpawned();
	}

	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private synchronized void checkPercentage(int hpPercentage)
	{
		if (hpPercentage > 99 && percents.size() < 10)
			addPercent();

		for (Integer percent : percents)
		{
			if (hpPercentage <= percent)
			{
				switch(percent)
				{
					case 99:
						SkillEngine.getInstance().getSkill(getOwner(), 16699, 1, getOwner()).useSkill();
						break;
					case 90:
						spawnFire();
						break;
					case 80:
						spawnWater();
						break;
					case 70:
						spawnEarth();
						break;
					case 60:
						spawnWind();
						break;
					case 50:
						spawnFire();
						break;
					case 40:
						spawnFire();
						spawnWater();
						break;
					case 30:
						spawnEarth();
						spawnWind();
						break;
					case 20:
						spawnWind();
						spawnFire();
						break;
					case 10:
						spawnWater();
						spawnEarth();
						break;
					case 5:
						spawnWind();
						spawnFire();
						spawnWater();
						spawnEarth();
						break;
				}
				percents.remove(percent);
				break;
			}
		}
	}

	private void spawnFire()
	{
		startWalk((Npc) spawn(280975,  601.966f,  488.853f,  196.019f,  (byte) 0), "3101100002");
	}


	private void spawnWater()
	{
		startWalk((Npc) spawn(280976,  601.966f,  488.853f,  196.019f,  (byte) 0), "3101100003");
	}

	private void spawnEarth()
	{
		startWalk((Npc) spawn(280977,  601.966f,  488.853f,  196.019f,  (byte) 0), "3101100004");
	}

	private void spawnWind()
	{
		startWalk((Npc) spawn(280978,  601.966f,  488.853f,  196.019f,  (byte) 0), "3101100005");
	}

	private void startWalk(Npc npc, String walkId)
	{
		npc.getSpawn().setWalkerId(walkId);
		WalkManager.startWalking((NpcAI2) npc.getAi2());
		npc.setState(1);
		PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
	}

	private void addPercent()
	{
		percents.clear();
		Collections.addAll(percents, new Integer[]{99, 90, 80, 70, 60, 50, 40, 30, 20, 10, 5});
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
