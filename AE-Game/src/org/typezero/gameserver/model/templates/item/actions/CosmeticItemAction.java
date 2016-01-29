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

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.PlayerAppearanceDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerAppearance;
import org.typezero.gameserver.model.templates.cosmeticitems.CosmeticItemTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 *
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CosmeticItemAction")
public class CosmeticItemAction extends AbstractItemAction {

	@XmlAttribute(name = "name")
	protected String cosmeticName;

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		CosmeticItemTemplate template = DataManager.COSMETIC_ITEMS_DATA.getCosmeticItemsTemplate(cosmeticName);
		if (template == null) {
			return false;
		}
		if (!template.getRace().equals(player.getRace())) {
			return false;
		}
		if (!template.getGenderPermitted().equals("ALL")) {
			if (!player.getGender().toString().equals(template.getGenderPermitted())) {
				return false;
			}
		}
		if(player.getMoveController().isInMove())
			return false;
		return true;
	}

	@Override
	public void act(final Player player, Item parentItem, Item targetItem) {
		CosmeticItemTemplate template = DataManager.COSMETIC_ITEMS_DATA.getCosmeticItemsTemplate(cosmeticName);
		PlayerAppearance playerAppearance = player.getPlayerAppearance();
		String type = template.getType();
		int id = template.getId();
		if (type.equals("hair_color")) {
			playerAppearance.setHairRGB(id);
		}
		else if (type.equals("face_color")) {
			playerAppearance.setSkinRGB(id);
		}
		else if (type.equals("lip_color")) {
			playerAppearance.setLipRGB(id);
		}
		else if (type.equals("eye_color")) {
			playerAppearance.setEyeRGB(id);
		}
		else if (type.equals("hair_type")) {
			playerAppearance.setHair(id);
		}
		else if (type.equals("face_type")) {
			playerAppearance.setFace(id);
		}
		else if (type.equals("voice_type")) {
			playerAppearance.setVoice(id);
		}
		else if (type.equals("makeup_type")) {
			playerAppearance.setTattoo(id);
		}
		else if (type.equals("tattoo_type")) {
			playerAppearance.setDeco(id);
		}
		else if (type.equals("preset_name")) {
			CosmeticItemTemplate.Preset preset = template.getPreset();
			playerAppearance.setEyeRGB((preset.getEyeColor()));
			playerAppearance.setLipRGB((preset.getLipColor()));
			playerAppearance.setHairRGB((preset.getHairColor()));
			playerAppearance.setSkinRGB((preset.getEyeColor()));
			playerAppearance.setHair((preset.getHairType()));
			playerAppearance.setFace((preset.getFaceType()));
			playerAppearance.setHeight((preset.getScale()));
		}
		DAOManager.getDAO(PlayerAppearanceDAO.class).store(player);
		player.getInventory().delete(targetItem);
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		player.clearKnownlist();
		player.updateKnownlist();
	}
}
