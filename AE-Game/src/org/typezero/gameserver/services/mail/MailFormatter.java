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

package org.typezero.gameserver.services.mail;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.LetterType;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.siege.SiegeLocation;
import org.typezero.gameserver.model.templates.mail.MailPart;
import org.typezero.gameserver.model.templates.mail.MailTemplate;

/**
 * @author Rolandas
 */
public final class MailFormatter {

	public static void sendBlackCloudMail(String recipientName, final int itemObjectId, final int itemCount) {
		final MailTemplate template = DataManager.SYSTEM_MAIL_TEMPLATES
			.getMailTemplate("$$CASH_ITEM_MAIL", "", Race.PC_ALL);

		MailPart formatter = new MailPart() {

			@Override
			public String getParamValue(String name) {
				if ("itemid".equals(name))
					return Integer.toString(itemObjectId);
				else if ("count".equals(name))
					return Integer.toString(itemCount);
				else if ("unk1".equals(name))
					return "0";
				else if ("purchasedate".equals(name))
					return Long.toString(System.currentTimeMillis() / 1000);
				return "";
			}

		};

		String title = template.getFormattedTitle(formatter);
		String body = template.getFormattedMessage(formatter);

		SystemMailService.getInstance().sendMail("$$CASH_ITEM_MAIL", recipientName, title, body, itemObjectId, itemCount,
			0, LetterType.BLACKCLOUD);
	}

	public static void sendHouseMaintenanceMail(final House ownedHouse, int warnCount, final long impoundTime) {
		String templateName = "";
		switch (warnCount) {
			case 1:
				templateName = "$$HS_OVERDUE_FIRST";
				break;
			case 2:
				templateName = "$$HS_OVERDUE_SECOND";
				break;
			case 3:
				templateName = "$$HS_OVERDUE_FINAL";
				break;
			default:
				return;
		}

		final MailTemplate template = DataManager.SYSTEM_MAIL_TEMPLATES.getMailTemplate(templateName, "",
			ownedHouse.getPlayerRace());

		MailPart formatter = new MailPart() {

			@Override
			public String getParamValue(String name) {
				if ("address".equals(name))
					return Integer.toString(ownedHouse.getAddress().getId());
				else if ("datetime".equals(name))
					return Long.toString(impoundTime / 1000);
				return "";
			}

		};

		String title = template.getFormattedTitle(null);
		String message = template.getFormattedMessage(formatter);

		SystemMailService.getInstance().sendMail(templateName, ownedHouse.getButler().getMasterName(), title, message, 0,
			0, 0, LetterType.NORMAL);
	}

	public static void sendHouseAuctionMail(final House ownedHouse, final PlayerCommonData playerData,
		final AuctionResult result, final long time, long returnKinah) {
		final MailTemplate template = DataManager.SYSTEM_MAIL_TEMPLATES.getMailTemplate("$$HS_AUCTION_MAIL", "",
			playerData.getRace());
		if (ownedHouse == null || playerData == null || result == null)
		   return;

		MailPart formatter = new MailPart() {

			@Override
			public String getParamValue(String name) {
				if ("address".equals(name))
					return Integer.toString(ownedHouse.getAddress().getId());
				else if ("datetime".equals(name))
					return Long.toString(time / 1000);
				else if ("resultid".equals(name))
					return Integer.toString(result.getId());
				else if ("raceid".equals(name))
					return Integer.toString(playerData.getRace().getRaceId());
				return "";
			}
		};

		String title = template.getFormattedTitle(formatter);
		String message = template.getFormattedMessage(formatter);

		SystemMailService.getInstance().sendMail("$$HS_AUCTION_MAIL", playerData.getName(), title, message, 0, 0,
			returnKinah, LetterType.NORMAL);
	}

	public static void sendAbyssRewardMail(final SiegeLocation siegeLocation, final PlayerCommonData playerData,
		final AbyssSiegeLevel level, final SiegeResult result, final long time, int attachedItemObjId,
		long attachedItemCount, long attachedKinahCount) {

		final MailTemplate template = DataManager.SYSTEM_MAIL_TEMPLATES.getMailTemplate("$$ABYSS_REWARD_MAIL", "",
			playerData.getRace());

		MailPart formatter = new MailPart() {

			@Override
			public String getParamValue(String name) {
				if ("siegelocid".equals(name))
					return Integer.toString(siegeLocation.getTemplate().getId());
				else if ("datetime".equals(name))
					return Long.toString(time / 1000);
				else if ("rankid".equals(name))
					return Integer.toString(level.getId());
				else if ("raceid".equals(name))
					return Integer.toString(playerData.getRace().getRaceId());
				else if ("resultid".equals(name))
					return Integer.toString(result.getId());
				return "";
			}
		};

		String title = template.getFormattedTitle(formatter);
		String message = template.getFormattedMessage(formatter);

		SystemMailService.getInstance().sendMail("$$ABYSS_REWARD_MAIL", playerData.getName(), title, message,
			attachedItemObjId, attachedItemCount, attachedKinahCount, LetterType.NORMAL);
	}
}
