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

package com.aionemu.commons.versionning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author lord_rex
 */
public class Version {

	private static final Logger log = LoggerFactory.getLogger(Version.class);
	private String revision;
	private String date;
	private String branch;
	private String commitTime;

	public Version() {
	}

	public Version(Class<?> c) {
		loadInformation(c);
	}

	public void loadInformation(Class<?> c) {
		File jarName = null;
		try {
			jarName = Locator.getClassSource(c);
			JarFile jarFile = new JarFile(jarName);

			Attributes attrs = jarFile.getManifest().getMainAttributes();
			this.revision = getAttribute("Revision", attrs);
			this.date = getAttribute("Date", attrs);
			this.branch = getAttribute("Branch", attrs);
			this.commitTime = getAttribute("CommitTime", attrs);
		} catch (IOException e) {
			log.error("Unable to get Soft information\nFile name '" + (jarName == null ? "null" : jarName.getAbsolutePath())
					+ "' isn't a valid jar", e);
		}

	}

	public void transferInfo(String jarName, String type, File fileToWrite) {
		try {
			if (!fileToWrite.exists()) {
				log.error("Unable to Find File :" + fileToWrite.getName() + " Please Update your " + type);
				return;
			}
			// Open the JAR file
			JarFile jarFile = new JarFile("./" + jarName);
			// Get the manifest
			Manifest manifest = jarFile.getManifest();
			// Write the manifest to a file
			OutputStream fos = new FileOutputStream(fileToWrite);
			manifest.write(fos);
			fos.close();
		} catch (IOException e) {
			log.error("Error, " + e);
		}
	}

	public final String getRevision() {
		return revision;
	}

	public final String getDate() {
		return date;
	}

	public final String getBranch() {
		return branch;
	}

	public final String getCommitTime() {
		return commitTime;
	}

	private final String getAttribute(String attribute, Attributes attrs) {
		String date = attrs.getValue(attribute);
		return date != null ? date : "Unknown " + attribute;
	}
}
