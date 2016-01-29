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
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;


/**
 * @author Cheatkiller
 *
 */
@AIName("muragan")
public class MuraganAI2 extends GeneralNpcAI2 {

	private boolean isMove;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		if(getOwner().getNpcId() == 800438) {
			NpcShoutsService.getInstance().sendMsg(getOwner(), 390852, getOwner().getObjectId(), 0, 1000);
		}
	}

  @Override
  protected void handleCreatureSee(Creature creature) {
      checkDistance(this, creature);
  }

  @Override
  protected void handleCreatureMoved(Creature creature) {
      checkDistance(this, creature);
  }

  private void checkDistance(NpcAI2 ai, Creature creature) {
  	if (creature instanceof Player) {
  		if (MathUtil.isIn3dRange(getOwner(), creature, 15) && !isMove) {
  			isMove = true;
  			openSuramaDoor();
  			startWalk((Player) creature);
  		}
  	}
  }

  private void startWalk(final Player player) {
  	int owner = getOwner().getNpcId();
  	if (owner == 800436 || owner == 800438)
  		return;
  	switch(owner) {
  		case 800435:
  			NpcShoutsService.getInstance().sendMsg(getOwner(), 390837, getOwner().getObjectId(), 0, 0);
  			NpcShoutsService.getInstance().sendMsg(getOwner(), 390838, getOwner().getObjectId(), 0, 4000);
  			killGuardCaptain();
  			break;
  	}
  	setStateIfNot(AIState.WALKING);
  	getOwner().setState(1);
		getMoveController().moveToPoint(838, 1317, 396);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
		ThreadPoolManager.getInstance().schedule(new Runnable() {

		  @Override
		  public void run() {
		  	forQuest(player);
		  	AI2Actions.deleteOwner(MuraganAI2.this);
		  }
	  }, 10000);
	}

  private void openSuramaDoor() {
  	if (getOwner().getNpcId() == 800436) {
  		NpcShoutsService.getInstance().sendMsg(getOwner(), 390835, getOwner().getObjectId(), 0, 0);
			getPosition().getWorldMapInstance().getDoors().get(56).setOpen(true);
			AI2Actions.deleteOwner(this);
  	}
  }

  private void killGuardCaptain() {
  	WorldMapInstance instance = getOwner().getPosition().getWorldMapInstance();
  	for (Npc npc : instance.getNpcs()) {
  		if (npc.getNpcId() == 219392) {
  			spawn(283145, npc.getX(), npc.getY(), npc.getZ(), (byte) npc.getHeading());
  			npc.getController().onDelete();
  		}
  	}
  }

  private void forQuest(Player player) {
  	int quest = player.getRace().equals(Race.ELYOS) ? 30708 : 30758;
  	final QuestState qs = player.getQuestStateList().getQuestState(quest);
  	if (qs != null && qs.getQuestVarById(0) != 5) {
  		qs.setQuestVar(qs.getQuestVarById(0) + 1);
  		PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(quest, qs.getStatus(), qs.getQuestVars().getQuestVars()));
  	}
  }
}


