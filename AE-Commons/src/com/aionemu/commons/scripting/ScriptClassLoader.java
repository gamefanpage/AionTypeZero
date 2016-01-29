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
package com.aionemu.commons.scripting;

import com.aionemu.commons.scripting.url.VirtualClassURLStreamHandler;
import com.aionemu.commons.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class loader that should be extended by child classloaders. If needed, this class should wrap another
 * classloader.
 *
 * @author SoulKeeper
 */
public abstract class ScriptClassLoader extends URLClassLoader {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(ScriptClassLoader.class);

	/**
	 * URL Stream handler to allow valid url generation by {@link #getResource(String)}
	 */
	private final VirtualClassURLStreamHandler urlStreamHandler = new VirtualClassURLStreamHandler(this);

	/**
	 * Classes that were loaded from libraries. They are no parsed for any annotations, but they are needed by
	 * JavaCompiler to perform valid compilation
	 */
	private Set<String> libraryClassNames = new HashSet<String>();

	/**
	 * List of jar files that were scanned by this classloader for classes
	 */
	private Set<File> loadedLibraries = new HashSet<File>();

	/**
	 * Just for compatibility with {@link URLClassLoader}
	 *
	 * @param urls   list of urls
	 * @param parent parent classloader
	 */
	public ScriptClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	/**
	 * Just for compatibility with {@link URLClassLoader}
	 *
	 * @param urls list of urls
	 */
	public ScriptClassLoader(URL[] urls) {
		super(urls);
	}

	/**
	 * Just for compatibility with {@link URLClassLoader}
	 *
	 * @param urls    list of urls
	 * @param parent  parent classloader
	 * @param factory {@link java.net.URLStreamHandlerFactory}
	 */
	public ScriptClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
	}

	/**
	 * Adds library to this classloader, it shuould be jar file
	 *
	 * @param file jar file
	 * @throws IOException if can't add library
	 */
	public void addJarFile(File file) throws IOException {
		if (!loadedLibraries.contains(file)) {
			Set<String> jarFileClasses = ClassUtils.getClassNamesFromJarFile(file);
			libraryClassNames.addAll(jarFileClasses);
			loadedLibraries.add(file);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public URL getResource(String name) {
		if (!name.endsWith(".class")) {
			return super.getResource(name);
		}
		String newName = name.substring(0, name.length() - 6);
		newName = newName.replace('/', '.');
		if (getCompiledClasses().contains(newName)) {
			try {
				return new URL(null, VirtualClassURLStreamHandler.HANDLER_PROTOCOL + newName, urlStreamHandler);
			} catch (MalformedURLException e) {
				log.error("Can't create url for compiled class", e);
			}
		}

		return super.getResource(name);
	}

	/**
	 * Loads class from library, parent or compiled
	 *
	 * @param name class to load
	 * @return loaded class
	 * @throws ClassNotFoundException if class not found
	 */
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		boolean isCompiled = getCompiledClasses().contains(name);
		if (!isCompiled) {
			return super.loadClass(name, true);
		}

		Class<?> c = getDefinedClass(name);
		if (c == null) {
			byte[] b = getByteCode(name);
			c = super.defineClass(name, b, 0, b.length);
			setDefinedClass(name, c);
		}
		return c;
	}

	protected Set<String> getLibraryClassNames() {
		return Collections.unmodifiableSet(libraryClassNames);
	}

	/**
	 * Retuns unmodifiable set of class names that were compiled
	 *
	 * @return unmodifiable set of class names that were compiled
	 */
	public abstract Set<String> getCompiledClasses();

	/**
	 * Returns bytecode for given className. Array is copy of actual bytecode, so modifications will not harm.
	 *
	 * @param className class name
	 * @return bytecode
	 */
	public abstract byte[] getByteCode(String className);

	/**
	 * Returns cached class instance for give name or null if is not cached yet
	 *
	 * @param name class name
	 * @return cached class instance or null
	 */
	public abstract Class<?> getDefinedClass(String name);

	/**
	 * Sets defined class into cache
	 *
	 * @param name  class name
	 * @param clazz class object
	 * @throws IllegalArgumentException if class was not loaded by this class loader
	 */
	public abstract void setDefinedClass(String name, Class<?> clazz) throws IllegalArgumentException;
}
