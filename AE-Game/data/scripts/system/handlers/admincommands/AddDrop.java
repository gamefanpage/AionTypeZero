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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author ATracer
 */
public class AddDrop extends AdminCommand {

	public AddDrop() {
		super("adddrop");
	}

	@Override
	public void execute(Player player, String... params) {
		PacketSendUtility.sendMessage(player, "Now this is not implemented.");
		/*
		if (params.length != 5) {
			onFail(player, null);
			return;
		}

		try {
			final int mobId = Integer.parseInt(params[0]);
			final int itemId = Integer.parseInt(params[1]);
			final int min = Integer.parseInt(params[2]);
			final int max = Integer.parseInt(params[3]);
			final float chance = Float.parseFloat(params[4]);

			DropList dropList = DropRegistration.getInstance().getDropList();

			DropTemplate dropTemplate = new DropTemplate(mobId, itemId, min, max, chance, false);
			dropList.addDropTemplate(mobId, dropTemplate);

			DB.insertUpdate("INSERT INTO droplist (" + "`mob_id`, `item_id`, `min`, `max`, `chance`)" + " VALUES "
				+ "(?, ?, ?, ?, ?)", new IUStH() {

				@Override
				public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
					ps.setInt(1, mobId);
					ps.setInt(2, itemId);
					ps.setInt(3, min);
					ps.setInt(4, max);
					ps.setFloat(5, chance);
					ps.execute();
				}
			});
		}
		catch (Exception ex) {
			PacketSendUtility.sendMessage(player, "Only numbers are allowed");
			return;
		}
		*/
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //adddrop <mobid> <itemid> <min> <max> <chance>");
	}
}
