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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.configs.main.SecurityConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import org.typezero.gameserver.network.aion.serverpackets.SM_CAPTCHA;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.PunishmentService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Cura
 */
public class CM_CAPTCHA extends AionClientPacket {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(CM_CAPTCHA.class);

	private int type;
	private int count;
	private String word;

	/**
	 * @param opcode
	 */
	public CM_CAPTCHA(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		type = readC();

		switch (type) {
			case 0x02:
				count = readC();
				word = readS();
				break;
			default:
				log.warn("Unknown CAPTCHA packet type? 0x" + Integer.toHexString(type).toUpperCase());
				break;
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();

		switch (type) {
			case 0x02:
				if (player.getCaptchaWord().equalsIgnoreCase(word)) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400270));
					PacketSendUtility.sendPacket(player, new SM_CAPTCHA(true, 0));

					PunishmentService.setIsNotGatherable(player, 0, false, 0);

					// fp bonus (like retail)
					player.getLifeStats().increaseFp(TYPE.FP, SecurityConfig.CAPTCHA_BONUS_FP_TIME);
				}
				else {
					int banTime = SecurityConfig.CAPTCHA_EXTRACTION_BAN_TIME + (SecurityConfig.CAPTCHA_EXTRACTION_BAN_ADD_TIME * count);

					if (count < 3) {
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400271, 3 - count));
						PacketSendUtility.sendPacket(player, new SM_CAPTCHA(false, banTime));
						PunishmentService.setIsNotGatherable(player, count, true, banTime * 1000L);
					}
					else {
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400272));
						PunishmentService.setIsNotGatherable(player, count, true, banTime * 1000L);
					}
				}
				break;
		}
	}
}
