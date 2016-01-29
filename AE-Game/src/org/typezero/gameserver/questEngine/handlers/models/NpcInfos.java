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

/**
 * @author Hilgert
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NpcInfos")
public class NpcInfos {

	@XmlAttribute(name = "npc_id", required = true)
	protected int npcId;
	@XmlAttribute(name = "var", required = true)
	protected int var;
	@XmlAttribute(name = "quest_dialog", required = true)
	protected int DialogAction;
	@XmlAttribute(name = "close_dialog")
	protected int closeDialog;
	@XmlAttribute(name = "movie")
	protected int movie;
	@XmlAttribute(name = "set_reward_var")
	protected int setrewardvar;

	/**
	 * Gets the value of the npcId property.
	 */
	public int getNpcId() {
		return npcId;
	}

	/**
	 * Gets the value of the var property.
	 */
	public int getVar() {
		return var;
	}

	/**
	 * Gets the value of the DialogAction property.
	 */
	public int getQuestDialog() {
		return DialogAction;
	}

	/**
	 * Gets the value of the closeDialog property.
	 */
	public int getCloseDialog() {
		return closeDialog;
	}

	/**
	 * @return the movie
	 */
	public int getMovie() {
		return movie;
	}

	public int SetRewardvar() {
		return setrewardvar;
	}
	/**
	 * @param movie
	 *          the movie to set
	 */
	public void setMovie(int movie) {
		this.movie = movie;
	}
}
