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

package ai;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AI2Request;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.configs.main.LoggingConfig;
import org.typezero.gameserver.controllers.observer.ItemUseObserver;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.siege.SiegeNpc;
import org.typezero.gameserver.model.siege.ArtifactLocation;
import org.typezero.gameserver.model.siege.ArtifactStatus;
import org.typezero.gameserver.model.team.legion.LegionPermissionsMask;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.siegelocation.ArtifactActivation;
import org.typezero.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_ABYSS_ARTIFACT_INFO3;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;

import static org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE.*;
import org.typezero.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.skillengine.model.SkillTemplate;
import org.typezero.gameserver.skillengine.properties.TargetSpeciesAttribute;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.knownlist.Visitor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ATracer, Source
 */
@AIName("artifact")
public class ArtifactAI2 extends NpcAI2 {

	private static final Logger log = LoggerFactory.getLogger("SIEGE_LOG");

	private Map<Integer, ItemUseObserver> observers = new HashMap<Integer, ItemUseObserver>();

	@Override
	protected SiegeSpawnTemplate getSpawnTemplate() {
		return (SiegeSpawnTemplate) super.getSpawnTemplate();
	}

	@Override
	protected void handleDialogStart(final Player player) {
		final ArtifactLocation loc = SiegeService.getInstance().getArtifact(getSpawnTemplate().getSiegeId());
		AI2Actions.addRequest(this, player, 160028, new AI2Request() {

			@Override
			public void acceptRequest(Creature requester, Player responder) {
                final ArtifactLocation loc1 = SiegeService.getInstance().getArtifact(getSpawnTemplate().getSiegeId());
                ArtifactActivation activation1 = loc.getTemplate().getActivation();
                final int itemId1 = activation1.getItemId();
                final ItemTemplate desc = DataManager.ITEM_DATA.getItemTemplate(itemId1);
                final int descid = desc.getNameId();

				AI2Actions.addRequest(ArtifactAI2.this, player, 160016, new AI2Request() {
					@Override
					public void acceptRequest(Creature requester, Player responder) {
						onActivate(responder);
					}
				}, new DescriptionId(descid), SiegeService.getInstance().getArtifact(getSpawnTemplate().getSiegeId()).getTemplate().getActivation().getCount());
			}
		}, loc);
	}

	@Override
	protected void handleDialogFinish(Player player) {
	}

	public void onActivate(final Player player) {
		final ArtifactLocation loc = SiegeService.getInstance().getArtifact(getSpawnTemplate().getSiegeId());

		// Get Skill id, item, count and target defined for each artifact.
		ArtifactActivation activation = loc.getTemplate().getActivation();
		int skillId = activation.getSkillId();
		final int itemId = activation.getItemId();
		final int count = activation.getCount();
		final SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);

		if (skillTemplate == null) {
			LoggerFactory.getLogger(ArtifactAI2.class).error("No skill template for artifact effect id : " + skillId);
			return;
		}

		if (loc.getCoolDown() > 0 || !loc.getStatus().equals(ArtifactStatus.IDLE)) {
			PacketSendUtility.sendPacket(player, STR_CANNOT_USE_ARTIFACT_OUT_OF_ORDER);
			return;
		}

		if (loc.getLegionId() != 0)
			if (!player.isLegionMember() || player.getLegion().getLegionId() != loc.getLegionId()
					|| !player.getLegionMember().hasRights(LegionPermissionsMask.ARTIFACT)) {
				PacketSendUtility.sendPacket(player, STR_CANNOT_USE_ARTIFACT_HAVE_NO_AUTHORITY);
				return;
			}

		if (player.getInventory().getItemCountByItemId(itemId) < count)
			return;

		if(LoggingConfig.LOG_SIEGE)
			log.info("Artifact "+getSpawnTemplate().getSiegeId()+" activated by "+player.getName()+". Activator race: "+player.getRace().toString());

		if (!loc.getStatus().equals(ArtifactStatus.IDLE))
			return;
		// Brodcast start activation.
		final SM_SYSTEM_MESSAGE startMessage = STR_ARTIFACT_CASTING(player.getRace().getRaceDescriptionId(),
				player.getName(), new DescriptionId(skillTemplate.getNameId()));
		loc.setStatus(ArtifactStatus.ACTIVATION);
		final SM_ABYSS_ARTIFACT_INFO3 artifactInfo = new SM_ABYSS_ARTIFACT_INFO3(loc.getLocationId());
		player.getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, startMessage);
				PacketSendUtility.sendPacket(player, artifactInfo);
			}

		});

		PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 10000, 1));
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_QUESTLOOT, 0, getObjectId()),
				true);

		ItemUseObserver observer = new ItemUseObserver() {

			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ACTION_ITEM_NPC);
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()),
						true);
				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 10000, 0));
				final SM_SYSTEM_MESSAGE message = STR_ARTIFACT_CANCELED(loc.getRace().getDescriptionId(),
						new DescriptionId(skillTemplate.getNameId()));
				loc.setStatus(ArtifactStatus.IDLE);
				final SM_ABYSS_ARTIFACT_INFO3 artifactInfo = new SM_ABYSS_ARTIFACT_INFO3(loc.getLocationId());
				getOwner().getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

					@Override
					public void visit(Player player) {
						PacketSendUtility.sendPacket(player, message);
						PacketSendUtility.sendPacket(player, artifactInfo);
					}

				});
			}

		};
		observers.put(player.getObjectId(), observer);
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ACTION_ITEM_NPC, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				ItemUseObserver observer = observers.remove(player.getObjectId());
				if (observer != null)
					player.getObserveController().removeObserver(observer);

				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 10000, 0));
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()),
						true);
				if (!player.getInventory().decreaseByItemId(itemId, count))
					return;
				final SM_SYSTEM_MESSAGE message = STR_ARTIFACT_CORE_CASTING(loc.getRace().getDescriptionId(),
						new DescriptionId(skillTemplate.getNameId()));
				loc.setStatus(ArtifactStatus.CASTING);
				final SM_ABYSS_ARTIFACT_INFO3 artifactInfo = new SM_ABYSS_ARTIFACT_INFO3(loc.getLocationId());

				player.getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

					@Override
					public void visit(Player player) {
						PacketSendUtility.sendPacket(player, message);
						PacketSendUtility.sendPacket(player, artifactInfo);
					}

				});

				loc.setLastActivation(System.currentTimeMillis());
				if (loc.getTemplate().getActivation().getRepeatCount() == 1)
					ThreadPoolManager.getInstance().schedule(new ArtifactUseSkill(loc, player, skillTemplate), 13000);
				else {
					final ScheduledFuture<?> s = ThreadPoolManager.getInstance().scheduleAtFixedRate(
							new ArtifactUseSkill(loc, player, skillTemplate), 13000, loc.getTemplate().getActivation().getRepeatInterval() * 1000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							s.cancel(true);
							loc.setStatus(ArtifactStatus.IDLE);
						}

					}, 13000 + (loc.getTemplate().getActivation().getRepeatInterval() * loc.getTemplate().getActivation().getRepeatCount() * 1000));
				}

			}

		}, 10000));
	}

	class ArtifactUseSkill implements Runnable {

		private ArtifactLocation artifact;
		private Player player;
		private SkillTemplate skill;
		private int runCount = 1;
		private SM_ABYSS_ARTIFACT_INFO3 pkt;
		private SM_SYSTEM_MESSAGE message;

		/**
		 * @param artifact
		 * @param targetRace
		 */
		private ArtifactUseSkill(ArtifactLocation artifact, Player activator, SkillTemplate skill) {
			this.artifact = artifact;
			this.player = activator;
			this.skill = skill;
			this.pkt = new SM_ABYSS_ARTIFACT_INFO3(artifact.getLocationId());
			this.message = STR_ARTIFACT_FIRE(activator.getRace().getRaceDescriptionId(), player.getName(),
					new DescriptionId(skill.getNameId()));
		}

		@Override
		public void run() {
			if (artifact.getTemplate().getActivation().getRepeatCount() < runCount)
				return;

			final boolean start = (runCount == 1);
			final boolean end = (runCount == artifact.getTemplate().getActivation().getRepeatCount());

			runCount++;
			player.getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

				@Override
				public void visit(Player player) {
					if (start)
						PacketSendUtility.sendPacket(player, message);
					artifact.setStatus(ArtifactStatus.ACTIVATED);
					PacketSendUtility.sendPacket(player, pkt);
					if (end) {
						artifact.setStatus(ArtifactStatus.IDLE);
						PacketSendUtility.sendPacket(player, pkt);
					}
				}

			});
			boolean pc = skill.getProperties().getTargetSpecies() == TargetSpeciesAttribute.PC;
			for (Creature creature : artifact.getCreatures().values()) {
				if (creature.getActingCreature() instanceof Player || (creature instanceof SiegeNpc && !pc)) {
					switch (skill.getProperties().getTargetRelation()) {
						case FRIEND:
							if (player.isEnemy(creature))
								continue;
							break;
						case ENEMY:
							if (!player.isEnemy(creature))
								continue;
							break;
					}
					AI2Actions.applyEffect(ArtifactAI2.this, skill, creature);
				}
			}
		}

	}

}
