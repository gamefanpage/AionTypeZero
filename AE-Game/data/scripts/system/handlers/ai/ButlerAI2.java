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

package ai;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.DialogPage;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.house.PlayerScript;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_SCRIPTS;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Rolandas
 */
@AIName("butler")
public class ButlerAI2 extends GeneralNpcAI2 {

	private static final Logger log = LoggerFactory.getLogger(ButlerAI2.class);

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		return kickDialog(player, DialogPage.getPageByAction(dialogId));
	}

	private boolean kickDialog(Player player, DialogPage page) {
		if (page == DialogPage.NULL)
			return false;

		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), page.id()));
		return true;
	}

	@Override
	protected void handleCreatureSee(Creature creature) {
		if (creature instanceof Player) {
			Player player = (Player) creature;
			House house = (House) getCreator();
			if (player.getObjectId() == house.getOwnerId()) {
				// DO SOMETHING SPECIAL
			}

			Map<Integer, PlayerScript> scriptMap = house.getPlayerScripts().getScripts();
			try {
				// protect against writing
				for (int position = 0; position < 8; position++) {
					scriptMap.get(position).writeLock();
				}
				int totalSize = 0;
				int position = 0;
				int from = 0;
				while (position != 7) {
					for (; position < 8; position++) {
						PlayerScript script = scriptMap.get(position);
						byte[] bytes = script.getCompressedBytes();
						if (bytes == null) {
							continue;
						}
						if (bytes.length > 8141) {
							log.warn("Player " + player.getObjectId() + " has too big script at position " + position);
							return;
						}
						if (totalSize + bytes.length > 8141) {
							position--;
							PacketSendUtility.sendPacket(player, new SM_HOUSE_SCRIPTS(house.getAddress().getId(), house.getPlayerScripts(), from,
								position));
							from = position + 1;
							totalSize = 0;
							continue;
						}
						totalSize += bytes.length + 8;
					}
					position--;
					if (totalSize > 0 || from == 0 && position == 7)
						PacketSendUtility
							.sendPacket(player, new SM_HOUSE_SCRIPTS(house.getAddress().getId(), house.getPlayerScripts(), from, position));
				}
			}
			finally {
				// remove write locks finally
				for (int position = 0; position < 8; position++) {
					scriptMap.get(position).writeUnlock();
				}
			}
		}
	}
}
