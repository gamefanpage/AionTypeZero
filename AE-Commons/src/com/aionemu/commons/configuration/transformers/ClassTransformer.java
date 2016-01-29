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
package com.aionemu.commons.configuration.transformers;

import com.aionemu.commons.configuration.PropertyTransformer;
import com.aionemu.commons.configuration.TransformationException;

import java.lang.reflect.Field;

/**
 * Returns the <code>Class</code> object associated with the class or interface with the given string name. The class is
 * not being initialized. <br />
 * Created on: 12.09.2009 15:10:47
 *
 * @author Aquanox
 * @see Class#forName(String)
 * @see Class#forName(String, boolean, ClassLoader)
 */
public class ClassTransformer implements PropertyTransformer<Class<?>> {

	/**
	 * Shared instance.
	 */
	public static final ClassTransformer SHARED_INSTANCE = new ClassTransformer();

	@Override
	public Class<?> transform(String value, Field field) throws TransformationException {
		try {
			return Class.forName(value, false, getClass().getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new TransformationException("Cannot find class with name '" + value + "'");
		}
	}
}
