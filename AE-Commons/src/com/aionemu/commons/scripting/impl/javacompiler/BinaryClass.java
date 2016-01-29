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
package com.aionemu.commons.scripting.impl.javacompiler;

import java.io.*;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;


/**
 * This class is just a hack to make javac compiler work with classes loaded by prevoius classloader. Also it's used as
 * container for loaded class
 *
 * @author SoulKeeper
 */
public class BinaryClass extends SimpleJavaFileObject {

	/**
	 * Class data will be written here
	 */
	private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

	/**
	 * Locaded class will be set here
	 */
	private Class<?> definedClass;

	/**
	 * Constructor that accepts class name as parameter
	 *
	 * @param name class name
	 */
	protected BinaryClass(String name) {
		super(URI.create(name), Kind.CLASS);
	}

	/**
	 * Throws {@link UnsupportedOperationException}
	 *
	 * @return nothing
	 */
	@Override
	public URI toUri() {
		return super.toUri();
	}

	/**
	 * Creates new ByteArrayInputStream, it just wraps class binary data
	 *
	 * @return input stream for class data
	 * @throws IOException never thrown
	 */
	@Override
	public InputStream openInputStream() throws IOException {
		return new ByteArrayInputStream(baos.toByteArray());
	}

	/**
	 * Opens ByteArrayOutputStream for class data
	 *
	 * @return output stream
	 * @throws IOException never thrown
	 */
	@Override
	public OutputStream openOutputStream() throws IOException {
		return baos;
	}

	/**
	 * Throws {@link UnsupportedOperationException}
	 *
	 * @return nothing
	 */
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Throws {@link UnsupportedOperationException}
	 *
	 * @return nothing
	 */
	@Override
	public Writer openWriter() throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported operation, always reutrns 0
	 *
	 * @return 0
	 */
	@Override
	public long getLastModified() {
		return 0;
	}

	/**
	 * Unsupported operation, returns false
	 *
	 * @return false
	 */
	@Override
	public boolean delete() {
		return false;
	}

	/**
	 * Returns true if {@link javax.tools.JavaFileObject.Kind#CLASS}
	 *
	 * @param simpleName doesn't matter
	 * @param kind       kind to compare
	 * @return true if Kind is {@link javax.tools.JavaFileObject.Kind#CLASS}
	 */
	@Override
	public boolean isNameCompatible(String simpleName, Kind kind) {
		return Kind.CLASS.equals(kind);
	}

	/**
	 * Returns bytes of class
	 *
	 * @return bytes of class
	 */
	public byte[] getBytes() {
		return baos.toByteArray();
	}

	/**
	 * Returns class that was loaded from binary data of this object
	 *
	 * @return loaded class
	 */
	public Class<?> getDefinedClass() {
		return definedClass;
	}

	/**
	 * Sets class that was loaded by this object
	 *
	 * @param definedClass class that was loaded
	 */
	public void setDefinedClass(Class<?> definedClass) {
		this.definedClass = definedClass;
	}

	/* (non-Javadoc)
	 * @see javax.tools.JavaFileObject#getKind()
	 */
	@Override
	public Kind getKind() {
		return Kind.CLASS;
	}

}
