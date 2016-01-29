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
package com.aionemu.commons.scripting.impl;

import com.aionemu.commons.scripting.CompilationResult;
import com.aionemu.commons.scripting.ScriptCompiler;
import com.aionemu.commons.scripting.ScriptContext;
import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is actual implementation of {@link com.aionemu.commons.scripting.ScriptContext}
 *
 * @author SoulKeeper
 */
public class ScriptContextImpl implements ScriptContext {

	/**
	 * logger for this class
	 */
	private static final Logger log = LoggerFactory.getLogger(ScriptContextImpl.class);

	/**
	 * Script context that is parent for this script context
	 */
	private final ScriptContext parentScriptContext;

	/**
	 * Libraries (list of jar files) that have to be loaded class loader
	 */
	private Iterable<File> libraries;

	/**
	 * Root directory of this script context. It and it's subdirectories will be scanned for .java files.
	 */
	private final File root;

	/**
	 * Result of compilation of script context
	 */
	private CompilationResult compilationResult;

	/**
	 * List of child script contexts
	 */
	private Set<ScriptContext> childScriptContexts;

	/**
	 * Classlistener for this script context
	 */
	private ClassListener classListener;

	/**
	 * Class name of the compiler that will be used to compile sources
	 */
	private String compilerClassName;

	/**
	 * Creates new scriptcontext with given root file
	 *
	 * @param root file that represents root directory of this script context
	 * @throws NullPointerException     if root is null
	 * @throws IllegalArgumentException if root directory doesn't exists or is not a directory
	 */
	public ScriptContextImpl(File root) {
		this(root, null);
	}

	/**
	 * Creates new ScriptContext with given file as root and another ScriptContext as parent
	 *
	 * @param root   file that represents root directory of this script context
	 * @param parent parent ScriptContex. It's classes and libraries will be accessible for this script context
	 * @throws NullPointerException     if root is null
	 * @throws IllegalArgumentException if root directory doesn't exists or is not a directory
	 */
	public ScriptContextImpl(File root, ScriptContext parent) {
		if (root == null) {
			throw new NullPointerException("Root file must be specified");
		}

		if (!root.exists() || !root.isDirectory()) {
			throw new IllegalArgumentException("Root directory not exists or is not a directory");
		}

		this.root = root;
		this.parentScriptContext = parent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void init() {

		if (compilationResult != null) {
			log.error("Init request on initialized ScriptContext");
			return;
		}

		ScriptCompiler scriptCompiler = instantiateCompiler();

		Collection<File> files = FileUtils.listFiles(root, scriptCompiler.getSupportedFileTypes(), true);

		if (parentScriptContext != null) {
			scriptCompiler.setParentClassLoader(parentScriptContext.getCompilationResult().getClassLoader());
		}

		scriptCompiler.setLibraires(libraries);
		compilationResult = scriptCompiler.compile(files);

		getClassListener().postLoad(compilationResult.getCompiledClasses());

		if (childScriptContexts != null) {
			for (ScriptContext context : childScriptContexts) {
				context.init();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void shutdown() {

		if (compilationResult == null) {
			log.error("Shutdown of not initialized stript context", new Exception());
			return;
		}

		if (childScriptContexts != null) {
			for (ScriptContext child : childScriptContexts) {
				child.shutdown();
			}
		}

		getClassListener().preUnload(compilationResult.getCompiledClasses());
		compilationResult = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() {
		shutdown();
		init();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getRoot() {
		return root;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompilationResult getCompilationResult() {
		return compilationResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized boolean isInitialized() {
		return compilationResult != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLibraries(Iterable<File> files) {
		this.libraries = files;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<File> getLibraries() {
		return libraries;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScriptContext getParentScriptContext() {
		return parentScriptContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<ScriptContext> getChildScriptContexts() {
		return childScriptContexts;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addChildScriptContext(ScriptContext context) {

		synchronized (this) {
			if (childScriptContexts == null) {
				childScriptContexts = new HashSet<ScriptContext>();
			}

			if (childScriptContexts.contains(context)) {
				log.error("Double child definition, root: " + root.getAbsolutePath() + ", child: "
						+ context.getRoot().getAbsolutePath());
				return;
			}

			if (isInitialized()) {
				context.init();
			}
		}

		childScriptContexts.add(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setClassListener(ClassListener cl) {
		classListener = cl;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClassListener getClassListener() {
		if (classListener == null) {
			if (getParentScriptContext() == null) {
				AggregatedClassListener acl = new AggregatedClassListener();
				acl.addClassListener(new OnClassLoadUnloadListener());
				acl.addClassListener(new ScheduledTaskClassListener());
				setClassListener(acl);
				return classListener;
			}
			return getParentScriptContext().getClassListener();
		}
		return classListener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCompilerClassName(String className) {
		this.compilerClassName = className;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCompilerClassName() {
		return this.compilerClassName;
	}

	/**
	 * Creates new instance of ScriptCompiler that should be used with this ScriptContext
	 *
	 * @return instance of ScriptCompiler
	 * @throws RuntimeException if failed to create instance
	 */
	protected ScriptCompiler instantiateCompiler() throws RuntimeException {
		ClassLoader cl = getClass().getClassLoader();
		if (getParentScriptContext() != null) {
			cl = getParentScriptContext().getCompilationResult().getClassLoader();
		}

		ScriptCompiler sc;
		try {
			sc = (ScriptCompiler) Class.forName(getCompilerClassName(), true, cl).newInstance();
		} catch (Exception e) {
			log.error("Can't create instance of compiler");
			throw new RuntimeException(e);
		}

		return sc;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ScriptContextImpl)) {
			return false;
		}

		ScriptContextImpl another = (ScriptContextImpl) obj;

		if (parentScriptContext == null) {
			return another.getRoot().equals(root);
		}
		return another.getRoot().equals(root) && parentScriptContext.equals(another.parentScriptContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int result = parentScriptContext != null ? parentScriptContext.hashCode() : 0;
		result = 31 * result + root.hashCode();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finalize() throws Throwable {
		if (compilationResult != null) {
			log.error("Finalization of initialized ScriptContext. Forcing context shutdown.");
			shutdown();
		}
		super.finalize();
	}
}
