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

import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;

/**
 * @author xTz
 */
@InstanceID(300270000)
public class ArgentManorInstance extends GeneralInstanceHandler {

	@Override
	public void onDie(Npc npc) {
		switch (npc.getNpcId()) {
			case 217243:
				Npc prison = instance.getNpc(205498);
				if (prison != null) {
					NpcShoutsService.getInstance().sendMsg(prison, 1500263, prison.getObjectId(), 0, 0);
					prison.getSpawn().setWalkerId("69B73541CCBF9F7BAB484BA68FF4BE0D2A9B6AD6");
					WalkManager.startWalking((NpcAI2) prison.getAi2());
				}
				spawn(701011, 955.91956f, 1240.153f, 54.090305f, (byte) 90);
				break;
		}
	}

	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch(npc.getNpcId()) {
			case 701001:
				SkillEngine.getInstance().getSkill(npc, 19316, 60, player).useNoAnimationSkill();
				break;
			case 701002:
				SkillEngine.getInstance().getSkill(npc, 19317, 60, player).useNoAnimationSkill();
				break;
			case 701003:
				SkillEngine.getInstance().getSkill(npc, 19318, 60, player).useNoAnimationSkill();
				break;
			case 701004:
				SkillEngine.getInstance().getSkill(npc, 19319, 60, player).useNoAnimationSkill();
				break;
		}
	}

	@Override
	public void onPlayerLogOut(Player player) {
		removeEffects(player);
	}

	@Override
	public void onLeaveInstance(Player player) {
		removeEffects(player);
	}

	private void removeEffects(Player player) {
		player.getEffectController().removeEffect(19316);
		player.getEffectController().removeEffect(19317);
		player.getEffectController().removeEffect(19318);
		player.getEffectController().removeEffect(19319);
	}
}
