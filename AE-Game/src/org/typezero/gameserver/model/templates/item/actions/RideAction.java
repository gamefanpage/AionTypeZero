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

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.controllers.observer.ItemUseObserver;
import org.typezero.gameserver.controllers.observer.ObserverType;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.actions.PlayerMode;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.ride.RideInfo;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.skillengine.effect.AbnormalState;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.zone.ZoneInstance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rolandas, ginho1
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RideAction")
public class RideAction extends AbstractItemAction {

	@XmlAttribute(name = "npc_id")
	protected int npcId;

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (parentItem == null) {
			return false;
		}

		if (CustomConfig.ENABLE_RIDE_RESTRICTION) {
			for (ZoneInstance zone : player.getPosition().getMapRegion().getZones(player)) {
				if (!zone.canRide()) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401099));
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void act(final Player player, final Item parentItem, Item targetItem) {
		player.getController().cancelUseItem();
		if (player.isInPlayerMode(PlayerMode.RIDE)) {
			player.unsetPlayerMode(PlayerMode.RIDE);
			return;
		}

		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
				parentItem.getObjectId(), parentItem.getItemId(), 3000, 0, 0), true);
		final ItemUseObserver observer = new ItemUseObserver() {

			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.removeItemCoolDown(parentItem.getItemTemplate().getUseLimits().getDelayId());
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(parentItem.getItemTemplate().getNameId())));
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
						parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 2, 0), true);
				player.getObserveController().removeObserver(this);
			}

		};

		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
                if ((player.getState() & CreatureState.PRIVATE_SHOP.getId()) == CreatureState.PRIVATE_SHOP.getId()) {
                    player.getController().cancelUseItem();
                    if (player.isInPlayerMode(PlayerMode.RIDE)) {
                        player.unsetPlayerMode(PlayerMode.RIDE);
                    }
                    return;
                }
				player.unsetState(CreatureState.ACTIVE);
				player.setState(CreatureState.RESTING);
				player.getObserveController().removeObserver(observer);
				ItemTemplate itemTemplate = parentItem.getItemTemplate();
				player.setPlayerMode(PlayerMode.RIDE, getRideInfo());
				//PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_USE_ITEM(new DescriptionId(itemTemplate.getNameId())));
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_EMOTE2, 0, 0), true);
				PacketSendUtility.broadcastPacket(player,
						new SM_EMOTION(player, EmotionType.RIDE, 0, getRideInfo().getNpcId()), true);
				PacketSendUtility.broadcastPacket(player,
						new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, 1, 1), true);
				player.getController().cancelTask(TaskId.ITEM_USE);
				QuestEngine.getInstance().rideAction(new QuestEnv(null, player, 0, 0), itemTemplate.getTemplateId());
			}

		}, 3000));
		
		ActionObserver rideObserver = new ActionObserver(ObserverType.ABNORMALSETTED) {

			@Override
			public void abnormalsetted(AbnormalState state) {
				if ((state.getId() & AbnormalState.DISMOUT_RIDE.getId()) > 0)
					player.unsetPlayerMode(PlayerMode.RIDE);
			}
		};
		player.getObserveController().addObserver(rideObserver);
		player.setRideObservers(rideObserver);
		
		//TODO some mounts have lower change of dismounting
		ActionObserver attackedObserver = new ActionObserver(ObserverType.ATTACKED) {

			@Override
			public void attacked(Creature creature) {
				if (Rnd.get(1000) < 200)//20% from client action file
					player.unsetPlayerMode(PlayerMode.RIDE);
			}
		};
		player.getObserveController().addObserver(attackedObserver);
		player.setRideObservers(attackedObserver);
		
		ActionObserver dotAttackedObserver = new ActionObserver(ObserverType.DOT_ATTACKED) {

			@Override
			public void dotattacked(Creature creature, Effect dotEffect) {
				if (Rnd.get(1000) < 200)//20% from client action file
					player.unsetPlayerMode(PlayerMode.RIDE);
			}
		};
		player.getObserveController().addObserver(dotAttackedObserver);
		player.setRideObservers(dotAttackedObserver);
	}

	public RideInfo getRideInfo() {
		return DataManager.RIDE_DATA.getRideInfo(npcId);
	}

}