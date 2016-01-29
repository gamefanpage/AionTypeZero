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

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_CUSTOM_PACKET;
import org.typezero.gameserver.network.aion.serverpackets.SM_CUSTOM_PACKET.PacketElementType;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * Send packet in raw format.
 *
 * @author Luno
 * @author Aquanox
 */
public class Raw extends AdminCommand {

	private static final File ROOT = new File("data/packets/");

	private static final Logger logger = LoggerFactory.getLogger(Raw.class);

	public Raw() {
		super("raw");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length != 1) {
			PacketSendUtility.sendMessage(admin, "Usage: //raw [name]");
			return;
		}

		File file = new File(ROOT, params[0] + ".txt");

		if (!file.exists() || !file.canRead()) {
			PacketSendUtility.sendMessage(admin, "Wrong file selected.");
			return;
		}

		try {
			List<String> lines = FileUtils.readLines(file);

			SM_CUSTOM_PACKET packet = null;
			PacketSendUtility.sendMessage(admin, "lines "+lines.size());
			boolean init = false;
			for (int r = 0 ; r< lines.size(); r++){
				String row = lines.get(r);
				String[] tokens = row.substring(0, 48).trim().split(" ");
				int len = tokens.length;

				for (int i = 0; i < len; i++) {
					if (!init) {
						if (i == 1){
						packet = new SM_CUSTOM_PACKET(Integer.decode("0x"+tokens[i]+tokens[i-1]));
						init = true;
						}
					}
					else if ( r > 0 || i > 4){
						packet.addElement(PacketElementType.C, "0x" + tokens[i]);
					}
				}
			}
			if (packet != null){
				PacketSendUtility.sendMessage(admin, "Packet send..");
				PacketSendUtility.sendPacket(admin, packet);
			}
		}
		catch (Exception e) {
			PacketSendUtility.sendMessage(admin, "An error has occurred.");
			logger.warn("IO Error.", e);
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Usage: //raw [name]");
	}
}
