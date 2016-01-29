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

import java.io.File;

/**
 * This interface reperesents common functionality list that should be available for any commpiler that is going to be
 * used with scripting engine. For instance, groovy can be used, hoever it produces by far not the best bytecode so by
 * default javac from sun is used.
 *
 * @author SoulKeeper
 */
public interface ScriptCompiler {

	/**
	 * Sets parent class loader for this compiler.<br>
	 * <br>
	 * <font color="red">Warning, for now only</font>
	 *
	 * @param classLoader ScriptClassLoader that will be used as parent
	 */
	public void setParentClassLoader(ScriptClassLoader classLoader);

	/**
	 * List of jar files that are required for compilation
	 *
	 * @param files list of jar files
	 */
	public void setLibraires(Iterable<File> files);

	/**
	 * Compiles single class that is represented as string
	 *
	 * @param className  class name
	 * @param sourceCode class sourse code
	 * @return {@link com.aionemu.commons.scripting.CompilationResult}
	 */
	public CompilationResult compile(String className, String sourceCode);

	/**
	 * Compiles classes that are represented as strings
	 *
	 * @param className  class names
	 * @param sourceCode class sources
	 * @return {@link com.aionemu.commons.scripting.CompilationResult}
	 * @throws IllegalArgumentException if number of class names != number of sources
	 */
	public CompilationResult compile(String[] className, String[] sourceCode) throws IllegalArgumentException;

	/**
	 * Compiles list of files
	 *
	 * @param compilationUnits list of files
	 * @return {@link com.aionemu.commons.scripting.CompilationResult}
	 */
	public CompilationResult compile(Iterable<File> compilationUnits);

	/**
	 * Returns array of supported file types. This files will be threated as source files.
	 *
	 * @return array of supported file types.
	 */
	public String[] getSupportedFileTypes();
}
