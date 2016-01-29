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

/**
 *
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PortalPath")
public class PortalPath {

    @XmlElement(name = "portal_req")
    protected PortalReq portalReq;
    @XmlAttribute(name = "dialog")
    protected int dialog;
    @XmlAttribute(name = "loc_id")
    protected int locId;
    @XmlAttribute(name = "player_count")
    protected int playerCount;
    @XmlAttribute(name = "instance")
    protected boolean instance;
	@XmlAttribute(name = "siege_id")
	protected int siegeId;
	@XmlAttribute(name = "race")
	protected Race race = Race.PC_ALL;
	@XmlAttribute(name = "err_group")
	protected int errGroup;

    public PortalReq getPortalReq() {
        return portalReq;
    }

    public int getDialog() {
        return dialog;
    }

    public void setDialog(int value) {
        this.dialog = value;
    }

    public int getLocId() {
        return locId;
    }

    public void setLocId(int value) {
        this.locId = value;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int value) {
        this.playerCount = value;
    }

    public boolean isInstance() {
		return instance;
    }

    public void setInstance(boolean value) {
        this.instance = value;
    }

	public int getSigeId() {
		return siegeId;
	}

	public Race getRace() {
		return race;
	}

	public int getErrGroup() {
		return errGroup;
	}
}
