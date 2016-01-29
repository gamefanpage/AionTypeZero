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

package org.typezero.gameserver.model.templates.assemblednpc;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AssembledNpcTemplate")
public class AssembledNpcTemplate {
	@XmlAttribute(name = "nr")
	private int nr;
	@XmlAttribute(name = "routeId")
	private int routeId;
	@XmlAttribute(name = "mapId")
	private int mapId;
	@XmlAttribute(name = "liveTime")
	private int liveTime;
	@XmlElement(name = "assembled_part")
	private List<AssembledNpcPartTemplate> parts;

	public int getNr() {
		return nr;
	}

	public int getRouteId() {
		return routeId;
	}

	public int getMapId() {
		return mapId;
	}

	public int getLiveTime() {
		return liveTime;
	}

	public List<AssembledNpcPartTemplate> getAssembledNpcPartTemplates() {
		return parts;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "AssembledNpcPart")
	public static class AssembledNpcPartTemplate {

		@XmlAttribute(name = "npcId")
		private int npcId;
		@XmlAttribute(name = "staticId")
		private int staticId;

		public int getNpcId() {
			return npcId;
		}

		public int getStaticId() {
			return staticId;
		}
	}
}
