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

package org.typezero.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;

/**
 * @author ATracer, KID
 */
public class CM_SUMMON_CASTSPELL extends AionClientPacket {
	private static final Logger log = LoggerFactory.getLogger(CM_SUMMON_CASTSPELL.class);
	private int summonObjId;
	private int targetObjId;
	private int skillId;
	@SuppressWarnings("unused")
	private int skillLvl;
	@SuppressWarnings("unused")
	private float unk;

	public CM_SUMMON_CASTSPELL(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		summonObjId = readD();
		skillId = readH();
		skillLvl = readC();
		targetObjId = readD();
		unk = readF();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();

		long currentTime = System.currentTimeMillis();
		if (player.getNextSummonSkillUse() > currentTime) {
			return;
		}

		Summon summon = player.getSummon();
		if (summon == null) {
			log.warn("summon castspell without active summon on "+player.getName()+".");
			return;
		}
		if(summon.getObjectId() != summonObjId) {
			log.warn("summon castspell from a different summon instance on "+player.getName()+".");
			return;
		}

		Creature target = null;
		if(targetObjId != summon.getObjectId()) {
		  VisibleObject obj = summon.getKnownList().getObject(targetObjId);
		  if(obj instanceof Creature) {
		  	target = (Creature)obj;
		  }
		}
		else {
			target = summon;
		}

		if(target != null) {
			player.setNextSummonSkillUse(currentTime + 1100);
			summon.getController().useSkill(skillId, target);
		}
		else
			log.warn("summon castspell on a wrong target on "+player.getName());
	}
}
