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

import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.road.Road;
import org.typezero.gameserver.model.templates.road.RoadExit;
import org.typezero.gameserver.model.utils3d.Point3D;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.world.WorldType;

/**
 * @author SheppeR
 */
public class RoadObserver extends ActionObserver {

	private Player player;
	private Road road;
	private Point3D oldPosition;

	public RoadObserver() {
		super(ObserverType.MOVE);
		this.player = null;
		this.road = null;
		this.oldPosition = null;
	}

	public RoadObserver(Road road, Player player) {
		super(ObserverType.MOVE);
		this.player = player;
		this.road = road;
		this.oldPosition = new Point3D(player.getX(), player.getY(), player.getZ());
	}

	@Override
	public void moved() {
		Point3D newPosition = new Point3D(player.getX(), player.getY(), player.getZ());
		boolean passedThrough = false;

		if (road.getPlane().intersect(oldPosition, newPosition)) {
			Point3D intersectionPoint = road.getPlane().intersection(oldPosition, newPosition);
			if (intersectionPoint != null) {
				double distance = Math.abs(road.getPlane().getCenter().distance(intersectionPoint));

				if (distance < road.getTemplate().getRadius()) {
					passedThrough = true;
				}
			}
			else {
				if (MathUtil.isIn3dRange(road, player, road.getTemplate().getRadius()))
				{
					passedThrough = true;
				}
			}
		}

		if (passedThrough) {
			RoadExit exit = road.getTemplate().getRoadExit();

			WorldType type = road.getWorldType();
			if (type == WorldType.ELYSEA) {
				if (player.getRace() == Race.ELYOS) {
					TeleportService2.teleportTo(player, exit.getMap(), exit.getX(), exit.getY(), exit.getZ(), (byte) 0, TeleportAnimation.BEAM_ANIMATION);
				}
			}
			else if (type == WorldType.ASMODAE) {
				if (player.getRace() == Race.ASMODIANS) {
					TeleportService2.teleportTo(player, exit.getMap(), exit.getX(), exit.getY(), exit.getZ(), (byte) 0, TeleportAnimation.BEAM_ANIMATION);
				}
			}
			else {
				TeleportService2.teleportTo(player, exit.getMap(), exit.getX(), exit.getY(), exit.getZ(), (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			}
		}
		oldPosition = newPosition;
	}
}
