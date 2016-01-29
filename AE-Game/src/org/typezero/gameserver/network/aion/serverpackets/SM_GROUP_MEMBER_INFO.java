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

import java.util.List;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.model.stats.container.PlayerLifeStats;
import org.typezero.gameserver.model.team2.common.legacy.GroupEvent;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.world.WorldPosition;

/**
 * @author Lyahim, ATracer
 */
public class SM_GROUP_MEMBER_INFO extends AionServerPacket {

	private int groupId;
	private Player player;
	private GroupEvent event;

	public SM_GROUP_MEMBER_INFO(PlayerGroup group, Player player, GroupEvent event) {
		this.groupId = group.getTeamId();
		this.player = player;
		this.event = event;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		PlayerLifeStats pls = player.getLifeStats();
		PlayerCommonData pcd = player.getCommonData();
		WorldPosition wp = pcd.getPosition();

		if (event == GroupEvent.ENTER && !player.isOnline()) {
			event = GroupEvent.ENTER_OFFLINE;
		}

		writeD(groupId);
		writeD(player.getObjectId());
		if (player.isOnline()) {
			writeD(pls.getMaxHp());
			writeD(pls.getCurrentHp());
			writeD(pls.getMaxMp());
			writeD(pls.getCurrentMp());
			writeD(pls.getMaxFp()); // maxflighttime
			writeD(pls.getCurrentFp()); // currentflighttime
		}
		else {
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
		}

		writeD(0);//unk 3.5
		writeD(wp.getMapId());
		writeD(wp.getMapId());
		writeF(wp.getX());
		writeF(wp.getY());
		writeF(wp.getZ());
		writeC(pcd.getPlayerClass().getClassId()); // class id
		writeC(pcd.getGender().getGenderId()); // gender id
		writeC(pcd.getLevel()); // level

		writeC(event.getId()); // something events
		writeH(player.isOnline() ? 1 : 0); // TODO channel?
		writeC(player.isMentor() ? 0x01 : 0x00);

		switch (event) {
			case MOVEMENT:
			case DISCONNECTED:
				break;
			case LEAVE:
				writeH(0x00); // unk
				writeC(0x00); // unk
				break;
			case ENTER_OFFLINE:
			case JOIN:
				writeS(pcd.getName()); // name
				break;
            case UPDATE:
                writeS(pcd.getName()); // name
                writeD(0x00); // unk
                writeD(0x00); // unk
                writeC(127); // TODO  unk 4.5 slots ? can be 0x7F:127, 0x04:4, 0x01:1
                List<Effect> abnormalEffects1 = player.getEffectController().getAbnormalEffects();
                writeH(abnormalEffects1.size()); // Abnormal effects
                for (Effect effect : abnormalEffects1) {
                    writeD(effect.getEffectorId()); // casterid
                    writeH(effect.getSkillId()); // spellid
                    writeC(effect.getSkillLevel()); // spell level
                    if(effect.getTargetSlot()== 3) {
                        writeC(0); // unk ?
                    }
                    else {
                        writeC(effect.getTargetSlot()); // unk ?
                    }
                    writeD(effect.getRemainingTime()); // estimatedtime
                }
                writeD(0x25F7); // unk 9719
                break;
			default:
				writeS(pcd.getName()); // name
				writeD(0x00); // unk
				writeD(0x00); // unk
				writeC(127); // // TODO unk 4.5 slots ? can be 0x7F:127, 0x04:4, 0x01:1
				List<Effect> abnormalEffects = player.getEffectController().getAbnormalEffects();
				writeH(abnormalEffects.size()); // Abnormal effects
				for (Effect effect : abnormalEffects) {
					writeD(effect.getEffectorId()); // casterid
					writeH(effect.getSkillId()); // spellid
					writeC(effect.getSkillLevel()); // spell level
                    if(effect.getTargetSlot()== 3) {
                        writeC(0); // unk ?
                    }
					else {
                        writeC(effect.getTargetSlot()); // unk ?
                    }
					writeD(effect.getRemainingTime()); // estimatedtime
				}
				writeD(0x25F7); // unk 9719
				break;
		}
	}
}
