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
package com.aionemu.commons.database.dao;

import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.utils.ClassUtils;

import java.lang.reflect.Modifier;

/**
 * Utility class that loads all DAO's after script context initialization.<br>
 * DAO should be public, not abstract, not interface, must have default no-arg public constructor.
 *
 * @author SoulKeeper, Aquanox
 */
public class DAOLoader implements ClassListener {

	@SuppressWarnings("unchecked")
	@Override
	public void postLoad(Class<?>[] classes) {
		// Register DAOs
		for (Class<?> clazz : classes) {
			if (!isValidDAO(clazz))
				continue;

			try {
				DAOManager.registerDAO((Class<? extends DAO>) clazz);
			} catch (Exception e) {
				throw new Error("Can't register DAO class", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void preUnload(Class<?>[] classes) {
		// Unregister DAO's
		for (Class<?> clazz : classes) {
			if (!isValidDAO(clazz))
				continue;

			try {
				DAOManager.unregisterDAO((Class<? extends DAO>) clazz);
			} catch (Exception e) {
				throw new Error("Can't unregister DAO class", e);
			}
		}
	}

	/**
	 * @param clazz
	 * @return boolean
	 */
	public boolean isValidDAO(Class<?> clazz) {
		if (!ClassUtils.isSubclass(clazz, DAO.class))
			return false;

		final int modifiers = clazz.getModifiers();

		if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers))
			return false;

		if (!Modifier.isPublic(modifiers))
			return false;

		if (clazz.isAnnotationPresent(DisabledDAO.class))
			return false;

		return true;
	}
}
