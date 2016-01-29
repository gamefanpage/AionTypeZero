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
import org.typezero.gameserver.model.templates.stats.SummonStatsTemplate;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ATracer
 */
@XmlRootElement(name = "summon_stats_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class SummonStatsData {

	@XmlElement(name = "summon_stats", required = true)
	private List<SummonStatsType> summonTemplatesList = new ArrayList<SummonStatsType>();

	private final TIntObjectHashMap<SummonStatsTemplate> summonTemplates = new TIntObjectHashMap<SummonStatsTemplate>();

	/**
	 * @param u
	 * @param parent
	 */
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (SummonStatsType st : summonTemplatesList) {
			int code1 = makeHash(st.getNpcIdDark(), st.getRequiredLevel());
			summonTemplates.put(code1, st.getTemplate());
			int code2 = makeHash(st.getNpcIdLight(), st.getRequiredLevel());
			summonTemplates.put(code2, st.getTemplate());
		}
	}

	/**
	 * @param npcId
	 * @param level
	 * @return
	 */
	public SummonStatsTemplate getSummonTemplate(int npcId, int level) {
		SummonStatsTemplate template = summonTemplates.get(makeHash(npcId, level));
		if (template == null)
			template = summonTemplates.get(makeHash(201022, 10));// TEMP till all templates are done
		return template;
	}

	/**
	 * Size of summon templates
	 *
	 * @return
	 */
	public int size() {
		return summonTemplates.size();
	}

	@XmlRootElement(name = "summonStatsTemplateType")
	private static class SummonStatsType {

		@XmlAttribute(name = "npc_id_dark", required = true)
		private int npcIdDark;
		@XmlAttribute(name = "npc_id_light", required = true)
		private int npcIdLight;
		@XmlAttribute(name = "level", required = true)
		private int requiredLevel;

		@XmlElement(name = "stats_template")
		private SummonStatsTemplate template;

		public int getNpcIdDark() {
			return npcIdDark;
		}

		public int getNpcIdLight() {
			return npcIdLight;
		}

		public int getRequiredLevel() {
			return requiredLevel;
		}

		public SummonStatsTemplate getTemplate() {
			return template;
		}
	}

	/**
	 * Note:<br>
	 * max level is 255
	 *
	 * @param npcId
	 * @param level
	 * @return
	 */
	private static int makeHash(int npcId, int level) {
		return npcId << 8 | level;
	}
}
