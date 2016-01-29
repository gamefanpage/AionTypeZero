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

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_TRANSFORM;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.TransformType;
import org.typezero.gameserver.utils.PacketSendUtility;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Sweetkr, kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransformEffect")
public abstract class TransformEffect extends EffectTemplate {

	@XmlAttribute
	protected int model;

	@XmlAttribute
	protected TransformType type = TransformType.NONE;

	@XmlAttribute
	protected int panelid;

	@XmlAttribute
	protected AbnormalState state = AbnormalState.BUFF;

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
		if (state != null) {
			effect.getEffected().getEffectController().setAbnormal(state.getId());
			effect.setAbnormal(state.getId());
		}
	}

	public void endEffect(Effect effect) {
		final Creature effected = effect.getEffected();

		if (state != null)
			effected.getEffectController().unsetAbnormal(state.getId());

		if (effected instanceof Player) {
			int newModel = 0;
			TransformType transformType = TransformType.PC;
			for (Effect tmp : effected.getEffectController().getAbnormalEffects())	{
				for (EffectTemplate template : tmp.getEffectTemplates()) {
					if (template instanceof TransformEffect) {
						if (((TransformEffect)template).getTransformId() == model)
							continue;
						newModel = ((TransformEffect)template).getTransformId();
						transformType = ((TransformEffect)template).getTransformType();
						break;
					}
				}
			}
			effected.getTransformModel().setModelId(newModel);
			effected.getTransformModel().setTransformType(transformType);
		}
		else if (effected instanceof Summon) {
			effected.getTransformModel().setModelId(0);
		}
		else if (effected instanceof Npc) {
			effected.getTransformModel().setModelId(effected.getObjectTemplate().getTemplateId());
		}
		effected.getTransformModel().setPanelId(0);
		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TRANSFORM(effected, 0, false));

		if (effected instanceof Player)
			((Player) effected).setTransformed(false);
	}

	public void startEffect(Effect effect) {
		final Creature effected = effect.getEffected();
		// заглушка от превращения дверей и ворот в другие объекты
		if (effected.isPlayer() == 0 && (effected.getName().contains("wall") || effected.getName().contains("door")))
			return;
		effected.getTransformModel().setModelId(model);
		effected.getTransformModel().setPanelId(panelid);
		effected.getTransformModel().setTransformType(effect.getTransformType());
		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TRANSFORM(effected, panelid, true));

		if (effected instanceof Player) {
			((Player) effected).setTransformed(true);
		}
	}

	public TransformType getTransformType() {
		return type;
	}

	public int getTransformId()	{
		return model;
	}

	public int getPanelId()	{
		return panelid;
	}
}
