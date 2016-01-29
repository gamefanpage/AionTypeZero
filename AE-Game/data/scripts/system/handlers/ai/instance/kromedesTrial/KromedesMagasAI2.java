/*
 * This file is part of Aion Lightning <aion-lightning.org>.
 *
 *  Aion Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion Lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package ai.instance.kromedesTrial;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Gigi
 */
@AIName("krmagas")
public class KromedesMagasAI2 extends NpcAI2 {

	@Override
	public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == DialogAction.SETPRO1.id()) {
			if (player.getInventory().getItemCountByItemId(185000109) > 0) {
				PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 454));
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
			}
			else
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
		}
		else if (dialogId == DialogAction.SELECT_ACTION_1012.id())
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1012));
		return true;
	}

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}
}
