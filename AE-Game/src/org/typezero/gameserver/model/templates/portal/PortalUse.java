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

package org.typezero.gameserver.model.templates.portal;

import org.typezero.gameserver.model.Race;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 *
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PortalUse")
public class PortalUse {

    @XmlElement(name = "portal_path")
    protected List<PortalPath> portalPath;
    @XmlAttribute(name = "npc_id")
    protected int npcId;
    @XmlAttribute(name = "siege_id")
    protected int siegeId;

    public List<PortalPath> getPortalPaths() {
        return portalPath;
    }

	public PortalPath getPortalPath(Race race) {
		if (portalPath != null) {
			for (PortalPath path : portalPath) {
				if (path.getRace().equals(race) || path.getRace().equals(Race.PC_ALL)) {
					return path;
				}
			}
		}
		return null;
	}

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int value) {
        this.npcId = value;
    }

    public int getSiegeId() {
        return siegeId;
    }

    public void setSiegeId(int value) {
        this.siegeId = value;
    }

}
