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

import java.util.List;
import java.util.concurrent.Future;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.handler.TalkEventHandler;
import org.typezero.gameserver.dataholders.SkillData;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.skillengine.effect.AbnormalState;
import org.typezero.gameserver.skillengine.model.SkillTemplate;
import org.typezero.gameserver.utils.MathUtil;

/**
 * @author Ritsu
 */

@AIName("spaller_echtra")
public class SpallerEchtraAI2 extends AggressiveNpcAI2
{

	private Future<?> skillTask;
	private Future<?> skill2Task;

	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		checkDirection();
	}

	private void checkDirection()
	{
		List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(281178);
		SkillData data = new SkillData();
		SkillTemplate paralyze = data.getSkillTemplate(8256);
		if(npcs != null)
		{
			for (Npc npc : npcs)
			{
				if(MathUtil.getDistance(getOwner(), npc) <= 2)
				{
					TalkEventHandler.onTalk(this, npc);
					AI2Actions.applyEffect(this, paralyze, getOwner());
					getOwner().getEffectController().setAbnormal(4);
					getOwner().getController().cancelCurrentSkill();
					getOwner().getMoveController().abortMove();
					getOwner().getEffectController().setAbnormal(AbnormalState.PARALYZE.getId());
					skillTask = ThreadPoolManager.getInstance().schedule(new Runnable()
					{

						@Override
						public void run()
						{
							SkillEngine.getInstance().getSkill(getOwner(), 18534, 50, getOwner()).useSkill();
							skillTask = ThreadPoolManager.getInstance().schedule(new Runnable()
							{

								@Override
								public void run()
								{
									SkillEngine.getInstance().getSkill(getOwner(), 18574, 50, getOwner()).useSkill();
								}
							}, 3000);
						}
					}, 28000);
				}
			}
		}
	}

	private void cancelTask()
	{
		if(skillTask != null && !skillTask.isDone())
			skillTask.cancel(true);
		else if(skill2Task != null && !skill2Task.isDone())
			skill2Task.cancel(true);
	}

	@Override
	protected void handleBackHome()
	{
		cancelTask();
		super.handleBackHome();
	}

	@Override
	protected void handleDespawned()
	{
		cancelTask();
		super.handleDespawned();
	}

	@Override
	protected void handleDied()
	{
		cancelTask();
		super.handleDied();
	}
}
