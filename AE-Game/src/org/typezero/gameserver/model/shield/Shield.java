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

package org.typezero.gameserver.model.shield;

import org.typezero.gameserver.controllers.ShieldController;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.templates.shield.ShieldTemplate;
import org.typezero.gameserver.utils.idfactory.IDFactory;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldPosition;
import org.typezero.gameserver.world.knownlist.SphereKnownList;

/**
 * @author Wakizashi
 */
public class Shield extends VisibleObject {

	private ShieldTemplate template = null;
	private String name = null;
	private int id = 0;

	public Shield(ShieldTemplate template) {
		super(IDFactory.getInstance().nextId(), new ShieldController(), null, null, null);

		((ShieldController) getController()).setOwner(this);
		this.template = template;
		this.name = (template.getName() == null) ? "SHIELD" : template.getName();
		this.id = template.getId();
		setKnownlist(new SphereKnownList(this, template.getRadius() * 2));
	}

	public ShieldTemplate getTemplate() {
		return template;
	}

	@Override
	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public void spawn() {
		World w = World.getInstance();
		WorldPosition position = w.createPosition(template.getMap(), template.getCenter().getX(), template.getCenter().getY(), template
			.getCenter().getZ(), (byte) 0, 0);
		this.setPosition(position);
		w.storeObject(this);
		w.spawn(this);
	}
}
