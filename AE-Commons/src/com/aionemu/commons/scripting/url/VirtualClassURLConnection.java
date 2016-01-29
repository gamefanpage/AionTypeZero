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
package com.aionemu.commons.scripting.url;

import com.aionemu.commons.scripting.ScriptClassLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class represents URL Connection that is used to "connect" to scripts binary data that was loaded by specified
 * {@link com.aionemu.commons.scripting.impl.javacompiler.ScriptCompilerImpl}.<br>
 * <br>
 * TODO: Implement all methods of {@link URLConnection} to ensure valid behaviour
 *
 * @author SoulKeeper
 */
public class VirtualClassURLConnection extends URLConnection {

	/**
	 * Input stream, is assigned from class
	 */
	private InputStream is;

	/**
	 * Creates URL connections that "connects" to class binary data
	 *
	 * @param url class name
	 * @param cl  classloader
	 */
	protected VirtualClassURLConnection(URL url, ScriptClassLoader cl) {
		super(url);
		is = new ByteArrayInputStream(cl.getByteCode(url.getHost()));
	}

	/**
	 * This method is ignored
	 */
	@Override
	public void connect() throws IOException {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return is;
	}
}
