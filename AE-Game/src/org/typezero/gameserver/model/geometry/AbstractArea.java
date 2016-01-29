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
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * Class with basic method implementation for ares.<br>
 * If possible it should be subclassed. <br>
 * In other case {@link org.typezero.gameserver.model.geometry.Area} should be implemented directly
 */
public abstract class AbstractArea implements Area {

	/**
	 * Minimal z of area
	 */
	private final float minZ;

	/**
	 * Maximal Z of area
	 */
	private final float maxZ;

	private ZoneName zoneName;

	private int worldId;
	/**
	 * Creates new AbstractArea with min and max z
	 *
	 * @param minZ
	 *          min z
	 * @param maxZ
	 *          max z
	 */
	protected AbstractArea(ZoneName zoneName, int worldId, float minZ, float maxZ) {
		if (minZ > maxZ) {
			throw new IllegalArgumentException("minZ(" + minZ + ") > maxZ(" + maxZ + ")");
		}
		this.minZ = minZ;
		this.maxZ = maxZ;
		this.zoneName = zoneName;
		this.worldId = worldId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInside2D(Point2D point) {
		return isInside2D(point.getX(), point.getY());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInside3D(Point3D point) {
		return isInside3D(point.getX(), point.getY(), point.getZ());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInside3D(float x, float y, float z) {
		return isInsideZ(z) && isInside2D(x, y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInsideZ(Point3D point) {
		return isInsideZ(point.getZ());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInsideZ(float z) {
		return z >= getMinZ() && z <= getMaxZ();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getDistance2D(Point2D point) {
		return getDistance2D(point.getX(), point.getY());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getDistance3D(Point3D point) {
		return getDistance3D(point.getX(), point.getY(), point.getZ());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point2D getClosestPoint(Point2D point) {
		return getClosestPoint(point.getX(), point.getY());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point3D getClosestPoint(Point3D point) {
		return getClosestPoint(point.getX(), point.getY(), point.getZ());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point3D getClosestPoint(float x, float y, float z) {
		Point2D closest2d = getClosestPoint(x, y);

		float zCoord;

		if (isInsideZ(z)) {
			zCoord = z;
		}
		else if (z < getMinZ()) {
			zCoord = getMinZ();
		}
		else {
			zCoord = getMaxZ();
		}

		return new Point3D(closest2d.getX(), closest2d.getY(), zCoord);
	}

	/**
	 * {@inheritDoc}
	 */
	public float getMinZ() {
		return minZ;
	}

	/**
	 * {@inheritDoc}
	 */
	public float getMaxZ() {
		return maxZ;
	}

	@Override
	public int getWorldId() {
		return worldId;
	}


	/**
	 * @return the zoneName
	 */
	@Override
	public ZoneName getZoneName() {
		return zoneName;
	}
}
