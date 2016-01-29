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
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.typezero.gameserver.model.gameobjects.HouseObject;
import org.typezero.gameserver.model.gameobjects.UseableItemObject;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PortalCooldown;
import org.typezero.gameserver.model.items.ItemCooldown;
import org.typezero.gameserver.model.templates.InstanceCooltime;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_COOLDOWN;
import org.typezero.gameserver.network.aion.serverpackets.SM_SKILL_COOLDOWN;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author kecimis
 */
public class RemoveCd extends AdminCommand {

	public RemoveCd() {
		super("removecd");
	}

	@Override
	public void execute(Player admin, String... params) {
		VisibleObject target = admin.getTarget();
		if (target == null)
			target = admin;

		if (target instanceof Player) {
			Player player = (Player) target;
			if (params.length == 0) {
				List<Integer> delayIds = new ArrayList<Integer>();
				if (player.getSkillCoolDowns() != null) {
					long currentTime = System.currentTimeMillis();
					for (Entry<Integer, Long> en : player.getSkillCoolDowns().entrySet())
						delayIds.add(en.getKey());

					for (Integer delayId : delayIds)
						player.setSkillCoolDown(delayId, currentTime);

					delayIds.clear();
					PacketSendUtility.sendPacket(player, new SM_SKILL_COOLDOWN(player.getSkillCoolDowns()));
				}

				if (player.getItemCoolDowns() != null) {
					for (Entry<Integer, ItemCooldown> en : player.getItemCoolDowns().entrySet())
						delayIds.add(en.getKey());

					for (Integer delayId : delayIds)
						player.addItemCoolDown(delayId, 0, 0);

					delayIds.clear();
					PacketSendUtility.sendPacket(player, new SM_ITEM_COOLDOWN(player.getItemCoolDowns()));
				}

				if (player.getHouseRegistry() != null && player.getHouseObjectCooldownList().getHouseObjectCooldowns().size() > 0) {
					Iterator<HouseObject<?>> iter = player.getHouseRegistry().getObjects().iterator();
					while (iter.hasNext()) {
						HouseObject<?> obj = iter.next();
						if (obj instanceof UseableItemObject) {
							if (!player.getHouseObjectCooldownList().isCanUseObject(obj.getObjectId()))
								player.getHouseObjectCooldownList().addHouseObjectCooldown(obj.getObjectId(), 0);
						}
					}
				}

				if (player.equals(admin))
					PacketSendUtility.sendMessage(admin, "Your cooldowns were removed");
				else {
					PacketSendUtility.sendMessage(admin, "You have removed cooldowns of player: " + player.getName());
					PacketSendUtility.sendMessage(player, "Your cooldowns were removed by admin");
				}
			}
			else if (params[0].contains("instance")) {
				if (player.getPortalCooldownList() == null || player.getPortalCooldownList().getPortalCoolDowns() == null)
					return;
				if (params.length >= 2) {
					if (params[1].equalsIgnoreCase("all")) {
						List<Integer> mapIds = new ArrayList<Integer>();
						for (Entry<Integer, PortalCooldown> mapId : player.getPortalCooldownList().getPortalCoolDowns().entrySet())
							mapIds.add(mapId.getKey());

						for (Integer id : mapIds)
							player.getPortalCooldownList().addPortalCooldown(id, 0);

						mapIds.clear();
						if (player.equals(admin))
							PacketSendUtility.sendMessage(admin, "Your instance cooldowns were removed");
						else {
							PacketSendUtility
								.sendMessage(admin, "You have removed instance cooldowns of player: " + player.getName());
							PacketSendUtility.sendMessage(player, "Your instance cooldowns were removed by admin");
						}
					}
					else {
						int worldId = 0;
						try {
							worldId = Integer.parseInt(params[1]);
						}
						catch (NumberFormatException e) {
							PacketSendUtility.sendMessage(admin, "WorldId has to be integer or use \"all\"");
							return;
						}

						if (player.getPortalCooldownList().isPortalUseDisabled(worldId)) {
							player.getPortalCooldownList().addPortalCooldown(worldId, 0);

							if (player.equals(admin))
								PacketSendUtility.sendMessage(admin, "Your instance cooldown worldId: " + worldId + " was removed");
							else {
								PacketSendUtility.sendMessage(admin, "You have removed instance cooldown worldId: " + worldId
									+ " of player: " + player.getName());
								PacketSendUtility.sendMessage(player, "Your instance cooldown worldId: " + worldId
									+ " was removed by admin");
							}
						}
						else
							PacketSendUtility.sendMessage(admin, "You or your target can enter given instance");

					}
				}
				else
					PacketSendUtility.sendMessage(admin, "syntax: //removecd instance <all|worldId>");
			}
		}
		else
			PacketSendUtility.sendMessage(admin, "Only players are allowed as target");
	}
}
