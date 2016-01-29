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

package org.typezero.gameserver.instance.handlers;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Gatherable;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.instance.StageList;
import org.typezero.gameserver.model.instance.StageType;
import org.typezero.gameserver.model.instance.instancereward.InstanceReward;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.zone.ZoneInstance;

/**
 * @author ATracer
 */
public interface InstanceHandler {

	/**
	 * Executed during instance creation.<br>
	 * This method will run after spawns are loaded
	 * 
	 * @param instance
	 *          created
	 */
	void onInstanceCreate(WorldMapInstance instance);

	/**
	 * Executed during instance destroy.<br>
	 * This method will run after all spawns unloaded.<br>
	 * All class-shared objects should be cleaned in handler
	 */
	void onInstanceDestroy();

	void onPlayerLogin(Player player);

	void onPlayerLogOut(Player player);

	void onEnterInstance(Player player);

	void onLeaveInstance(Player player);

	void onOpenDoor(int door);

	void onEnterZone(Player player, ZoneInstance zone);

	void onLeaveZone(Player player, ZoneInstance zone);

	void onPlayMovieEnd(Player player, int movieId);

	boolean onReviveEvent(Player player);

	void onExitInstance(Player player);

	void doReward(Player player);

	boolean onDie(Player player, Creature lastAttacker);

	void onStopTraining(Player player);

	void onDie(Npc npc);

	void onChangeStage(StageType type);
	
	void onChangeStageList(StageList list);

	StageType getStage();

	void onDropRegistered(Npc npc);

	void onGather(Player player, Gatherable gatherable);

	InstanceReward<?> getInstanceReward();

	boolean onPassFlyingRing(Player player, String flyingRing);

	void handleUseItemFinish(Player player, Npc npc);
}