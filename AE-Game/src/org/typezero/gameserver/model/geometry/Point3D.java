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

import java.io.Serializable;

import org.typezero.gameserver.model.templates.zone.Point2D;

/**
 * This class represents 3D point.<br>
 * It's valid for serializing and cloning.
 *
 * @author SoulKeeper
 */
@SuppressWarnings("serial")
public class Point3D implements Cloneable, Serializable {

	/**
	 * X coord of the point
	 */
	private float x;

	/**
	 * Y coord of the point
	 */
	private float y;

	/**
	 * Z coord of the point
	 */
	private float z;

	/**
	 * Creates new point with coords 0, 0, 0
	 */
	public Point3D() {
	}

	/**
	 * Creates new 3D point from 2D point and z coord
	 *
	 * @param point
	 *          2D point
	 * @param z
	 *          z coord
	 */
	public Point3D(Point2D point, float z) {
		this(point.getX(), point.getY(), z);
	}

	/**
	 * Clones another 3D point
	 *
	 * @param point
	 *          3d point to clone
	 */
	public Point3D(Point3D point) {
		this(point.getX(), point.getY(), point.getZ());
	}

	/**
	 * Creates new 3d point with given coords
	 *
	 * @param x
	 *          x coord
	 * @param y
	 *          y coord
	 * @param z
	 *          z coord
	 */
	public Point3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Returns x coord
	 *
	 * @return x coord
	 */
	public float getX() {
		return x;
	}

	/**
	 * Sets x coord of this point
	 *
	 * @param x
	 *          x coord
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * Returns y coord of this point
	 *
	 * @return y coord
	 */
	public float getY() {
		return y;
	}

	/**
	 * Sets y coord of this point
	 *
	 * @param y
	 *          y coord
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Returns z coord of this point
	 *
	 * @return z coord
	 */
	public float getZ() {
		return z;
	}

	/**
	 * Sets z coord of this point
	 *
	 * @param z
	 *          z coord
	 */
	public void setZ(float z) {
		this.z = z;
	}

	/**
	 * Checks if this point is equal to another point
	 *
	 * @param o
	 *          point to compare with
	 * @return true if equal
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Point3D))
			return false;

		Point3D point3D = (Point3D) o;

		return x == point3D.x && y == point3D.y && z == point3D.z;
	}

	/**
	 * Returns point's hashcode.<br>
	 *
	 * <pre>
	 * int result = x;
	 * result = 31 * result + y;
	 * result = 31 * result + z;
	 * return result;
	 * </pre>
	 *
	 * @return hashcode
	 */
	@Override
	public int hashCode() {
		float result = x;
		result = 31 * result + y;
		result = 31 * result + z;
		return (int)(result*100);
	}

	/**
	 * Clones this point
	 *
	 * @return copy of this point
	 * @throws CloneNotSupportedException
	 *           never thrown
	 */
	@Override
	public Point3D clone() throws CloneNotSupportedException {
		return new Point3D(this);
	}

	/**
	 * Formatted string representation of this point
	 *
	 * @return returns formatted string that represents this point
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Point3D");
		sb.append("{x=").append(x);
		sb.append(", y=").append(y);
		sb.append(", z=").append(z);
		sb.append('}');
		return sb.toString();
	}
}
