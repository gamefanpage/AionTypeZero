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

package org.typezero.gameserver.model.house;

import com.aionemu.commons.taskmanager.AbstractLockManager;

/**
 * @author Rolandas
 * Use readLock + readUnlock to read both fields, because they may change !!!
 */
public final class PlayerScript extends AbstractLockManager {

	/**
	 * Default constructor, bytes null, uncompressed size -1
	 */
	public PlayerScript() {

	}

	public PlayerScript(byte[] compressedBytes, int uncompressedSize) {
		this.compressedBytes = compressedBytes;
		this.uncompressedSize = uncompressedSize;
	}

	private int uncompressedSize = -1;
	private byte[] compressedBytes = null;

	public int getUncompressedSize() {
		return uncompressedSize;
	}

	public byte[] getCompressedBytes() {
		return compressedBytes;
	}

	public void setData(byte[] compressedBytes, int uncompressedSize) {
		writeLock();
		this.compressedBytes = compressedBytes;
		this.uncompressedSize = uncompressedSize;
		writeUnlock();
	}
}
