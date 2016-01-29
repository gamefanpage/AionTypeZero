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
 * @author MrPoke
 */
public class SphereArea implements Area {

	protected float x;
	protected float y;
	protected float z;
	protected float r;
	protected int worldId;
	protected ZoneName zoneName;

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param r
	 * @param worldId
	 * @param zoneName
	 */
	public SphereArea(ZoneName zoneName, int worldId, float x, float y, float z, float r) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
		this.worldId = worldId;
		this.zoneName = zoneName;
	}

	@Deprecated
	@Override
	public boolean isInside2D(Point2D point) {
		return false;
	}

	@Deprecated
	@Override
	public boolean isInside2D(float x, float y) {
		return false;
	}

	@Override
	public boolean isInside3D(Point3D point) {
		return MathUtil.isIn3dRange(x, y, z, point.getX(), point.getY(), point.getZ(), r);
	}

	@Override
	public boolean isInside3D(float x, float y, float z) {
		return MathUtil.isIn3dRange(x, y, z, this.x, this.y, this.z, r);
	}

	@Override
	public boolean isInsideZ(Point3D point) {
		return isInsideZ(point.getZ());
	}

	@Override
	public boolean isInsideZ(float z) {
		return z >= this.getMinZ() && z <= this.getMaxZ();
	}

	@Deprecated
	@Override
	public double getDistance2D(Point2D point) {
		return 0;
	}

	@Deprecated
	@Override
	public double getDistance2D(float x, float y) {
		return 0;
	}

	@Override
	public double getDistance3D(Point3D point) {
		return getDistance3D(point.getX(), point.getY(), point.getZ());
	}

	@Override
	public double getDistance3D(float x, float y, float z) {
		double distance = MathUtil.getDistance(x, y, z, this.x, this.y, this.z) - r;
		return distance > 0 ? distance : 0;
	}

	@Deprecated
	@Override
	public Point2D getClosestPoint(Point2D point) {
		return null;
	}

	@Deprecated
	@Override
	public Point2D getClosestPoint(float x, float y) {
		return null;
	}

	@Override
	public Point3D getClosestPoint(Point3D point) {
		return null;
	}

	@Override
	public Point3D getClosestPoint(float x, float y, float z) {
		return null;
	}

	@Override
	public float getMinZ() {
		return z - r;
	}

	@Override
	public float getMaxZ() {
		return z + r;
	}

	@Override
	public boolean intersectsRectangle(RectangleArea area) {
		if (area.getDistance3D(x, y, z) <= r)
			return true;
		return false;
	}

	@Override
	public int getWorldId() {
		return worldId;
	}

	@Override
	public ZoneName getZoneName() {
		return zoneName;
	}

}
