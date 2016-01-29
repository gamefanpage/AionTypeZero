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

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.geoEngine.collision.CollisionIntention;
import org.typezero.gameserver.geoEngine.math.Vector3f;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_TARGET_UPDATE;
import org.typezero.gameserver.skillengine.model.DashStatus;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.Skill;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.geo.GeoService;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Bio
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RandomMoveLocEffect")
public class RandomMoveLocEffect extends EffectTemplate {

	@XmlAttribute(name = "distance")
	private float distance;
	@XmlAttribute(name = "direction")
	private float direction;

	@Override
	public void applyEffect(Effect effect) {
		final Player effector = (Player) effect.getEffector();

		// Deselect targets
        PacketSendUtility.sendPacket(effector, new SM_TARGET_UPDATE(effector));
        Skill skill = effect.getSkill();
        effector.getEffectController().setAbnormal(AbnormalState.CANNOT_MOVE.getId());
        effector.getEffectController().updatePlayerEffectIcons();
        World.getInstance().updatePosition(effector, skill.getX(), skill.getY(), skill.getZ(), skill.getH());
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                effector.getEffectController().unsetAbnormal(AbnormalState.CANNOT_MOVE.getId());
                effector.getEffectController().updatePlayerEffectIcons();
            }
        }, 50);
    }

	@Override
	public void calculate(Effect effect) {
		effect.addSucessEffect(this);
		if (((Player)effect.getEffector()).getRobotId() != 0 && effect.getSkillId() != 2424 && effect.getSkillId() != 2425) {
		effect.setDashStatus(DashStatus.ROBOTMOVELOC);
		} else {
		effect.setDashStatus(DashStatus.RANDOMMOVELOC);
		}

        final Player effector = (Player) effect.getEffector();
        Vector3f closestCollision = closestCollision(effector, direction);
        float direct = direction == 0 ? 1 : 0;
        int m = (int) (closestCollision.getZ() - effector.getZ());
        //fix 1 bag
        if (m >= 3) {
            closestCollision = closestCollision(effector, direct);
        }
        effect.getSkill().setTargetPosition(closestCollision.getX(), closestCollision.getY(), closestCollision.getZ(), effector.getHeading());
    }

    Vector3f closestCollision(Player effector, float direct) {
        // Move Effector backwards direction=1 or frontwards direction=0
        double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
        float x1 = (float) (Math.cos(Math.PI * direct + radian) * distance);
        float y1 = (float) (Math.sin(Math.PI * direct + radian) * distance);
        float targetZ = GeoService.getInstance().getZ(effector.getWorldId(), effector.getX() + x1, effector.getY() + y1, effector.getZ() + 1.5f, 0.2f, effector.getInstanceId());
        byte intentions = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId());
        return GeoService.getInstance().getClosestCollision(effector, effector.getX() + x1,
                effector.getY() + y1, targetZ, false, intentions);
    }
}
