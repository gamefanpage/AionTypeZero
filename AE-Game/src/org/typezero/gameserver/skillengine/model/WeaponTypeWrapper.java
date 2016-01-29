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

package org.typezero.gameserver.skillengine.model;

import org.typezero.gameserver.model.templates.item.WeaponType;
import org.typezero.gameserver.services.MotionLoggingService;


/**
 * @author kecimis
 *
 */
public class WeaponTypeWrapper implements Comparable<WeaponTypeWrapper> {

	private WeaponType mainHand = null;
	private WeaponType offHand = null;

	public WeaponTypeWrapper(WeaponType mainHand, WeaponType offHand) {
		if (mainHand != null && offHand != null) {
			if(mainHand != WeaponType.GUN_1H && offHand != WeaponType.GUN_1H){ //dirty code - dirty fix
				this.mainHand = WeaponType.SWORD_1H;
				this.offHand = WeaponType.SWORD_1H;
			}
			else {
				this.mainHand = WeaponType.GUN_1H;
				this.offHand = WeaponType.GUN_1H;
			}
		}
		else {
			this.mainHand = mainHand;
			this.offHand = offHand;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WeaponTypeWrapper other = (WeaponTypeWrapper) obj;
		if (!getOuterType().equals(other.getOuterType()))
			return false;
		if (mainHand != other.mainHand)
			return false;
		if (offHand != other.offHand)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "mainHandType=\"" + (mainHand != null ? mainHand.toString() : "null") + "\"" + " offHandType=\""
			+ (offHand != null ? offHand.toString() : "null");
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getOuterType().hashCode();
		result = prime * result + ((mainHand == null) ? 0 : mainHand.hashCode());
		result = prime * result + ((offHand == null) ? 0 : offHand.hashCode());
		return result;
	}

	@Override
	public int compareTo(WeaponTypeWrapper o) {
		if (mainHand == null || o.getMainHand() == null)
			return 0;
		else if (offHand != null && o.getOffHand() != null)
			return 0;
		else if (offHand != null && o.getOffHand() == null)
			return 1;
		else if (offHand == null && o.getOffHand() != null)
			return -1;
		else
			return mainHand.toString().compareTo(o.getMainHand().toString());
	}

	public WeaponType getMainHand() {
		return this.mainHand;
	}

	public WeaponType getOffHand() {
		return this.offHand;
	}

	private MotionLoggingService getOuterType() {
		return MotionLoggingService.getInstance();
	}
}
