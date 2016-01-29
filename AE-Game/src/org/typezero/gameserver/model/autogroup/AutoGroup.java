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


package org.typezero.gameserver.model.autogroup;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.Collections;
import java.util.List;

/**
 * @author MrPoke
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AutoGroup")
public class AutoGroup {

    @XmlAttribute(required = true)
    protected int id;
    @XmlAttribute(required = true)
    protected int instanceId;
    @XmlAttribute(name = "name_id")
    protected int nameId;
    @XmlAttribute(name = "title_id")
    protected int titleId;
    @XmlAttribute(name = "min_lvl")
    protected int minLvl;
    @XmlAttribute(name = "max_lvl")
    protected int maxLvl;
    @XmlAttribute(name = "register_quick")
    protected boolean registerQuick;
    @XmlAttribute(name = "register_group")
    protected boolean registerGroup;
	@XmlAttribute(name = "register_new")
    protected boolean registerNew;
    @XmlAttribute(name = "npc_ids")
    protected List<Integer> npcIds;

    public int getId() {
        return id;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public int getNameId() {
        return nameId;
    }

    public int getTitleId() {
        return titleId;
    }

    public int getMinLvl() {
        return minLvl;
    }

    public int getMaxLvl() {
        return maxLvl;
    }

    public boolean hasRegisterQuick() {
        return registerQuick;
    }

    public boolean hasRegisterGroup() {
        return registerGroup;
    }

	public boolean hasRegisterNew() {
        return registerNew;
    }

    public List<Integer> getNpcIds() {
        if (npcIds == null) {
            npcIds = Collections.emptyList();
        }
        return this.npcIds;
    }
}
