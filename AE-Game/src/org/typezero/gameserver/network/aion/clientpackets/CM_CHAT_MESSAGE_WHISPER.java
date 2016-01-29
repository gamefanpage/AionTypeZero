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

import org.typezero.gameserver.configs.administration.AdminConfig;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.configs.main.LoggingConfig;
import org.typezero.gameserver.model.ChatType;
import org.typezero.gameserver.model.gameobjects.player.FriendList;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_MESSAGE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.restrictions.RestrictionsManager;
import org.typezero.gameserver.services.MuiService;
import org.typezero.gameserver.services.NameRestrictionService;
import org.typezero.gameserver.services.WordFilterService;
import org.typezero.gameserver.utils.ChatUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Packet that reads Whisper chat messages.<br>
 *
 * @author SoulKeeper
 */
public class CM_CHAT_MESSAGE_WHISPER extends AionClientPacket {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger("CHAT_LOG");

	/**
	 * To whom this message is sent
	 */
	private String name;

	/**
	 * Message text
	 */
	private String message;

	/**
	 * Constructs new client packet instance.
	 *
	 * @param opcode
	 */
	public CM_CHAT_MESSAGE_WHISPER(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);

	}

	/**
	 * Read message
	 */
	@Override
	protected void readImpl() {
		name = readS();
		message = readS();
	}

	/**
	 * Print debug info
	 */
	@Override
	protected void runImpl() {
		name = name.replace("\uE0B0", "");
		name = name.replace("\uE0AD", "");
		name = name.replace("\uE0AE", "");
                name = name.replace("\uE0AF", "");
                name = name.replace("\uE0B0", "");
		name = ChatUtil.getRealAdminName(name);

		String formatname = Util.convertName(name);

		Player sender = getConnection().getActivePlayer();
		Player receiver = World.getInstance().findPlayer(formatname);

		message = WordFilterService.replaceBanWord(sender, message);

		if (LoggingConfig.LOG_CHAT)
			log.info(String.format("[MESSAGE] [%s] Whisper To: %s, Message: %s", sender.getName(), formatname, message));

        if (receiver == null) {
			sendPacket(SM_SYSTEM_MESSAGE.STR_NO_SUCH_USER(formatname));
		}
        else if (receiver.getFriendList().getStatus() == FriendList.Status.OFFLINE && sender.getAccessLevel() < AdminConfig.GM_LEVEL)
        {
            sendPacket(SM_SYSTEM_MESSAGE.STR_NO_SUCH_USER(formatname));
        } else if (!receiver.isWispable()) {
            PacketSendUtility.sendMessage(sender, MuiService.getInstance().getMessage("WHISPER"));
            return;
        } else if (sender.getLevel() < CustomConfig.LEVEL_TO_WHISPER) {
            sendPacket(SM_SYSTEM_MESSAGE.STR_CANT_WHISPER_LEVEL(String.valueOf(CustomConfig.LEVEL_TO_WHISPER)));
        } else if (receiver.getBlockList().contains(sender.getObjectId())) {
            sendPacket(SM_SYSTEM_MESSAGE.STR_YOU_EXCLUDED(receiver.getName()));
        } else if ((!CustomConfig.SPEAKING_BETWEEN_FACTIONS)
                && (sender.getRace().getRaceId() != receiver.getRace().getRaceId())
                && (sender.getAccessLevel() < AdminConfig.GM_LEVEL) && (receiver.getAccessLevel() < AdminConfig.GM_LEVEL)) {
            sendPacket(SM_SYSTEM_MESSAGE.STR_NO_SUCH_USER(formatname));
        } else {
            if (RestrictionsManager.canChat(sender))
                //PacketSendUtility.sendPacket(receiver, new SM_MESSAGE(sender, NameRestrictionService.filterMessage(message), ChatType.WHISPER));
				PacketSendUtility.sendPacket(receiver, new SM_MESSAGE(sender, message, ChatType.WHISPER));
        }
	}
}
