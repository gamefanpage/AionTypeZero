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

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.assemblednpc.AssembledNpc;
import org.typezero.gameserver.model.assemblednpc.AssembledNpcPart;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.assemblednpc.AssembledNpcTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_NPC_ASSEMBLER;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.utils.idfactory.IDFactory;
import org.typezero.gameserver.world.World;
import java.util.Iterator;
import javolution.util.FastList;

/**
 *
 * @author xTz
 */
public class SpawnAssembledNpc  extends AdminCommand {

	public SpawnAssembledNpc() {
		super("spawnAssembledNpc");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params.length != 1) {
			onFail(player, null);
			return;
		}
		int spawnId = 0;
		try {
			spawnId = Integer.parseInt(params[0]);
		}
		catch(Exception e) {
			onFail(player, null);
			return;
		}

		AssembledNpcTemplate template = DataManager.ASSEMBLED_NPC_DATA.getAssembledNpcTemplate(spawnId);
		if (template == null) {
			PacketSendUtility.sendMessage(player, "This spawnId is Wrong.");
			return;
		}
		FastList<AssembledNpcPart> assembledPatrs = new FastList<AssembledNpcPart>();
		for (AssembledNpcTemplate.AssembledNpcPartTemplate npcPart : template.getAssembledNpcPartTemplates()) {
			assembledPatrs.add(new AssembledNpcPart(IDFactory.getInstance().nextId(), npcPart));
		}
		AssembledNpc npc = new AssembledNpc(template.getRouteId(), template.getMapId(), template.getLiveTime(), assembledPatrs);
		Iterator<Player> iter = World.getInstance().getPlayersIterator();
		Player findedPlayer = null;
		while (iter.hasNext()) {
			findedPlayer = iter.next();
			PacketSendUtility.sendPacket(findedPlayer, new SM_NPC_ASSEMBLER(npc));
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //spawnAssembledNpc <sapwnId>");
	}
}
