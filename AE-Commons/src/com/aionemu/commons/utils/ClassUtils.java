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
package com.aionemu.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class contains utilities that are used when we are working with classes
 *
 * @author SoulKeeper
 */
public class ClassUtils {

	private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);

	/**
	 * Return true if class a is either equivalent to class b, or if class a is a subclass of class b, i.e. if a either
	 * "extends" or "implements" b. Note tht either or both "Class" objects may represent interfaces.
	 *
	 * @param a class
	 * @param b class
	 * @return true if a == b or a extends b or a implements b
	 */
	public static boolean isSubclass(Class<?> a, Class<?> b) {
		// We rely on the fact that for any given java class or
		// primtitive type there is a unqiue Class object, so
		// we can use object equivalence in the comparisons.
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		for (Class<?> x = a; x != null; x = x.getSuperclass()) {
			if (x == b) {
				return true;
			}
			if (b.isInterface()) {
				Class<?>[] interfaces = x.getInterfaces();
				for (Class<?> anInterface : interfaces) {
					if (isSubclass(anInterface, b)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks if class in member of the package
	 *
	 * @param clazz       class to check
	 * @param packageName package
	 * @return true if is member
	 */
	public static boolean isPackageMember(Class<?> clazz, String packageName) {
		return isPackageMember(clazz.getName(), packageName);
	}

	/**
	 * Checks if classNames belongs to package
	 *
	 * @param className   class name
	 * @param packageName package
	 * @return true if belongs
	 */
	public static boolean isPackageMember(String className, String packageName) {
		if (!className.contains(".")) {
			return packageName == null || packageName.isEmpty();
		}
		String classPackage = className.substring(0, className.lastIndexOf('.'));
		return packageName.equals(classPackage);
	}

	/**
	 * Returns class names from directory.
	 *
	 * @param directory folder with class files
	 * @return Set of fully qualified class names
	 * @throws IllegalArgumentException if specified file is not directory or does not exists
	 * @throws NullPointerException     if directory is null
	 */
	public static Set<String> getClassNamesFromDirectory(File directory) throws IllegalArgumentException {

		if (!directory.isDirectory() || !directory.exists()) {
			throw new IllegalArgumentException("Directory " + directory + " doesn't exists or is not directory");
		}

		return getClassNamesFromPackage(directory, null, true);
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @param recursive   include subpackages or not
	 * @return The classes
	 */
	public static Set<String> getClassNamesFromPackage(File directory, String packageName, boolean recursive) {
		Set<String> classes = new HashSet<String>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {

				if (!recursive) {
					continue;
				}

				String newPackage = file.getName();
				if (!GenericValidator.isBlankOrNull(packageName)) {
					newPackage = packageName + "." + newPackage;
				}
				classes.addAll(getClassNamesFromPackage(file, newPackage, recursive));
			} else if (file.getName().endsWith(".class")) {
				String className = file.getName().substring(0, file.getName().length() - 6);
				if (!GenericValidator.isBlankOrNull(packageName)) {
					className = packageName + "." + className;
				}
				classes.add(className);
			}
		}
		return classes;
	}

	/**
	 * Method that returns all class file names from given jar file
	 *
	 * @param file jar file
	 * @return class names from jar file
	 * @throws IOException              if something went wrong
	 * @throws IllegalArgumentException if file doesn't exists or is not jar file
	 * @throws NullPointerException     if file is null
	 */
	public static Set<String> getClassNamesFromJarFile(File file) throws IOException {

		if (!file.exists() || file.isDirectory()) {
			throw new IllegalArgumentException("File " + file + " is not valid jar file");
		}

		Set<String> result = new HashSet<String>();

		JarFile jarFile = null;
		try {
			jarFile = new JarFile(file);

			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();

				String name = entry.getName();
				if (name.endsWith(".class")) {
					name = name.substring(0, name.length() - 6);
					name = name.replace('/', '.');
					result.add(name);
				}
			}
		} finally {
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) {
					log.error("Failed to close jar file " + jarFile.getName(), e);
				}
			}
		}

		return result;
	}
}
