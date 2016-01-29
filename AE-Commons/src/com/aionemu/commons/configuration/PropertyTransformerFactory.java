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
package com.aionemu.commons.configuration;

import com.aionemu.commons.configuration.transformers.*;
import com.aionemu.commons.utils.ClassUtils;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

/**
 * This class is responsible for creating property transformers. Each time it creates new instance of custom property
 * transformer, but for build-in it uses shared instances to avoid overhead
 *
 * @author SoulKeeper
 */
public class PropertyTransformerFactory {

	/**
	 * Returns property transformer or throws {@link com.aionemu.commons.configuration.TransformationException} if can't
	 * create new one.
	 *
	 * @param clazzToTransform Class that will is going to be transformed
	 * @param tc               {@link com.aionemu.commons.configuration.PropertyTransformer} class that will be instantiated
	 * @return instance of PropertyTransformer
	 * @throws TransformationException if can't instantiate {@link com.aionemu.commons.configuration.PropertyTransformer}
	 */
	@SuppressWarnings("rawtypes")
	public static PropertyTransformer newTransformer(Class clazzToTransform, Class<? extends PropertyTransformer> tc)
			throws TransformationException {

		// Just a hack, we can't set null to annotation value
		if (tc == PropertyTransformer.class) {
			tc = null;
		}

		if (tc != null) {
			try {
				return tc.newInstance();
			} catch (Exception e) {
				throw new TransformationException("Can't instantiate property transfromer", e);
			}
		}
		if (clazzToTransform == Boolean.class || clazzToTransform == Boolean.TYPE) {
			return BooleanTransformer.SHARED_INSTANCE;
		} else if (clazzToTransform == Byte.class || clazzToTransform == Byte.TYPE) {
			return ByteTransformer.SHARED_INSTANCE;
		} else if (clazzToTransform == Character.class || clazzToTransform == Character.TYPE) {
			return CharTransformer.SHARED_INSTANCE;
		} else if (clazzToTransform == Double.class || clazzToTransform == Double.TYPE) {
			return DoubleTransformer.SHARED_INSTANCE;
		} else if (clazzToTransform == Float.class || clazzToTransform == Float.TYPE) {
			return FloatTransformer.SHARED_INSTANCE;
		} else if (clazzToTransform == Integer.class || clazzToTransform == Integer.TYPE) {
			return IntegerTransformer.SHARED_INSTANCE;
		} else if (clazzToTransform == Long.class || clazzToTransform == Long.TYPE) {
			return LongTransformer.SHARED_INSTANCE;
		} else if (clazzToTransform == Short.class || clazzToTransform == Short.TYPE) {
			return ShortTransformer.SHARED_INSTANCE;
		} else if (clazzToTransform == String.class) {
			return StringTransformer.SHARED_INSTANCE;
		} else if (clazzToTransform.isEnum()) {
			return EnumTransformer.SHARED_INSTANCE;
			// TODO: Implement
			// } else if (ClassUtils.isSubclass(clazzToTransform,
			// Collection.class)) {
			// return new CollectionTransformer();
			// } else if (clazzToTransform.isArray()) {
			// return new ArrayTransformer();
		} else if (clazzToTransform == File.class) {
			return FileTransformer.SHARED_INSTANCE;
		} else if (ClassUtils.isSubclass(clazzToTransform, InetSocketAddress.class)) {
			return InetSocketAddressTransformer.SHARED_INSTANCE;
		} else if (clazzToTransform == Pattern.class) {
			return PatternTransformer.SHARED_INSTANCE;
		} else if (clazzToTransform == Class.class) {
			return ClassTransformer.SHARED_INSTANCE;
		} else {
			throw new TransformationException("Transformer not found for class " + clazzToTransform.getName());
		}
	}
}
