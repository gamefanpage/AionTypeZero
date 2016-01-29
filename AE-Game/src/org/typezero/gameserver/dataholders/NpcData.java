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
import org.typezero.gameserver.model.TribeClass;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.utils.PacketSendUtility;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.File;
import java.util.*;

/**
 * This is a container holding and serving all {@link NpcTemplate} instances.<br>
 * Briefly: Every {@link Npc} instance represents some class of NPCs among which each have the same id, name, items,
 * statistics. Data for such NPC class is defined in {@link NpcTemplate} and is uniquely identified by npc id.
 *
 * @author Luno
 */
@XmlRootElement(name = "npc_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class NpcData extends ReloadableData {

	@XmlElement(name = "npc_template")
	private List<NpcTemplate> npcs;

	/** A map containing all npc templates */
	@XmlTransient
	private TIntObjectHashMap<NpcTemplate> npcData = new TIntObjectHashMap<NpcTemplate>();

	@XmlTransient
	private HashSet<TribeClass> unusedTribes = new HashSet<TribeClass>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		unusedTribes.addAll(Arrays.asList(TribeClass.values()));
		for (NpcTemplate npc : npcs) {
			npcData.put(npc.getTemplateId(), npc);
			if (npc.getTribe() != null)
				unusedTribes.remove(npc.getTribe());
		}
		npcs.clear();
		npcs = null;

		Iterator<TribeClass> iter = unusedTribes.iterator();
		if (unusedTribes.size() > 0) {
			while (iter.hasNext())
				iter.next().setUsed(false);
			unusedTribes.clear();
		}
		unusedTribes = null;
	}

	public int size() {
		return npcData.size();
	}

	/**
	 * /** Returns an {@link NpcTemplate} object with given id.
	 *
	 * @param id
	 *          id of NPC
	 * @return NpcTemplate object containing data about NPC with that id.
	 */
	public NpcTemplate getNpcTemplate(int id) {
		return npcData.get(id);
	}

	/**
	 * @return the npcData
	 */
	public TIntObjectHashMap<NpcTemplate> getNpcData() {
		return npcData;
	}

	@Override
	public void reload(Player admin) {
		File dir = new File("./data/static_data/npcs");
		try {
			JAXBContext jc = JAXBContext.newInstance(StaticData.class);
			Unmarshaller un = jc.createUnmarshaller();
			un.setSchema(getSchema("./data/static_data/static_data.xsd"));
			List<NpcTemplate> newTemplates = new ArrayList<NpcTemplate>();
			for (File file : listFiles(dir, true)) {
				NpcData data = (NpcData) un.unmarshal(file);
				if (data != null && data.getData() != null)
					newTemplates.addAll(data.getData());
			}
			DataManager.NPC_DATA.setData(newTemplates);
		}
		catch (Exception e) {
			PacketSendUtility.sendMessage(admin, "Npc reload failed!");
			log.error("Npc reload failed!", e);
		}
		finally {
			PacketSendUtility.sendMessage(admin, "Npc reload Success! Total loaded: " + DataManager.NPC_DATA.size());
		}
	}

	protected List<NpcTemplate> getData() {
		return npcs;
	}

	@SuppressWarnings("unchecked")
	protected void setData(List<?> templates) {
		this.npcs = (List<NpcTemplate>) templates;
		afterUnmarshal(null, null);
	}
}
