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

package org.typezero.gameserver.services;

import org.typezero.gameserver.configs.main.NameConfig;

/**
 *
 * @author nrg
 */
public class NameRestrictionService {

        private static final String ENCODED_BAD_WORD = "----";
        private static String[] forbiddenSequences;
        private static String[] forbiddenByClient;

        /**
	 * Checks if a name is valid. It should contain only english letters
	 *
	 * @param character
	 *          name
	 * @return true if name is valid, false otherwise
	 */
	public static boolean isValidName(String name) {
		return NameConfig.CHAR_NAME_PATTERN.matcher(name).matches();
	}

    	/**
	 * Checks if a name is forbidden
	 *
	 * @param name
	 * @return true if name is forbidden
	 */
	public static boolean isForbiddenWord(String name) {
		return isForbiddenByClient(name) || isForbiddenBySequence(name);
	}

	/**
	 * Checks if a name is forbidden (contains string sequences from config)
	 *
	 * @param name
	 * @return true if name is forbidden
	 */
        private static boolean isForbiddenByClient(String name) {
        	if(!NameConfig.NAME_FORBIDDEN_ENABLE || NameConfig.NAME_FORBIDDEN_CLIENT.equals(""))
                    return false;

                if(forbiddenByClient == null || forbiddenByClient.length == 0)
                    forbiddenByClient = NameConfig.NAME_FORBIDDEN_CLIENT.split(",");

                for(String s : forbiddenByClient) {
                    if(name.equalsIgnoreCase(s))
                        return true;
                }
                return false;
        }

	/**
	 * Checks if a name is forbidden (contains string sequences from config)
	 *
	 * @param name
	 * @return true if name is forbidden
	 */
        private static boolean isForbiddenBySequence(String name) {
                if(NameConfig.NAME_SEQUENCE_FORBIDDEN.equals(""))
                    return false;

                if(forbiddenSequences == null || forbiddenSequences.length == 0)
                    forbiddenSequences = NameConfig.NAME_SEQUENCE_FORBIDDEN.toLowerCase().split(",");

                for(String s : forbiddenSequences) {
                    if(name.toLowerCase().contains(s))
                        return true;
                }
                return false;
        }

        /**
         * Filters chatmessages
         * @param message
         * @return
         */
        public static String filterMessage(String message) {
                for (String word : message.split(" ")) {
                    if (isForbiddenWord(word))
						message.replace(word, ENCODED_BAD_WORD);
                }
				return message;
        }
}
