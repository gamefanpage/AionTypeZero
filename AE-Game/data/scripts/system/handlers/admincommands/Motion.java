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

import java.util.ArrayList;
import java.util.List;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.stats.calc.StatOwner;
import org.typezero.gameserver.model.stats.calc.functions.IStatFunction;
import org.typezero.gameserver.model.stats.calc.functions.StatAddFunction;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.services.MotionLoggingService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;


/**
 * @author kecimis
 *
 */
public class Motion extends AdminCommand implements StatOwner {

	public Motion() {
		super("motion");
	}

	/* (non-Javadoc)
	 * @see org.typezero.gameserver.utils.chathandlers.AdminCommand#execute(org.typezero.gameserver.model.gameobjects.player.Player, java.lang.String[])
	 */
	@Override
	public void execute(Player player, String... params) {
		if (params.length == 0) {
			onFail(player, "");
			return;
		}
		if (params[0].equalsIgnoreCase("help")) {
			onFail(player, "");
			PacketSendUtility.sendMessage(player, "//motion start - starts MotionLoggingService, plus loads data from db");
			PacketSendUtility.sendMessage(player, "//motion advanced - turns on/of advanced logging info");
			PacketSendUtility.sendMessage(player, "//motion as (value) - adds attack speed");
			PacketSendUtility.sendMessage(player, "//motion analyze - creats .txt files in SERVER_DIR/motions with detailed info about motions");
			PacketSendUtility.sendMessage(player, "//motion savetosql - saves content of MotionLoggingService to database");
			PacketSendUtility.sendMessage(player, "//motion createxml - create new_motion_times.xml in static_data/skills");
		}
		else if (params[0].equalsIgnoreCase("start")) {
			MotionLoggingService.getInstance().start();
			PacketSendUtility.sendMessage(player, "MotionLogginService was started!\nData loaded from DB.");
		}
		else if (params[0].equalsIgnoreCase("analyze")) {
			MotionLoggingService.getInstance().createAnalyzeFiles();
			PacketSendUtility.sendMessage(player, "Created testing files!");
		}
		else if (params[0].equalsIgnoreCase("createxml")) {
			MotionLoggingService.getInstance().createFinalFile();
			PacketSendUtility.sendMessage(player, "Created new_motion_times.xml in data/static_data/skills!");
		}
		else if (params[0].equalsIgnoreCase("savetosql")) {
			MotionLoggingService.getInstance().saveToSql();
			PacketSendUtility.sendMessage(player, "MotionLog data saved to sql!");
		}
		else if (params[0].equalsIgnoreCase("advanced")) {
			MotionLoggingService.getInstance().setAdvancedLog((!MotionLoggingService.getInstance().getAdvancedLog()));
			PacketSendUtility.sendMessage(player, "AdvancedLog set to: "+MotionLoggingService.getInstance().getAdvancedLog());
		}
		else if (params[0].equalsIgnoreCase("as")) {
			int parameter = 10000;
			if (params.length == 2) {
				try {
					parameter = Integer.parseInt(params[1]);
				}
				catch (NumberFormatException e) {
					PacketSendUtility.sendMessage(player, "Parameter should number");
					return;
				}
			}
			this.addAttackSpeed(player, -parameter);
			PacketSendUtility.sendMessage(player, "Attack Speed updated");
		}
		else
			onFail(player, "");
	}

	private void addAttackSpeed(Player player, int i) {
		if (i == 0) {
			player.getGameStats().endEffect(this);
		}	else {
			List<IStatFunction> modifiers = new ArrayList<IStatFunction>();
			modifiers.add(new StatAddFunction(StatEnum.ATTACK_SPEED, i, true));
			player.getGameStats().endEffect(this);
			player.getGameStats().addEffect(this, modifiers);
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax: //motion <HELP|analyze|savetosql|advanced|as>");
	}

}
