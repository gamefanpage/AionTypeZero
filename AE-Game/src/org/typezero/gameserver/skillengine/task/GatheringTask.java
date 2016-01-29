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

package org.typezero.gameserver.skillengine.task;

import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.Gatherable;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.gather.GatherableTemplate;
import org.typezero.gameserver.model.templates.gather.Material;
import org.typezero.gameserver.network.aion.serverpackets.SM_GATHER_STATUS;
import org.typezero.gameserver.network.aion.serverpackets.SM_GATHER_UPDATE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.item.ItemPacketService.ItemUpdateType;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.services.item.ItemService.ItemUpdatePredicate;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class GatheringTask extends AbstractCraftTask {

	private GatherableTemplate template;
	private Material material;

	public GatheringTask(Player requestor, Gatherable gatherable, Material material, int skillLvlDiff) {
		super(requestor, gatherable, skillLvlDiff);
		this.template = gatherable.getObjectTemplate();
		this.material = material;
	}

	@Override
	protected void onInteractionAbort() {
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, 0, 0, 5));
		// TODO this packet is incorrect cause i need to find emotion of aborted gathering
		PacketSendUtility.broadcastPacket(requestor, new SM_GATHER_STATUS(requestor.getObjectId(), responder.getObjectId(),
			2));
	}

	@Override
	protected void onInteractionFinish() {
		((Gatherable) responder).getController().completeInteraction();
	}

	@Override
	protected void onInteractionStart() {
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, 0, 0, 0));
		PacketSendUtility.broadcastPacket(requestor, new SM_GATHER_STATUS(requestor.getObjectId(), responder.getObjectId(),
			0), true);
		PacketSendUtility.broadcastPacket(requestor, new SM_GATHER_STATUS(requestor.getObjectId(), responder.getObjectId(),
			1), true);
	}

	@Override
	protected void sendInteractionUpdate() {
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, currentSuccessValue,
			currentFailureValue, 1));
	}

	@Override
	protected void onFailureFinish() {
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, currentSuccessValue,
			currentFailureValue, 1));
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, currentSuccessValue,
			currentFailureValue, 7));
		PacketSendUtility.broadcastPacket(requestor, new SM_GATHER_STATUS(requestor.getObjectId(), responder.getObjectId(),
			3), true);
	}

	@Override
	protected boolean onSuccessFinish() {
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, currentSuccessValue, currentFailureValue, 2));
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, currentSuccessValue, currentFailureValue, 6));
		PacketSendUtility.broadcastPacket(requestor, new SM_GATHER_STATUS(requestor.getObjectId(), responder.getObjectId(), 2), true);
		PacketSendUtility.sendPacket(requestor, SM_SYSTEM_MESSAGE.STR_EXTRACT_GATHER_SUCCESS_1_BASIC(new DescriptionId(material.getNameid())));
		if (template.getEraseValue() > 0)
		 requestor.getInventory().decreaseByItemId(template.getRequiredItemId(), template.getEraseValue());
		ItemService.addItem(requestor, material.getItemid(), requestor.getRates().getGatheringCountRate());
		if (requestor.isInInstance()) {
			requestor.getPosition().getWorldMapInstance().getInstanceHandler().onGather(requestor, (Gatherable) responder);
		}
		((Gatherable) responder).getController().rewardPlayer(requestor);
		return true;
	}
}
