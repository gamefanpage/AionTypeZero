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

package admincommands;

import org.typezero.gameserver.model.gameobjects.Gatherable;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.StaticObject;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.knownlist.Visitor;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMapType;


/**
 * @author Luno, reworked Bobobear
 */
public class ReloadSpawn extends AdminCommand {

	public ReloadSpawn() {
		super("reload_spawn");
	}

	@Override
	public void execute(Player player, String... params) {
		int worldId;
		String destination;

		worldId = 0;
		destination = "null";

		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "syntax //reload_spawn <location name | all>");
		}
		else {
			StringBuilder sbDestination = new StringBuilder();
			for(String p : params)
				sbDestination.append(p + " ");

			destination = sbDestination.toString().trim();

		if (destination.equalsIgnoreCase("Sanctum"))
			worldId = WorldMapType.SANCTUM.getId();
		else if (destination.equalsIgnoreCase("Kaisinel"))
			worldId = WorldMapType.KAISINEL.getId();
		else if (destination.equalsIgnoreCase("Poeta"))
			worldId = WorldMapType.POETA.getId();
		else if (destination.equalsIgnoreCase("Verteron"))
			worldId = WorldMapType.VERTERON.getId();
		else if (destination.equalsIgnoreCase("Eltnen"))
			worldId = WorldMapType.ELTNEN.getId();
		else if (destination.equalsIgnoreCase("Theobomos"))
			worldId = WorldMapType.THEOBOMOS.getId();
		else if (destination.equalsIgnoreCase("Heiron"))
			worldId = WorldMapType.HEIRON.getId();
		else if  (destination.equalsIgnoreCase("Pandaemonium"))
			worldId = WorldMapType.PANDAEMONIUM.getId();
		else if (destination.equalsIgnoreCase("Marchutan"))
			worldId = WorldMapType.MARCHUTAN.getId();
		else if (destination.equalsIgnoreCase("Ishalgen"))
			worldId = WorldMapType.ISHALGEN.getId();
		else if (destination.equalsIgnoreCase("Altgard"))
			worldId = WorldMapType.ALTGARD.getId();
		else if (destination.equalsIgnoreCase("Morheim"))
			worldId = WorldMapType.MORHEIM.getId();
		else if (destination.equalsIgnoreCase("Brusthonin"))
			worldId = WorldMapType.BRUSTHONIN.getId();
		else if (destination.equalsIgnoreCase("Beluslan"))
			worldId = WorldMapType.BELUSLAN.getId();
		else if (destination.equalsIgnoreCase("Inggison"))
			worldId = WorldMapType.INGGISON.getId();
		else if (destination.equalsIgnoreCase("Gelkmaros"))
			worldId = WorldMapType.GELKMAROS.getId();
		else if (destination.equalsIgnoreCase("Silentera"))
			worldId = 600010000;
		else if (destination.equalsIgnoreCase("Reshanta"))
			worldId = WorldMapType.RESHANTA.getId();
		else if (destination.equalsIgnoreCase("Kaisinel Academy"))
			worldId = 110070000;
		else if (destination.equalsIgnoreCase("Marchutan Priory"))
			worldId = 120080000;
		else if (destination.equalsIgnoreCase("Sarpan"))
			worldId = 600020000;
		else if (destination.equalsIgnoreCase("Tiamaranta"))
			worldId = 600030000;
		else if (destination.equalsIgnoreCase("Oriel"))
			worldId = 700010000;
		else if (destination.equalsIgnoreCase("Pernon"))
			worldId = 710010000;
		else if (destination.equalsIgnoreCase("Katalam"))
			worldId = 600050000;
		else if (destination.equalsIgnoreCase("Danaria"))
			worldId = 600060000;
		else if (destination.equalsIgnoreCase("All"))
			worldId = 0;
		else
			PacketSendUtility.sendMessage(player, "Could not find the specified map !");
		}
		final String destinationMap = destination;

		// despawn specified map, no instance
		if (destination.equalsIgnoreCase("All")) {
			reloadMap(WorldMapType.SANCTUM.getId(), player, "Sanctum");
			reloadMap(WorldMapType.KAISINEL.getId(), player, "Kaisinel");
			reloadMap(WorldMapType.POETA.getId(), player, "Poeta");
			reloadMap(WorldMapType.VERTERON.getId(), player, "Verteron");
			reloadMap(WorldMapType.ELTNEN.getId(), player, "Eltnen");
			reloadMap(WorldMapType.THEOBOMOS.getId(), player, "Theobomos");
			reloadMap(WorldMapType.HEIRON.getId(), player, "Heiron");
			reloadMap(WorldMapType.PANDAEMONIUM.getId(), player, "Pandaemonium");
			reloadMap(WorldMapType.MARCHUTAN.getId(), player, "Marchutan");
			reloadMap(WorldMapType.ISHALGEN.getId(), player, "Ishalgen");
			reloadMap(WorldMapType.ALTGARD.getId(), player, "Altgard");
			reloadMap(WorldMapType.MORHEIM.getId(), player, "Morheim");
			reloadMap(WorldMapType.BRUSTHONIN.getId(), player, "Brusthonin");
			reloadMap(WorldMapType.BELUSLAN.getId(), player, "Beluslan");
			reloadMap(WorldMapType.INGGISON.getId(), player, "Inggison");
			reloadMap(WorldMapType.GELKMAROS.getId(), player, "Gelkmaros");
			reloadMap(600010000, player, "Silentera");
			reloadMap(WorldMapType.RESHANTA.getId(), player, "Reshanta");
			reloadMap(110070000, player, "Kaisinel Academy");
			reloadMap(120080000, player, "Marchutan Priory");
			reloadMap(600020000, player, "Sarpan");
			reloadMap(600030000, player, "Tiamaranta");
			reloadMap(700010000, player, "Oriel");
			reloadMap(710010000, player, "Pernon");
			reloadMap(600050000, player, "Katalam");
			reloadMap(600060000, player, "Danaria");
		}
		else {
			reloadMap(worldId, player, destinationMap);
		}
	}

	private void reloadMap (int worldId, Player admin, String destinationMap) {
		final int IdWorld = worldId;
		final Player adm = admin;
		final String dest = destinationMap;

		if (IdWorld != 0) {
			World.getInstance().doOnAllObjects(new Visitor<VisibleObject>() {
				@Override
				public void visit(VisibleObject object) {
					if (object.getWorldId() != IdWorld) {
						return;
					}
					if (object instanceof Npc || object instanceof Gatherable || object instanceof StaticObject) {
						object.getController().onDelete();
					}
				}
			});
			SpawnEngine.spawnWorldMap(IdWorld);
			PacketSendUtility.sendMessage(adm, "Spawns for map: " + IdWorld + " (" + dest + ") reloaded succesfully");
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //reload_spawn <location name | all>");
	}
}
