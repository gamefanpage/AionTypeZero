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

package org.typezero.gameserver.ai2.event;

/**
 * @author ATracer
 */
public enum AIEventType {
	ACTIVATE,
	DEACTIVATE,
	FREEZE,
	UNFREEZE,

	/**
	 * Creature is being attacked (internal)
	 */
	ATTACK, 
	/**
	 * Creature's attack part is complete (internal)
	 */
	ATTACK_COMPLETE,
	/**
	 * Creature's stopping attack (internal)
	 */
	ATTACK_FINISH,
	/**
	 * Some neighbour creature is being attacked (broadcast)
	 */
	CREATURE_NEEDS_SUPPORT,
	
	MOVE_VALIDATE,
	MOVE_ARRIVED,

	CREATURE_SEE,
	CREATURE_NOT_SEE,
	CREATURE_MOVED,
	CREATURE_AGGRO,
	SPAWNED,
	RESPAWNED,
	DESPAWNED,
	DIED,

	TARGET_REACHED,
	TARGET_TOOFAR,
	TARGET_GIVEUP,
	TARGET_CHANGED,
	FOLLOW_ME,
	STOP_FOLLOW_ME,

	NOT_AT_HOME,
	BACK_HOME,

	DIALOG_START,
	DIALOG_FINISH,

	DROP_REGISTERED
}
