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

package org.typezero.gameserver.services.player;

import org.typezero.gameserver.configs.administration.AdminConfig;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Kisk;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceService;
import org.typezero.gameserver.model.team2.common.legacy.GroupEvent;
import org.typezero.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import org.typezero.gameserver.model.team2.group.PlayerGroupService;
import org.typezero.gameserver.model.templates.item.ItemUseLimits;
import org.typezero.gameserver.model.vortex.VortexLocation;
import org.typezero.gameserver.network.aion.serverpackets.*;
import org.typezero.gameserver.services.SerialKillerService;
import org.typezero.gameserver.services.VortexService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.audit.AuditLogger;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMap;
import org.typezero.gameserver.world.WorldPosition;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author Jego, xTz
 */
public class PlayerReviveService {

	public static final void duelRevive(Player player) {
		revive(player, 30, 30, false, 0);
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RESURRECT), true);
		player.getGameStats().updateStatsAndSpeedVisually();
		player.unsetResPosState();
	}

	public static final void skillRevive(Player player) {
		if (!(player.getResStatus())) {
			cancelRes(player);
			return;
		}
		revive(player, 10, 10, true, player.getResurrectionSkill());
		if (player.getIsFlyingBeforeDeath()) {
			player.setState(CreatureState.FLYING);
		}
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RESURRECT), true);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		//if player was flying before res, start flying
		if (player.getIsFlyingBeforeDeath()) {
			player.getFlyController().startFly();
		}
		player.getGameStats().updateStatsAndSpeedVisually();

		if (player.isInPrison())
			TeleportService2.teleportToPrison(player);

		if (player.isInResPostState())
			TeleportService2.teleportTo(player, player.getWorldId(), player.getInstanceId(), player.getResPosX(), player.getResPosY(), player.getResPosZ());
		player.unsetResPosState();
		//unset isflyingbeforedeath
		player.setIsFlyingBeforeDeath(false);
	}

	public static final void rebirthRevive(Player player) {
		if (!player.canUseRebirthRevive()) {
			return;
		}
		if (player.getRebirthResurrectPercent() <= 0) {
			PacketSendUtility.sendMessage(player, "Error: Rebirth effect missing percent.");
			player.setRebirthResurrectPercent(5);
		}
		boolean soulSickness = true;
		int rebirthResurrectPercent = player.getRebirthResurrectPercent();
		if (player.getAccessLevel() >= AdminConfig.ADMIN_AUTO_RES) {
			rebirthResurrectPercent = 100;
			soulSickness = false;
		}

		revive(player, rebirthResurrectPercent, rebirthResurrectPercent, soulSickness, player.getRebirthSkill());
		if (player.getIsFlyingBeforeDeath()) {
			player.setState(CreatureState.FLYING);
		}
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RESURRECT), true);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		if (player.getIsFlyingBeforeDeath()) {
			player.getFlyController().startFly();
		}
		player.getGameStats().updateStatsAndSpeedVisually();

		if (player.isInPrison())
			TeleportService2.teleportToPrison(player);
		player.unsetResPosState();

		//if player was flying before res, start flying

		//unset isflyingbeforedeath
		player.setIsFlyingBeforeDeath(false);
	}

	public static final void bindRevive(Player player) {
		bindRevive(player, 0);
	}

	public static final void bindRevive(Player player, int skillId) {
		revive(player, 25, 25, true, skillId);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		// TODO: It is not always necessary.
		// sendPacket(new SM_QUEST_LIST(activePlayer));
		player.getGameStats().updateStatsAndSpeedVisually();
		if (player.isInPrison()) {
			TeleportService2.teleportToPrison(player);
		}
		else {
			boolean isInviadeActiveVortex = false;
			for (VortexLocation loc : VortexService.getInstance().getVortexLocations().values()) {
				isInviadeActiveVortex = loc.isInsideActiveVotrex(player)
						&& player.getRace().equals(loc.getInvadersRace());
				if (isInviadeActiveVortex) {
					TeleportService2.teleportTo(player, loc.getResurrectionPoint());
				}
			}

			if (!isInviadeActiveVortex) {
				TeleportService2.moveToBindLocation(player, true);
			}
		}
		player.unsetResPosState();
	}

	public static final void kiskRevive(Player player) {
		kiskRevive(player, 0);
	}

	public static final void kiskRevive(Player player, int skillId) {
		// TODO: find right place for this
		if (player.getSKInfo().getRank() > 1) {
			if (SerialKillerService.getInstance().isEnemyWorld(player)) {
				bindRevive(player);
				return;
			}
		}

		Kisk kisk = player.getKisk();
		if (kisk == null) {
			return;
		}
		if (player.isInPrison())
			TeleportService2.teleportToPrison(player);
		else if (kisk.isActive()) {
			WorldPosition bind = kisk.getPosition();
			kisk.resurrectionUsed();
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
			revive(player, 25, 25, false, skillId);
			player.getGameStats().updateStatsAndSpeedVisually();
			player.unsetResPosState();
			TeleportService2.moveToKiskLocation(player, bind);
		}
	}

	public static final void instanceRevive(Player player) {
		instanceRevive(player, 0);
	}

	public static final void instanceRevive(Player player, int skillId) {
		// Revive in Instances
		if (player.getPosition().getWorldMapInstance().getInstanceHandler().onReviveEvent(player)) {
			return;
		}
		WorldMap map = World.getInstance().getWorldMap(player.getWorldId());
		if (map == null) {
			bindRevive(player);
			return;
		}
		revive(player, 25, 25, true, skillId);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		player.getGameStats().updateStatsAndSpeedVisually();
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
		if (map.isInstanceType()
				&& (player.getInstanceStartPosX() != 0 && player.getInstanceStartPosY() != 0 && player.getInstanceStartPosZ() != 0)) {
			TeleportService2.teleportTo(player, player.getWorldId(), player.getInstanceStartPosX(),
					player.getInstanceStartPosY(), player.getInstanceStartPosZ());
		}
		else {
			bindRevive(player);
		}
		player.unsetResPosState();
	}

	public static final void revive(final Player player, int hpPercent, int mpPercent, boolean setSoulsickness, int resurrectionSkill) {
		player.getKnownList().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player visitor) {
				VisibleObject target = visitor.getTarget();
				if (target != null && target.getObjectId() == player.getObjectId()) {
					visitor.setTarget(null);
					PacketSendUtility.sendPacket(visitor, new SM_TARGET_SELECTED(null));
				}
			}
		
		});
		boolean isNoResurrectPenalty = player.getController().isNoResurrectPenaltyInEffect();
		player.getMoveController().stopFalling(player.getZ());
		player.setPlayerResActivate(false);
		player.getLifeStats().setCurrentHpPercent(isNoResurrectPenalty ? 100 : hpPercent);
		player.getLifeStats().setCurrentMpPercent(isNoResurrectPenalty ? 100 : mpPercent);
		if (player.getCommonData().getDp() > 0 && !isNoResurrectPenalty)
			player.getCommonData().setDp(0);
		player.getLifeStats().triggerRestoreOnRevive();
		if (!isNoResurrectPenalty && setSoulsickness) {
			player.getController().updateSoulSickness(resurrectionSkill);
		}
		player.setResurrectionSkill(0);
		player.getAggroList().clear();
		player.getController().onBeforeSpawn();
		if (player.isInGroup2()) {
			PlayerGroupService.updateGroup(player, GroupEvent.MOVEMENT);
		}
		if (player.isInAlliance2()) {
			PlayerAllianceService.updateAlliance(player, PlayerAllianceEvent.MOVEMENT);
		}
	}

	public static final void itemSelfRevive(Player player) {
		Item item = player.getSelfRezStone();
		if (item == null) {
			cancelRes(player);
			return;
		}

		// Add Cooldown and use item
		ItemUseLimits useLimits = item.getItemTemplate().getUseLimits();
		int useDelay = useLimits.getDelayTime();
		player.addItemCoolDown(useLimits.getDelayId(), System.currentTimeMillis() + useDelay, useDelay / 1000);
		player.getController().cancelUseItem();
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(),
				item.getItemTemplate().getTemplateId()), true);
		if (!player.getInventory().decreaseByObjectId(item.getObjectId(), 1)) {
			cancelRes(player);
			return;
		}
		// Tombstone Self-Rez retail verified 15%
		revive(player, 15, 15, true, player.getResurrectionSkill());
		//if player was flying before res, start flying
		if (player.getIsFlyingBeforeDeath()) {
			player.setState(CreatureState.FLYING);
		}
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RESURRECT), true);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		if (player.getIsFlyingBeforeDeath()) {
			player.getFlyController().startFly();
		}
		player.getGameStats().updateStatsAndSpeedVisually();

		if (player.isInPrison())
			TeleportService2.teleportToPrison(player);
		player.unsetResPosState();
		//unset isflyingbeforedeath
		player.setIsFlyingBeforeDeath(false);

	}

	private static final void cancelRes(Player player) {
		AuditLogger.info(player, "Possible selfres hack.");
		player.getController().sendDie();
	}

}