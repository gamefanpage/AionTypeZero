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

import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.AbsoluteStatOwner;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.stats.calc.StatOwner;
import org.typezero.gameserver.model.stats.calc.functions.IStatFunction;
import org.typezero.gameserver.model.stats.calc.functions.StatFunctionProxy;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author MrPoke
 */
public class Stat extends AdminCommand {

	private static final Logger log = LoggerFactory.getLogger(Stat.class);

	/**
	 * @param alias
	 */
	public Stat() {
		super("stat");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length >= 1) {
			VisibleObject target = admin.getTarget();
			if (target == null) {
				PacketSendUtility.sendMessage(admin, "No target selected");
				return;
			}
			if (target instanceof Creature) {
				Creature creature = (Creature) target;

				if (params.length == 1) {
					TreeSet<IStatFunction> stats = creature.getGameStats().getStatsByStatEnum(StatEnum.valueOf(params[0]));
					for (IStatFunction stat : stats) {
						PacketSendUtility.sendMessage(admin, stat.toString());
					}
				}
				else if (params.length == 2 && "details".equals(params[1])) {
					TreeSet<IStatFunction> stats = creature.getGameStats().getStatsByStatEnum(StatEnum.valueOf(params[0]));
					for (IStatFunction stat : stats) {
						String details = collectDetails(stat);
						PacketSendUtility.sendMessage(admin, details);
						log.info(details);
					}
				}
				else if (params.length > 0 && "abs".equals(params[0])) {
					if (!(target instanceof Player)) {
						PacketSendUtility.sendMessage(admin, "Only players can be selected");
						return;
					}
					if (params.length < 2) {
						PacketSendUtility.sendMessage(admin, "Syntax: stat abs [templateId|cancel]");
						return;
					}
					AbsoluteStatOwner absStats = ((Player) target).getAbsoluteStats();
					try {
						Integer templateId = Integer.parseInt(params[1]);
						absStats.setTemplate(templateId);
						absStats.apply();
						if (absStats.isActive()) {
							PacketSendUtility.sendMessage(admin, "Successfully applied absolute stats");
						}
						else {
							PacketSendUtility.sendMessage(admin, "No such template exists!");
						}
					}
					catch (NumberFormatException ex) {
						if (!"cancel".equalsIgnoreCase(params[1])) {
							PacketSendUtility.sendMessage(admin, "Not a number");
							return;
						}
						if (!absStats.isActive()) {
							PacketSendUtility.sendMessage(admin, "Nothing to cancel");
							return;
						}
						absStats.cancel();
						PacketSendUtility.sendMessage(admin, "Successfully canceled absolute stats");
						PacketSendUtility.sendPacket((Player) target, new SM_STATS_INFO((Player) target));
					}
				}
			}
		}
	}

	private String collectDetails(IStatFunction stat) {
		StringBuffer sb = new StringBuffer();
		sb.append(stat.toString() + "\n");
		if (stat instanceof StatFunctionProxy) {
			StatFunctionProxy proxy = (StatFunctionProxy) stat;
			sb.append(" -- " + proxy.getProxiedFunction().toString());
		}
		StatOwner owner = stat.getOwner();
		if (owner instanceof Effect) {
			Effect effect = (Effect) owner;
			sb.append("\n -- skillId: " + effect.getSkillId());
			sb.append("\n -- skillName: " + effect.getSkillName());
		}
		return sb.toString();
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub

	}

}
