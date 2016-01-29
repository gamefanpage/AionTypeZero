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

import java.util.concurrent.atomic.AtomicBoolean;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.MathUtil;

/**
 * @author Ritsu
 */
@AIName("triroan_summon")
public class TriroansSummonAI2 extends AggressiveNpcAI2
{

	private AtomicBoolean isDestroyed = new AtomicBoolean(false);
	private int walkPosition;
	private int helperSkill;

	@Override
	public boolean canThink()
	{
		return false;
	}

	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		switch (getNpcId())
		{
			case 280975:
				walkPosition = 3;
				helperSkill = 18493;
				break;
			case 280976:
				walkPosition = 4;
				helperSkill = 18492;
				break;
			case 280977:
				walkPosition = 2;
				helperSkill = 18485;
				break;
			case 280978:
				walkPosition = 5;
				helperSkill = 18491;
				break;
		}
	}

	@Override
	protected void handleMoveArrived()
	{
		super.handleMoveArrived();
		int point = getOwner().getMoveController().getCurrentPoint();
		if (walkPosition == point)
		{
			if (isDestroyed.compareAndSet(false, true))
			{
				getSpawnTemplate().setWalkerId(null);
				WalkManager.stopWalking(this);
				useSkill();
				startDespawnTask();
			}
		}
	}

	private synchronized void useSkill()
	{
		Npc boss = getPosition().getWorldMapInstance().getNpc(214669);
		if (boss != null && checkLocation(getOwner()) && !boss.getLifeStats().isAlreadyDead())
		{
			SkillEngine.getInstance().getSkill(boss, helperSkill, 50, boss).useSkill();
		}
		else
			checkSkillUse(boss);
	}

	private void checkSkillUse(Npc boss)
	{
		if(boss != null && checkDistance() == 0 && !boss.getLifeStats().isAlreadyDead())
		{
			if(!boss.isCasting())
				SkillEngine.getInstance().getSkill(boss, helperSkill, 50, boss).useSkill();
			else{
				ThreadPoolManager.getInstance().schedule(new Runnable()
				{
					@Override
					public void run()
					{
						Npc boss = getPosition().getWorldMapInstance().getNpc(214669);
						if(boss != null && checkDistance() == 0 && !boss.getLifeStats().isAlreadyDead())
							checkSkillUse(boss);
					}
				}, 5000);
			}
		}
	}

	private boolean checkLocation(Npc npc)
	{
		if(checkDistance() == 1 && npc.getNpcId() == 280975)
			return true;
		else if(checkDistance() == 2 && npc.getNpcId() == 280976)
			return true;
		else if(checkDistance() == 3 && npc.getNpcId() == 280977)
			return true;
		else if(checkDistance() == 4 && npc.getNpcId() == 280978)
			return true;
		else
			return false;
	}

	public int checkDistance()
	{
		Npc boss = getPosition().getWorldMapInstance().getNpc(214669);
		if(MathUtil.getDistance(boss, 624.002f, 474.241f, 196.160f) <= 5)
			return 1;
		else if(MathUtil.getDistance(boss, 623.23f, 502.715f, 196.087f) <= 5)
			return 2;
		else if(MathUtil.getDistance(boss, 579.943f, 500.999f, 196.604f) <= 5)
			return 3;
		else if(MathUtil.getDistance(boss, 578.323f, 475.784f, 196.463f) <= 5)
			return 4;
		else
			return 0;
	}

	private void startDespawnTask()
	{
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{

			@Override
			public void run()
			{
				AI2Actions.deleteOwner(TriroansSummonAI2.this);
			}
		}, 3000);
	}
}
