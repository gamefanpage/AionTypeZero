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
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.SkillMoveType;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Sarynth modified by Wakizashi, Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PulledEffect")
public class PulledEffect extends EffectTemplate {

    @Override
    public void applyEffect(Effect effect) {
        if (effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.SNARE)){
            return;
        }
        effect.addToEffectedController();
        final Creature effected = effect.getEffected();
        effected.getController().cancelCurrentSkill();
        //effected.getMoveController().abortMove();
        World.getInstance().updatePosition(effected, effect.getTargetX(), effect.getTargetY(), effect.getTargetZ(), effected.getHeading());
        PacketSendUtility.broadcastPacketAndReceive(effected, new SM_FORCED_MOVE(effect.getEffector(), effected.getObjectId(), effect.getTargetX(), effect.getTargetY(), effect.getTargetZ()));
    }

    @Override
    public void calculate(Effect effect) {
        if (!super.calculate(effect, StatEnum.PULLED_RESISTANCE, null)) {
            return;
        }

        effect.setSkillMoveType(SkillMoveType.PULL);
        final Creature effector = effect.getEffector();

        // Target must be pulled just one meter away from effector, not IN place of effector
        double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
        final float x1 = (float) Math.cos(radian);
        final float y1 = (float) Math.sin(radian);
        effect.setTargetLoc(effector.getX() + x1, effector.getY() + y1, effector.getZ() + 0.25F);
    }

    @Override
    public void startEffect(Effect effect) {
        final Creature effected = effect.getEffected();
        effected.getEffectController().setAbnormal(AbnormalState.CANNOT_MOVE.getId());
        effect.setAbnormal(AbnormalState.CANNOT_MOVE.getId());
    }

    @Override
    public void endEffect(Effect effect) {
        effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.CANNOT_MOVE.getId());
    }
}
