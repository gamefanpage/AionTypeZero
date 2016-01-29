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

package org.typezero.gameserver.skillengine.effect;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.network.aion.serverpackets.SM_STATUPDATE_EXP;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author kecimis, source.com
 */
public class ProcVPHealInstantEffect extends EffectTemplate {

	@XmlAttribute(required = true)
	protected int value2;//cap
	@XmlAttribute
	protected boolean percent;

	@Override
	public void applyEffect(Effect effect) {
		if (effect.getEffected() instanceof Player) {
			Player player = (Player) effect.getEffected();
			PlayerCommonData pcd = player.getCommonData();

			long cap = pcd.getMaxReposteEnergy() * value2 / 100;

			if (pcd.getCurrentReposteEnergy() < cap ) {
				int valueWithDelta = value + delta * effect.getSkillLevel();
				long addEnergy = 0;
				if (percent)
					addEnergy = (int)(pcd.getMaxReposteEnergy() * valueWithDelta * 0.001);//recheck when more skills
				else
					addEnergy = valueWithDelta;

				pcd.addReposteEnergy(addEnergy);
				PacketSendUtility.sendPacket(player, new SM_STATUPDATE_EXP(
						pcd.getExpShown(),
						pcd.getExpRecoverable(),
						pcd.getExpNeed(),
						pcd.getCurrentReposteEnergy(),
						pcd.getMaxReposteEnergy()));
			}
		}
	}

}
