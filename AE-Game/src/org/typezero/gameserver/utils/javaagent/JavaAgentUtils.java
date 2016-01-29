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

package org.typezero.gameserver.utils.javaagent;

import com.aionemu.commons.callbacks.Callback;
import com.aionemu.commons.callbacks.CallbackResult;
import com.aionemu.commons.callbacks.EnhancedObject;
import com.aionemu.commons.callbacks.metadata.GlobalCallback;
import com.aionemu.commons.callbacks.metadata.ObjectCallback;
import com.aionemu.commons.callbacks.util.GlobalCallbackHelper;

public class JavaAgentUtils {
	static
	{
		GlobalCallbackHelper.addCallback(new CheckCallback());
	}

	public static boolean isConfigured() {
		JavaAgentUtils jau = new JavaAgentUtils();
		if(!(jau instanceof EnhancedObject))
			throw new Error("Please configure -javaagent jvm option.");

		if(!checkGlobalCallback())
			throw new Error("Global callbacks are not working correctly!");

		((EnhancedObject)jau).addCallback(new CheckCallback());
		if(!jau.checkObjectCallback())
			throw new Error("Object callbacks are not working correctly!");

		return true;
	}

	@GlobalCallback(CheckCallback.class)
	private static boolean checkGlobalCallback()
	{
		return false;
	}

	@ObjectCallback(CheckCallback.class)
	private boolean checkObjectCallback()
	{
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static class CheckCallback implements Callback{

		@Override
		public CallbackResult<Boolean> beforeCall(Object obj, Object[] args) {
			return CallbackResult.newFullBlocker(true);
		}

		@Override
		public CallbackResult<Boolean> afterCall(Object obj, Object[] args, Object methodResult) {
			return CallbackResult.newContinue();
		}

		@Override
		public Class<? extends Callback> getBaseClass() {
			return CheckCallback.class;
		}
	}
}
