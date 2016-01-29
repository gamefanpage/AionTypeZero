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

package org.typezero.gameserver.dataholders;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.skill.PlayerSkillEntry;
import org.typezero.gameserver.model.templates.HiddenStigmasTemplate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.List;

/**
 * @author Cain
 */
@XmlRootElement(name = "hidden_stigma_tree")
@XmlAccessorType(XmlAccessType.FIELD)
public class HiddenStigmaTreeData {

	@XmlElement(name = "playerclass", type = HiddenStigmasTemplate.class)
	private List<HiddenStigmasTemplate> hiddenStigmasByClass;

	public int size() {
		return hiddenStigmasByClass.size();
	}

	public List<HiddenStigmasTemplate> getHiddenStigmasByClass() {
		return hiddenStigmasByClass;
	}

	public int getHiddenStigmaSkill(Player player) {
		for (HiddenStigmasTemplate hst : hiddenStigmasByClass) {
			if (hst.getClassname().equals(player.getPlayerClass().name())) {
                int maxAvailHiddenStigmaLvl = player.getSkillList().getMaxAvailHiddenStigmaLvl();
				int regularHiddenStigmaSkillId = hst.getRegularStigmaSkillId(player.getRace(), maxAvailHiddenStigmaLvl);
                for (HiddenStigmasTemplate.HiddenStigmaTemplate oneStigma : hst.getHiddenStigmas()) {
                    boolean haveRequiredSkill = false;
                    int customVariants = 0;
                    for (PlayerSkillEntry pse : player.getSkillList().getStigmaSkills()) {
                        if (oneStigma.getRequiredId() == null)
                            continue;

                        if (oneStigma.getRequiredId().equals(pse.getSkillTemplate().getStack()))
                            haveRequiredSkill = true;

                        for (String customStack : oneStigma.getCustomStacks()) {
                            if (customStack.equals(pse.getSkillTemplate().getStack()))
                                customVariants++;
                        }
                        if (haveRequiredSkill && customVariants > 1) {
                            return DataManager.SKILL_TREE_DATA.getStigmaTree().get(player.getRace()).get(oneStigma.getId()).get(maxAvailHiddenStigmaLvl);
                        }
                    }

				}
                return regularHiddenStigmaSkillId;
			}
		}
		return 0;
	}

}
