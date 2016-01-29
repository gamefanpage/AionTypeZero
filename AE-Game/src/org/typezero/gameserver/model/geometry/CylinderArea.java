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

package org.typezero.gameserver.model.geometry;

import org.typezero.gameserver.model.templates.zone.Point2D;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * This class implements cylinder area
 *
 * @author SoulKeeper
 */
public class CylinderArea extends AbstractArea {

	/**
	 * Center of cylinder
	 */
	private final float centerX;

	/**
	 * Center of cylinder
	 */
	private final float centerY;

	/**
	 * Cylinder radius
	 */
	private final float radius;

	/**
	 * Creates new cylinder with given radius
	 *
	 * @param center
	 *          center of the circle
	 * @param radius
	 *          radius of the circle
	 * @param minZ
	 *          min z
	 * @param maxZ
	 *          max z
	 */
	public CylinderArea(ZoneName zoneName, int worldId, Point2D center, float radius, float minZ, float maxZ) {
		this(zoneName, worldId, center.getX(), center.getY(), radius, minZ, maxZ);
	}

	/**
	 * Creates new cylider with given radius
	 *
	 * @param x
	 *          center coord
	 * @param y
	 *          center coord
	 * @param radius
	 *          radius of the circle
	 * @param minZ
	 *          min z
	 * @param maxZ
	 *          max z
	 */
	public CylinderArea(ZoneName zoneName, int worldId, float x, float y, float radius, float minZ, float maxZ) {
		super(zoneName, worldId, minZ, maxZ);
		this.centerX = x;
		this.centerY = y;
		this.radius = radius;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInside2D(float x, float y) {
		return MathUtil.getDistance(centerX, centerY, x, y) < radius;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getDistance2D(float x, float y) {
		if (isInside2D(x, y)) {
			return 0;
		}
		else {
			return Math.abs(MathUtil.getDistance(centerX, centerY, x, y) - radius);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getDistance3D(float x, float y, float z) {
		if (isInside3D(x, y, z)) {
			return 0;
		}
		else if (isInsideZ(z)) {
			return getDistance2D(x, y);
		}
		else {
			if (z < getMinZ()) {
				return MathUtil.getDistance(centerX, centerY, getMinZ(), x, y, z);
			}
			else {
				return MathUtil.getDistance(centerX, centerY, getMaxZ(), x, y, z);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point2D getClosestPoint(float x, float y) {
		if (isInside2D(x, y)) {
			return new Point2D(x, y);
		}
		else {
			float vX = x - this.centerX;
			float vY = y - this.centerY;
			double magV = MathUtil.getDistance(centerX, centerY, x, y);
			double pointX = centerX + vX / magV * radius;
			double pointY = centerY + vY / magV * radius;
			return new Point2D((float)pointX, (float)pointY);
		}
	}

	@Override
	public boolean intersectsRectangle(RectangleArea area) {
		if (area.getMinZ() > getMaxZ() || area.getMaxZ() < getMinZ())
			return false;
		if (area.getDistance2D(centerX, centerY) < radius)
			return true;
		return false;
	}
}
