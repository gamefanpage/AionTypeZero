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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

/**
 * This class is designed to simplify routine job with properties
 *
 * @author SoulKeeper
 */
public class PropertiesUtils {

	/**
	 * Loads properties by given file
	 *
	 * @param file filename
	 * @return loaded properties
	 * @throws java.io.IOException if can't load file
	 */
	public static Properties load(String file) throws IOException {
		return load(new File(file));
	}

	/**
	 * Loads properties by given file
	 *
	 * @param file filename
	 * @return loaded properties
	 * @throws java.io.IOException if can't load file
	 */
	public static Properties load(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		Properties p = new Properties();
		p.load(fis);
		fis.close();
		return p;
	}

	/**
	 * Loades properties from given files
	 *
	 * @param files list of string that represents files
	 * @return array of loaded properties
	 * @throws IOException if was unable to read properties
	 */
	public static Properties[] load(String... files) throws IOException {
		Properties[] result = new Properties[files.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = load(files[i]);
		}
		return result;
	}

	/**
	 * Loades properties from given files
	 *
	 * @param files list of files
	 * @return array of loaded properties
	 * @throws IOException if was unable to read properties
	 */
	public static Properties[] load(File... files) throws IOException {
		Properties[] result = new Properties[files.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = load(files[i]);
		}
		return result;
	}

	/**
	 * Loads non-recursively all .property files form directory
	 *
	 * @param dir string that represents directory
	 * @return array of loaded properties
	 * @throws IOException if was unable to read properties
	 */
	public static Properties[] loadAllFromDirectory(String dir) throws IOException {
		return loadAllFromDirectory(new File(dir), false);
	}

	/**
	 * Loads non-recursively all .property files form directory
	 *
	 * @param dir directory
	 * @return array of loaded properties
	 * @throws IOException if was unable to read properties
	 */
	public static Properties[] loadAllFromDirectory(File dir) throws IOException {
		return loadAllFromDirectory(dir, false);
	}

	/**
	 * Loads all .property files form directory
	 *
	 * @param dir       string that represents directory
	 * @param recursive parse subdirectories or not
	 * @return array of loaded properties
	 * @throws IOException if was unable to read properties
	 */
	public static Properties[] loadAllFromDirectory(String dir, boolean recursive) throws IOException {
		return loadAllFromDirectory(new File(dir), recursive);
	}

	/**
	 * Loads all .property files form directory
	 *
	 * @param dir       directory
	 * @param recursive parse subdirectories or not
	 * @return array of loaded properties
	 * @throws IOException if was unable to read properties
	 */
	public static Properties[] loadAllFromDirectory(File dir, boolean recursive) throws IOException {
		Collection<File> files = FileUtils.listFiles(dir, new String[]{"properties"}, recursive);
		return load(files.toArray(new File[files.size()]));
	}

	/**
	 * All initial properties will be overriden with properties supplied as second argument
	 *
	 * @param initialProperties to be overriden
	 * @param properties
	 * @return merged properties
	 */
	public static Properties[] overrideProperties(Properties[] initialProperties, Properties[] properties) {
		if (properties != null) {
			for (Properties props : properties) {
				overrideProperties(initialProperties, props);
			}
		}
		return initialProperties;
	}

	/**
	 * All initial properties will be overriden with properties supplied as second argument
	 *
	 * @param initialProperties
	 * @param properties
	 * @return
	 */
	public static Properties[] overrideProperties(Properties[] initialProperties, Properties properties) {
		if (properties != null) {
			for (Properties initialProps : initialProperties) {
				initialProps.putAll(properties);
			}
		}
		return initialProperties;
	}
}
