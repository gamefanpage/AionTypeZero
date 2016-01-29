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


import org.typezero.gameserver.model.templates.gather.GatherableTemplate;
import org.typezero.gameserver.model.templates.gather.Material;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author ATracer, orz
 */
public class SM_GATHER_UPDATE extends AionServerPacket {

	private GatherableTemplate template;
	private int action;
	private int itemId;
	private int success;
	private int failure;
	private int nameId;

	public SM_GATHER_UPDATE(GatherableTemplate template, Material material, int success, int failure, int action) {
		this.action = action;
		this.template = template;
		this.itemId = material.getItemid();
		this.success = success;
		this.failure = failure;
		this.nameId = material.getNameid();
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeH(template.getHarvestSkill());
		writeC(action);
		writeD(itemId);

		switch (action) {
			case 0: {
				writeD(template.getSuccessAdj());
				writeD(template.getFailureAdj());
				writeD(0);
				writeD(1200); // timer??
				writeD(1330011); // ??text??skill??
				writeH(0x24); // 0x24
				writeD(nameId);
				writeH(0); // 0x24
				break;
			}
			case 1: {
				writeD(success);
				writeD(failure);
				writeD(700); // unk timer??
				writeD(1200); // unk timer??
				writeD(0); // unk timer??writeD(700);
				writeH(0);
				break;
			}
			case 2: {
				writeD(template.getSuccessAdj());
				writeD(failure);
				writeD(700);// unk timer??
				writeD(1200); // unk timer??
				writeD(0); // unk timer??writeD(700);
				writeH(0);
				break;
			}
			case 5: // you have stopped gathering
			{
				writeD(0);
				writeD(0);
				writeD(700);// unk timer??
				writeD(1200); // unk timer??
				writeD(1330080); // unk timer??writeD(700);
				writeH(0);
				break;
			}
			case 6: {
				writeD(template.getSuccessAdj());
				writeD(failure);
				writeD(700); // unk timer??
				writeD(1200); // unk timer??
				writeD(0); // unk timer??writeD(700);
				writeH(0);
				break;
			}
			case 7: {
				writeD(success);
				writeD(template.getFailureAdj());
				writeD(0);
				writeD(1200); // timer??
				writeD(1330079); // ??text??skill??
				writeH(0x24); // 0x24
				writeD(nameId);
				writeH(0); // 0x24
				break;
			}
		}
	}

}
