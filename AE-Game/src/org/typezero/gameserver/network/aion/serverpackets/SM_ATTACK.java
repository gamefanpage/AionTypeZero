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

package org.typezero.gameserver.network.aion.serverpackets;

import org.typezero.gameserver.controllers.attack.AttackResult;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import java.util.List;

/**
 * @author -Nemesiss-, Sweetkr
 */
public class SM_ATTACK extends AionServerPacket {

	private int attackno;
	private int time;
	private int type;
	private List<AttackResult> attackList;
	private Creature attacker;
	private Creature target;

	public SM_ATTACK(Creature attacker, Creature target, int attackno, int time, int type, List<AttackResult> attackList) {
		this.attacker = attacker;
		this.target = target;
		this.attackno = attackno;// empty
		this.time = time;// empty
		this.type = type;// empty
		this.attackList = attackList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(attacker.getObjectId());
		writeC(attackno); // unknown
		writeH(time); // unknown
		writeC(0);
		writeC(type); // 0, 1, 2
		writeD(target.getObjectId());

		int attackerMaxHp = attacker.getLifeStats().getMaxHp();
		int attackerCurrHp = attacker.getLifeStats().getCurrentHp();
		int targetMaxHp = target.getLifeStats().getMaxHp();
		int targetCurrHp = target.getLifeStats().getCurrentHp();

		writeC((int) (100f * targetCurrHp / targetMaxHp)); // target %hp
		writeC((int) (100f * attackerCurrHp / attackerMaxHp)); // attacker %hp

		// TODO refactor attack controller
		switch (attackList.get(0).getAttackStatus().getId()) // Counter skills
		{
			case -60: // case CRITICAL_BLOCK
			case 4: // case BLOCK
				writeH(32);
				break;
			case -62: // case CRITICAL_PARRY
			case 2: // case PARRY
				writeH(64);
				break;
			case -64: // case CRITICAL_DODGE
			case 0: // case DODGE
				writeH(128);
				break;
			case -58: // case PHYSICAL_CRITICAL_RESIST
			case 6: // case RESIST
				writeH(256); // need more info becuz sometimes 0
				break;
			default:
				writeH(0);
				break;
		}
		//setting counter skill from packet to have the best synchronization of time with client
		if (target instanceof Player) {
			if (attackList.get(0).getAttackStatus().isCounterSkill())
				((Player) target).setLastCounterSkill(attackList.get(0).getAttackStatus());
		}

		writeH(0);

		//TODO! those 2h (== d) up is some kind of very weird flag...
		//writeD(attackFlag);
		/*if(attackFlag & 0x10A0F != 0)
		{
			writeF(0);
			writeF(0);
			writeF(0);
		}
		if(attackFlag & 0x10010 != 0)
		{
			writeC(0);
		}
		if(attackFlag & 0x10000 != 0)
		{
			writeD(0);
			writeD(0);
		}*/

		writeC(attackList.size());
		for (AttackResult attack : attackList) {
			writeD(attack.getDamage());
			writeC(attack.getAttackStatus().getId());

			byte shieldType = (byte) attack.getShieldType();
			writeC(shieldType);

			/**
			 * shield Type:
			 * 1: reflector
			 * 2: normal shield
			 * 8: protect effect (ex. skillId: 417 Bodyguard)
			 * TODO find out 4
			 */
			switch (shieldType) {
				case 0:
				case 2:
					break;
				case 8:
				case 10:
					writeD(attack.getProtectorId()); // protectorId
					writeD(attack.getProtectedDamage()); // protected damage
					writeD(attack.getProtectedSkillId()); // skillId
					break;
                case 16:
					writeD(0); // unk
					writeD(0); // unk
					writeD(0); // unk
                    writeD(0); // unk
					writeD(0); // unk
					writeD(attack.getShieldMp());
					writeD(attack.getReflectedSkillId());
				default:
					writeD(attack.getProtectorId()); // protectorId
					writeD(attack.getProtectedDamage()); // protected damage
					writeD(attack.getProtectedSkillId()); // skillId
					writeD(attack.getReflectedDamage()); // reflect damage
					writeD(attack.getReflectedSkillId()); // skill id
					writeD(0); // unk
                    writeD(0); // unk
					break;
			}
		}
		writeC(0);
	}

}
