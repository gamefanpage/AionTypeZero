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

package org.typezero.gameserver.network.aion.gmhandler;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.world.World;

public class CmdWish extends AbstractGMHandler {

	public CmdWish(Player admin, String params) {
		super(admin, params);
		run();
	}

	public void run() {
        Player t = admin;

        if (admin.getTarget() != null && admin.getTarget() instanceof Player)
            t = World.getInstance().findPlayer(Util.convertName(admin.getTarget().getName()));

        String[] p = params.split(" ");
        if (p.length != 2) {
            PacketSendUtility.sendMessage(admin, "not enough parameters");
            return;
        }

        if (p[0].length() < 6) {
            Integer qty = Integer.parseInt(p[0]);
            Integer itemId = Integer.parseInt(p[1]);

            if (qty > 0 && itemId > 0) {
                if (DataManager.ITEM_DATA.getItemTemplate(itemId) == null) {
                    PacketSendUtility.sendMessage(admin, "Item id is incorrect: " + itemId);
                } else {
                    long count = ItemService.addItem(t, itemId, qty);
                    if (count == 0) {
                        PacketSendUtility.sendMessage(admin, "You successfully gave " + qty + " x [item:" + itemId + "] to " + t.getName() + ".");
                    } else {
                        PacketSendUtility.sendMessage(admin, "Item couldn't be added");
                    }
                }
            }
        }

        if (p[0].length() > 6) {
            String itemDesc = p[0];
            Integer countitems = Integer.parseInt(p[1]);

            if (itemDesc != null && countitems > 0) {
                for (ItemTemplate template : DataManager.ITEM_DATA.getItemData().valueCollection()) {
                    if (template.getNamedesc() != null && template.getNamedesc().equalsIgnoreCase(itemDesc)) {
                        long count = ItemService.addItem(t, template.getTemplateId(), countitems);
                        if (count == 0) {
                            PacketSendUtility.sendMessage(admin, "You successfully gave " + countitems + " x [item:" + template.getTemplateId() + "] ID: " +template.getTemplateId()+ " to " + t.getName() + ".");
                        } else {
                            PacketSendUtility.sendMessage(admin, "Item couldn't be added");
                        }
                    }
                }
            }
        }
    }
}
