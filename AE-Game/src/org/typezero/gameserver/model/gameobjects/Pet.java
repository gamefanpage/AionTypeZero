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

package org.typezero.gameserver.model.gameobjects;

import org.typezero.gameserver.controllers.PetController;
import org.typezero.gameserver.controllers.movement.MoveController;
import org.typezero.gameserver.controllers.movement.PetMoveController;
import org.typezero.gameserver.model.gameobjects.player.PetCommonData;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.pet.PetTemplate;
import org.typezero.gameserver.world.WorldPosition;

/**
 * @author ATracer
 */
public class Pet extends VisibleObject {

	private final Player master;
	private MoveController moveController;
	private final PetTemplate petTemplate;

	/**
	 * @param petTemplate
	 * @param controller
	 * @param commonData
	 * @param master
	 */
	public Pet(PetTemplate petTemplate, PetController controller, PetCommonData commonData, Player master) {
		super(commonData.getObjectId(), controller, null, commonData, new WorldPosition());
		controller.setOwner(this);
		this.master = master;
		this.petTemplate = petTemplate;
		this.moveController = new PetMoveController();
	}

	public Player getMaster() {
		return master;
	}

	public int getPetId() {
		return objectTemplate.getTemplateId();
	}

	@Override
	public String getName() {
		return objectTemplate.getName();
	}

	public final PetCommonData getCommonData() {
		return (PetCommonData) objectTemplate;
	}

	public final MoveController getMoveController() {
		return moveController;
	}

	public final PetTemplate getPetTemplate() {
		return petTemplate;
	}

}
