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

package com.aionemu.commons.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * This is implementation of <code>Dispatcher</code> that may accept connections, read and write data.
 *
 * @author -Nemesiss-
 * @see com.aionemu.commons.network.Dispatcher
 * @see java.nio.channels.Selector
 */
public class AcceptReadWriteDispatcherImpl extends Dispatcher {

	/**
	 * List of connections that should be closed by this <code>Dispatcher</code> as soon as possible.
	 */
	private final List<AConnection> pendingClose = new ArrayList<AConnection>();

	/**
	 * Constructor that accept <code>String</code> name and <code>DisconnectionThreadPool</code> dcPool as parameter.
	 *
	 * @param name
	 * @param dcPool
	 * @throws IOException
	 * @see com.aionemu.commons.network.DisconnectionThreadPool
	 */
	public AcceptReadWriteDispatcherImpl(String name, Executor dcPool) throws IOException {
		super(name, dcPool);
	}

	/**
	 * Process Pending Close connections and then dispatch <code>Selector</code> selected-key set.
	 *
	 * @see com.aionemu.commons.network.Dispatcher#dispatch()
	 */
	@Override
	void dispatch() throws IOException {
		int selected = selector.select();

		processPendingClose();

		if (selected != 0) {
			Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
			while (selectedKeys.hasNext()) {
				SelectionKey key = selectedKeys.next();
				selectedKeys.remove();

				if (!key.isValid())
					continue;

				/** Check what event is available and deal with it */
				switch (key.readyOps()) {
					case SelectionKey.OP_ACCEPT:
						this.accept(key);
						break;
					case SelectionKey.OP_READ:
						this.read(key);
						break;
					case SelectionKey.OP_WRITE:
						this.write(key);
						break;
					case SelectionKey.OP_READ | SelectionKey.OP_WRITE:
						this.read(key);
						if (key.isValid())
							this.write(key);
						break;
				}
			}
		}
	}

	/**
	 * Add connection to pendingClose list, so this connection will be closed by this <code>Dispatcher</code> as soon as
	 * possible.
	 *
	 * @see com.aionemu.commons.network.Dispatcher#closeConnection(com.aionemu.commons.network.AConnection)
	 */
	@Override
	void closeConnection(AConnection con) {
		synchronized (pendingClose) {
			pendingClose.add(con);
		}
	}

	/**
	 * Process Pending Close connections.
	 */
	private void processPendingClose() {
		synchronized (pendingClose) {
			for (AConnection connection : pendingClose)
				closeConnectionImpl(connection);
			pendingClose.clear();
		}
	}
}
