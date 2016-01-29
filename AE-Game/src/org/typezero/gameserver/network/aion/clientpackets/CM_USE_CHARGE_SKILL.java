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

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_CASTSPELL_RESULT;
import org.typezero.gameserver.network.aion.serverpackets.SM_SKILL_CANCEL;
import org.typezero.gameserver.skillengine.model.ChargedSkill;
import org.typezero.gameserver.skillengine.model.Skill;
import org.typezero.gameserver.skillengine.properties.Properties;
import org.typezero.gameserver.utils.PacketSendUtility;
import javassist.expr.Cast;

/**
 * @author Cheatkiller
 */
public class CM_USE_CHARGE_SKILL extends AionClientPacket {

	public CM_USE_CHARGE_SKILL(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}


	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		Skill chargeCastingSkill = player.getCastingSkill();
		if (chargeCastingSkill == null) {
            player.setCasting(null);
            PacketSendUtility.broadcastPacketAndReceive(player, new SM_SKILL_CANCEL(player, 0));
            return;
		}
        if (player.getTarget() == null){
            player.setCasting(null);
            PacketSendUtility.broadcastPacketAndReceive(player, new SM_SKILL_CANCEL(player, 0));
            return;
        }

        if (player.getLifeStats().isAlreadyDead()){
            player.setCasting(null);
            PacketSendUtility.broadcastPacketAndReceive(player, new SM_SKILL_CANCEL(player, 0));
            return;
        }

        if (player.getTarget() instanceof Npc){
            if (((Npc) player.getTarget()).getLifeStats().isAlreadyDead()){
                player.setCasting(null);
                PacketSendUtility.broadcastPacketAndReceive(player, new SM_SKILL_CANCEL(player, 0));
                return;
            }
        }
        if (player.getTarget() instanceof Player){
            if (((Player) player.getTarget()).getLifeStats().isAlreadyDead()){
                player.setCasting(null);
                PacketSendUtility.broadcastPacketAndReceive(player, new SM_SKILL_CANCEL(player, 0));
                return;
            }
        }

        if (!player.isCasting()) {
            player.setCasting(null);
            PacketSendUtility.broadcastPacketAndReceive(player, new SM_SKILL_CANCEL(player, 0));
            return;
        }

            long time = System.currentTimeMillis() - chargeCastingSkill.getCastStartTime();
		    int i = 0;
		for (ChargedSkill skill : chargeCastingSkill.getChargeSkillList()) {
			if (time > skill.getTime()) {
				i++;
				time -= skill.getTime();
				continue;
			}
		}

          if (chargeCastingSkill.getChargeSkillList().size() == 0)
          {
              player.setCasting(null);
              PacketSendUtility.broadcastPacketAndReceive(player, new SM_SKILL_CANCEL(player, 0));
              return;
          }

		player.getController().useChargeSkill(chargeCastingSkill.getChargeSkillList().get(i).getId(), 1);
		chargeCastingSkill.cancelCast();
		chargeCastingSkill.getChargeSkillList().clear();
	}


	@Override
	protected void readImpl() {

	}
}
