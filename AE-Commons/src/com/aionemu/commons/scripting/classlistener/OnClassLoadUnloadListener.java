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
package com.aionemu.commons.scripting.classlistener;

import com.aionemu.commons.scripting.metadata.OnClassLoad;
import com.aionemu.commons.scripting.metadata.OnClassUnload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author SoulKeeper
 */
public class OnClassLoadUnloadListener implements ClassListener {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(OnClassLoadUnloadListener.class);

	@Override
	public void postLoad(Class<?>[] classes) {
		for (Class<?> c : classes) {
			doMethodInvoke(c.getDeclaredMethods(), OnClassLoad.class);
		}
	}

	@Override
	public void preUnload(Class<?>[] classes) {
		for (Class<?> c : classes) {
			doMethodInvoke(c.getDeclaredMethods(), OnClassUnload.class);
		}
	}

	/**
	 * Actually invokes method where given annotation class is present. Only static methods can be invoked
	 *
	 * @param methods         Methods to scan for annotations
	 * @param annotationClass class of annotation to search for
	 */
	protected final void doMethodInvoke(Method[] methods, Class<? extends Annotation> annotationClass) {
		for (Method m : methods) {
			if (!Modifier.isStatic(m.getModifiers()))
				continue;

			boolean accessible = m.isAccessible();
			m.setAccessible(true);

			if (m.getAnnotation(annotationClass) != null) {
				try {
					m.invoke(null);
				} catch (IllegalAccessException e) {
					log.error("Can't access method " + m.getName() + " of class " + m.getDeclaringClass().getName(), e);
				} catch (InvocationTargetException e) {
					log.error("Can't invoke method " + m.getName() + " of class " + m.getDeclaringClass().getName(), e);
				}
			}

			m.setAccessible(accessible);
		}
	}
}
