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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import javolution.util.FastMap;

import org.typezero.gameserver.model.templates.npcshout.NpcShout;
import org.typezero.gameserver.model.templates.npcshout.ShoutEventType;
import org.typezero.gameserver.model.templates.npcshout.ShoutGroup;
import org.typezero.gameserver.model.templates.npcshout.ShoutList;

/**
 * @author Rolandas
 */

/**
 * <p>
 * Java class for anonymous complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shout_group" type="{}ShoutGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "shoutGroups" })
@XmlRootElement(name = "npc_shouts")
public class NpcShoutData {

	@XmlElement(name = "shout_group")
	protected List<ShoutGroup> shoutGroups;

	@XmlTransient
	private TIntObjectHashMap<FastMap<Integer, List<NpcShout>>> shoutsByWorldNpcs = new TIntObjectHashMap<FastMap<Integer, List<NpcShout>>>();

	@XmlTransient
	private int count = 0;

	public void afterUnmarshal(Unmarshaller u, Object parent) {
		for (ShoutGroup group : shoutGroups) {
			for (int i = group.getShoutNpcs().size() - 1; i >= 0; i--) {
				ShoutList shoutList = group.getShoutNpcs().get(i);
				int worldId = shoutList.getRestrictWorld();

				FastMap<Integer, List<NpcShout>> worldShouts = shoutsByWorldNpcs.get(worldId);
				if (worldShouts == null) {
					worldShouts = FastMap.newInstance();
					this.shoutsByWorldNpcs.put(worldId, worldShouts);
				}

				this.count += shoutList.getNpcShouts().size();
				for (int j = shoutList.getNpcIds().size() - 1; j >= 0; j--) {
					int npcId = shoutList.getNpcIds().get(j);
					List<NpcShout> shouts = new ArrayList<NpcShout>(shoutList.getNpcShouts());
					if (worldShouts.get(npcId) == null) {
						worldShouts.put(npcId, shouts);
					}
					else {
						worldShouts.get(npcId).addAll(shouts);
					}
					shoutList.getNpcIds().remove(j);
				}
				shoutList.getNpcShouts().clear();
				shoutList.makeNull();
				group.getShoutNpcs().remove(i);
			}
			group.makeNull();
		}
		this.shoutGroups.clear();
		this.shoutGroups = null;
	}

	public int size() {
		return this.count;
	}

	/**
	 * Get global npc shouts plus world specific shouts. Make sure to clean it after the use.
	 *
	 * @return null if not found
	 */
	public List<NpcShout> getNpcShouts(int worldId, int npcId) {
		FastMap<Integer, List<NpcShout>> worldShouts = shoutsByWorldNpcs.get(0);

		if (worldShouts == null || worldShouts.get(npcId) == null) {
			worldShouts = shoutsByWorldNpcs.get(worldId);
			if (worldShouts == null || worldShouts.get(npcId) == null)
				return null;
			return new ArrayList<NpcShout>(worldShouts.get(npcId));
		}

		List<NpcShout> npcShouts = new ArrayList<NpcShout>(worldShouts.get(npcId));
		worldShouts = shoutsByWorldNpcs.get(worldId);
		if (worldShouts == null || worldShouts.get(npcId) == null)
			return npcShouts;
		npcShouts.addAll(worldShouts.get(npcId));

		return npcShouts;
	}

	/**
	 * Lightweight check for shouts, doesn't use memory as {@link #getNpcShouts(int worldId, int npcId)})
	 */
	public boolean hasAnyShout(int worldId, int npcId) {
		FastMap<Integer, List<NpcShout>> worldShouts = shoutsByWorldNpcs.get(0);

		if (worldShouts == null || worldShouts.get(npcId) == null) {
			worldShouts = shoutsByWorldNpcs.get(worldId);
			if (worldShouts == null || worldShouts.get(npcId) == null)
				return false;
		}
		return true;
	}

	/**
	 * Lightweight check for shouts, doesn't use memory as {@link #getNpcShouts(int worldId, int npcId, ShoutEventType type, String pattern, int skillNo)})
	 */
	public boolean hasAnyShout(int worldId, int npcId, ShoutEventType type) {
		List<NpcShout> shouts = getNpcShouts(worldId, npcId);
		if (shouts == null)
			return false;

		for (NpcShout s : shouts) {
			if (s.getWhen() == type)
				return true;
		}
		return false;
	}

	/**
	 * Gets shouts for npc
	 *
	 * @param worldId
	 *          - npc World Id
	 * @param npcId
	 *          - npc Id
	 * @param type
	 *          - shout event type
	 * @param pattern
	 *          - specific pattern; if null, returns all
	 * @param skillNo
	 *          - specific skill number; if 0, returns all
	 */
	public List<NpcShout> getNpcShouts(int worldId, int npcId, ShoutEventType type, String pattern, int skillNo) {
		List<NpcShout> shouts = getNpcShouts(worldId, npcId);
		if (shouts == null)
			return null;

		List<NpcShout> result = new ArrayList<NpcShout>();
		for (NpcShout s : shouts) {
			if (s.getWhen() == type) {
				if (pattern != null && !pattern.equals(s.getPattern()))
					continue;
				if (skillNo != 0 && skillNo != s.getSkillNo())
					continue;
				result.add(s);
			}
		}
		shouts.clear();
		return result.size() > 0 ? result : null;
	}

}
