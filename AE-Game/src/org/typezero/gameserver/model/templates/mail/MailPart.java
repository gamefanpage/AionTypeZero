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

import javax.xml.bind.annotation.*;

import org.apache.commons.lang.StringUtils;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MailPart")
@XmlSeeAlso({ Sender.class, Header.class, Body.class, Tail.class, Title.class })
public abstract class MailPart extends StringParamList implements IMailFormatter {

	@XmlAttribute(name = "id")
	protected Integer id;

	@Override
	public MailPartType getType() {
		return MailPartType.CUSTOM;
	}

	public Integer getId() {
		return id;
	}

	public String getFormattedString(IMailFormatter customFormatter) {
		String result = "";
		IMailFormatter formatter = this;
		if (customFormatter != null) {
			formatter = customFormatter;
		}

		result = getFormattedString(getType());

		String[] paramValues = new String[getParam().size()];
		for (int i = 0; i < getParam().size(); i++) {
			Param param = getParam().get(i);
			paramValues[i] = formatter.getParamValue(param.getId());
		}
		String joinedParams = StringUtils.join(paramValues, ',');
		if (StringUtils.isEmpty(result))
			return joinedParams;
		else if (!StringUtils.isEmpty(joinedParams))
			result += "," + joinedParams;

		return result;
	}

	public String getFormattedString(MailPartType partType) {
		String result = "";
		if (id > 0)
			result += id.toString();
		return result;
	}

}
