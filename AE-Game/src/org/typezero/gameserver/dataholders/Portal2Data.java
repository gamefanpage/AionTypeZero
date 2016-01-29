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
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.templates.portal.PortalDialog;
import org.typezero.gameserver.model.templates.portal.PortalPath;
import org.typezero.gameserver.model.templates.portal.PortalScroll;
import org.typezero.gameserver.model.templates.portal.PortalUse;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "portalUse",
    "portalDialog",
    "portalScroll"
})
@XmlRootElement(name = "portal_templates2")
public class Portal2Data {

    @XmlElement(name = "portal_use")
    protected List<PortalUse> portalUse;
    @XmlElement(name = "portal_dialog")
    protected List<PortalDialog> portalDialog;
    @XmlElement(name = "portal_scroll")
    protected List<PortalScroll> portalScroll;

	@XmlTransient
	private TIntObjectHashMap<PortalUse> portalUses = new TIntObjectHashMap<PortalUse>();
	@XmlTransient
	private TIntObjectHashMap<PortalDialog> portalDialogs = new TIntObjectHashMap<PortalDialog>();
	@XmlTransient
	private Map<String, PortalScroll> portalScrolls = new HashMap<String, PortalScroll>();

	void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		if (portalUse != null) {
			for (PortalUse portal : portalUse) {
				portalUses.put(portal.getNpcId(), portal);
			}
		}
		if (portalDialog != null) {
			for (PortalDialog portal : portalDialog) {
				portalDialogs.put(portal.getNpcId(), portal);
			}
		}
		if (portalScroll != null) {
			for (PortalScroll portal : portalScroll) {
				portalScrolls.put(portal.getName(), portal);
			}
		}
	}

	public int size() {
		return portalScrolls.size() + portalDialogs.size() + portalUses.size();
	}

	public PortalPath getPortalDialog(int npcId, int dialogId, Race race) {
		PortalDialog portal = portalDialogs.get(npcId);
		if (portal != null) {
			for (PortalPath path : portal.getPortalPath()) {
				if (path.getDialog() == dialogId && (race.equals(path.getRace())
						|| path.getRace().equals(Race.PC_ALL))) {
					return path;
				}
			}
		}
		return null;
	}

	public boolean isPortalNpc(int npcId) {
		return  portalUses.get(npcId) != null || portalDialogs.get(npcId) != null;
	}

	public PortalUse getPortalUse(int npcId) {
		return portalUses.get(npcId);
	}

	public PortalScroll getPortalScroll(String name) {
		return portalScrolls.get(name);
	}

	public int getTeleportDialogId(int npcId) {
		PortalDialog portal = portalDialogs.get(npcId);
		return portal == null ? 1011 : portal.getTeleportDialogId();
	}
}
