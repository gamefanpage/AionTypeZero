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

import java.io.IOException;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Gatherable;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Luno, modified Bobobear
 */
public class Delete extends AdminCommand {

	public Delete() {
		super("delete");
	}

	@Override
	public void execute(Player admin, String... params) {
		Npc npc = null;
		Gatherable gather = null;
		SpawnTemplate spawn = null;

		if (admin.getTarget() != null && admin.getTarget() instanceof Npc)
			npc = (Npc) admin.getTarget();

		if (admin.getTarget() != null && admin.getTarget() instanceof Gatherable)
			gather = (Gatherable) admin.getTarget();

		if (npc == null && gather == null) {
			PacketSendUtility.sendMessage(admin, "you need to target an Npc or Gatherable type.");
			return;
		}

		if (npc != null)
			spawn = npc.getSpawn();
		else
			spawn = gather.getSpawn();

		if (spawn.hasPool()) {
			PacketSendUtility.sendMessage(admin, "Can't delete pooled spawn template");
			return;
		}
		if (spawn instanceof SiegeSpawnTemplate) {
			PacketSendUtility.sendMessage(admin, "Can't delete siege spawn template");
			return;
		}

		if (npc != null)
			npc.getController().onDelete();
		else
			gather.getController().onDelete();

		try {
			DataManager.SPAWNS_DATA2.saveSpawn(admin, (npc != null ? npc : gather), true);
		}
		catch (IOException e) {
			e.printStackTrace();
			PacketSendUtility.sendMessage(admin, "Could not remove spawn");
			return;
		}
		PacketSendUtility.sendMessage(admin, "Spawn removed");
	}

	@Override
	public void onFail(Player admin, String message) {
		// TODO Auto-generated method stub
	}
}
