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
 * This class implements basic boolean transfromer.
 * <p/>
 * Boolean can be represented by "true/false" (case doen't matter) or "1/0". In other cases
 * {@link com.aionemu.commons.configuration.TransformationException} is thrown
 *
 * @author SoulKeeper
 */
public class BooleanTransformer implements PropertyTransformer<Boolean> {

	/**
	 * Shared instance of this transformer, it's thread safe so no need to create multiple instances
	 */
	public static final BooleanTransformer SHARED_INSTANCE = new BooleanTransformer();

	/**
	 * Transforms string to boolean.
	 *
	 * @param value value that will be transformed
	 * @param field value will be assigned to this field
	 * @return Boolean object that represents transformed value
	 * @throws TransformationException if something goes wrong
	 */
	@Override
	public Boolean transform(String value, Field field) throws TransformationException {
		// We should have error here if value is not correct, default
		// "Boolean.parseBoolean" returns false if string
		// is not "true" ignoring case
		if ("true".equalsIgnoreCase(value) || "1".equals(value)) {
			return true;
		} else if ("false".equalsIgnoreCase(value) || "0".equals(value)) {
			return false;
		} else {
			throw new TransformationException("Invalid boolean string: " + value);
		}
	}
}
