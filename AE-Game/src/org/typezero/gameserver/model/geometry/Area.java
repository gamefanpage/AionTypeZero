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
 * Basic interface for all areas in AionEmu.<br>
 * It should be implemented in different ways for performance reasons.<br>
 * For instance, we don't need complex math for squares or circles, but we need it for more complex polygons.
 *
 * @author SoulKeeper
 */
public interface Area {

	/**
	 * Returns true if point is inside area ignoring z value
	 *
	 * @param point
	 *          point to check
	 * @return point is inside or not
	 */
	public boolean isInside2D(Point2D point);

	/**
	 * Returns true if coords are inside area ignoring z value
	 *
	 * @param x
	 *          x coord
	 * @param y
	 *          y coord
	 * @return coords are inside or not
	 */
	public boolean isInside2D(float x, float y);

	/**
	 * Returns true if point is inside area
	 *
	 * @param point
	 *          point to check
	 * @return true if point is inside
	 */
	public boolean isInside3D(Point3D point);

	/**
	 * Returns true if coors are inside area
	 *
	 * @param x
	 *          x coord
	 * @param y
	 *          y coord
	 * @param z
	 *          z coord
	 * @return true if coords are inside
	 */
	public boolean isInside3D(float x, float y, float z);

	/**
	 * Checks if z coord is insize
	 *
	 * @param point
	 *          point to check
	 * @return is z inside or not
	 */
	public boolean isInsideZ(Point3D point);

	/**
	 * Checks is z coord is inside
	 *
	 * @param z
	 *          z coord
	 * @return is z inside or not
	 */
	public boolean isInsideZ(float z);

	/**
	 * Returns distance from point to closest point of this area ignoring z.<br>
	 * Returns 0 if point is inside area.
	 *
	 * @param point
	 *          point to calculate distance from
	 * @return distance or 0 if is inside area
	 */
	public double getDistance2D(Point2D point);

	/**
	 * Returns distance from point to closest point of this area ignoring z.<br>
	 * Returns 0 point is inside area.
	 *
	 * @param x
	 *          x coord
	 * @param y
	 *          y coord
	 * @return distance or 0 if is inside area
	 */
	public double getDistance2D(float x, float y);

	/**
	 * Returns distance from point to this area.<br>
	 * Returns 0 if is inside.
	 *
	 * @param point
	 *          point to check
	 * @return distance or 0 if is inside
	 */
	public double getDistance3D(Point3D point);

	/**
	 * Returns distance from coords to this area
	 *
	 * @param x
	 *          x coord
	 * @param y
	 *          y coord
	 * @param z
	 *          z coord
	 * @return distance or 0 if is inside
	 */
	public double getDistance3D(float x, float y, float z);

	/**
	 * Returns closest point of area to given point.<br>
	 * Returns point with coords = point arg if is inside
	 *
	 * @param point
	 *          point to check
	 * @return closest point
	 */
	public Point2D getClosestPoint(Point2D point);

	/**
	 * Returns closest point of area to given coords.<br>
	 * Returns point with coords x and y if coords are inside
	 *
	 * @param x
	 *          x coord
	 * @param y
	 *          y coord
	 * @return closest point
	 */
	public Point2D getClosestPoint(float x, float y);

	/**
	 * Returns closest point of area to given point.<br>
	 * Works exactly like {@link #getClosestPoint(int, int)} if {@link #isInsideZ(int)} returns true.<br>
	 * In other case closest z edge is set as z coord.
	 *
	 * @param point
	 *          point to check
	 * @return closest point of area to point
	 */
	public Point3D getClosestPoint(Point3D point);

	/**
	 * Returns closest point of area to given coords.<br>
	 * Works exactly like {@link #getClosestPoint(int, int)} if {@link #isInsideZ(int)} returns true.<br>
	 * In other case closest z edge is set as z coord.
	 *
	 * @param x
	 *          x coord
	 * @param y
	 *          y coord
	 * @param z
	 *          z coord
	 * @return closest point of area to point
	 */
	public Point3D getClosestPoint(float x, float y, float z);

	/**
	 * Return minimal z of this area
	 *
	 * @return minimal z of this area
	 */
	public float getMinZ();

	/**
	 * Returns maximal z of this area
	 *
	 * @return maximal z of this area
	 */
	public float getMaxZ();

	public boolean intersectsRectangle(RectangleArea area);

	public int getWorldId();

	public ZoneName getZoneName();
}
