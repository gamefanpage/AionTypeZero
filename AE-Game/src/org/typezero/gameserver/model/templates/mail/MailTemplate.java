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

package org.typezero.gameserver.model.templates.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang.StringUtils;

import org.typezero.gameserver.model.Race;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MailTemplate")
public class MailTemplate {

	@XmlElements({
		@XmlElement(name = "sender", type = Sender.class),
		@XmlElement(name = "title", type = Title.class),
		@XmlElement(name = "header", type = Header.class),
		@XmlElement(name = "body", type = Body.class),
		@XmlElement(name = "tail", type = Tail.class) })
	private List<MailPart> mailParts;

	@XmlAttribute(name = "name", required = true)
	protected String name;

	@XmlAttribute(name = "race", required = true)
	protected Race race;

	@XmlTransient
	private Map<MailPartType, MailPart> mailPartsMap = new HashMap<MailPartType, MailPart>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (MailPart part : mailParts) {
			mailPartsMap.put(((IMailFormatter) part).getType(), part);
		}
		mailParts.clear();
		mailParts = null;
	}

	public MailPart getSender() {
		return mailPartsMap.get(MailPartType.SENDER);
	}

	public MailPart getTitle() {
		return mailPartsMap.get(MailPartType.TITLE);
	}

	public MailPart getHeader() {
		return mailPartsMap.get(MailPartType.HEADER);
	}

	public MailPart getBody() {
		return mailPartsMap.get(MailPartType.BODY);
	}

	public MailPart getTail() {
		return mailPartsMap.get(MailPartType.TAIL);
	}

	public String getName() {
		return name;
	}

	public Race getRace() {
		return race;
	}

	public String getFormattedTitle(IMailFormatter customFormatter) {
		return getTitle().getFormattedString(customFormatter);
	}

	public String getFormattedMessage(IMailFormatter customFormatter) {
		String headerStr = getHeader().getFormattedString(customFormatter);
		String bodyStr = getBody().getFormattedString(customFormatter);
		String tailStr = getTail().getFormattedString(customFormatter);
		String message = headerStr;
		if (StringUtils.isEmpty(message))
			message = bodyStr;
		else if (!StringUtils.isEmpty(bodyStr)) {
			message += "," + bodyStr;
		}
		if (StringUtils.isEmpty(message))
			message = tailStr;
		else if (!StringUtils.isEmpty(tailStr)) {
			message += "," + tailStr;
		}
		return message;
	}

}
