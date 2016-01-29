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

package ai.instance.tiamatStrongHold;

import ai.GeneralNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.CreatureType;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_CUSTOM_SETTINGS;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;


/**
 * @author Cheatkiller
 *
 */
@AIName("suramathetraitor")
public class SuramaTheTraitorAI2 extends GeneralNpcAI2 {

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		moveToRaksha();
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		NpcShoutsService.getInstance().sendMsg(getOwner(), 390845, getOwner().getObjectId(), 0, 2000);
	}

	private void moveToRaksha() {
		setStateIfNot(AIState.WALKING);
  	getOwner().setState(1);
		getMoveController().moveToPoint(651, 1319, 487);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
		ThreadPoolManager.getInstance().schedule(new Runnable() {

		  @Override
		  public void run() {
		  	startDialog();
		  }
	  }, 10000);
	}


	private void startDialog() {
		final Npc raksha = getPosition().getWorldMapInstance().getNpc(219356);
		NpcShoutsService.getInstance().sendMsg(getOwner(), 390841, getOwner().getObjectId(), 0, 0);
		NpcShoutsService.getInstance().sendMsg(getOwner(), 390842, getOwner().getObjectId(), 0, 3000);
		NpcShoutsService.getInstance().sendMsg(raksha, 390843, raksha.getObjectId(), 0, 6000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

		  @Override
		  public void run() {
		  	raksha.setTarget(getOwner());
		  	SkillEngine.getInstance().getSkill(raksha, 20952, 60, getOwner()).useNoAnimationSkill();
		  	changeNpcType(raksha, CreatureType.ATTACKABLE.getId());
		  }
	  }, 8000);
	}

	private void changeNpcType(Npc npc, final int newType) {
	  npc.setNpcType(newType);
	  for (final Player player : npc.getKnownList().getKnownPlayers().values()) {
	  	PacketSendUtility.sendPacket(player, new SM_CUSTOM_SETTINGS(npc.getObjectId(), 0, newType, 0));
	  }
  }
}
