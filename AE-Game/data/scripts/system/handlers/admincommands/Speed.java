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

import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.stats.calc.Stat2;
import org.typezero.gameserver.model.stats.calc.StatOwner;
import org.typezero.gameserver.model.stats.calc.functions.IStatFunction;
import org.typezero.gameserver.model.stats.calc.functions.StatFunction;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author ATracer
 */
public class Speed extends AdminCommand implements StatOwner {

	public Speed() {
		super("speed");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(admin, "Syntax //speed <percent>");
			return;
		}

		int parameter = 0;
		try {
			parameter = Integer.parseInt(params[0]);
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(admin, "Parameter should number");
			return;
		}

		if (parameter < 0 || parameter > 1000) {
			PacketSendUtility.sendMessage(admin, "Valid values are in 0-1000 range");
			return;
		}

		admin.getGameStats().endEffect(this);
		List<IStatFunction> functions = new ArrayList<IStatFunction>();
		functions.add(new SpeedFunction(StatEnum.SPEED, parameter));
		functions.add(new SpeedFunction(StatEnum.FLY_SPEED, parameter));
		admin.getGameStats().addEffect(this, functions);

		PacketSendUtility.broadcastPacket(admin, new SM_EMOTION(admin, EmotionType.START_EMOTE2, 0, 0), true);
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax //speed <percent>");
	}

	class SpeedFunction extends StatFunction {

		static final int speed = 6000;
		static final int flyspeed = 9000;
		int modifier = 1;

		SpeedFunction(StatEnum stat, int modifier) {
			this.stat = stat;
			this.modifier = modifier;
		}

		@Override
		public void apply(Stat2 stat) {
			switch (this.stat) {
				case SPEED:
					stat.setBase(speed + (speed * modifier) / 100);
					break;
				case FLY_SPEED:
					stat.setBase(flyspeed + (flyspeed * modifier) / 100);
					break;
			}
		}

		@Override
		public int getPriority() {
			return 60;
		}
	}

}
