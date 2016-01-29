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

package org.typezero.gameserver.model.gameobjects.player;

import java.util.HashMap;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

/**
 * Manages the asking of and responding to <tt>SM_QUESTION_WINDOW</tt>
 *
 * @author Ben
 */
public class ResponseRequester {

	private Player player;
	private HashMap<Integer, RequestResponseHandler> map = new HashMap<Integer, RequestResponseHandler>();
	private static Logger log = LoggerFactory.getLogger(ResponseRequester.class);

	public ResponseRequester(Player player) {
		this.player = player;
	}

	/**
	 * Adds this handler to this messageID, returns false if there already exists one
	 *
	 * @param messageId
	 *          ID of the request message
	 * @return true or false
	 */
	public synchronized boolean putRequest(int messageId, RequestResponseHandler handler) {
		if (map.containsKey(messageId))
			return false;

		map.put(messageId, handler);
		return true;
	}

	/**
	 * Responds to the given message ID with the given response Returns success
	 *
	 * @param messageId
	 * @param response
	 * @return Success
	 */
	public synchronized boolean respond(int messageId, int response) {
		RequestResponseHandler handler = map.get(messageId);
		if (handler != null) {
			map.remove(messageId);
			log.debug("RequestResponseHandler triggered for response code " + messageId + " from " + player.getName());
			handler.handle(player, response);
			return true;
		}
		return false;
	}

	/**
	 * Automatically responds 0 to all requests, passing the given player as the responder
	 */
	public synchronized void denyAll() {
		for (RequestResponseHandler handler : map.values()) {
			handler.handle(player, 0);
		}

		map.clear();
	}
}
