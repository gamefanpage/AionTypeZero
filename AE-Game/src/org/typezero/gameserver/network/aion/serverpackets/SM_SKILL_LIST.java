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

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.skill.PlayerSkillEntry;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * In this packet Server is sending Skill Info?
 *
 * @author modified by ATracer,MrPoke
 */
public class SM_SKILL_LIST extends AionServerPacket {

	private PlayerSkillEntry[] skillList;
	private int messageId;
	private int skillNameId;
	private String skillLvl;
	public static final int YOU_LEARNED_SKILL = 1300050;
	boolean isNew = false;

	/**
	 * This constructor is used on player entering the world Constructs new <tt>SM_SKILL_LIST </tt> packet
	 */
	public SM_SKILL_LIST(Player player, PlayerSkillEntry[] basicSkills) {
		this.skillList = player.getSkillList().getBasicSkills();
		this.messageId = 0;
	}

	public SM_SKILL_LIST(Player player, PlayerSkillEntry stigmaSkill) {
		this.skillList = new PlayerSkillEntry[] { stigmaSkill };
		this.messageId = 0;
	}

	public SM_SKILL_LIST(PlayerSkillEntry skillListEntry, int messageId, boolean isNew) {
		this.skillList = new PlayerSkillEntry[] { skillListEntry };
		this.messageId = messageId;
		this.skillNameId = DataManager.SKILL_DATA.getSkillTemplate(skillListEntry.getSkillId()).getNameId();
		this.skillLvl = String.valueOf(skillListEntry.getSkillLevel());
		this.isNew = isNew;
	}

	@Override
    protected void writeImpl(AionConnection con) {

        final int size = skillList.length;
        writeH(size); // skills list size
        if (isNew)
			writeC(0);
		else
			writeC(1);

        if (size > 0) {
            for (PlayerSkillEntry entry : skillList) {
                writeH(entry.getSkillId());// id
                writeH(entry.getSkillLevel());// lvl
                writeC(0x00);
                int extraLevel = entry.getExtraLvl();
                writeC(extraLevel);
                if (isNew && extraLevel == 0 && !entry.isStigma()) {
                    writeD((int) (System.currentTimeMillis() / 1000)); // Learned date NCSoft......
                } else {
                    writeD(0);
                }
                writeC(entry.isStigma() ? 1 : 0); // stigma
            }
        }
        writeD(messageId);
        if (messageId != 0) {
            writeH(0x24); // unk
            writeD(skillNameId);
            writeH(0x00);
            writeS(skillLvl);
        }
    }
}
