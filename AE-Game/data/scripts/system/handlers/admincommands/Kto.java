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
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMap;
import gnu.trove.map.hash.TIntObjectHashMap;

//By Evil_dnk

public class Kto extends AdminCommand {
    public TIntObjectHashMap<WorldMap> worldMaps;

    public Kto() {
        super("kto");
    }

    @Override
    public void execute(Player player, String... params) {
        if ((params.length < 0) || (params.length < 1)) {
            onFail(player, null);
            return;
        }
        if (params[0].getBytes().length < 9 || params[0].getBytes().length > 9) {
            PacketSendUtility.sendMessage(player, "Wrong World Id");
            return;
        } else {
            String insid = params[0];
            int getId = Integer.parseInt(insid);
            ktotam(player, getId);
        }
    }

    private void ktotam(final Player player, int worldId) {

        WorldMap destinationMap = World.getInstance().getWorldMap(worldId);

        int i = 0;
        if (destinationMap != null) {
            for (final Player p : World.getInstance().getAllPlayers()) {
                if (p.getWorldId() == worldId) {
                    i++;
                    PacketSendUtility.sendMessage(player, " " + p.getName());
                }
            }
            if (i == 0) {
                PacketSendUtility.sendMessage(player, "There is empty");
            }
        } else
            PacketSendUtility.sendMessage(player, "Wrong World Id");
    }

    @Override
    public void onFail(Player player, String message) {
        PacketSendUtility.sendMessage(player, "syntax //kto <LocId>");

    }
}
