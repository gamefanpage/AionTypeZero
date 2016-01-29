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

package org.typezero.gameserver.model.stats.container;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.actions.PlayerMode;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Equipment;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.model.stats.calc.AdditionStat;
import org.typezero.gameserver.model.stats.calc.Stat2;
import org.typezero.gameserver.model.templates.item.WeaponType;
import org.typezero.gameserver.model.templates.ride.RideInfo;
import org.typezero.gameserver.model.templates.stats.PlayerStatsTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import org.typezero.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author xavier modified by GoodT
 */
public class PlayerGameStats extends CreatureGameStats<Player> {

	private int cachedSpeed;
	private int cachedAttackSpeed;

	/**
	 * @param owner
	 */
	public PlayerGameStats(Player owner) {
		super(owner);
	}

	@Override
	protected void onStatsChange() {
		super.onStatsChange();
		updateStatsAndSpeedVisually();
	}

	public void updateStatsAndSpeedVisually() {
		updateStatsVisually();
		checkSpeedStats();
	}

	public void updateStatsVisually() {
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_STATS);
	}

	private void checkSpeedStats() {
		int current = getMovementSpeed().getCurrent();
		int currentAttackSpeed = getAttackSpeed().getCurrent();
		if (current != cachedSpeed || currentAttackSpeed != cachedAttackSpeed) {
			owner.addPacketBroadcastMask(BroadcastMode.UPDATE_SPEED);
		}
		cachedSpeed = current;
		cachedAttackSpeed = currentAttackSpeed;
	}

	@Override
	public Stat2 getMaxHp() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.MAXHP, pst.getMaxHp());
	}

	@Override
	public Stat2 getMaxMp() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.MAXMP, pst.getMaxMp());
	}

	@Override
	public Stat2 getPCR() {
		int base = 0;
		int pLevel = owner.getLevel();
		if(pLevel > 50) {
			base += (pLevel - 50) * 6;
		}
		else
			base = 60;
		return getStat(StatEnum.PHYSICAL_CRITICAL_RESIST, base);
	}

	@Override
	public Stat2 getMCR() {
		int base = 0;
		int Pclass = owner.getPlayerClass().getClassId();
		if (Pclass == 7 || Pclass == 8 || Pclass == 10 || Pclass == 16)
			base = 50;
		return getStat(StatEnum.MAGICAL_CRITICAL_RESIST, base);
	}

	public Stat2 getMaxDp() {
		return getStat(StatEnum.MAXDP, 4000);
	}

	public Stat2 getFlyTime() {
		return getStat(StatEnum.FLY_TIME, CustomConfig.BASE_FLYTIME);
	}

	@Override
	public Stat2 getAllSpeed() {
		int base = 7500; //TODO current value
		return getStat(StatEnum.ALLSPEED, base);
	}

	@Override
	public Stat2 getAttackSpeed() {
		int base = 1500;
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();

		if (mainHandWeapon != null) {
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackSpeed();
			Item offWeapon = owner.getEquipment().getOffHandWeapon();
			if (offWeapon != null && !offWeapon.getItemTemplate().isTwoHandWeapon())
				base += offWeapon.getItemTemplate().getWeaponStats().getAttackSpeed() / 4;
		}
		Stat2 aSpeed = getStat(StatEnum.ATTACK_SPEED, base);
		return aSpeed;
	}

	@Override
	public Stat2 getMovementSpeed() {
		Stat2 movementSpeed;
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		if (owner.isInPlayerMode(PlayerMode.RIDE)) {
			RideInfo ride = owner.ride;
			int runSpeed = (int) pst.getRunSpeed() * 1000;
			if (owner.isInState(CreatureState.FLYING)) {
				movementSpeed = new AdditionStat(StatEnum.FLY_SPEED, runSpeed, owner);
				movementSpeed.addToBonus((int)(ride.getFlySpeed() * 1000) - runSpeed);
			}
			else {
				float speed = owner.isInSprintMode() ? ride.getSprintSpeed() : ride.getMoveSpeed();
				movementSpeed = new AdditionStat(StatEnum.SPEED, runSpeed, owner);
				movementSpeed.addToBonus((int)(speed * 1000) - runSpeed);
			}
		}
		else if (owner.isInFlyingState())
			movementSpeed = getStat(StatEnum.FLY_SPEED, Math.round(pst.getFlySpeed() * 1000));
		else if (owner.isInState(CreatureState.FLIGHT_TELEPORT) && !owner.isInState(CreatureState.RESTING))
			movementSpeed = getStat(StatEnum.SPEED, 12000);
		else if (owner.isInState(CreatureState.WALKING))
			movementSpeed = getStat(StatEnum.SPEED, Math.round(pst.getWalkSpeed() * 1000));
		else if (getAllSpeed().getBonus() != 0){
			movementSpeed = getStat(StatEnum.SPEED, getAllSpeed().getCurrent());
		}
		else
			movementSpeed = getStat(StatEnum.SPEED, Math.round(pst.getRunSpeed() * 1000));
		return movementSpeed;
	}

	@Override
	public Stat2 getAttackRange() {
		int base = 1500;
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null) {
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackRange();
		}
		return getStat(StatEnum.ATTACK_RANGE, base);
	}

	@Override
	public Stat2 getPDef() {
		return getStat(StatEnum.PHYSICAL_DEFENSE, 0);
	}

	@Override
	public Stat2 getMDef() {
		return getStat(StatEnum.MAGICAL_DEFEND, 0);
	}

	@Override
	public Stat2 getMResist() {
		return getStat(StatEnum.MAGICAL_RESIST, 0);
	}

	@Override
	public Stat2 getPower() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.POWER, pst.getPower());
	}

	@Override
	public Stat2 getHealth() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.HEALTH, pst.getHealth());
	}

	@Override
	public Stat2 getAccuracy() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.ACCURACY, pst.getAccuracy());
	}

	@Override
	public Stat2 getAgility() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.AGILITY, pst.getAgility());
	}

	@Override
	public Stat2 getKnowledge() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.KNOWLEDGE, pst.getKnowledge());
	}

	@Override
	public Stat2 getWill() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.WILL, pst.getWill());
	}

	@Override
	public Stat2 getEvasion() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.EVASION, pst.getEvasion());
	}

	@Override
	public Stat2 getParry() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getParry();
		Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null) {
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getParry();
		}
		return getStat(StatEnum.PARRY, base);
	}

	@Override
	public Stat2 getBlock() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.BLOCK, pst.getBlock());
	}

	@Override
	public Stat2 getMainHandPAttack() {
		int base = 18;
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null) {
			if (mainHandWeapon.getItemTemplate().getAttackType().isMagical())
				return new AdditionStat(StatEnum.MAIN_HAND_POWER, 0, owner);
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
		}
		Stat2 stat = getStat(StatEnum.PHYSICAL_ATTACK, base);
		if (mainHandWeapon != null) {
			if (!mainHandWeapon.getItemTemplate().isTwoHandWeapon())
				return getStat(StatEnum.MAIN_HAND_POWER, stat);
			else
				return getStat(StatEnum.OFF_HAND_POWER, stat);
		}
		return getStat(StatEnum.MAIN_HAND_POWER, stat);
	}

	public Stat2 getOffHandPAttack() {
		Equipment equipment = owner.getEquipment();
		Item offHandWeapon = equipment.getOffHandWeapon();
		if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon() && !offHandWeapon.getItemTemplate().isTwoHandWeapon()) {
			int base = offHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
			base *= 0.98;
			Stat2 stat = getStat(StatEnum.PHYSICAL_ATTACK, base);
			return getStat(StatEnum.OFF_HAND_POWER, stat);
		}
		return new AdditionStat(StatEnum.OFF_HAND_POWER, 0, owner);
	}

	@Override
	public Stat2 getMainHandPCritical() {
		int base = 2;
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null) {
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical();
		}
		return getStat(StatEnum.PHYSICAL_CRITICAL, base);
	}

	public Stat2 getOffHandPCritical() {
		Equipment equipment = owner.getEquipment();
		Item offHandWeapon = equipment.getOffHandWeapon();
		if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon() && !offHandWeapon.getItemTemplate().isTwoHandWeapon()) {
			int base = offHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical();
			return getStat(StatEnum.PHYSICAL_CRITICAL, base);
		}
		return new AdditionStat(StatEnum.OFF_HAND_CRITICAL, 0, owner);
	}

	@Override
	public Stat2 getMainHandPAccuracy() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getMainHandAccuracy();
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null) {
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalAccuracy();
		}
		return getStat(StatEnum.PHYSICAL_ACCURACY, base);
	}

	public Stat2 getOffHandPAccuracy() {
		Equipment equipment = owner.getEquipment();
		Item offHandWeapon = equipment.getOffHandWeapon();
		if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon() && !offHandWeapon.getItemTemplate().isTwoHandWeapon()) {
			PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
			int base = pst.getMainHandAccuracy();
			base += offHandWeapon.getItemTemplate().getWeaponStats().getPhysicalAccuracy();
			return getStat(StatEnum.PHYSICAL_ACCURACY, base);
		}
		return new AdditionStat(StatEnum.OFF_HAND_ACCURACY, 0, owner);
	}

	@Override
	public Stat2 getMainHandMAttack() {
		int base = 16;
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null) {
			if (!mainHandWeapon.getItemTemplate().getAttackType().isMagical())
				return new AdditionStat(StatEnum.MAIN_HAND_MAGICAL_ATTACK, 0, owner);
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
		}
		Stat2 stat = getStat(StatEnum.MAGICAL_ATTACK, base);
		return getStat(StatEnum.MAIN_HAND_MAGICAL_ATTACK, stat);
	}

	public Stat2 getOffHandMAttack() {
		Equipment equipment = owner.getEquipment();
		Item offHandWeapon = equipment.getOffHandWeapon();
		if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon() && !offHandWeapon.getItemTemplate().isTwoHandWeapon()) {
			if (!offHandWeapon.getItemTemplate().getAttackType().isMagical())
				return new AdditionStat(StatEnum.OFF_HAND_MAGICAL_ATTACK, 0, owner);
			int base = offHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
			base *= 0.82;
			Stat2 stat = getStat(StatEnum.MAGICAL_ATTACK, base);
			return getStat(StatEnum.OFF_HAND_MAGICAL_ATTACK, stat);
		}
		return new AdditionStat(StatEnum.OFF_HAND_MAGICAL_ATTACK, 0, owner);
	}

	@Override
	public Stat2 getMBoost() {
		int base = 0;
		Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null) {
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getBoostMagicalSkill();
		}
		return getStat(StatEnum.BOOST_MAGICAL_SKILL, base);
	}

	@Override
	public Stat2 getMBResist() {
		int base = 0;
		return getStat(StatEnum.MAGIC_SKILL_BOOST_RESIST, base);
	}

	@Override
	public Stat2 getMainHandMAccuracy() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getMagicAccuracy();
		Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null) {
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getMagicalAccuracy();
		}
		return getStat(StatEnum.MAGICAL_ACCURACY, base);
	}

	public Stat2 getOffHandMAccuracy() {
		Equipment equipment = owner.getEquipment();
		Item offHandWeapon = equipment.getOffHandWeapon();
		if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon() && !offHandWeapon.getItemTemplate().isTwoHandWeapon()) {
			PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
			int base = pst.getMagicAccuracy();
			base += offHandWeapon.getItemTemplate().getWeaponStats().getMagicalAccuracy();
			return getStat(StatEnum.MAGICAL_ACCURACY, base);
		}
		return new AdditionStat(StatEnum.OFF_HAND_MAGICAL_ACCURACY, 0, owner);
	}

	@Override
	public Stat2 getMCritical() {
        int base = 50;
        Equipment equipment = owner.getEquipment();
        Item mainHandWeapon = equipment.getMainHandWeapon();
        if (mainHandWeapon != null) {
            if (mainHandWeapon.getItemTemplate().getWeaponType() != null && (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.GUN_1H)
                    || mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.KEYBLADE_2H
                    || mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.CANNON_2H) {
                base = mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical() + base;
            }
        }
       return getStat(StatEnum.MAGICAL_CRITICAL, base);
    }

	@Override
	public Stat2 getHpRegenRate() {
		int base = owner.getLevel() + 3;
		if (owner.isInState(CreatureState.RESTING))
			base *= 8;
		base *= getHealth().getCurrent() / 100f;
		return getStat(StatEnum.REGEN_HP, base);
	}

	@Override
	public Stat2 getMpRegenRate() {
		int base = owner.getLevel() + 8;
		if (owner.isInState(CreatureState.RESTING))
			base *= 8;
		base *= getWill().getCurrent() / 100f;
		return getStat(StatEnum.REGEN_MP, base);
	}

	@Override
	public Stat2 getHealBoost() {
		int base = 0;
		/*int Pclass = owner.getPlayerClass().getClassId();
		if (Pclass == 10)
			base = 139;
		if (Pclass == 16)
			base = 56;*/
		return getStat(StatEnum.HEAL_BOOST, base);
	}

	@Override
	public void updateStatInfo() {
		PacketSendUtility.sendPacket(owner, new SM_STATS_INFO(owner));
	}

	@Override
	public void updateSpeedInfo() {
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2, 0, 0), true);
	}

}
