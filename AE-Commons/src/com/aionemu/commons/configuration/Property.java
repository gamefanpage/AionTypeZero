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

import java.lang.annotation.*;

/**
 * This annotation is used to mark field that should be processed by
 * {@link com.aionemu.commons.configuration.ConfigurableProcessor}<br>
 * <br>
 * This annotation is Documented, all definitions with it will appear in javadoc
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {

	/**
	 * This string shows to {@link com.aionemu.commons.configuration.ConfigurableProcessor} that init value of the object
	 * should not be overriden.
	 */
	public static final String DEFAULT_VALUE = "DO_NOT_OVERWRITE_INITIALIAZION_VALUE";

	/**
	 * Property name in configuration
	 *
	 * @return name of the property that will be used
	 */
	public String key();

	/**
	 * PropertyTransformer to use.<br>
	 * List of automaticly transformed types:<br>
	 * <ul>
	 * <li>{@link Boolean} and boolean by {@link com.aionemu.commons.configuration.transformers.BooleanTransformer}</li>
	 * <li>{@link Byte} and byte by {@link com.aionemu.commons.configuration.transformers.ByteTransformer}</li>
	 * <li>{@link Character} and char by {@link com.aionemu.commons.configuration.transformers.CharTransformer}</li>
	 * <li>{@link Short} and short by {@link com.aionemu.commons.configuration.transformers.ShortTransformer}</li>
	 * <li>{@link Integer} and int by {@link com.aionemu.commons.configuration.transformers.IntegerTransformer}</li>
	 * <li>{@link Float} and float by {@link com.aionemu.commons.configuration.transformers.FloatTransformer}</li>
	 * <li>{@link Long} and long by {@link com.aionemu.commons.configuration.transformers.LongTransformer}</li>
	 * <li>{@link Double} and double by {@link com.aionemu.commons.configuration.transformers.DoubleTransformer}</li>
	 * <li>{@link String} by {@link com.aionemu.commons.configuration.transformers.StringTransformer}</li>
	 * <li>{@link Enum} and enum by {@link com.aionemu.commons.configuration.transformers.EnumTransformer}</li>
	 * <li>{@link java.io.File} by {@link com.aionemu.commons.configuration.transformers.FileTransformer}</li>
	 * <li>{@link java.net.InetSocketAddress} by
	 * {@link com.aionemu.commons.configuration.transformers.InetSocketAddressTransformer}</li>
	 * <li>{@link java.util.regex.Pattern} by {@link com.aionemu.commons.configuration.transformers.PatternTransformer}
	 * </ul>
	 * <p/>
	 * If your value is one of this types - just leave this field empty
	 *
	 * @return returns class that will be used to transform value
	 */
	@SuppressWarnings("rawtypes")
	public Class<? extends PropertyTransformer> propertyTransformer() default PropertyTransformer.class;

	/**
	 * Represents default value that will be parsed if key not found. If this key equals(default) {@link #DEFAULT_VALUE}
	 * init value of the object won't be overriden
	 *
	 * @return default value of the property
	 */
	public String defaultValue() default DEFAULT_VALUE;
}
