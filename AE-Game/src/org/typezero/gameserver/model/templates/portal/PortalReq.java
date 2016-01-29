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

import org.typezero.gameserver.configs.main.GSConfig;

import javax.xml.bind.annotation.*;
import java.util.List;
/**
 *
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PortalReq")
public class PortalReq {

    @XmlElement(name = "quest_req")
    protected List<QuestReq> questReq;
    @XmlElement(name = "item_req")
    protected List<ItemReq> itemReq;
    @XmlAttribute(name = "min_level")
    protected int minLevel;
    @XmlAttribute(name = "max_level")
    protected int maxLevel = GSConfig.PLAYER_MAX_LEVEL;
    @XmlAttribute(name = "kinah_req")
    protected int kinahReq;
	@XmlAttribute(name = "title_id")
    protected int titleId;
	@XmlAttribute(name = "err_level")
    protected int errLevel;

    public List<QuestReq> getQuestReq() {
        return this.questReq;
    }

    public List<ItemReq> getItemReq() {
        return this.itemReq;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int value) {
        this.minLevel = value;
    }

    public int getMaxLevel() {
		return maxLevel;
    }

    public void setMaxLevel(int value) {
        this.maxLevel = value;
    }

    public int getKinahReq() {
        return kinahReq;
    }

    public void setKinahReq(int value) {
        this.kinahReq = value;
    }

	public int getTitleId() {
		return titleId;
	}

	public int getErrLevel() {
		return errLevel;
	}
}
