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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import java.util.Locale;

/**
 * This class is simple compiler error listener that forwards errors to log4j logger
 *
 * @author SoulKeeper
 */
public class ErrorListener implements DiagnosticListener<JavaFileObject> {

	private static final Logger log = LoggerFactory.getLogger(ErrorListener.class);

	/**
	 * Reports compilation errors to log4j
	 *
	 * @param diagnostic compiler errors
	 */
	@Override
	public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
		StringBuilder sb = new StringBuilder();
		sb.append("Java Compiler ");
		sb.append(diagnostic.getKind());
		sb.append(": ");
		sb.append(diagnostic.getMessage(Locale.ENGLISH));
		if (diagnostic.getSource() != null) {
			sb.append("\n");
			sb.append("Source: ");
			sb.append(diagnostic.getSource().getName());
			sb.append("\n");
			sb.append("Line: ");
			sb.append(diagnostic.getLineNumber());
			sb.append("\n");
			sb.append("Column: ");
			sb.append(diagnostic.getColumnNumber());
		}
		log.error(sb.toString());
	}
}
