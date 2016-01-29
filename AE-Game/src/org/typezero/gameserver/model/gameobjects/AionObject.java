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


import com.google.common.base.Function;

import javax.annotation.Nullable;

/**
 * This is the base class for all "in-game" objects, that player can interact with, such as: npcs, monsters, players,
 * items.<br>
 * <br>
 * Each AionObject is uniquely identified by objectId.
 *
 * @author -Nemesiss-, SoulKeeper
 */
public abstract class AionObject {

	public static Function<AionObject, Integer> OBJECT_TO_ID_TRANSFORMER = new Function<AionObject, Integer>() {
		@Override
		public Integer apply(@Nullable AionObject input) {
			return input != null ? input.getObjectId() : null;
		}
	};

	/**
	 * Unique id, for all game objects such as: items, players, monsters.
	 */
	private Integer objectId;

	public AionObject(Integer objId) {
		this.objectId = objId;
	}

	/**
	 * Returns unique ObjectId of AionObject
	 *
	 * @return Int ObjectId
	 */
	public Integer getObjectId() {
		return objectId;
	}

	/**
	 * Returns name of the object.<br>
	 * Unique for players, common for NPCs, items, etc
	 *
	 * @return name of the object
	 */
	public abstract String getName();
}
