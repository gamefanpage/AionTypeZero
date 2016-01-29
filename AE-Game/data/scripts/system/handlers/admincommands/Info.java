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

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Gatherable;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.npcFaction.ENpcFactionQuestState;
import org.typezero.gameserver.model.gameobjects.player.npcFaction.NpcFaction;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.model.templates.npcskill.NpcSkillTemplate;
import org.typezero.gameserver.model.templates.npcskill.NpcSkillTemplates;
import org.typezero.gameserver.services.TownService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
/**
 * @author Nemiroff Date: 28.12.2009
 */
public class Info extends AdminCommand {

	public Info() {
		super("info");
	}

	@Override
	public void execute(Player admin, String... params) {
		VisibleObject target = admin.getTarget();

		if (target instanceof Player) {
			Player player = (Player) target;
			PacketSendUtility.sendMessage(admin, "[Info about " + player.getName() +"]"
				+ "\nPlayer Id: " + player.getObjectId()
				+ "\nMap ID: " + player.getWorldId()
				+ "\nX: " + player.getCommonData().getPosition().getX() + " / Y: " + player.getCommonData().getPosition().getY()
				+ " / Z: " + player.getCommonData().getPosition().getZ() + " / Heading: " + player.getCommonData().getPosition().getHeading()
				+ "\n Town ID: "+TownService.getInstance().getTownResidence(player)
				+ "\n Tribe: "+ player.getTribe()
				+ "\n TribeBase: "+ player.getBaseTribe()
                + "\n Distance: " + admin.getTarget().getDistanceToTarget());

			PacketSendUtility.sendMessage(admin, "[Stats]"
				+ "\nPvP attack psyhical: " + player.getGameStats().getStat(StatEnum.PVP_ATTACK_RATIO, 0).getCurrent()/2 * 0.1f + "%"
				+ "\nPvP attack magical: " + player.getGameStats().getStat(StatEnum.PVP_ATTACK_RATIO, 0).getCurrent()/2 * 0.1f + "%"
				+ "\nPvP attack all: " + player.getGameStats().getStat(StatEnum.PVP_ATTACK_RATIO, 0).getCurrent() * 0.1f + "%"
				+ "\nPvP defend: " + player.getGameStats().getStat(StatEnum.PVP_DEFEND_RATIO, 0).getCurrent() * 0.1f + "%"
				+ "\nCast Time Boost: +" + (player.getGameStats().getStat(StatEnum.BOOST_CASTING_TIME, 1000).getCurrent() * 0.1f - 100) + "%"
				+ "\nAttack Speed: " + player.getGameStats().getAttackSpeed().getCurrent() * 0.001f
				+ "\nMovement Speed: " + player.getGameStats().getMovementSpeedFloat()
				+ "\n----------Main Hand------------\nAttack: " + player.getGameStats().getMainHandPAttack().getCurrent()
				+ "\nAccuracy: " + player.getGameStats().getMainHandPAccuracy().getCurrent()
				+ "\nCritical: " + player.getGameStats().getMainHandPCritical().getCurrent()
				+ "\n------------Off Hand------------\nAttack: " + player.getGameStats().getOffHandPAttack().getCurrent()
				+ "\nAccuracy: " + player.getGameStats().getOffHandPAccuracy().getCurrent()
				+ "\nCritical: " + player.getGameStats().getOffHandPCritical().getCurrent()
				+ "\n-------------Magical-------------\nAttack: " + player.getGameStats().getMainHandMAttack().getCurrent()
				+ "\nAccuracy: " + player.getGameStats().getMainHandMAccuracy().getCurrent()
				+ "\nCritical: " + player.getGameStats().getMCritical().getCurrent()
				+ "\nBoost: " + player.getGameStats().getMBoost().getCurrent()
				+ "\n-------------Protect--------------\nPhysical Defence: " + player.getGameStats().getPDef().getCurrent()
				+ "\nBlock: " + player.getGameStats().getBlock().getCurrent()
				+ "\nParry: " + player.getGameStats().getParry().getCurrent()
				+ "\nEvasion: " + player.getGameStats().getEvasion().getCurrent()
				+ "\nMagic Resist: " + player.getGameStats().getMResist().getCurrent());

			for (int i = 0; i < 2; i++) {
				NpcFaction faction = player.getNpcFactions().getActiveNpcFaction(i == 0);
				if (faction != null) {
					PacketSendUtility.sendMessage(admin, player.getName() + " have join to " + (i == 0 ? "mentor" : "daily") + " faction: " + DataManager.NPC_FACTIONS_DATA.getNpcFactionById(faction.getId()).getName()
						+ "\nCurrent quest state: " + faction.getState().name()
						+ (faction.getState().equals(ENpcFactionQuestState.COMPLETE) ? ("\nNext after: " + ((faction.getTime() - System.currentTimeMillis() / 1000) / 3600f) + " h.") : ""));
				}
			}
		}
		else if (target instanceof Npc) {
			Npc npc = (Npc) admin.getTarget();
			PacketSendUtility.sendMessage(admin,
				"[color:Info ;0 1 1] [color:NPC:;0 1 1]"
					+ "\n[color:Name:;1 0 0] " + npc.getName()
					+ "\n[color:Npc ;1 0 0][color:Id:;1 0 0] " + npc.getNpcId() + " / [color:Stati;1 0 0][color:cId:;1 0 0] " + npc.getSpawn().getStaticId()
					+ "\n[color:Map ;1 0 0][color:Id:;1 0 0] " + admin.getTarget().getWorldId()
					+ "\n[color:X:;1 0 0] " + admin.getTarget().getX() + " [color:Y:;1 0 0] " + admin.getTarget().getY()
					+ " [color:Z:;1 0 0] " + admin.getTarget().getZ() + " [color:H:;1 0 0] " + admin.getTarget().getHeading()
                    + " \nDistance: " + admin.getDistanceToTarget());
			PacketSendUtility.sendMessage(admin, "[color:AI:;1 0 0] " + npc.getAi2().getName());
			PacketSendUtility.sendMessage(admin, "[color:Stat;0 0 0][color:s;0 0 0]" + "\n[color:HP:;1 0 0] " + npc.getLifeStats().getCurrentHp()
				+ " / " + npc.getLifeStats().getMaxHp() + "\n[color:MP:;0 0 1] " + npc.getLifeStats().getCurrentMp()
				+ " / " + npc.getLifeStats().getMaxMp()	+ "\n[color:XP:;1 1 0] " + npc.getObjectTemplate().getStatsTemplate().getMaxXp()
				+ "\n[color:Race:;1 0 0] " + npc.getObjectTemplate().getRace()
                                + "\n[color:Trib:;1 0 0] " + npc.getTribe()
				+ "\n[color:Pow;1 1 0][color:er:;1 1 0] " + npc.getObjectTemplate().getStatsTemplate().getPower()
				+ "\n[color:PDe;1 1 0][color:f:;1 1 0] " + npc.getObjectTemplate().getStatsTemplate().getPdef()
				+ "\n[color:MRes;1 1 0][color:ist:;1 1 0] " + npc.getObjectTemplate().getStatsTemplate().getMresist());
            NpcSkillTemplates npcTemplate = DataManager.NPC_SKILL_DATA.getNpcSkillList(((Npc) admin.getTarget()).getNpcId());
            if (npcTemplate != null){
            for (NpcSkillTemplate skills: npcTemplate.getNpcSkills()){
                    PacketSendUtility.sendMessage(admin, "[color:Skill;1 0 0][color:ID:;1 0 0] " + skills.getSkillid() + " [color:Lvl:;1 0 0]: " + skills.getSkillLevel() + " [color:chc:;1 0 0]:" + skills.getProbability());
                }
            }
        }
		else if (target instanceof Gatherable) {
			Gatherable gather = (Gatherable) target;
			PacketSendUtility.sendMessage(admin, "[Info about gather]\n" + "Name: " + gather.getName()
				+ "\nId: " + gather.getObjectTemplate().getTemplateId() + " / ObjectId: " + admin.getTarget().getObjectId()
				+ "\nMap ID: " + admin.getTarget().getWorldId()
				+ "\nX: " + admin.getTarget().getX() + " / Y: " + admin.getTarget().getY() + " / Z: " + admin.getTarget().getZ()
				+ " / Heading: " + admin.getTarget().getHeading());
		}
	}


	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}

}
