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

import org.typezero.gameserver.geoEngine.collision.CollisionIntention;
import org.typezero.gameserver.geoEngine.math.Vector3f;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.SkillMoveType;
import org.typezero.gameserver.skillengine.model.SpellStatus;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.geo.GeoService;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author ATracer
 * @modified Alex - fix effect SNARE if this effect start
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StumbleEffect")
public class StumbleEffect extends EffectTemplate {

    @Override
    public void applyEffect(Effect effect) {
        if (!effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.OPENAERIAL) && !effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.CANNOT_MOVE) && !effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.SNARE)) {
            effect.addToEffectedController();
            final Creature effected = effect.getEffected();
            effected.getEffectController().removeParalyzeEffects();
            //effected.getMoveController().abortMove();
            World.getInstance().updatePosition(effected, effect.getTargetX(), effect.getTargetY(), effect.getTargetZ(),
                    effected.getHeading());
            PacketSendUtility.broadcastPacketAndReceive(effect.getEffected(),
                    new SM_FORCED_MOVE(effect.getEffector(), effect.getEffected().getObjectId(), effect.getTargetX(), effect.getTargetY(), effect.getTargetZ()));
        }
    }

    @Override
    public void startEffect(Effect effect) {
        effect.getEffected().getEffectController().setAbnormal(AbnormalState.STUMBLE.getId());
        effect.setAbnormal(AbnormalState.STUMBLE.getId());
    }

    @Override
    public void calculate(Effect effect) {
        if (!super.calculate(effect, StatEnum.STUMBLE_RESISTANCE, SpellStatus.STUMBLE)) {
            return;
        }
        effect.setSkillMoveType(SkillMoveType.STUMBLE);
        final Creature effector = effect.getEffector();
        final Creature effected = effect.getEffected();
        effected.getController().cancelCurrentSkill();
        float direction = effected instanceof Player ? 1.5f : 0.5f;
        double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
        float x1 = (float) (Math.cos(radian) * direction);
        float y1 = (float) (Math.sin(radian) * direction);
        float z = effected.getZ();
        byte intentions = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId());
        Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effected, effected.getX() + x1,
                effected.getY() + y1, effected.getZ() - 0.4f, false, intentions);
        x1 = closestCollision.x;
        y1 = closestCollision.y;
        z = closestCollision.z;
        effect.setTargetLoc(x1, y1, z);
    }

    @Override
    public void endEffect(Effect effect) {
        effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.STUMBLE.getId());
    }
}
