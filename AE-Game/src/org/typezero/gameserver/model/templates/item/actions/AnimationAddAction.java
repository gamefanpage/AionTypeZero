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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.motion.Motion;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.network.aion.serverpackets.SM_MOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnimationAddAction")
public class AnimationAddAction
    extends AbstractItemAction
{

    @XmlAttribute
    protected Integer idle;
    @XmlAttribute
    protected Integer run;
    @XmlAttribute
    protected Integer jump;
    @XmlAttribute
    protected Integer rest;
    @XmlAttribute
    protected Integer shop;
    @XmlAttribute
    protected Integer minutes;

		@Override
		public boolean canAct(Player player, Item parentItem, Item targetItem) {
			if (parentItem == null) { // no item selected.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
				return false;
			}

			return true;
		}

		@Override
		public void act(final Player player, final Item parentItem, Item targetItem) {
			player.getController().cancelUseItem();
			PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(),
				parentItem.getItemTemplate().getTemplateId(), 1000, 0, 0));
			player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					if (player.getInventory().decreaseItemCount(parentItem, 1) != 0)
						return;
					if (idle != null){
						addMotion(player, idle);
					}
					if (run != null){
						addMotion(player, run);
					}
					if (jump != null){
						addMotion(player, jump);
					}
					if (rest != null){
						addMotion(player, rest);
					}
					if (shop != null){
						addMotion(player, shop);
					}
					PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
						parentItem.getObjectId(), parentItem.getItemId(), 0, 1, 0));
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300423, new DescriptionId(parentItem.getItemTemplate().getNameId())));
					PacketSendUtility.broadcastPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()), false);
				}
			}, 1000));
		}

		private void addMotion(Player player, int motionId){
			Motion motion = new Motion(motionId, minutes == null ? 0 : (int)(System.currentTimeMillis()/1000)+minutes*60, true);
			player.getMotions().add(motion, true);
			PacketSendUtility.sendPacket(player, new SM_MOTION((short) motion.getId(), motion.getRemainingTime()));
		}
}
