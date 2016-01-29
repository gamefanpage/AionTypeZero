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

package org.typezero.gameserver.network.aion.clientpackets;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.actions.CompositionAction;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.restrictions.RestrictionsManager;

/**
 * Created with IntelliJ IDEA.
 * User: pixfid
 * Date: 7/14/13
 * Time: 5:30 PM
 */
public class CM_COMPOSITE_STONES extends AionClientPacket {

    private int compinationToolItemObjectId;
    private int firstItemObjectId;
    private int secondItemObjectId;


    /**
     * Constructs new client packet instance. ByBuffer and ClientConnection should be later set manually, after using this
     * constructor.
     *
     * @param opcode     packet id
     * @param state      connection valid state
     * @param restStates rest of connection valid state (optional - if there are more than one)
     */
    public CM_COMPOSITE_STONES(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        compinationToolItemObjectId = readD();
        firstItemObjectId = readD();
        secondItemObjectId = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        if (player == null)
            return;

        if (player.isProtectionActive()) {
            player.getController().stopProtectionActiveTask();
        }

        if (player.isCasting()) {
            player.getController().cancelCurrentSkill();
        }

        Item tools = player.getInventory().getItemByObjId(compinationToolItemObjectId);
        if (tools == null)
            return;
        Item first = player.getInventory().getItemByObjId(firstItemObjectId);
        if (first == null)
            return;
        Item second = player.getInventory().getItemByObjId(secondItemObjectId);
        if (second == null)
            return;

        if (!RestrictionsManager.canUseItem(player, tools))
            return;

        CompositionAction action = new CompositionAction();

        if (!action.canAct(player, tools, first, second))
            return;

        action.act(player, tools, first, second);
    }
}
