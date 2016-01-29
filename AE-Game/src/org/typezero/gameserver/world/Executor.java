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

package org.typezero.gameserver.world;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.model.gameobjects.AionObject;
import org.typezero.gameserver.utils.ThreadPoolManager;

import java.util.Collection;

/**
 * @author xavier
 */
public abstract class Executor<T extends AionObject> {

	private static final Logger log = LoggerFactory.getLogger(Executor.class);

	public abstract boolean run(T object);

	private final void runImpl(Collection<T> objects) {
		try {
			for (T o : objects) {
				if (o != null) {
					if (!Executor.this.run(o))
						break;
				}
			}
		}
		catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}

	public final void execute(final Collection<T> objects, boolean now) {
		if (now) {
			runImpl(objects);
		}
		else {
			ThreadPoolManager.getInstance().execute(new Runnable() {

				@Override
				public void run() {
					runImpl(objects);
				}
			});
		}
	}

	public final void execute(final Collection<T> objects) {
		execute(objects, false);
	}
}
