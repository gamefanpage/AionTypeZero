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

package org.typezero.gameserver.utils.chathandlers;

import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.utils.ClassUtils;

import java.lang.reflect.Modifier;

/**
 * Created on: 12.09.2009 14:13:24
 *
 * @author Aquanox
 */
public class ChatCommandsLoader implements ClassListener {

	private ChatProcessor processor;

	public ChatCommandsLoader(ChatProcessor processor) {
		this.processor = processor;
	}

	@Override
	public void postLoad(Class<?>[] classes) {
		for (Class<?> c : classes) {
			if (!isValidClass(c))
				continue;
			Class<?> tmp = (Class<?>) c;
			if (tmp != null)
				try {
					processor.registerCommand((ChatCommand) tmp.newInstance());
				}
				catch (InstantiationException e) {
					e.printStackTrace();
				}
				catch (IllegalAccessException e) {
					e.printStackTrace();
				}
		}
		processor.onCompileDone();
	}

	@Override
	public void preUnload(Class<?>[] classes) {

	}

	public boolean isValidClass(Class<?> clazz) {
		final int modifiers = clazz.getModifiers();

		if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers))
			return false;

		if (!Modifier.isPublic(modifiers))
			return false;

		if (!ClassUtils.isSubclass(clazz, AdminCommand.class) && !ClassUtils.isSubclass(clazz, PlayerCommand.class)
			&& !ClassUtils.isSubclass(clazz, WeddingCommand.class))
			return false;
		return true;
	}
}
