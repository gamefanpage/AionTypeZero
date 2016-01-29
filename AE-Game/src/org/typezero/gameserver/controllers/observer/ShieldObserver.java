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

package org.typezero.gameserver.controllers.observer;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.shield.Shield;
import org.typezero.gameserver.model.utils3d.Point3D;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Wakizashi, Source
 */
public class ShieldObserver extends ActionObserver {

	private Creature creature;
	private Shield shield;
	private Point3D oldPosition;

	public ShieldObserver() {
		super(ObserverType.MOVE);
		this.creature = null;
		this.shield = null;
		this.oldPosition = null;
	}

	public ShieldObserver(Shield shield, Creature creature) {
		super(ObserverType.MOVE);
		this.creature = creature;
		this.shield = shield;
		this.oldPosition = new Point3D(creature.getX(), creature.getY(), creature.getZ());
	}

	@Override
	public void moved() {
		boolean passedThrough = false;
		boolean isGM = false;

		if (SiegeService.getInstance().getFortress(shield.getId()).isUnderShield())
			if (!(creature.getZ() < shield.getZ() && oldPosition.getZ() < shield.getZ()))
				if (MathUtil.isInSphere(shield, (float) oldPosition.getX(), (float) oldPosition.getY(),
						(float) oldPosition.getZ(), shield.getTemplate().getRadius()) != MathUtil.isIn3dRange(shield, creature,
						shield.getTemplate().getRadius()))
					passedThrough = true;

		if (passedThrough) {
			if (creature instanceof Player) {
				PacketSendUtility.sendMessage(((Player) creature), "You passed through shield.");
				isGM = ((Player) creature).isGM();
			}

			if (!isGM) {
				if (!(creature.getLifeStats().isAlreadyDead()))
					creature.getController().die();
				if (creature instanceof Player)
					((Player) creature).getFlyController().endFly(true);
				creature.getObserveController().removeObserver(this);
			}
		}

		oldPosition.x = creature.getX();
		oldPosition.y = creature.getY();
		oldPosition.z = creature.getZ();
	}

}
