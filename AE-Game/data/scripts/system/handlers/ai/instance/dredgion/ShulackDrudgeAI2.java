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

package ai.instance.dredgion;

import ai.GeneralNpcAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.CreatureType;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_CUSTOM_SETTINGS;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.knownlist.Visitor;


/**
 * @author cheatkiller
 *
 */
@AIName("shulackdrudge")
public class ShulackDrudgeAI2 extends GeneralNpcAI2 {


	@Override
	protected void handleDialogFinish(Player player) {
		addItems(player);
		super.handleDialogFinish(player);
	}

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	private void addItems(Player player) {
		int itemId = player.getRace() == Race.ELYOS ? 182212606 : 182212607;
		Item dredgionSupplies = player.getInventory().getFirstItemByItemId(itemId);
	  if (dredgionSupplies == null) {
	  	ItemService.addItem(player, itemId, 1);
	  	getOwner().setNpcType(CreatureType.PEACE.getId());
	  	getKnownList().doOnAllPlayers(new Visitor<Player>() {

				@Override
				public void visit(Player player) {
					PacketSendUtility.sendPacket(player, new SM_CUSTOM_SETTINGS(getOwner().getObjectId(), 0, getOwner().getType(player), 0));
				 }
			 });
	  }
	}
}


