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

package org.typezero.gameserver.questEngine.handlers.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * @author MrPoke
 * @reworked vlog, Bobobear
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Monster")
public class Monster {

	@XmlAttribute(name = "var", required = true)
	protected int var;
	@XmlAttribute(name = "start_var")
	protected Integer startVar;
	@XmlAttribute(name = "end_var", required = true)
	protected int endVar;
    @XmlAttribute(name = "end_var_reward")
    protected boolean rewardVar = false;
	@XmlAttribute(name = "set_reward_var")
	protected int setRewardVar;
	@XmlAttribute(name = "npc_ids")
	protected List<Integer> npcIds;
	@XmlAttribute(name = "npc_seq")
	private Integer npcSequence;

	public int getVar() {
		return var;
	}

	public int getSetRewardVar() {
		return setRewardVar;
	}
	public Integer getStartVar() {
		return startVar;
	}

	public int getEndVar() {
		return endVar;
	}

	public List<Integer> getNpcIds() {
		return npcIds;
	}

	public Integer getNpcSequence() {
		return npcSequence;
	}
	    public boolean getRewardVar() {
        return rewardVar;
    }
}
