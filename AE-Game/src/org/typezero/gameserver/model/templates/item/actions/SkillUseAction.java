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

package org.typezero.gameserver.model.templates.item.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.MuiService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.skillengine.effect.EffectTemplate;
import org.typezero.gameserver.skillengine.effect.SummonEffect;
import org.typezero.gameserver.skillengine.effect.TransformEffect;
import org.typezero.gameserver.skillengine.model.Skill;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillUseAction")
public class SkillUseAction extends AbstractItemAction {

	@XmlAttribute
	protected int skillid;

	@XmlAttribute
	protected int level;

	@XmlAttribute(required = false)
	private Integer mapid;

	/**
	 * Gets the value of the skillid property.
	 */
	public int getSkillid() {
		return skillid;
	}

	/**
	 * Gets the value of the level property.
	 */
	public int getLevel() {
		return level;
	}

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		Skill skill = SkillEngine.getInstance().getSkill(player, skillid, level, player.getTarget(), parentItem.getItemTemplate());
		if (skill == null)
			return false;
		// Не используем свитки телепорта в положении сидя
		if (skill.getSkillTemplate().getStack().equals("ITEM_SKILL_RETURNPOINT") && player.isInState(CreatureState.RESTING)) {
			PacketSendUtility.sendBrightYellowMessageOnCenter(player, MuiService.getInstance().getMessage("NOFLY_SID"));
			return false;
		}
		int nameId = parentItem.getItemTemplate().getNameId();
		byte levelRestrict = parentItem.getItemTemplate().getMaxLevelRestrict(player);
		if (levelRestrict != 0) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_TOO_LOW_LEVEL_MUST_BE_THIS_LEVEL(levelRestrict, nameId));
			return false;
		}
		// Cant use transform items while already transformed
		if (player.isTransformed()) {
			for (EffectTemplate template : skill.getSkillTemplate().getEffects().getEffects()) {
				if (template instanceof TransformEffect) {
					PacketSendUtility.sendPacket(player,
						SM_SYSTEM_MESSAGE.STR_CANT_USE_ITEM(new DescriptionId(nameId)));
					return false;
				}
			}
		}
		if (player.getSummon() != null) {
			for (EffectTemplate template : skill.getSkillTemplate().getEffects().getEffects()) {
				if (template instanceof SummonEffect) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300072));
					return false;
				}
			}
		}
		return skill.canUseSkill();
	}

	@Override
	public void act(Player player, Item parentItem, Item targetItem) {
		Skill skill = SkillEngine.getInstance().getSkill(player, skillid, level, player.getTarget(), parentItem.getItemTemplate());
		if (skill != null) {
			player.getController().cancelUseItem();
			skill.setItemObjectId(parentItem.getObjectId());
			skill.useSkill();
		}
	}

	public int getMapid() {
		if (mapid == null)
			return 0;
		return mapid;
	}

}
