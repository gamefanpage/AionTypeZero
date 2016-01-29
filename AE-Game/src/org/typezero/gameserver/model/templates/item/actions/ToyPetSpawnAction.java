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

package org.typezero.gameserver.model.templates.item.actions;

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.controllers.observer.ItemUseObserver;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Kisk;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.KiskService;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.spawnengine.VisibleObjectSpawner;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.zone.ZoneInstance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.concurrent.Future;

/**
 * @author Sarynth, Source
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ToyPetSpawnAction")
public class ToyPetSpawnAction extends AbstractItemAction {

	@XmlAttribute
	protected int npcid;

	@XmlAttribute
	protected int time;

	/**
	 * @return the Npc Id
	 */
	public int getNpcId() {
		return npcid;
	}

	public int getTime() {
		return time;
	}

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (player.getFlyState() != 0) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_BINDSTONE_ITEM_WHILE_FLYING);
			return false;
		}
		if (player.isInInstance()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_BINDSTONE_FAR_FROM_NPC);
			return false;
		}
		if (KiskService.getInstance().haveKisk(player.getObjectId()) && CustomConfig.ENABLE_KISK_RESTRICTION) {
			PacketSendUtility.sendPacket(player, new  SM_SYSTEM_MESSAGE(1390160));
			return false;
		}
		if (!isPutKiskZone(player)) {
 			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_INVALID_LOCATION);
 			return false;
		}
		return true;
	}

	@Override
	public void act(final Player player, final Item parentItem, Item targetItem) {
		// ShowAction
		player.getController().cancelUseItem();
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
				parentItem.getObjectId(), parentItem.getItemId(), 10000, 0, 0), true);
		final ItemUseObserver observer = new ItemUseObserver() {
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.removeItemCoolDown(parentItem.getItemTemplate().getUseLimits().getDelayId());
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(parentItem.getItemTemplate().getNameId())));
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 2, 0), true);
			}
		};

		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				PacketSendUtility.broadcastPacket(player,
					new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, 1, 1), true);
				player.getObserveController().removeObserver(observer);
				// RemoveKisk
				if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1))
					return;
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_USE_ITEM(new DescriptionId(parentItem.getItemTemplate().getNameId())));
				float x = player.getX();
				float y = player.getY();
				float z = player.getZ();
				byte heading = (byte) ((player.getHeading() + 60) % 120);
				int worldId = player.getWorldId();
				int instanceId = player.getInstanceId();
				SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcid, x, y, z, heading);

				final Kisk kisk = VisibleObjectSpawner.spawnKisk(spawn, instanceId, player);
				final Integer objOwnerId = player.getObjectId();
				// Schedule Despawn Action
				Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						kisk.getController().onDelete();
					}
				}, 7200000);
				// Fixed 2 hours 2 * 60 * 60 * 1000
				kisk.getController().addTask(TaskId.DESPAWN, task);

				// ShowFinalAction
				//TODO Bad idea...
				//player.getController().cancelUseItem();
				player.getController().cancelTask(TaskId.ITEM_USE);
				KiskService.getInstance().regKisk(kisk, objOwnerId);

				if (kisk.getMaxMembers() > 1)
					kisk.getController().onDialogRequest(player);
				else
					KiskService.getInstance().onBind(kisk, player);
			}
		}, 10000));
	}

	private boolean isPutKiskZone(Player player) {
		for (ZoneInstance zone : player.getPosition().getMapRegion().getZones(player)) {
			if (!zone.canPutKisk())
				return false;
		}
		return true;
	}
}
