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

package org.typezero.gameserver.model.autogroup;

import org.typezero.gameserver.dataholders.DataManager;
import java.util.List;

/**
 *
 * @author xTz
 */
public enum AutoGroupType {
	BARANATH_DREDGION(1, 600000, 12) { @Override AutoInstance newAutoInstance() { return new AutoDredgionInstance(); } },
	CHANTRA_DREDGION(2, 600000, 12) { @Override AutoInstance newAutoInstance() { return new AutoDredgionInstance(); } },
	TERATH_DREDGION(3, 600000, 12) { @Override AutoInstance newAutoInstance() { return new AutoDredgionInstance(); } },
	ELYOS_FIRE_TEMPLE(4, 300000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	NOCHSANA_TRAINING_CAMP(5, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	DARK_POETA(6, 1200000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	STEEL_RAKE(7, 1200000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	UDAS_TEMPLE(8, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	LOWER_UDAS_TEMPLE(9, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	EMPYREAN_CRUCIBLE(11, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	ASMODIANS_FIRE_TEMPLE(14, 300000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	ARENA_OF_CHAOS_1(21, 110000, 8, 1) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	ARENA_OF_CHAOS_2(22, 110000, 8, 2) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	ARENA_OF_CHAOS_3(23, 110000, 8, 3) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	ARENA_OF_DISCIPLINE_1(24, 110000, 2, 1) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	ARENA_OF_DISCIPLINE_2(25, 110000, 2, 2) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	ARENA_OF_DISCIPLINE_3(26, 110000, 2, 3) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	CHAOS_TRAINING_GROUNDS_1(27, 110000, 8, 1) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	CHAOS_TRAINING_GROUNDS_2(28, 110000, 8, 2) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	CHAOS_TRAINING_GROUNDS_3(29, 110000, 8, 3) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	DISCIPLINE_TRAINING_GROUNDS_1(30, 110000, 2, 1) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	DISCIPLINE_TRAINING_GROUNDS_2(31, 110000, 2, 2) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	DISCIPLINE_TRAINING_GROUNDS_3(32, 110000, 2, 3) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	ARENA_OF_HARMONY_1(33, 110000, 6, 1) { @Override AutoInstance newAutoInstance() { return new AutoHarmonyInstance(); } },
	ARENA_OF_HARMONY_2(34, 110000, 6, 2) { @Override AutoInstance newAutoInstance() { return new AutoHarmonyInstance(); } },
	ARENA_OF_HARMONY_3(35, 110000, 6, 3) { @Override AutoInstance newAutoInstance() { return new AutoHarmonyInstance(); } },
	ARENA_OF_GLORY_1(38, 110000, 4, 1) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	ARENA_OF_CHAOS_4(39, 110000, 8, 4) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	ARENA_OF_DISCIPLINE_4(40, 110000, 2, 4) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	ARENA_OF_HARMONY_4(41, 110000, 6, 3) { @Override AutoInstance newAutoInstance() { return new AutoHarmonyInstance(); } },
	ARENA_OF_GLORY_2(42, 110000, 4, 2) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	CHAOS_TRAINING_GROUNDS_4(43, 110000, 8, 4) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	DISCIPLINE_TRAINING_GROUNDS_4(44, 110000, 2, 4) { @Override AutoInstance newAutoInstance() { return new AutoPvPFFAInstance(); } },
	HARAMONIOUS_TRAINING_CENTER_4(45, 110000, 6, 4) { @Override AutoInstance newAutoInstance() { return new AutoHarmonyInstance(); } },
	HARAMONIOUS_TRAINING_CENTER_1(101, 110000, 6, 1) { @Override AutoInstance newAutoInstance() { return new AutoHarmonyInstance(); } },
	HARAMONIOUS_TRAINING_CENTER_2(102, 110000, 6, 2) { @Override AutoInstance newAutoInstance() { return new AutoHarmonyInstance(); } },
	HARAMONIOUS_TRAINING_CENTER_3(103, 110000, 6, 3) { @Override AutoInstance newAutoInstance() { return new AutoHarmonyInstance(); } },
	KAMAR_BATTLEFIELD(107, 600000, 24) { @Override AutoInstance newAutoInstance() { return new AutoKamarInstance(); } },
	STEEL_RAKE_ELYOS(308, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } },
	STEEL_RAKE_ASMODIANS(401, 600000, 6) { @Override AutoInstance newAutoInstance() { return new AutoGeneralInstance(); } };

	private int instanceMaskId;
	private int time;
	private byte playerSize;
	private byte difficultId;
	private AutoGroup template;

	private AutoGroupType(int instanceMaskId, int time, int playerSize, int difficultId) {
		this(instanceMaskId, time, playerSize);
		this.difficultId = (byte) difficultId;
	}

	private AutoGroupType(int instanceMaskId, int time, int playerSize) {
		this.instanceMaskId = instanceMaskId;
		this.time = time;
		this.playerSize = (byte) playerSize;
		template = DataManager.AUTO_GROUP.getTemplateByInstaceMaskId(this.instanceMaskId);
	}

	public int getInstanceMapId() {
		return template.getInstanceId();
	}

	public byte getPlayerSize() {
		return playerSize;
	}

	public int getInstanceMaskId() {
		return instanceMaskId;
	}

	public int getNameId() {
		return template.getNameId();
	}

	public int getTittleId() {
		return template.getTitleId();
	}

	public int getTime() {
		return time;
	}

	public int getMinLevel() {
		return template.getMinLvl();
	}

	public int getMaxLevel() {
		return template.getMaxLvl();
	}

	public boolean hasRegisterGroup() {
		return template.hasRegisterGroup();
	}

	public boolean hasRegisterQuick() {
		return template.hasRegisterQuick();
	}

	public boolean hasRegisterNew() {
		return template.hasRegisterNew();
	}

	public boolean containNpcId(int npcId) {
		return template.getNpcIds().contains(npcId);
	}

	public List<Integer> getNpcIds() {
		return template.getNpcIds();
	}

	public boolean isDredgion() {
		switch (this) {
			case BARANATH_DREDGION:
			case CHANTRA_DREDGION:
			case TERATH_DREDGION:
				return true;
		}
		return false;
	}

	public static AutoGroupType getAGTByMaskId(int instanceMaskId) {
		for (AutoGroupType autoGroupsType : values()) {
			if (autoGroupsType.getInstanceMaskId() == instanceMaskId) {
				return autoGroupsType;
			}
		}
		return null;
	}

	public static AutoGroupType getAutoGroup(int level, int npcId) {
		for (AutoGroupType agt : values()) {
			if (agt.hasLevelPermit(level) && agt.containNpcId(npcId)) {
				return agt;
			}
		}
		return null;
	}

	public static AutoGroupType getAutoGroupByWorld(int level, int worldId) {
		for (AutoGroupType agt : values()) {
			if (agt.getInstanceMapId() == worldId && agt.hasLevelPermit(level)) {
				return agt;
			}
		}
		return null;
	}

	public static AutoGroupType getAutoGroup(int npcId) {
		for (AutoGroupType agt : values()) {
			if (agt.containNpcId(npcId)) {
				return agt;
			}
		}
		return null;
	}

	public boolean isPvPSoloArena() {
		switch (this) {
			case ARENA_OF_DISCIPLINE_1:
			case ARENA_OF_DISCIPLINE_2:
			case ARENA_OF_DISCIPLINE_3:
			case ARENA_OF_DISCIPLINE_4:
				return true;
		}
		return false;
	}

	public boolean isTrainigPvPSoloArena() {
		switch (this) {
			case DISCIPLINE_TRAINING_GROUNDS_1:
			case DISCIPLINE_TRAINING_GROUNDS_2:
			case DISCIPLINE_TRAINING_GROUNDS_3:
			case DISCIPLINE_TRAINING_GROUNDS_4:
				return true;
		}
		return false;
	}

	public boolean isPvPFFAArena() {
		switch (this) {
			case ARENA_OF_CHAOS_1:
			case ARENA_OF_CHAOS_2:
			case ARENA_OF_CHAOS_3:
			case ARENA_OF_CHAOS_4:
				return true;
		}
		return false;
	}

	public boolean isTrainigPvPFFAArena() {
		switch (this) {
			case CHAOS_TRAINING_GROUNDS_1:
			case CHAOS_TRAINING_GROUNDS_2:
			case CHAOS_TRAINING_GROUNDS_3:
			case CHAOS_TRAINING_GROUNDS_4:
				return true;
		}
		return false;
	}

	public boolean isTrainigHarmonyArena() {
		switch (this) {
			case HARAMONIOUS_TRAINING_CENTER_1:
			case HARAMONIOUS_TRAINING_CENTER_2:
			case HARAMONIOUS_TRAINING_CENTER_3:
			case HARAMONIOUS_TRAINING_CENTER_4:
				return true;
		}
		return false;
	}

	public boolean isHarmonyArena() {
		switch (this) {
			case ARENA_OF_HARMONY_1:
			case ARENA_OF_HARMONY_2:
			case ARENA_OF_HARMONY_3:
			case ARENA_OF_HARMONY_4:
				return true;
		}
		return false;
	}

	public boolean isGloryArena() {
		switch (this) {
			case ARENA_OF_GLORY_1:
			case ARENA_OF_GLORY_2:
				return true;
		}
		return false;
	}

	public boolean isPvpArena() {
		return isTrainigPvPFFAArena() || isPvPFFAArena() || isTrainigPvPSoloArena() || isPvPSoloArena();
	}

	public boolean hasLevelPermit(int level) {
		return level >= getMinLevel() && level <= getMaxLevel();
	}

	public byte getDifficultId() {
		return difficultId;
	}

	public AutoInstance getAutoInstance() {
		return newAutoInstance();
	}

	abstract AutoInstance newAutoInstance();
}
