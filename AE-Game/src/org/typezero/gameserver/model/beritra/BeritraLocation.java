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

package org.typezero.gameserver.model.beritra;

import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.beritra.BeritraTemplate;
import org.typezero.gameserver.services.beritraservice.BeritraInvasion;
import java.util.*;
import javolution.util.FastMap;

/**
 * @author Rinzler (Encom)
 */

public class BeritraLocation
{
	protected int id;
	protected boolean isActive;
	protected BeritraTemplate template;
	protected BeritraInvasion<BeritraLocation> activeBeritra;
	protected FastMap<Integer, Player> players = new FastMap<Integer, Player>();
	private final List<VisibleObject> spawned = new ArrayList<VisibleObject>();

	public BeritraLocation() {
	}

	public BeritraLocation(BeritraTemplate template) {
		this.template = template;
		this.id = template.getId();
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActiveBeritra(BeritraInvasion<BeritraLocation> beritra) {
		isActive = beritra != null;
		this.activeBeritra = beritra;
	}

	public BeritraInvasion<BeritraLocation> getActiveBeritra() {
		return activeBeritra;
	}

	public final BeritraTemplate getTemplate() {
		return template;
	}

	public int getId() {
		return id;
	}

	public List<VisibleObject> getSpawned() {
		return spawned;
	}

	public FastMap<Integer, Player> getPlayers() {
		return players;
	}
}
