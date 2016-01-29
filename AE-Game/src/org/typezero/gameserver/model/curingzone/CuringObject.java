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

package org.typezero.gameserver.model.curingzone;

import org.typezero.gameserver.controllers.VisibleObjectController;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.templates.curingzones.CuringTemplate;
import org.typezero.gameserver.utils.idfactory.IDFactory;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.NpcKnownList;

/**
 *
 * @author xTz
 */
public class CuringObject extends VisibleObject {

	private CuringTemplate template;
	private float range;
	public CuringObject(CuringTemplate template, int instanceId) {
		super(IDFactory.getInstance().nextId(), new VisibleObjectController<CuringObject>() {}, null, null, World.getInstance().
				createPosition(template.getMapId(), template.getX(), template.getY(), template.getZ(), (byte) 0, instanceId));
		this.template = template;
		this.range = template.getRange();
		setKnownlist(new NpcKnownList(this));
	}

	public CuringTemplate getTemplate() {
		return template;
	}

	@Override
	public String getName() {
		return "";
	}

	public float getRange() {
		return range;
	}

	public void spawn() {
		World w = World.getInstance();
		w.storeObject(this);
		w.spawn(this);
	}
}
