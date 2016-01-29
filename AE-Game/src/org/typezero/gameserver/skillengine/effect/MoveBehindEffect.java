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
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.npc.AbyssNpcType;
import org.typezero.gameserver.network.aion.serverpackets.SM_TARGET_UPDATE;
import org.typezero.gameserver.skillengine.action.DamageType;
import org.typezero.gameserver.skillengine.model.DashStatus;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.geo.GeoService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Sarynth, modified Bobobear
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MoveBehindEffect")
public class MoveBehindEffect extends DamageEffect {

	@Override
	public void applyEffect(Effect effect) {
		super.applyEffect(effect);
	}

	@Override
	public void calculate(Effect effect) {
        if (effect.getEffected() == null)
			return;
		if (!(effect.getEffector() instanceof Player))
			return;

		final Player effector = (Player) effect.getEffector();
		final Creature effected = effect.getEffected();
        double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effected.getHeading()));
        float x1 = (float) (Math.cos(Math.PI + radian) * 1.3F);
        float y1 = (float) (Math.sin(Math.PI + radian) * 1.3F);

        if(effector.getTarget() != null && effector.getTarget() instanceof Npc){
            if (((Npc) effector.getTarget()).getObjectTemplate().getAbyssNpcType() == AbyssNpcType.DOOR ) {
                radian = Math.toRadians(MathUtil.convertHeadingToDegree(effected.getHeading()))*-1;
                x1 = (float) (Math.cos(Math.PI + radian) * 4F);
                y1 = (float) (Math.sin(Math.PI + radian) * 4F);
            }
        }

		float z = GeoService.getInstance().getZAfterMoveBehind(effected.getWorldId(), effected.getX() + x1,
				effected.getY() + y1, effected.getZ(), effected.getInstanceId());
		byte intentions = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId());
		Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effector, effected.getX() + x1,
				effected.getY() + y1, z, false, intentions);

		//stop moving
		effected.getMoveController().abortMove();

		// Deselect targets
		PacketSendUtility.sendPacket(effector, new SM_TARGET_UPDATE(effector));

		// Move Effector to Effected
		effect.setDashStatus(DashStatus.MOVEBEHIND);
		World.getInstance().updatePosition(effector, closestCollision.getX(), closestCollision.getY(), closestCollision.getZ(), effected.getHeading());
		//set target position for SM_CASTSPELL_RESULT
		effect.getSkill().setTargetPosition(closestCollision.getX(), closestCollision.getY(), closestCollision.getZ(), effected.getHeading());

		if (!super.calculate(effect, DamageType.PHYSICAL))
			return;
	}

}
