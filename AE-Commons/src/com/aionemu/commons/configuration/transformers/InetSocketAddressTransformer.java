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
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Thransforms string to InetSocketAddress. InetSocketAddress can be represented in following ways:
 * <ul>
 * <li>address:port</li>
 * <li>*:port - will use all avaiable network interfaces</li>
 * </ul>
 *
 * @author SoulKeeper
 */
public class InetSocketAddressTransformer implements PropertyTransformer<InetSocketAddress> {

	/**
	 * Shared instance of this transformer. It's thread-safe so no need of multiple instances
	 */
	public static final InetSocketAddressTransformer SHARED_INSTANCE = new InetSocketAddressTransformer();

	/**
	 * Transforms string to InetSocketAddress
	 *
	 * @param value value that will be transformed
	 * @param field value will be assigned to this field
	 * @return InetSocketAddress that represetns value
	 * @throws TransformationException if somehting went wrong
	 */
	@Override
	public InetSocketAddress transform(String value, Field field) throws TransformationException {
		String[] parts = value.split(":");

		if (parts.length != 2) {
			throw new TransformationException("Can't transform property, must be in format \"address:port\"");
		}

		try {
			if ("*".equals(parts[0])) {
				return new InetSocketAddress(Integer.parseInt(parts[1]));
			}
			InetAddress address = InetAddress.getByName(parts[0]);
			int port = Integer.parseInt(parts[1]);
			return new InetSocketAddress(address, port);
		} catch (Exception e) {
			throw new TransformationException(e);
		}
	}
}
