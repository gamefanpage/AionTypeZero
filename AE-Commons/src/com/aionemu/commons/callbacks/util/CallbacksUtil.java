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

package com.aionemu.commons.callbacks.util;

import com.aionemu.commons.callbacks.Callback;
import com.aionemu.commons.callbacks.CallbackPriority;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;

import java.util.List;

@SuppressWarnings("rawtypes")
public class CallbacksUtil {

	/**
	 * Checks if annotation is present on method
	 *
	 * @param method     Method to check
	 * @param annotation Annotation to look for
	 * @return result
	 */
	public static boolean isAnnotationPresent(CtMethod method, Class<? extends java.lang.annotation.Annotation> annotation) {
		for (Object o : method.getMethodInfo().getAttributes()) {
			if (o instanceof AnnotationsAttribute) {
				AnnotationsAttribute attribute = (AnnotationsAttribute) o;
				if (attribute.getAnnotation(annotation.getName()) != null) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Returns priority of callback.<br>
	 * Method checks if callback is instance of {@link com.aionemu.commons.callbacks.CallbackPriority}, and returns
	 * <p/>
	 * <pre>
	 * {@link com.aionemu.commons.callbacks.CallbackPriority#DEFAULT_PRIORITY} - {@link com.aionemu.commons.callbacks.CallbackPriority#getPriority()}
	 * </pre>
	 * <p/>
	 * .<br>
	 * If callback is not instance of CallbackPriority then it returns {@link com.aionemu.commons.callbacks.CallbackPriority#DEFAULT_PRIORITY}
	 *
	 * @param callback priority to get from
	 * @return priority of callback
	 */
	public static int getCallbackPriority(Callback callback) {
		if (callback instanceof CallbackPriority) {
			CallbackPriority instancePriority = (CallbackPriority) callback;
			return CallbackPriority.DEFAULT_PRIORITY - instancePriority.getPriority();
		} else {
			return CallbackPriority.DEFAULT_PRIORITY;
		}
	}

	protected static void insertCallbackToList(Callback callback, List<Callback> list) {
		int callbackPriority = CallbacksUtil.getCallbackPriority(callback);

		if (!list.isEmpty()) {
			// hand-made sorting, if needed to insert to the middle
			for (int i = 0, n = list.size(); i < n; i++) {
				Callback c = list.get(i);

				int cPrio = CallbacksUtil.getCallbackPriority(c);

				if (callbackPriority < cPrio) {
					list.add(i, callback);
					return;
				}
			}
		}
		// add last
		list.add(callback);
	}
}
