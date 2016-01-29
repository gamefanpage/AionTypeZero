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

package org.typezero.gameserver.dataholders;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.model.templates.teleport.TeleporterTemplate;

/**
 * This is a container holding and serving all {@link NpcTemplate} instances.<br>
 * Briefly: Every {@link Npc} instance represents some class of NPCs among which each have the same id, name, items,
 * statistics. Data for such NPC class is defined in {@link NpcTemplate} and is uniquely identified by npc id.
 *
 * @author orz
 */
@XmlRootElement(name = "npc_teleporter")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeleporterData {

	@XmlElement(name = "teleporter_template")
	private List<TeleporterTemplate> tlist;

	/** A map containing all trade list templates */
	private TIntObjectHashMap<TeleporterTemplate> npctlistData = new TIntObjectHashMap<TeleporterTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (TeleporterTemplate template : tlist) {
			npctlistData.put(template.getTeleportId(), template);
		}
	}

	public int size() {
		return npctlistData.size();
	}

	public TeleporterTemplate getTeleporterTemplateByNpcId(int npcId) {
		for (TeleporterTemplate template : npctlistData.valueCollection()) {
			if (template.containNpc(npcId)) {
				return template;
			}
		}
		return null;
	}

	public TeleporterTemplate getTeleporterTemplateByTeleportId(int teleportId) {
		return npctlistData.get(teleportId);
	}
}
