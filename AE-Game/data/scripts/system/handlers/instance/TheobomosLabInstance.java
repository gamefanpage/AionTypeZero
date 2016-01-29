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

package instance;

import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.WorldPosition;

/**
 * @author xTz,Ritsu
 */
@InstanceID(310110000)
public class TheobomosLabInstance extends GeneralInstanceHandler {

	private boolean isInstanceDestroyed;
	private boolean isDead1 = false;
	private boolean isDead2 = false;

	@Override
	public void onDie(Npc npc) {
		if(isInstanceDestroyed)
			return;
		Creature master = npc.getMaster();
		if (master instanceof Player)
			return;

		int npcId = npc.getNpcId();
		switch (npcId) {
			case 280971:
			case 280972: {
				if(guardDie(npc))
				removeBuff();
			}
		}
	}

/**	@Override
	public void onEnterInstance(Player player)
	{
		final QuestState qs = player.getQuestStateList().getQuestState(1094);
		if (qs != null && qs.getStatus() == QuestStatus.COMPLETE)
			doors.get(37).setOpen(true);
		else
			doors.get(37).setOpen(false);
	}//this door is static door, so we cant control it.
	*/

	@Override
	public void onInstanceDestroy() {
		isDead1 = false;
		isDead2 = false;
		isInstanceDestroyed = true;
	}

	private boolean guardDie(Npc npc) {
		WorldPosition p = npc.getPosition();
		int npcId = npc.getNpcId();
		Npc orb = getNpc(280973);
		if(MathUtil.getDistance(orb, npc) <= 7) {
			switch (npcId) {
				case 280971: {
					isDead1 = true;
					break;
				}
				case 280972: {
					isDead2 = true;
					break;
				}
			}
			return true;
		}
		else {
			npc.getController().onDelete();
			spawn(npcId, p.getX(), p.getY(), p.getZ(), (byte) 41);
			return false;
		}
	}

	private void removeBuff() {
		ThreadPoolManager.getInstance().schedule(new Runnable()  {
			@Override
			public void run() {
				if(!isInstanceDestroyed && isDead1 && isDead2) {
					getNpc(214668).getEffectController().removeEffect(18481);
					getNpc(280973).getController().onDelete();
				}
			}
		}, 1000);
	}
}
