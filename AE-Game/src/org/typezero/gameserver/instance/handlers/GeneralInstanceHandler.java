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

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.manager.EmoteManager;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Gatherable;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.instance.StageList;
import org.typezero.gameserver.model.instance.StageType;
import org.typezero.gameserver.model.instance.instancereward.InstanceReward;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.zone.ZoneInstance;




/**
 * @author ATracer
 */
public class GeneralInstanceHandler implements InstanceHandler {

	protected final long creationTime;
    private boolean _destroyed = false;
	protected WorldMapInstance instance;
	protected int instanceId;
	protected Integer mapId;

	public GeneralInstanceHandler() {
		creationTime = System.currentTimeMillis();
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		this.instance = instance;
		this.instanceId = instance.getInstanceId();
		this.mapId = instance.getMapId();
	}

	@Override
	public void onInstanceDestroy() {
	}

	@Override
	public void onPlayerLogin(Player player) {
	}

	@Override
	public void onPlayerLogOut(Player player) {
	}

	@Override
	public void onEnterInstance(Player player) {
	}

	@Override
	public void onLeaveInstance(Player player) {
	}

	@Override
	public void onOpenDoor(int door) {
	}

	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
	}

	@Override
	public void onLeaveZone(Player player, ZoneInstance zone) {
	}

	@Override
	public void onPlayMovieEnd(Player player, int movieId) {
	}

	@Override
	public boolean onReviveEvent(Player player) {
		return false;
	}

	protected VisibleObject spawn(int npcId, float x, float y, float z, byte heading) {
		SpawnTemplate template = SpawnEngine.addNewSingleTimeSpawn(mapId, npcId, x, y, z, heading);
		return SpawnEngine.spawnObject(template, instanceId);
	}

	protected VisibleObject spawn(int npcId, float x, float y, float z, byte heading, int staticId) {
		SpawnTemplate template = SpawnEngine.addNewSingleTimeSpawn(mapId, npcId, x, y, z, heading);
		template.setStaticId(staticId);
		return SpawnEngine.spawnObject(template, instanceId);
	}

    protected final boolean isDestroyed() {
        return _destroyed;
    }


	protected Npc getNpc(int npcId) {
		return instance.getNpc(npcId);
	}

    protected final int getMapId() {
        return instance.getMapId();
    }

    protected final WorldMapInstance getInstance() {
        return instance;
    }

    protected final int getInstanceId() {
        return instance.getInstanceId();
    }

	protected void sendMsg(int msg, int Obj, boolean isShout, int color) {
		sendMsg(msg, Obj, isShout, color, 0);
	}

	protected void sendMsg(int msg, int Obj, boolean isShout, int color, int time) {
		NpcShoutsService.getInstance().sendMsg(instance, msg, Obj, isShout, color, time);
	}
    protected void spawnWalk(final int npcId, final float x, final float y, final float z, final byte h, final int time,
                    final String walkern) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isDestroyed()) {
                    Npc npc = (Npc) spawn(npcId, x, y, z, h);
                    npc.getSpawn().setWalkerId(walkern);
                }
            }

        }, time);
    }

	protected void sendMsg(int msg) {
		sendMsg(msg, 0, false, 25);
	}

	@Override
	public void onExitInstance(Player player) {
	}

	@Override
	public void doReward(Player player) {
	}

	@Override
	public boolean onDie(Player player, Creature lastAttacker) {
		return false;
	}

	@Override
	public void onStopTraining(Player player) {
	}

	@Override
	public void onDie(Npc npc) {
	}

	@Override
	public void onChangeStage(StageType type) {
	}

	@Override
	public void onChangeStageList(StageList list)
	{
	}

	@Override
	public StageType getStage() {
		return StageType.DEFAULT;
	}

	@Override
	public void onDropRegistered(Npc npc) {
	}

	@Override
	public void onGather(Player player, Gatherable gatherable) {
	}

	@Override
	public InstanceReward<?> getInstanceReward() {
		return null;
	}

	@Override
	public boolean onPassFlyingRing(Player player, String flyingRing) {
		return false;
	}

	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
	}

}
