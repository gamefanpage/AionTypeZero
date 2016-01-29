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

package org.typezero.gameserver.model;

/**
 * @author Rolandas
 */
public enum DialogPage {

	NULL(DialogAction.NULL, 0),
	STIGMA(DialogAction.OPEN_STIGMA_WINDOW, 1),
	CREATE_LEGION(DialogAction.CREATE_LEGION, 2),
	HTML_PAGE_SHOW_SELECT_QUEST_REWARD_WINDOW1(5),
	HTML_PAGE_SHOW_SELECT_QUEST_REWARD_WINDOW2(6),
	HTML_PAGE_SHOW_SELECT_QUEST_REWARD_WINDOW3(7),
	HTML_PAGE_SHOW_SELECT_QUEST_REWARD_WINDOW4(8),
	HTML_PAGE_SHOW_SELECT_QUEST_REWARD_WINDOW5(45),
	HTML_PAGE_SHOW_SELECT_QUEST_REWARD_WINDOW6(46),
	HTML_PAGE_SHOW_SELECT_QUEST_REWARD_WINDOW7(47),
	HTML_PAGE_SHOW_SELECT_QUEST_REWARD_WINDOW8(48),
	HTML_PAGE_SHOW_SELECT_QUEST_REWARD_WINDOW9(49),
	HTML_PAGE_SHOW_SELECT_QUEST_REWARD_WINDOW10(50),
	HTML_PAGE_SHOW_MOVE_ITEM_SKIN_WINDOW(51),
        HTML_PAGE_SHOW_ITEM_UPGRADE(52),
        HTML_PAGE_SHOW_STIGMA_ENCHANT(53),
	VENDOR(DialogAction.OPEN_VENDOR, 13),
	RETRIEVE_CHAR_WAREHOUSE(DialogAction.RETRIEVE_CHAR_WAREHOUSE, 14),
	DEPOSIT_CHAR_WAREHOUSE(DialogAction.DEPOSIT_CHAR_WAREHOUSE, 15),
	RETRIEVE_ACCOUNT_WAREHOUSE(DialogAction.RETRIEVE_ACCOUNT_WAREHOUSE, 16),
	DEPOSIT_ACCOUNT_WAREHOUSE(DialogAction.DEPOSIT_ACCOUNT_WAREHOUSE, 17),
	MAIL(DialogAction.OPEN_POSTBOX, 18),
	CHANGE_ITEM_SKIN(DialogAction.CHANGE_ITEM_SKIN, 19),
	REMOVE_MANASTONE(DialogAction.REMOVE_MANASTONE, 20),
	GIVE_ITEM_PROC(DialogAction.GIVE_ITEM_PROC, 21),
	GATHER_SKILL_LEVELUP(DialogAction.GATHER_SKILL_LEVELUP, 23),
	LOOT(DialogAction.NULL, 24),
	LEGION_WAREHOUSE(DialogAction.OPEN_LEGION_WAREHOUSE, 25),
	PERSONAL_WAREHOUSE(DialogAction.OPEN_PERSONAL_WAREHOUSE, 26),
	COMPOUND_WEAPON(DialogAction.COMPOUND_WEAPON, 29),
	DECOMPOUND_WEAPON(DialogAction.DECOMPOUND_WEAPON, 30),
	HOUSING_MARKER(DialogAction.NULL, 32),							// Unknown
	HOUSING_LIFETIME(DialogAction.NULL, 33),						// Unknown
	CHARGE_ITEM(DialogAction.NULL, 35),									// Actually, two choices
	HOUSING_FRIENDLIST(DialogAction.HOUSING_FRIENDLIST, 36),
	HOUSING_POST(DialogAction.NULL, 37),								// Unknown
	HOUSING_AUCTION(DialogAction.HOUSING_PERSONAL_AUCTION, 38),
	HOUSING_PAY_RENT(DialogAction.HOUSING_PAY_RENT, 39),
	HOUSING_KICK(DialogAction.HOUSING_KICK, 40),
	HOUSING_CONFIG(DialogAction.HOUSING_CONFIG, 41),
	HTML_PAGE_SHOW_TOWN_CHALLENGE_TASK(43);

	private int id;
	private DialogAction action;
	
	private DialogPage(int id) {
		this.id = id;
	}

	private DialogPage(DialogAction action, int id) {
		this.id = id;
		this.action = action;
	}

	public int id() {
		return id;
	}
	
	public int actionId() {
		return action.id();
	}
	
	public static DialogPage getPageByAction(int dialogId) {
		for (DialogPage page : DialogPage.values()) {
			if (page.action == null)
				continue;
			if (page.actionId() == dialogId)
				return page;
		}
		return DialogPage.NULL;
	}
}
