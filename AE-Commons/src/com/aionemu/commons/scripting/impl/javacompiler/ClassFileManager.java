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

import com.aionemu.commons.scripting.ScriptClassLoader;

import javax.tools.*;
import javax.tools.JavaFileObject.Kind;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This class extends manages loaded classes. It is also responsible for tricking compiler. Unfortunally compiler doen't
 * work with classloaders, so we have to pass class data manually for each compilation.
 *
 * @author SoulKeeper
 */
public class ClassFileManager extends ForwardingJavaFileManager<JavaFileManager> {

	/**
	 * This map contains classes compiled for this classloader
	 */
	private final Map<String, BinaryClass> compiledClasses = new HashMap<String, BinaryClass>();

	/**
	 * Classloader that will be used to load compiled classes
	 */
	protected ScriptClassLoaderImpl loader;

	/**
	 * Parent classloader for loader
	 */
	protected ScriptClassLoader parentClassLoader;

	/**
	 * Creates new ClassFileManager.
	 *
	 * @param compiler that will be used
	 * @param listener class that will report compilation errors
	 */
	public ClassFileManager(JavaCompiler compiler, DiagnosticListener<? super JavaFileObject> listener) {
		super(compiler.getStandardFileManager(listener, null, null));
	}

	/**
	 * Returns JavaFileObject that will be used to write class data into it by compier
	 *
	 * @param location  not used
	 * @param className JavaFileObject will have this className
	 * @param kind      not used
	 * @param sibling   not used
	 * @return JavaFileObject that will be uesd to store compiled class data
	 * @throws IOException never thrown
	 */
	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling)
			throws IOException {
		BinaryClass co = new BinaryClass(className);
		compiledClasses.put(className, co);
		return co;
	}

	/**
	 * Returns classloaded of this ClassFileManager. If not exists, creates new
	 *
	 * @param location not used
	 * @return classLoader of this ClassFileManager
	 */
	@Override
	public synchronized ScriptClassLoaderImpl getClassLoader(Location location) {
		if (loader == null) {
			if (parentClassLoader != null) {
				loader = new ScriptClassLoaderImpl(this, parentClassLoader);
			} else {
				loader = new ScriptClassLoaderImpl(this);
			}
		}
		return loader;
	}

	/**
	 * Sets paraentClassLoader for this classLoader
	 *
	 * @param classLoader parent class loader
	 */
	public void setParentClassLoader(ScriptClassLoader classLoader) {
		this.parentClassLoader = classLoader;
	}

	/**
	 * Adds library file. Library file must be a .jar archieve
	 *
	 * @param file link to jar archieve
	 * @throws IOException if something goes wrong
	 */
	public void addLibrary(File file) throws IOException {
		ScriptClassLoaderImpl classLoader = getClassLoader(null);
		classLoader.addJarFile(file);
	}

	/**
	 * Adds list of files as libraries. Files must be jar archieves
	 *
	 * @param files list of jar archives
	 * @throws IOException if something goes wrong
	 */
	public void addLibraries(Iterable<File> files) throws IOException {
		for (File f : files) {
			addLibrary(f);
		}
	}

	/**
	 * Returns list of classes that were compiled by compiler related to this ClassFileManager
	 *
	 * @return list of classes
	 */
	public Map<String, BinaryClass> getCompiledClasses() {
		return compiledClasses;
	}

	/**
	 * This method overrides class resolving procedure for compiler. It uses classloaders to resolve classes that compiler
	 * may need during compilation. Compiler by itself can't detect them. So we have to use this hack here. Hack is used
	 * only if compiler requests for classes in classpath.
	 *
	 * @param location    Location to search classes
	 * @param packageName package to scan for classes
	 * @param kinds       FileTypes to search
	 * @param recurse     not used
	 * @return list of requered files
	 * @throws IOException if something foes wrong
	 */
	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse)
			throws IOException {
		Iterable<JavaFileObject> objects = super.list(location, packageName, kinds, recurse);

		if (StandardLocation.CLASS_PATH.equals(location) && kinds.contains(Kind.CLASS)) {
			List<JavaFileObject> temp = new ArrayList<JavaFileObject>();
			for (JavaFileObject object : objects) {
				temp.add(object);
			}

			temp.addAll(loader.getClassesForPackage(packageName));
			objects = temp;
		}

		return objects;
	}


	@Override
	public String inferBinaryName(Location location, JavaFileObject file) {
		if (file instanceof BinaryClass) {
			return ((BinaryClass) file).getName();
		}

		return super.inferBinaryName(location, file);
	}
}
