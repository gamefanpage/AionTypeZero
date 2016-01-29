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

package instance;

import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.world.WorldPosition;
import org.typezero.gameserver.world.zone.ZoneInstance;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Luzien
 */
@InstanceID(300260000)
public class ElementisForestInstance extends GeneralInstanceHandler {

	private byte spawned = 0;

	@Override
	public void onDie(Npc npc) {
		switch (npc.getNpcId()) {
			case 217233:
				spawn(700999, 303.07858f, 768.25012f, 204.34013f, (byte) 7);
				deleteNpc(700998);
				deleteNpc(282362);
				break;
			case 217238:
				spawn(282204, 472.9886f, 798.10944f, 129.94006f, (byte) 90);
				sendJurdinDialog();
				break;
			case 217234:
				spawn(730378, 574.359f, 429.351f, 125.533f, (byte) 0, 82);
				break;
		}
	}

	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
		if (zone.getAreaTemplate().getZoneName() == ZoneName.get("CANYONGUARDS_RAVINE_300260000")) {
			if (spawned == 0) {
				spawn(217233, 301.77118f, 765.36951f, 193.03818f, (byte) 90);
				spawned++;
			}
		}
		else if (zone.getAreaTemplate().getZoneName() == ZoneName.get("JURDINS_DOMAIN_300260000")) {
			if (spawned == 1) {
				sendMsg(1500242);
				spawned++;
			}
		}
	}

	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 282440:
				SkillEngine.getInstance().getSkill(npc, 19402, 60, player).useNoAnimationSkill();
				npc.getController().onDelete();
				break;
			case 799637:
			case 799639:
			case 799641:
			case 799643:
			case 799645:
			case 799647:
				SkillEngine.getInstance().getSkill(npc, 19692, 60, player).useNoAnimationSkill();
				npc.getController().onDelete();
				break;
			case 282308:
				SkillEngine.getInstance().getSkill(npc, 19517, 40, player).useNoAnimationSkill();
				WorldPosition p = npc.getPosition();
				if (p != null && p.getWorldMapInstance() != null) {
					spawn(282441, p.getX(), p.getY(), p.getZ(), p.getHeading());
					Npc smoke = (Npc) spawn(282465, p.getX(), p.getY(), p.getZ(), p.getHeading());
					NpcActions.delete(smoke);
				}
				NpcActions.delete(npc);
				break;
		}
	}

	private void sendJurdinDialog() {
		sendMsg(1500243, getNpc(282204).getObjectId(), false, 0, 5000);
		sendMsg(1500244, getNpc(282204).getObjectId(), false, 0, 8000);
	}

	private void deleteNpc(int npcId) {
		if (getNpc(npcId) != null) {
			getNpc(npcId).getController().onDelete();
		}
	}
}
