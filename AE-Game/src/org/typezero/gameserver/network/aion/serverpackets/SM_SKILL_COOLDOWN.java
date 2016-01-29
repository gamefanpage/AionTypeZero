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

package org.typezero.gameserver.network.aion.serverpackets;

import java.util.ArrayList;
import java.util.Map;

import javolution.util.FastMap;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

import org.typezero.gameserver.skillengine.model.SkillTemplate;

/**
 * @author ATracer, nrg
 */
public class SM_SKILL_COOLDOWN extends AionServerPacket {

	private FastMap<Integer, Long> cooldowns;

	public SM_SKILL_COOLDOWN(FastMap<Integer, Long> cooldowns) {
		this.cooldowns = cooldowns;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {

        writeH(calculateSize());
		writeC(1); // unk 4.0
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<Integer, Long> entry : cooldowns.entrySet()) {
            int left = (int) ((entry.getValue() - currentTime) / 1000);
            ArrayList<SkillTemplate> skillsWithCooldown = DataManager.SKILL_DATA.getSkillsForCooldownId(entry.getKey());

            for (SkillTemplate skill : skillsWithCooldown) {

                writeH(skill.getSkillId());
                writeD(left > 0 ? left : 0);
				writeD(skill.getCooldown());
            }
        }
	}

    private int calculateSize() {
        int size = 0;
        for(Map.Entry<Integer, Long> entry : cooldowns.entrySet()) {
            size += DataManager.SKILL_DATA.getSkillsForCooldownId(entry.getKey()).size();
        }
        return size;
    }
}
