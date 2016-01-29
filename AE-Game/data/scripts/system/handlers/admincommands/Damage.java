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

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Source
 */
public class Damage extends AdminCommand {

	public Damage() {
		super("damage");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length > 1)
			onFail(admin, null);

		VisibleObject target = admin.getTarget();
		if (target == null)
			PacketSendUtility.sendMessage(admin, "No target selected");
		else if (target instanceof Creature) {
			Creature creature = (Creature) target;
			int dmg;
			try {
				String percent = params[0];
				Pattern damage = Pattern.compile("([^%]+)%");
				Matcher result = damage.matcher(percent);

				if (result.find()) {
					dmg = Integer.parseInt(result.group(1));

					if (dmg < 100)
						creature.getController().onAttack(admin, (int) (dmg / 100f * creature.getLifeStats().getMaxHp()), true);
					else
						creature.getController().onAttack(admin, creature.getLifeStats().getMaxHp() + 1, true);
				}
				else
					creature.getController().onAttack(admin, Integer.parseInt(params[0]), true);
			}
			catch (Exception ex) {
				onFail(admin, null);
			}
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //damage <dmg | dmg%>"
				+ "\n<dmg> must be a number.");
	}

}
