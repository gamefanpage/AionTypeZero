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

package org.typezero.gameserver.model.templates.teleport;

import org.typezero.gameserver.model.Race;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author Alcapwnd
 */
@XmlRootElement(name = "hotspot_template")
@XmlAccessorType(XmlAccessType.NONE)
public class HotspotTeleportTemplate {

    /**
     * Location Id.
     */
    @XmlAttribute(name = "id", required = true)
    private int locId;
    /**
     * location name.
     */
    @XmlAttribute(name = "name")
    private String name = "";
    @XmlAttribute(name = "mapId", required = true)
    private int mapId;
    @XmlAttribute(name = "posX", required = true)
    private float x = 0;
    @XmlAttribute(name = "posY", required = true)
    private float y = 0;
    @XmlAttribute(name = "posZ", required = true)
    private float z = 0;
    @XmlAttribute(name = "heading")
    private int heading = 0;
    @XmlAttribute(name = "race")
    private Race race;
    @XmlAttribute(name = "kinah")
    private int kinah = 0;
    @XmlAttribute(name = "kinah_dis")
    private float kinah_dis = 0;
    @XmlAttribute(name = "level")
    private int level = 0;

    public int getLocId() {
        return locId;
    }

    public int getMapId() {
        return mapId;
    }

    public String getName() {
        return name;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public int getHeading() {
        return heading;
    }

    public Race getRace() {
        return race;
    }

    public int getKinah() {
        return kinah;
    }

    public float getDisKinah() {
        return kinah_dis;
    }

    public int getLevel() {
        return level;
    }
}
