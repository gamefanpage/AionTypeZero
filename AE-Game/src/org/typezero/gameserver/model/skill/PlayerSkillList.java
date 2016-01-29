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

package org.typezero.gameserver.model.skill;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.HiddenStigmasTemplate;
import org.typezero.gameserver.model.templates.item.Stigma.StigmaSkill;
import org.typezero.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.SkillLearnService;
import org.typezero.gameserver.utils.PacketSendUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author IceReaper, orfeo087, Avol, AEJTester
 */
public final class PlayerSkillList implements SkillList<Player> {

	private final Map<Integer, PlayerSkillEntry> basicSkills;
	private final Map<Integer, PlayerSkillEntry> stigmaSkills;

	private final List<PlayerSkillEntry> deletedSkills;

	public PlayerSkillList() {
		this.basicSkills = new HashMap<Integer, PlayerSkillEntry>(0);
		this.stigmaSkills = new HashMap<Integer, PlayerSkillEntry>(0);
		this.deletedSkills = new ArrayList<PlayerSkillEntry>(0);
	}

	public PlayerSkillList(List<PlayerSkillEntry> skills) {
		this();
		for (PlayerSkillEntry entry : skills) {
			if (entry.isStigma())
				stigmaSkills.put(entry.getSkillId(), entry);
			else
				basicSkills.put(entry.getSkillId(), entry);
		}
	}

	/**
	 * Returns array with all skills
	 */
	public PlayerSkillEntry[] getAllSkills() {
		List<PlayerSkillEntry> allSkills = new ArrayList<PlayerSkillEntry>();
		allSkills.addAll(basicSkills.values());
		allSkills.addAll(stigmaSkills.values());
		return allSkills.toArray(new PlayerSkillEntry[allSkills.size()]);
	}

	public PlayerSkillEntry[] getBasicSkills() {
		return basicSkills.values().toArray(new PlayerSkillEntry[basicSkills.size()]);
	}

	public PlayerSkillEntry[] getStigmaSkills() {
		return stigmaSkills.values().toArray(new PlayerSkillEntry[stigmaSkills.size()]);
	}

	public PlayerSkillEntry getStigmaSkillEntry(int skillId) {
		return stigmaSkills.get(skillId);
	}

	public boolean isHaveHiddenStigma(Player player) {
        for (HiddenStigmasTemplate hst : DataManager.HIDDEN_STIGMA_DATA.getHiddenStigmasByClass()) {
            if (hst.getClassname().equals(player.getPlayerClass().name())) {
                for (PlayerSkillEntry pse : getStigmaSkills())
                    for (HiddenStigmasTemplate.HiddenStigmaTemplate oneStigma : hst.getHiddenStigmas())
                        if (oneStigma.getId().equals(pse.getSkillTemplate().getStack()))
                            return true;
                return false;
            }
        }
        return false;
    }

    public int getMaxAvailHiddenStigmaLvl() {
        int lvl = Integer.MAX_VALUE;
        for (PlayerSkillEntry playerSkillEntry : getStigmaSkills()) {
            if (playerSkillEntry.getSkillTemplate().getLvl() < lvl)
                lvl = playerSkillEntry.getSkillTemplate().getLvl();
        }
        return lvl < Integer.MAX_VALUE ? lvl : 1;
    }

    public void deleteHiddenStigmaSilent(Player player) {
        deleteHiddenStigmaAct(player, true);
    }

    public void deleteHiddenStigma(Player player) {
        deleteHiddenStigmaAct(player, false);
    }

    private void deleteHiddenStigmaAct(Player player, boolean silent) {
        for (HiddenStigmasTemplate hst : DataManager.HIDDEN_STIGMA_DATA.getHiddenStigmasByClass()) {
            if (hst.getClassname().equals(player.getPlayerClass().name())) {
                for (PlayerSkillEntry pse : getStigmaSkills()) {
                    for (HiddenStigmasTemplate.HiddenStigmaTemplate oneStigma : hst.getHiddenStigmas()) {
                        if (oneStigma.getId().equals(pse.getSkillTemplate().getStack())) {
                            int skillLvl = pse.getSkillLevel();
                            SkillLearnService.removeSkill(player, pse.getSkillId());
                            if (!silent)
                                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_DELETE_HIDDEN_SKILL(DataManager.SKILL_DATA.getSkillTemplate(pse.getSkillId()).getName(), skillLvl, player.getName()));
                            player.getEffectController().removeEffect(pse.getSkillId());
                            return;
                        }
                    }
                }
                return;
            }
        }
    }

	public PlayerSkillEntry[] getDeletedSkills() {
		return deletedSkills.toArray(new PlayerSkillEntry[deletedSkills.size()]);
	}

	public PlayerSkillEntry getSkillEntry(int skillId) {
		if (basicSkills.containsKey(skillId))
			return basicSkills.get(skillId);
		return stigmaSkills.get(skillId);
	}

	@Override
	public boolean addSkill(Player player, int skillId, int skillLevel) {
		return addSkill(player, skillId, skillLevel, false, PersistentState.NEW);
	}

	public boolean addStigmaSkill(Player player, int skillId, int skillLevel) {
		return addSkill(player, skillId, skillLevel, true, PersistentState.NEW);
	}

    private boolean addSkill(Player player, int skillId, int skillLevel, boolean isStigma, PersistentState state) {
        return addSkillAct(player, skillId, skillLevel, isStigma, state, false);
    }

    public boolean addGMSkill(Player player, int skillId, int skillLevel) {
        return addSkillAct(player, skillId, skillLevel, true, PersistentState.NEW, true);
    }

    /**
     * Add temporary skill which will not be saved in db
     *
     * @param player
     * @param skillId
     * @param skillLevel
     * @param msg
     * @return
     */
	public boolean addAbyssSkill(Player player, int skillId, int skillLevel) {
		return addSkill(player, skillId, skillLevel, false, PersistentState.NOACTION);
	}

	public void addStigmaSkill(Player player, List<StigmaSkill> skills, boolean equipedByNpc) {
		for (StigmaSkill sSkill : skills) {
			PlayerSkillEntry skill = new PlayerSkillEntry(sSkill.getSkillId(), true, sSkill.getSkillLvl(), PersistentState.NOACTION);
			this.stigmaSkills.put(sSkill.getSkillId(), skill);
			if (equipedByNpc) {
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(skill, 1300401, false));
			}
		}
	}

	public void addHiddenStigmaSkill(Player player, int skillId, int skillLvl) {
		PlayerSkillEntry skill = new PlayerSkillEntry(skillId, true, skillLvl, PersistentState.NOACTION);
		this.stigmaSkills.put(skillId, skill);
		PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player, skill));
        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_STIGMA_GET_HIDDEN_SKILL(skill.getSkillName(), skillLvl, player.getName()));
	}

	private synchronized boolean addSkillAct(Player player, int skillId, int skillLevel, boolean isStigma, PersistentState state, boolean isGMSkill) {
		PlayerSkillEntry existingSkill = isStigma ? stigmaSkills.get(skillId) : basicSkills.get(skillId);

		boolean isNew = false;
		if (existingSkill != null) {
			if (existingSkill.getSkillLevel() >= skillLevel) {
				return false;
			}
			existingSkill.setSkillLvl(skillLevel);
		}
		else {
			if (isStigma)
				stigmaSkills.put(skillId, new PlayerSkillEntry(skillId, true, skillLevel, state));
			else {
				basicSkills.put(skillId, new PlayerSkillEntry(skillId, false, skillLevel, state));
				isNew = true;
			}
		}
		if (player.isSpawned())
            if (!isStigma || isGMSkill)
			    sendMessage(player, skillId, isNew);
            else
                PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1300401, false));

		return true;
	}

	/**
	 * @param player
	 * @param skillId
	 * @param xpReward
	 * @return
	 */
	public boolean addSkillXp(Player player, int skillId, int xpReward, int objSkillPoints) {
		PlayerSkillEntry skillEntry = getSkillEntry(skillId);
		int maxDiff = 40;
		int SkillLvlDiff = skillEntry.getSkillLevel() - objSkillPoints;
		if (maxDiff < SkillLvlDiff) {
			return false;
		}
		switch (skillEntry.getSkillId()) {
			case 30001:
				if (skillEntry.getSkillLevel() == 49)
					return false;
			case 30002:
			case 30003:
				if (skillEntry.getSkillLevel() == 449)
					break;
			case 40001:
			case 40002:
			case 40003:
			case 40004:
			case 40007:
			case 40008:
			case 40010:
				switch (skillEntry.getSkillLevel()) {
					case 99:
					case 199:
					case 299:
					case 399:
					case 449:
					case 499:
					case 549:
						return false;
				}
				player.getRecipeList().autoLearnRecipe(player, skillId, skillEntry.getSkillLevel());
		}
		boolean updateSkill = skillEntry.addSkillXp(player, xpReward);
		if (updateSkill)
			sendMessage(player, skillId, false);
		return true;
	}

	@Override
	public boolean isSkillPresent(int skillId) {
		return basicSkills.containsKey(skillId) || stigmaSkills.containsKey(skillId);
	}

	@Override
	public int getSkillLevel(int skillId) {
		if (basicSkills.containsKey(skillId))
			return basicSkills.get(skillId).getSkillLevel();
		return stigmaSkills.get(skillId).getSkillLevel();
	}

	@Override
	public synchronized boolean removeSkill(int skillId) {
		PlayerSkillEntry entry = basicSkills.get(skillId);
		if (entry == null)
			entry = stigmaSkills.get(skillId);
		if (entry != null) {
			entry.setPersistentState(PersistentState.DELETED);
			deletedSkills.add(entry);
			basicSkills.remove(skillId);
			stigmaSkills.remove(skillId);
		}
		return entry != null;
	}

	@Override
	public int size() {
		return basicSkills.size() + stigmaSkills.size();
	}

	/**
	 * @param player
	 * @param skillId
	 */
	private void sendMessage(Player player, int skillId, boolean isNew) {
		switch (skillId) {
			case 30001:
			case 30002:
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1330005, false));
				break;
			case 30003:
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1330005, false));
				break;
			case 40001:
			case 40002:
			case 40003:
			case 40004:
			case 40005:
			case 40006:
			case 40007:
			case 40008:
			case 40009:
			case 40010:
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1330061, false));
				break;
			default:
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1300050, isNew));
		}
	}
}
