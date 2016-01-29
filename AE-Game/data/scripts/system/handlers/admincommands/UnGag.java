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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Future;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;

/**
 * @author Watson
 */
public class UnGag extends AdminCommand {

	public UnGag() {
		super("ungag");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(admin, "Syntax: //ungag <player>");
			return;
		}

		String name = Util.convertName(params[0]);
		Player player = World.getInstance().findPlayer(name);
		final int offlineid = DAOManager.getDAO(PlayerDAO.class).getPlayerIdByName(name);
		if (player == null) {
			deleteGag(offlineid);
			PacketSendUtility.sendMessage(admin, "Player " + name + " ungagged");
			return;
		}

		player.setGagged(false);
		Future<?> task = player.getController().getTask(TaskId.GAG);
		if (task != null)
			player.getController().cancelTask(TaskId.GAG);
		if (player.hasVar("chatgag"))
		{
			player.delVar("chatgag", true);
		}
		PacketSendUtility.sendMessage(player, "You have been ungagged");

		PacketSendUtility.sendMessage(admin, "Player " + name + " ungagged");
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: //ungag <player>");
	}

	private boolean deleteGag(final int objId) {
		boolean result = DB.insertUpdate(
				"DELETE FROM `player_vars` WHERE `player_id` = ? AND `param` = 'chatgag'", new IUStH() {
					@Override
					public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
						stmt.setInt(1, objId);
						stmt.execute();
					}
				});

		return result;
	}
}
