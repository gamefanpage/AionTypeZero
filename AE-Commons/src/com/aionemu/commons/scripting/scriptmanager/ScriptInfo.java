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
package com.aionemu.commons.scripting.scriptmanager;

import javax.xml.bind.annotation.*;
import java.io.File;
import java.util.List;

/**
 * Simple class that represents script info.<br>
 * <br>
 * It contains Script root, list of libraries and list of child contexes
 *
 * @author SoulKeeper
 */
@XmlRootElement(name = "scriptinfo")
@XmlAccessorType(XmlAccessType.NONE)
public class ScriptInfo {

	/**
	 * Root of this script context. Child directories of root will be scanned for script files
	 */
	@XmlAttribute(required = true)
	private File root;

	/**
	 * List of libraries of this script context
	 */
	@XmlElement(name = "library")
	private List<File> libraries;

	/**
	 * List of child contexts
	 */
	@XmlElement(name = "scriptinfo")
	private List<ScriptInfo> scriptInfos;

	/**
	 * Default compiler class name.
	 */
	@XmlElement(name = "compiler")
	private String compilerClass = ScriptManager.DEFAULT_COMPILER_CLASS.getName();

	/**
	 * Returns root of script context
	 *
	 * @return root of script context
	 */
	public File getRoot() {
		return root;
	}

	/**
	 * Sets root for script context
	 *
	 * @param root root for script context
	 */
	public void setRoot(File root) {
		this.root = root;
	}

	/**
	 * Returns list of libraries that will be used byscript context and it's children
	 *
	 * @return lib of libraries
	 */
	public List<File> getLibraries() {
		return libraries;
	}

	/**
	 * Sets list of libraries that will be used by script context and it's children
	 *
	 * @param libraries sets list of libraries
	 */
	public void setLibraries(List<File> libraries) {
		this.libraries = libraries;
	}

	/**
	 * Return list of child context descriptors
	 *
	 * @return list of child context descriptors
	 */
	public List<ScriptInfo> getScriptInfos() {
		return scriptInfos;
	}

	/**
	 * Sets list of child context descriptors
	 *
	 * @param scriptInfos list of child context descriptors
	 */
	public void setScriptInfos(List<ScriptInfo> scriptInfos) {
		this.scriptInfos = scriptInfos;
	}

	/**
	 * Returns compiler class name
	 *
	 * @return name of compiler class
	 */
	public String getCompilerClass() {
		return compilerClass;
	}

	/**
	 * Sets compiler class name
	 *
	 * @param compilerClass name of compiler class
	 */
	public void setCompilerClass(String compilerClass) {
		this.compilerClass = compilerClass;
	}

	/**
	 * Returns true if roots are quals
	 *
	 * @param o object to compare with
	 * @return true if this ScriptInfo and anothers ScriptInfo has same root
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ScriptInfo that = (ScriptInfo) o;

		return root.equals(that.root);

	}

	/**
	 * Returns hashcode of root
	 *
	 * @return hashcode of root
	 */
	@Override
	public int hashCode() {
		return root.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("ScriptInfo");
		sb.append("{root=").append(root);
		sb.append(", libraries=").append(libraries);
		sb.append(", compilerClass='").append(compilerClass).append('\'');
		sb.append(", scriptInfos=").append(scriptInfos);
		sb.append('}');
		return sb.toString();
	}
}
