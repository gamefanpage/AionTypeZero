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

import com.aionemu.commons.callbacks.enhancer.GlobalCallbackEnhancer;
import com.aionemu.commons.callbacks.enhancer.ObjectCallbackEnhancer;

import java.lang.instrument.Instrumentation;

/**
 * This class is used as javaagent to do on-class-load transformations with objects whose methods are marked by
 * {@link com.aionemu.commons.callbacks.metadata.ObjectCallback} or
 * {@link com.aionemu.commons.callbacks.metadata.GlobalCallback} annotation.<br>
 * Code is inserted dynamicly before method call and after method call.<br>
 * For implementation docs please reffer to: http://www.csg.is.titech.ac.jp/~chiba/javassist/tutorial/tutorial2.html<br>
 * <br>
 * Usage: java -javaagent:lib/ae_commons.jar
 *
 * @author SoulKeeper
 */
public class JavaAgentEnhancer {

	/**
	 * Premain method that registers this class as ClassFileTransformer
	 *
	 * @param args            arguments passed to javaagent, ignored
	 * @param instrumentation Instrumentation object
	 */
	public static void premain(String args, Instrumentation instrumentation) {
		instrumentation.addTransformer(new ObjectCallbackEnhancer(), true);
		instrumentation.addTransformer(new GlobalCallbackEnhancer(), true);
	}
}
