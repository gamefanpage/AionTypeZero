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

package org.typezero.gameserver.model.gameobjects.player.title;

import org.typezero.gameserver.model.IExpirable;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.TitleTemplate;

/**
 * @author Mr. Poke
 */
public class Title implements IExpirable{

	private TitleTemplate template;
	private int id;
	private int dispearTime;

	/**
	 * @param template
	 * @param id
	 * @param dispearTime
	 */
	public Title(TitleTemplate template, int id, int dispearTime) {
		this.template = template;
		this.id = id;
		this.dispearTime = dispearTime;
	}

	/**
	 * @return Returns the template.
	 */
	public TitleTemplate getTemplate() {
		return template;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return Returns the dispearTime.
	 */
	public int getRemainingTime() {
		if (dispearTime == 0)
			return 0;
		return dispearTime - (int)(System.currentTimeMillis() / 1000);
	}

	@Override
	public int getExpireTime() {
		return dispearTime;
	}

	@Override
	public void expireEnd(Player player) {
		player.getTitleList().removeTitle(id);
	}

	@Override
	public void expireMessage(Player player, int time) {
	}

	@Override
	public boolean canExpireNow() {
		return true;
	}
}
