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

package com.aionemu.commons.callbacks;

/**
 * This class represents callback result
 *
 * @param <T> Type of callback result
 * @author SoulKeeper
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class CallbackResult<T> {

	/**
	 * Continue mask for callbacks, future invocation of method or other callbacks is not blocked
	 */
	public static final int CONTINUE = 0x00;

	/**
	 * Block callbacks mask, future callbacks will be blocked, but method won't be
	 */
	public static final int BLOCK_CALLBACKS = 0x01;

	/**
	 * Method will be blocked, but not callbacks
	 */
	public static final int BLOCK_CALLER = 0x02;

	/**
	 * Caller and another callbacks will be blocked
	 */
	public static final int BLOCK_ALL = 0x01 | 0x02;

	/**
	 * Cache for continue instance
	 */
	private static final CallbackResult INSTANCE_CONTINUE = new CallbackResult(CONTINUE);

	/**
	 * Cache for callback blocker
	 */
	private static final CallbackResult INSTANCE_BLOCK_CALLBACKS = new CallbackResult(BLOCK_CALLBACKS);

	/**
	 * Result of callback invokation, used only when caller is blocked by callback
	 */
	private final T result;

	/**
	 * What this callback is blocking
	 */
	private final int blockPolicy;

	/**
	 * Creates new callback with specified blocking policy
	 *
	 * @param blockPolicy what this callback should block
	 */
	private CallbackResult(int blockPolicy) {
		this(null, blockPolicy);
	}

	/**
	 * Creates new callback with specified blocking policy and result
	 *
	 * @param result      result of callback
	 * @param blockPolicy what this callback blocks
	 */
	private CallbackResult(T result, int blockPolicy) {
		this.result = result;
		this.blockPolicy = blockPolicy;
	}

	/**
	 * Retruns result of this callback
	 *
	 * @return result of this callback
	 */
	public T getResult() {
		return result;
	}

	/**
	 * Returns true if is blocking callbacks
	 *
	 * @return true if is blocking callbacks
	 */
	public boolean isBlockingCallbacks() {
		return (blockPolicy & BLOCK_CALLBACKS) != 0;
	}

	/**
	 * Returns true if is blocking caller
	 *
	 * @return true if is blocking caller
	 */
	public boolean isBlockingCaller() {
		return (blockPolicy & BLOCK_CALLER) != 0;
	}

	/**
	 * Returns callback for continue action, for perfomance reasons returns cached instance
	 *
	 * @param <T> type of result object, ignored, always null
	 * @return callback with result type continue
	 */
	public static <T> CallbackResult<T> newContinue() {
		return INSTANCE_CONTINUE;
	}

	/**
	 * Returns callback that blocks another callbacks, cached instance is used for perfomance reasons
	 *
	 * @param <T> type of result object, ignored, always null
	 * @return callback that blocks invocation of another callbacks
	 */
	public static <T> CallbackResult<T> newCallbackBlocker() {
		return INSTANCE_BLOCK_CALLBACKS;
	}

	/**
	 * Returns callback that blocks another callbacks and method invocation.<br>
	 * {@link com.aionemu.commons.callbacks.Callback#afterCall(Object, Object[], Object)} will be invoked with the
	 * result from this call.
	 *
	 * @param result Result of callback
	 * @param <T>    type of result
	 * @return new callback instance with given result that will be returned as method result
	 */
	public static <T> CallbackResult<T> newFullBlocker(T result) {
		return new CallbackResult<T>(result, BLOCK_ALL);
	}
}
