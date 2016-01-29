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

package org.typezero.gameserver.model.account;

/**
 * @author cura
 */
public class CharacterPasskey {

	private int objectId;
	private int wrongCount = 0;
	private boolean isPass = false;
	private ConnectType connectType;

	/**
	 * @return the objectId
	 */
	public int getObjectId() {
		return objectId;
	}

	/**
	 * @param objectId
	 *          the objectId to set
	 */
	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}

	/**
	 * @return the wrongCount
	 */
	public int getWrongCount() {
		return wrongCount;
	}

	/**
	 * @param count
	 *          the wrongCount to set
	 */
	public void setWrongCount(int count) {
		this.wrongCount = count;
	}

	/**
	 * @return the isPass
	 */
	public boolean isPass() {
		return isPass;
	}

	/**
	 * @param isPass
	 *          the isPass to set
	 */
	public void setIsPass(boolean isPass) {
		this.isPass = isPass;
	}

	/**
	 * @return the connectType
	 */
	public ConnectType getConnectType() {
		return connectType;
	}

	/**
	 * @param connectType
	 *          the connectType to set
	 */
	public void setConnectType(ConnectType connectType) {
		this.connectType = connectType;
	}

	public enum ConnectType {
		ENTER,
		DELETE
	}
}
