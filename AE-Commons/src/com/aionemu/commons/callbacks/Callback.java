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
 * Basic callback class.<br>
 * Each enhanced method will call "beforeCall" and "afterCall" methods
 *
 * @author SoulKeeper
 */
@SuppressWarnings("rawtypes")
public interface Callback<T> {

	/**
	 * Method that is called before actual method is invoked.<br>
	 * <p/>
	 * Callback should return one of the following results:
	 * <ul>
	 * <li>{@link CallbackResult#newContinue()}</li>
	 * <li>{@link CallbackResult#newCallbackBlocker()}</li>
	 * <li>{@link CallbackResult#newFullBlocker(Object)}</li>
	 * </ul>
	 * <p/>
	 * if result is not {@link CallbackResult#newFullBlocker(Object)} then method will be executed normally. In other
	 * case {@link CallbackResult#getResult()} will be returned.
	 *
	 * @param obj  on whom method should be invoked
	 * @param args method args
	 * @return see {@link CallbackResult}
	 */
	public CallbackResult beforeCall(T obj, Object[] args);

	/**
	 * Method that is called after actual method call.<br>
	 * <p/>
	 * Callback should return one of the following results:
	 * <ul>
	 * <li>{@link CallbackResult#newContinue()}</li>
	 * <li>{@link CallbackResult#newCallbackBlocker()}</li>
	 * <li>{@link CallbackResult#newFullBlocker(Object)}</li>
	 * </ul>
	 * <p/>
	 * if result is not {@link CallbackResult#newFullBlocker(Object)} then mehodResult will return unmodified. In other
	 * case {@link CallbackResult#getResult()} will be returned.
	 *
	 * @param obj          on whom method was called
	 * @param args         method args
	 * @param methodResult result of method invocation
	 * @return see {@link CallbackResult}
	 */
	public CallbackResult afterCall(T obj, Object[] args, Object methodResult);

	/**
	 * Returns base class that will be used as callback identificator.<br> {@link com.aionemu.commons.callbacks.metadata.ObjectCallback#value()} should contain
	 * class that will be invoked
	 *
	 * @return base class of callback
	 */
	public Class<? extends Callback> getBaseClass();
}
