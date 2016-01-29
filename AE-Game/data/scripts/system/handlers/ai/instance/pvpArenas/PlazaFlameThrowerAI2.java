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

package ai.instance.pvpArenas;

import ai.ShifterAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.instance.instancereward.InstanceReward;
import org.typezero.gameserver.model.instance.instancereward.PvPArenaReward;
import org.typezero.gameserver.skillengine.SkillEngine;
import java.util.List;

/**
 *
 * @author xTz
 */
@AIName("plaza_flame_thrower")
public class PlazaFlameThrowerAI2 extends ShifterAI2 {

	private boolean isRewarded;

	@Override
	protected void handleDialogStart(Player player) {
		InstanceReward<?> instance = getPosition().getWorldMapInstance().getInstanceHandler().getInstanceReward();
		if (instance != null && !instance.isStartProgress()) {
			return;
		}
		super.handleDialogStart(player);
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		super.handleUseItemFinish(player);
		if (!isRewarded) {
			isRewarded = true;
			AI2Actions.handleUseItemFinish(this, player);
			switch(getNpcId()) {
				case 701169:
					useSkill(getNpcs(701178));
					useSkill(getNpcs(701192));
					break;
				case 701170:
					useSkill(getNpcs(701177));
					useSkill(getNpcs(701191));
					break;
				case 701171:
					useSkill(getNpcs(701176));
					useSkill(getNpcs(701190));
					break;
				case 701172:
					useSkill(getNpcs(701175));
					useSkill(getNpcs(701189));
					break;
			}
			AI2Actions.scheduleRespawn(this);
			AI2Actions.deleteOwner(this);
		}
	}

	private void useSkill(List<Npc> npcs) {
		PvPArenaReward instance = (PvPArenaReward) getPosition().getWorldMapInstance().getInstanceHandler().getInstanceReward();
		for (Npc npc : npcs) {
			int skill = instance.getNpcBonusSkill(npc.getNpcId());
			SkillEngine.getInstance().getSkill(npc, skill >> 8, skill & 0xFF, npc).useNoAnimationSkill();
		}
	}

	private List<Npc> getNpcs(int npcId) {
		return getPosition().getWorldMapInstance().getNpcs(npcId);
	}
}
