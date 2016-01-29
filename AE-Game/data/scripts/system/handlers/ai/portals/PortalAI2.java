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

package ai.portals;

import ai.ActionItemNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.portal.PortalPath;
import org.typezero.gameserver.model.templates.portal.PortalUse;
import org.typezero.gameserver.model.templates.teleport.TeleportLocation;
import org.typezero.gameserver.model.templates.teleport.TeleporterTemplate;
import org.typezero.gameserver.services.teleport.PortalService;
import org.typezero.gameserver.services.teleport.TeleportService2;

/**
 * @author xTz
 */
@AIName("portal")
public class PortalAI2 extends ActionItemNpcAI2 {

	protected TeleporterTemplate teleportTemplate;
	protected PortalUse portalUse;

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		return true;
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		teleportTemplate = DataManager.TELEPORTER_DATA.getTeleporterTemplateByNpcId(getNpcId());
		portalUse = DataManager.PORTAL2_DATA.getPortalUse(getNpcId());
	}

	@Override
	protected void handleDialogStart(Player player) {
		AI2Actions.selectDialog(this, player, 0, -1);
		if (getTalkDelay() != 0) {
			super.handleDialogStart(player);
		}
		else {
			handleUseItemFinish(player);
		}
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		if (portalUse != null) {
			PortalPath portalPath = portalUse.getPortalPath(player.getRace());
			if (portalPath != null) {
				PortalService.port(portalPath, player, getObjectId());
			}
		}
		else if (teleportTemplate != null) {
			TeleportLocation loc = teleportTemplate.getTeleLocIdData().getTelelocations().get(0);
			if (loc != null) {
				TeleportService2.teleport(teleportTemplate, loc.getLocId(), player, getOwner(), TeleportAnimation.BEAM_ANIMATION);
			}
		}
	}

}
