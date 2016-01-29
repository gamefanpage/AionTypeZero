/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 * <p/>
 * Aion-Lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * <p/>
 * Aion-Lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. *
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with Aion-Lightning.
 * If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * <p/>
 * Credits goes to all Open Source Core Developer Groups listed below
 * Please do not change here something, ragarding the developer credits, except the "developed by XXXX".
 * Even if you edit a lot of files in this source, you still have no rights to call it as "your Core".
 * Everybody knows that this Emulator Core was developed by Aion Lightning
 *
 * @-Aion-Unique-
 * @-Aion-Lightning
 * @Aion-Engine
 * @Aion-Extreme
 * @Aion-NextGen
 * @Aion-Core Dev.
 */
package org.typezero.chatserver.model.message;

import org.typezero.chatserver.model.ChatClient;
import org.typezero.chatserver.model.channel.Channel;

import java.io.UnsupportedEncodingException;

/**
 * @author ATracer
 */
public class Message {

	private Channel channel;
	private byte[] text;
	private ChatClient sender;

	/**
	 * @param channel
	 * @param text
	 */
	public Message(Channel channel, byte[] text, ChatClient sender) {
		this.channel = channel;
		this.text = text;
		this.sender = sender;
	}

	/**
	 * @return the channel
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * @return the text
	 */
	public byte[] getText() {
		return text;
	}

	public void setText(String str) {
		try {
			this.text = str.getBytes("utf-16le");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public int size() {
		return text.length;
	}

	/**
	 * @return the sender
	 */
	public ChatClient getSender() {
		return sender;
	}

	public String getSenderString() {
		try {
			String s = new String(sender.getIdentifier(), "UTF-16le");
			int pos = s.indexOf('@');
			s = s.substring(0, pos);
			return s;
		} catch (Exception e) {
			return "";
		}
	}

	public String getTextString() {
		try {
			String s = new String(text, "UTF-16le");
			return s;
		} catch (Exception e) {
			return "";
		}
	}

	public String getChannelString() {
		return channel.getChannelType().name();
	}
}
