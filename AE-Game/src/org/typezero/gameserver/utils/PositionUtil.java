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

package org.typezero.gameserver.utils;

import org.typezero.gameserver.model.gameobjects.VisibleObject;

/**
 * @author ATracer
 */
public class PositionUtil {

	private static final float MAX_ANGLE_DIFF = 90f;

	/**
	 * @param object1
	 * @param object2
	 * @return true or false
	 */
	public static boolean isBehindTarget(VisibleObject object1, VisibleObject object2) {
		float angleObject1 = MathUtil.calculateAngleFrom(object1, object2);
		float angleObject2 = MathUtil.convertHeadingToDegree(object2.getHeading());
		float angleDiff = angleObject1 - angleObject2;

		if (angleDiff <= -360 + MAX_ANGLE_DIFF)
			angleDiff += 360;
		if (angleDiff >= 360 - MAX_ANGLE_DIFF)
			angleDiff -= 360;
		return Math.abs(angleDiff) <= MAX_ANGLE_DIFF;
	}

	/**
	 * @param object1
	 * @param object2
	 * @return true or false
	 */
	public static boolean isInFrontOfTarget(VisibleObject object1, VisibleObject object2) {
		float angleObject2 = MathUtil.calculateAngleFrom(object2, object1);
		float angleObject1 = MathUtil.convertHeadingToDegree(object2.getHeading());
		float angleDiff = angleObject1 - angleObject2;

		if (angleDiff <= -360 + MAX_ANGLE_DIFF)
			angleDiff += 360;
		if (angleDiff >= 360 - MAX_ANGLE_DIFF)
			angleDiff -= 360;
		return Math.abs(angleDiff) <= MAX_ANGLE_DIFF;
	}

	/**
	 * Analyse two object position by coordinates
	 * 
	 * @param object1
	 * @param object2
	 * @return true if the analysed object is behind base object
	 */
	public static boolean isBehind(VisibleObject object1, VisibleObject object2) {
		float angle = MathUtil.convertHeadingToDegree(object1.getHeading()) + 90;
		if (angle >= 360)
			angle -= 360;
		double radian = Math.toRadians(angle);
		float x0 = object1.getX();
		float y0 = object1.getY();
		float x1 = (float) (Math.cos(radian) * 5) + x0;
		float y1 = (float) (Math.sin(radian) * 5) + y0;
		float xA = object2.getX();
		float yA = object2.getY();
		float temp = (x1 - x0) * (yA - y0) - (y1 - y0) * (xA - x0);
		return temp > 0;
	}

	/**
	 * <pre>
 	 *       0 (head view)
	 *  270     90
   *      180  (back)
   * </pre>
	 */
	public static float getAngleToTarget(VisibleObject object1, VisibleObject object2) {
		float angleObject1 = MathUtil.convertHeadingToDegree(object1.getHeading()) - 180;
		if (angleObject1 < 0)
			angleObject1 += 360;
		float angleObject2 = MathUtil.calculateAngleFrom(object1, object2);
		float angleDiff = angleObject1 - angleObject2 - 180;
		if (angleDiff < 0)
			angleDiff += 360;
		return angleDiff;
	}
	
	public static float getDirectionalBound(VisibleObject object1, VisibleObject object2, boolean inverseTarget)
	{
		float angle = 90 - (inverseTarget ? getAngleToTarget(object2, object1) : getAngleToTarget(object1, object2));
		if (angle < 0)
			angle += 360;
		double radians = Math.toRadians(angle);
		float x1 = (float) (object1.getX() + object1.getObjectTemplate().getBoundRadius().getSide() * Math.cos(radians));
		float y1 = (float) (object1.getY() + object1.getObjectTemplate().getBoundRadius().getFront() * Math.sin(radians));
		float x2 = (float) (object2.getX() + object2.getObjectTemplate().getBoundRadius().getSide() * Math.cos(Math.PI + radians));
		float y2 = (float) (object2.getY() + object2.getObjectTemplate().getBoundRadius().getFront() * Math.sin(Math.PI + radians));
		float bound1 = (float) MathUtil.getDistance(object1.getX(), object1.getY(), x1, y1);
		float bound2 = (float) MathUtil.getDistance(object2.getX(), object2.getY(), x2, y2);
		return bound1 - bound2;
	}
	
	public static float getDirectionalBound(VisibleObject object1, VisibleObject object2)
	{
		return getDirectionalBound(object1, object2, false);
	}

	public static byte getMoveAwayHeading(VisibleObject fromObject, VisibleObject object) {
		float angle = MathUtil.calculateAngleFrom(fromObject, object);
		byte heading = MathUtil.convertDegreeToHeading(angle);
		return heading;
	}
}
