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
import com.aionemu.commons.utils.ClassUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * This classloader is used to load script classes. <br>
 * <br>
 * Due to JavaCompiler limitations we have to keep list of available classes here.
 *
 * @author SoulKeeper
 */
public class ScriptClassLoaderImpl extends ScriptClassLoader {

	private static final Logger log = LoggerFactory.getLogger(ScriptClassLoaderImpl.class);

	/**
	 * ClassFileManager that is related to this ClassLoader
	 */
	private final ClassFileManager classFileManager;

	/**
	 * Creates new ScriptClassLoader with given ClassFileManger. <br>
	 * Parent ClassLoader is ClassLoader of current class: <pre>ScriptClassLoaderImpl.class.getClassLoader()</pre>
	 *
	 * @param classFileManager classFileManager of this classLoader
	 */
	ScriptClassLoaderImpl(ClassFileManager classFileManager) {
		super(new URL[]{}, ScriptClassLoaderImpl.class.getClassLoader());
		this.classFileManager = classFileManager;
	}

	/**
	 * Creates new ScriptClassLoader with given ClassFileManger and another classLoader as parent
	 *
	 * @param classFileManager classFileManager of this classLoader
	 * @param parent           parent classLoader
	 */
	ScriptClassLoaderImpl(ClassFileManager classFileManager, ClassLoader parent) {
		super(new URL[]{}, parent);
		this.classFileManager = classFileManager;
	}

	/**
	 * Returns ClassFileManager that is related to this ClassLoader
	 *
	 * @return classFileManager of this classLoader
	 */
	public ClassFileManager getClassFileManager() {
		return classFileManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getCompiledClasses() {
		Set<String> compiledClasses = classFileManager.getCompiledClasses().keySet();
		return Collections.unmodifiableSet(compiledClasses);
	}

	/**
	 * Returns list of classes that are members of a package
	 *
	 * @param packageName package to search for classes
	 * @return list of classes that are package members
	 * @throws IOException if was unable to load class
	 */
	public Set<JavaFileObject> getClassesForPackage(String packageName) throws IOException {
		Set<JavaFileObject> result = new HashSet<JavaFileObject>();

		// load parent
		ClassLoader parent = getParent();
		if (parent instanceof ScriptClassLoaderImpl) {
			ScriptClassLoaderImpl pscl = (ScriptClassLoaderImpl) parent;
			result.addAll(pscl.getClassesForPackage(packageName));
		}

		// load current classloader compiled classes
		for (String cn : classFileManager.getCompiledClasses().keySet()) {
			if (ClassUtils.isPackageMember(cn, packageName)) {
				BinaryClass bc = classFileManager.getCompiledClasses().get(cn);
				result.add(bc);
			}
		}

		// initialize set with class names, will be used to resolve classes
		Set<String> classNames = new HashSet<String>();

		// load package members from this classloader
		Enumeration<URL> urls = getResources(packageName.replace('.', '/'));
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			String path = URLDecoder.decode(url.getPath());
			if (new File(path).isDirectory()) {
				Set<String> packageClasses = ClassUtils.getClassNamesFromPackage(new File(path), packageName, false);
				classNames.addAll(packageClasses);
			} else if (path.toLowerCase().contains(".jar!")) {
				File file = new File(path);
				while (!file.getName().toLowerCase().endsWith(".jar!")) {
					file = file.getParentFile();
				}
				path = file.getPath().substring(0, file.getPath().length() - 1);
				path = path.replace('\\', '/');
				path = path.substring(path.indexOf(":") + 1);
				file = new File(path);
				// add jar file as library. Actually it's doesn't matter if we have it as library
				// or as file in class path
				addJarFile(file);
			}
		}

		// add library class names from this classloader to available classes
		classNames.addAll(getLibraryClassNames());

		// load classes for class names from this classloader
		for (String cn : classNames) {
			if (ClassUtils.isPackageMember(cn, packageName)) {
				BinaryClass bc = new BinaryClass(cn);
				try {
					byte[] data = getRawClassByName(cn);
					OutputStream os = bc.openOutputStream();
					os.write(data);
				} catch (IOException e) {
					log.error("Error while loading class from package " + packageName, e);
					throw e;
				}
				result.add(bc);
			}
		}

		return result;
	}

	/**
	 * Finds class with the specified name from the URL search path. Any URLs referring to JAR files are loaded and opened
	 * as needed until the class is found.
	 *
	 * @param name the name of the class
	 * @return the resulting class data
	 * @throws IOException              if failed to load class
	 * @throws IllegalArgumentException if failed to open input stream for class
	 */
	protected byte[] getRawClassByName(String name) throws IOException {
		String resourceName = name.replace('.', '/').concat(".class");
		URL resource = getResource(resourceName);
		InputStream is = null;
		byte[] clazz = null;

		try {
			is = resource.openStream();
			clazz = IOUtils.toByteArray(is);
		} catch (IOException e) {
			log.error("Error while loading class data: " + name, e);
			throw e;
		} catch (NullPointerException e) {
			log.error("Can't open input stream for resource: " + name);
			throw new IllegalArgumentException("Failed to open input stream for resource: " + name);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error("Error while closing stream", e);
				}
			}
		}
		return clazz;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getByteCode(String className) {
		BinaryClass bc = getClassFileManager().getCompiledClasses().get(className);
		byte[] b = new byte[bc.getBytes().length];
		System.arraycopy(bc.getBytes(), 0, b, 0, b.length);
		return b;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getDefinedClass(String name) {
		BinaryClass bc = classFileManager.getCompiledClasses().get(name);
		if (bc == null) {
			return null;
		}

		return bc.getDefinedClass();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDefinedClass(String name, Class<?> clazz) {
		BinaryClass bc = classFileManager.getCompiledClasses().get(name);

		if (bc == null) {
			throw new IllegalArgumentException("Attempt to set defined class for class that was not compiled?");
		}

		bc.setDefinedClass(clazz);
	}
}
