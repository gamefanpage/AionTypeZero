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

import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import org.typezero.gameserver.model.CreatureType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.ItemSlot;
import org.typezero.gameserver.model.items.NpcEquippedGear;
import org.typezero.gameserver.model.templates.BoundRadius;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.services.TownService;

/**
 * This packet is displaying visible npc/monsters.
 *
 * @author -Nemesiss-
 */
public class SM_NPC_INFO extends AionServerPacket {

	/**
	 * Visible npc
	 */
	private Creature _npc;
	private NpcTemplate npcTemplate;
	private int npcId;
	private int creatorId;
	private String masterName = StringUtils.EMPTY;
	@SuppressWarnings("unused")
	private float speed = 0.3f;
	private int npcTypeId;

	/**
	 * Constructs new <tt>SM_NPC_INFO </tt> packet
	 *
	 * @param player
	 * @param kisk - the visible npc.
	 */
	public SM_NPC_INFO(Npc npc, Player player) {
		this._npc = npc;
		npcTemplate = npc.getObjectTemplate();
		npcTypeId = npc.getType(player);
		npcId = npc.getNpcId();
		creatorId = npc.getCreatorId();
		masterName = npc.getMasterName();
	}

	/**
	 * @param summon
	 */
	public SM_NPC_INFO(Summon summon, Player player) {
		this._npc = summon;
		npcTemplate = summon.getObjectTemplate();
		npcId = summon.getNpcId();
		Player owner = summon.getMaster();
		npcTypeId = !player.isEnemy(owner) ? CreatureType.SUPPORT.getId() : CreatureType.ATTACKABLE.getId();
		if (owner != null) {
			creatorId = owner.getObjectId();
			masterName = owner.getName();
			speed = owner.getGameStats().getMovementSpeedFloat();
		}
		else {
			masterName = "LOST";
		}
	}

    /**
     * @param add mob
     */
    public SM_NPC_INFO(Npc npc, String master) {
        this._npc = npc;
        npcTemplate = npc.getObjectTemplate();
        npcTypeId = CreatureType.ATTACKABLE.getId();
        npcId = npc.getNpcId();
        masterName = master;
    }
    /**
     * {@inheritDoc}
     */
	@Override
	protected void writeImpl(AionConnection con) {
		writeF(_npc.getX());// x
		writeF(_npc.getY());// y
		writeF(_npc.getZ());// z
		writeD(_npc.getObjectId());
		writeD(npcId);
		writeD(npcId);

		writeC(npcTypeId);

		writeH(_npc.getState());// unk 65=normal,0x47 (71)= [dead npc ?]no drop,0x21(33)=fight state,0x07=[dead
		// monster?]
		// no drop
		// 3,19 - wings spread (NPCs)
		// 5,6,11,21 - sitting (NPC)
		// 7,23 - dead (no drop)
		// 8,24 - [dead][NPC only] - looks like some orb of light (no normal mesh)
		// 32,33 - fight mode

		writeC(_npc.getHeading());
		writeD(npcTemplate.getNameId());
		writeD(npcTemplate.getTitleId());// TODO: implement fortress titles

		writeH(0x00);// unk
		writeC(0x00);// unk
		writeD(0x00);// unk

		/*
		 * Creator/Master Info (Summon, Kisk, Etc)
		 */
		writeD(creatorId);// creatorId - playerObjectId or House address
		if (con.getActivePlayer() != null && con.getActivePlayer().isGM()) {
			masterName = npcId + " " + _npc.getAi2().getName() + " " + _npc.getLifeStats().getMaxHp();
		}
		writeS(masterName);// masterName

		int maxHp = _npc.getLifeStats().getMaxHp();
		int currHp = _npc.getLifeStats().getCurrentHp();

		writeC((int) (100f * currHp / maxHp));// %hp
		writeD(_npc.getGameStats().getMaxHp().getCurrent());
		writeC(_npc.getLevel());// lvl

		NpcEquippedGear gear = npcTemplate.getEquipment();
		boolean hasWeapon = false;
		BoundRadius boundRadius = npcTemplate.getBoundRadius();

		if (gear == null) {
			writeD(0x00);
			writeF(boundRadius.getFront());
		}
		else {
			writeD(gear.getItemsMask());
			for (Entry<ItemSlot, ItemTemplate> item : gear) // getting it from template ( later if we make sure that npcs
			// actually use items, we'll make Item from it )
			{
				if (item.getValue().getWeaponType() != null)
					hasWeapon = true;
				writeD(item.getValue().getTemplateId());
				writeD(0x00);
				writeD(0x00);
				writeH(0x00);
                writeH(0x00);
			}
			// we don't know weapon dimensions, just add 0.1
			writeF(boundRadius.getFront() + 0.125f + (hasWeapon ? 0.1f : 0f));
		}

		writeF(npcTemplate.getHeight());
		writeF(_npc.getGameStats().getMovementSpeedFloat());// speed

		writeH(npcTemplate.getAttackDelay());
		writeH(npcTemplate.getAttackDelay());

		writeC(_npc.isFlag() ? 0x13 : _npc.isNewSpawn() ? 0x01 : 0x00);

		/**
		 * Movement
		 */
		writeF(_npc.getMoveController().getTargetX2());// x
		writeF(_npc.getMoveController().getTargetY2());// y
		writeF(_npc.getMoveController().getTargetZ2());// z
		writeC(_npc.getMoveController().getMovementMask()); // move type

        SpawnTemplate spawn = _npc.getSpawn();
        if (spawn == null) {
            writeH(0);
        } else {
            writeH(spawn.getStaticId());
        }
        writeQ(0);
        writeC(_npc.getVisualState()); // visualState

		/**
		 * 1 : normal (kisk too) 2 : summon 32 : trap 64 : skill area 1024 : holy servant, noble energy
		 */
		writeH(_npc.getNpcObjectType().getId());
		writeC(0x00); // unk
		writeD(_npc.getTarget() == null ? 0 : _npc.getTarget().getObjectId());
		writeD(TownService.getInstance().getTownIdByPosition(_npc));
	}

}
