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

package org.typezero.gameserver.controllers;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.configs.main.SecurityConfig;
import org.typezero.gameserver.controllers.observer.StartMovingListener;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.Gatherable;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RewardType;
import org.typezero.gameserver.model.templates.gather.GatherableTemplate;
import org.typezero.gameserver.model.templates.gather.Material;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.PunishmentService;
import org.typezero.gameserver.services.RespawnService;
import org.typezero.gameserver.skillengine.task.GatheringTask;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.captcha.CAPTCHAUtil;
import org.typezero.gameserver.world.World;

import java.util.List;

/**
 * @author ATracer, sphinx, Cura
 */
public class GatherableController extends VisibleObjectController<Gatherable> {

	private int gatherCount;

	private int currentGatherer;

	private GatheringTask task;

	public enum GatherState {
		GATHERED,
		GATHERING,
		IDLE
	}

	private GatherState state = GatherState.IDLE;

	/**
	 * Start gathering process
	 *
	 * @param player
	 */
	public void onStartUse(final Player player) {
		// basic actions, need to improve here
		final GatherableTemplate template = this.getOwner().getObjectTemplate();
		if (template.getLevelLimit() > 0) {
			// You must be at least level %0 to perform extraction.
			if (player.getLevel() < template.getLevelLimit()) {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400737, template.getLevelLimit()));
				return;
			}
		}

		if (player.getInventory().isFull()) {
			// You must have at least one free space in your cube to gather.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1330036));
			return;
		}
		if (MathUtil.getDistance(getOwner(), player) > 6)
			return;

		// check is gatherable
		if (!checkGatherable(player, template))
			return;

		if (!checkPlayerSkill(player, template))
			return;

		// check for extractor in inventory
		byte result = checkPlayerRequiredExtractor(player, template);
		if (result == 0)
			return;

		// CAPTCHA
		if (SecurityConfig.CAPTCHA_ENABLE) {
			if (SecurityConfig.CAPTCHA_APPEAR.equals(template.getSourceType()) || SecurityConfig.CAPTCHA_APPEAR.equals("ALL")) {
				int rate = SecurityConfig.CAPTCHA_APPEAR_RATE;
				if (template.getCaptchaRate() > 0)
					rate = (int) (template.getCaptchaRate() * 0.1f);

				if (Rnd.get(0, 100) < rate) {
					player.setCaptchaWord(CAPTCHAUtil.getRandomWord());
					player.setCaptchaImage(CAPTCHAUtil.createCAPTCHA(player.getCaptchaWord()).array());
					PunishmentService.setIsNotGatherable(player, 0, true, SecurityConfig.CAPTCHA_EXTRACTION_BAN_TIME * 1000L);
				}
			}
		}

		List<Material> materials = null;
		switch (result) {
			case 1: // player has equipped item, or have a consumable in inventory, so he will obtain extra items
				materials = template.getExtraMaterials().getMaterial();
				break;
			case 2:// regular thing
				materials = template.getMaterials().getMaterial();
				break;
		}

		int chance = Rnd.get(10000000);
		int current = 0;
		Material curMaterial = null;
		for (Material mat : materials) {
			current += mat.getRate();
			if (current >= chance) {
				curMaterial = mat;
				break;
			}
		}

		synchronized (state) {
			if (state != GatherState.GATHERING) {
				state = GatherState.GATHERING;
				currentGatherer = player.getObjectId();
				player.getObserveController().attach(new StartMovingListener() {

					@Override
					public void moved() {
						finishGathering(player);
					}
				});
				int skillLvlDiff = player.getSkillList().getSkillLevel(template.getHarvestSkill()) - template.getSkillLevel();
				task = new GatheringTask(player, getOwner(), curMaterial, skillLvlDiff);
				task.start();
			}
		}
	}

	/**
	 * Checks whether player have needed skill for gathering and skill level is sufficient
	 *
	 * @param player
	 * @param template
	 * @return
	 */
	private boolean checkPlayerSkill(final Player player, final GatherableTemplate template) {
		int harvestSkillId = template.getHarvestSkill();
		if (!player.getSkillList().isSkillPresent(harvestSkillId)) {
			if (harvestSkillId == 30001) {
				//You are Daeva now, leave this to humans.
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GATHER_INCORRECT_SKILL);
			}
			else {
				// You must learn the %0 skill to start gathering.
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1330054, new DescriptionId(DataManager.SKILL_DATA
					.getSkillTemplate(harvestSkillId).getNameId())));
			}
			return false;
		}
		if (player.getSkillList().getSkillLevel(harvestSkillId) < template.getSkillLevel()) {
			// Your %0 skill level is not high enough.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1330001, new DescriptionId(DataManager.SKILL_DATA
				.getSkillTemplate(harvestSkillId).getNameId())));
			return false;
		}
		return true;
	}

	private byte checkPlayerRequiredExtractor(final Player player, final GatherableTemplate template) {
		if (template.getRequiredItemId() > 0) {
			if (template.getCheckType() == 1) {
				List<Item> items = player.getEquipment().getEquippedItemsByItemId(template.getRequiredItemId());
				boolean condOk = false;
				for (Item item : items) {
					if (item.isEquipped()) {
						condOk = true;
						break;
					}
				}
				return (byte) (condOk ? 1 : 2);

			}
			else if (template.getCheckType() == 2) {
				if (player.getInventory().getItemCountByItemId(template.getRequiredItemId()) < template.getEraseValue()){
					// You do not have enough %0 to gather.
					PacketSendUtility.sendPacket(player,
						new SM_SYSTEM_MESSAGE(1400376, new DescriptionId(template.getRequiredItemNameId())));
					return 0;
				}
				else
					return 1;
			}
		}

		return 2;
	}

	/**
	 * @param player
	 * @param template
	 * @return
	 * @author Cura
	 */
	private boolean checkGatherable(final Player player, final GatherableTemplate template) {
		if (player.isNotGatherable()) {
			// You are currently poisoned and unable to extract. (Time remaining: %DURATIONTIME0)
			PacketSendUtility.sendPacket(
				player,
				new SM_SYSTEM_MESSAGE(1400273, (int) ((player.getGatherableTimer() - (System.currentTimeMillis() - player
					.getStopGatherable())) / 1000)));
			return false;
		}
		return true;
	}

	public void completeInteraction() {
		state = GatherState.IDLE;
		gatherCount++;
		if (gatherCount == getOwner().getObjectTemplate().getHarvestCount()) {
			onDespawn();
		}
	}

	public void rewardPlayer(Player player) {
		if (player != null) {
			int skillLvl = getOwner().getObjectTemplate().getSkillLevel();
			int xpReward = (int) ((0.0031 * (skillLvl + 5.3) * (skillLvl + 1592.8) + 60));

			if (player.getSkillList().addSkillXp(player, getOwner().getObjectTemplate().getHarvestSkill(),
				(int) RewardType.GATHERING.calcReward(player, xpReward), skillLvl)) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_EXTRACT_GATHERING_SUCCESS_GETEXP);
				player.getCommonData().addExp(xpReward, RewardType.GATHERING);
			}
			else
				PacketSendUtility.sendPacket(
					player,
					SM_SYSTEM_MESSAGE.STR_MSG_DONT_GET_PRODUCTION_EXP(
							new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(getOwner().getObjectTemplate().getHarvestSkill()).getNameId())));
		}
	}

	/**
	 * Called by client when some action is performed or on finish gathering Called by move observer on player move
	 *
	 * @param player
	 */
	public void finishGathering(Player player) {
		if (currentGatherer == player.getObjectId()) {
			if (state == GatherState.GATHERING) {
				task.abort();
			}
			currentGatherer = 0;
			state = GatherState.IDLE;
		}
	}

	@Override
	public void onDespawn() {
		Gatherable owner = getOwner();
		if (!getOwner().isInInstance()) {
			RespawnService.scheduleRespawnTask(owner);
		}
		World.getInstance().despawn(owner);
	}

	@Override
	public void onBeforeSpawn() {
		this.gatherCount = 0;
	}

	@Override
	public Gatherable getOwner() {
		return super.getOwner();
	}
}
