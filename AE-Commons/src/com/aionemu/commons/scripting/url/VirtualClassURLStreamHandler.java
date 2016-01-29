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

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * This class represents URL Stream handler that accepts {@value #HANDLER_PROTOCOL} protocol
 *
 * @author SoulKeeper
 */
public class VirtualClassURLStreamHandler extends URLStreamHandler {

	/**
	 * Script Handler protocol for classes compiled from source
	 */
	public static final String HANDLER_PROTOCOL = "aescript://";

	/**
	 * Script class loader that loaded those classes
	 */
	private final ScriptClassLoader cl;

	/**
	 * Creates new instance of url stream handler with given classloader
	 *
	 * @param cl ScriptClassLoaderImpl that was used to load compiled class
	 */
	public VirtualClassURLStreamHandler(ScriptClassLoader cl) {
		this.cl = cl;
	}

	/**
	 * Opens new URL connection for URL
	 *
	 * @param u url
	 * @return Opened connection
	 * @throws IOException never thrown
	 */
	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		return new VirtualClassURLConnection(u, cl);
	}
}
