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
 */

package mui;

import org.typezero.gameserver.utils.mui.handlers.GeneralMuiHandler;
import org.typezero.gameserver.utils.mui.handlers.MuiName;

/**
 * Created by Magenik on 6/08/2015.
 */

@MuiName("en")
public class EnglishLanguage extends GeneralMuiHandler {

	@Override
	public String getMessage(String name, Object... params) {
		return String.format(messages.valueOf(name).getMessage(), params);
	}

	private static enum messages {

		HELLO_WORLD("Welcome to the wold of %s!"),
		HELLO_REMEMBER("Please remember:"),
		HELLO_STAFF("Announcement : %s Staff will never ask for your password!\n"),
		HELLO_RULLES("Advertising an other private server is prohibited!"),
		HELLO_ANNOUNCE("Server Info :"),
		HELLO_ANNOUNCE_INFO1("Server Info 1"),
		HELLO_ANNOUNCE_INFO2("Server Info 2"),
		HELLO_ANNOUNCE_INFO3("Server Info 3"),
		HELLO_ENJOY("Enjoy your stay on %s."),
		MEMBERSHIP_LOGIN("Your Status:\n"),
		MEMBERSHIP_STATUS(" %s"),
		MEMBERSHIP_PREMIUM("Premium"),
		MEMBERSHIP_VIP("VIP"),
		MEMBERSHIP_CRAFT("CritCraft"),
		MEMBERSHIP_AP("Abyss Point"),
		MEMBERSHIP_COLLECTION("Craft"),
		MEMBERSHIP_EXPIRE("\nExpiration date: %s\n"),
		ANNOUNCE_RATES("[color:Server;0 1 0] [color:Rate;0 1 0][color::;0 1 0] "),
		ANNOUNCE_RATES_XP("[color:Exp:;0 1 0] x %s"),
		ANNOUNCE_RATES_QS("[color:Quest :;0 1 0] x %s"),
		ANNOUNCE_RATES_DR("[color:Drop:;0 1 0] x %s"),
		ANNOUNCE_RATES_AP("[color:A.P : ;0 1 0] x %s"),
		MEMBERSHIP_WORLD_ANNOUNCE("%s login in game."),
		NO_ITEM_TO_TRADE("Данный итем отсутствует , сообщите администрации."),
		ONLINE_BONUS_TOLL("На ваш бонусный счет было зачислено %d Gpoint."),
		JSON_TITLE("Lastest news in VK %s:"),
		VK_COMMENTS_LIKE("<br>Date: %s<br><font color='829BAF'>Comments: %d &nbsp;Likes: %d</font>"),
		CHEST_KEY("Чтобы открыть ящик, нужен ключ."),
		PIG_EVENT_START("В Оке Тиамаранты начался ивент <Свинки> , поспешите принять участие !"),
		PIG_EVENT_REWARD(" нашел кабанчика и получил [item:%d]."),
		PIG_EVENT_STOP("Ивент <Свинки> завершен, до следующих встреч ! Хрю- хрю! :)"),
		ABYSS_EVENT_START("В Бездне начался ивент <Сокровища балауров> , поспешите принять участие !"),
		ABYSS_EVENT_REWARD(" нашел Сундук с сокровищами балауров и получил [item:%d]."),
		ABYSS_EVENT_STOP("Ивент <Сокровища балауров> завершен."),
		STATICDOORSERVICE("Для открытия двери необходим : [item:%d]"),
		PORTALSERVICE("Для входа необходим : [item:%d]"),
		MENOTIOS_SPAWN("В Бездне появился Гневный циклоп Менотиос."),
		SYNAYAKI_SPAWN("В Оке Тиамаранты появился Командир Сунаяка."),
		SYNAYAKI_DONE("Командир Сунаяка исчез."),
		MENOTIOS_DONE("Гневный циклоп Менотиос исчез."),
		DEFENCE_BASTION("Вы управляете заряженной водной пушкой."),
		DEFENCE_BASTION_NO("Нет генератора гаубицы. Управление невозможно."),
		DEFENCE_BASTION_BIG("Вы управляете большой пушкой армии. Внимание: На левом укреплении появился Офицер осадного отряда 43-го легиона."),
		DEFENCE_BASTION_BIG_NO("Нет генератора гаубицы. Управление невозможно."),
		TANK_BASTION("Вы управляете заряженной боевой повозкой Бритры."),
		TANK_BASTION_NO("Нет ключа от осадного орудия. Управление невозможно."),
		SIEGE_TELEPORT_BALAUR("Вы не можете телепортироваться в крепость принадлежащую балаурам."),
		SIEGE_TELEPORT_RACE("Вы не можете телепортироваться в крепость принадлежащую "),
		SIEGE_TELEPORT_ASSAULT("Вы не можете телепортироваться в крепость во время сражения."),
		SIEGE_6021_MINE1("Сработали мины северного рва крепости Парадес. Следующий заряд будет готов через 10 мин."),
		SIEGE_6021_MINE2("Сработали мины западного рва крепости Парадес. Следующий заряд будет готов через 10 мин."),
		SIEGE_6021_MINE3("Сработали мины южного рва крепости Парадес. Следующий заряд будет готов через 10 мин."),
		SIEGE_6021_MINE4("Сработали мины восточного рва крепости Парадес. Следующий заряд будет готов через 10 мин."),
		SIEGE_6021_DOOR_CONTROLLER1("Северо-западные ворота были открыты механизмом крепости."),
		SIEGE_6021_DOOR_CONTROLLER2("Юго-восточные ворота были открыты механизмом крепости."),
		SIEGE_6021_BOXBOMB("Берегись! Коробка со взрывчаткой сейчас взорвется."),
		SIEGE_6021_FIRE("Берегись! Подставка под факел сейчас сильно воспламенится."),
		LEGION_GAUBICE_SMALL("Вы сели за маленькую гаубицу Легиона."),
		LEGION_GAUBICE_NORMAL("Вы сели за среднюю гаубицу Легиона."),
		LEGION_GAUBICE_BIG("Вы сели за большую гаубицу Легиона."),
		SIEGE_START("Вы не можете переместится в крепость во время осады."),
		SIEGE_RACE("Вы не можете переместится в крепость которая принадлежит другой расе."),
		ARENA_AN1("Вы слышали новость о боевых аренах?"),
		ARENA_AN2("Боевые арены теперь работают круглосуточно!"),
		ARENA_AN3("Поспешите на боевые арены!"),
		ARENA_AN4("У меня для Вас есть новость!"),
		ARENA_AN5("Привествую Вас"),
		ARENA_AN6("Как Ваши дела"),
		NEW_AN("Привествую Вас"),
		NEW_AN2("приятной игры на сервере AtreiaWorld.com!"),
		EVENT_ALCHIMIC("Для активации нужна свеча. Обратитесь к агенту по обмену эфирной золы."),
		KATALAM_BOSS_SPAWN("В Каталаме появился подземный монстр."),
		KATALAM_BOSS_DONE("Подземный монстр исчез."),
		GERA_VS_TEGRAK_SPAWN("[pos:В Герхе началась битва посланников.;600100000 841.3 1109.7 332.7 0]"),
		GERHA_INVASION_SPAWN("В Герхе появился посланник Бритры."),
		GERHA_INVASION_SPAWN_1( "Возможные точки вторжения: [pos:1 точка;600100000 1158.5 1075.4 303.5 0], [pos:2 точка;600100000 681.2 1001.9 275.07 0], [pos:3 точка.;600100000 387.4 1809.4 226.4 0], [pos:4 точка;600100000 1838.4 141.1 242.5 0]."),
		KATALAM_INVASION_SPAWN("В Северном Каталаме появился посланник Бритры."),
		KATALAM_INVASION_SPAWN_1("Возможные точки вторжения: [pos:1 точка;600050000 1330.6 742.2 124.2 0], [pos:2 точка;600050000 2882.2 659.9 262.2 0], [pos:3 точка;600050000 1291.8 2063.3 62.3 0], [pos:4 точка;600050000 2800.5 2239.3 266.1 0]."),
		DANARIA_INVASION_SPAWN("В Южном Каталаме появился посланник Бритры."),
		DANARIA_INVASION_SPAWN_1("Возможные точки вторжения: [pos:1 точка;600060000 2715.9 1834.8 148.7 0], [pos:2 точка;600060000 1681.7 1619.8 140.6 0], [pos:3 точка;600060000 895.2 1722.3 361.0 0], [pos:4 точка;600060000 2444.3 1262.4 95.9 0], [pos:5 точка;600060000 1548.1 2153.0 158.2 0]."),
		DAEVA_DAY("Вы уже получили ивентовый баф , приходите позже."),
		COIN_GOLD("Для обмена нужна золотая медаль!"),
		T_PORTAL("Для входа нужен статус [color: VIP ;1 0 0] - аккаунта."),
		COST("Для покупки требуется : "),
		COST_END("Потрачено : "),
		BUY_STAR("Вы хотите купить Черную карту(30д.) ?"),
		BUY_MONEY("Вы хотите купить Большую монетку(5шт.) ?"),
		ALL_POINTS("Your game balance : "),
		NOFLY_SID("Вы не можете переместиться, находясь в состоянии отдыха."),
		ADD_BUFF("Для получения льгот требуется : "),
		BUY_BUFF("Вы хотите получить льготы управляющего (1час.) ?");
		private String message;

		private messages(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}
}
