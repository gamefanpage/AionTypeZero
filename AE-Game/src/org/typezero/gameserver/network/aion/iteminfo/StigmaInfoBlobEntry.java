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

package org.typezero.gameserver.network.aion.iteminfo;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.templates.item.Stigma;
import org.typezero.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

import java.nio.ByteBuffer;

/**
 * This blob contains stigma info.
 *
 * @author -Nemesiss-
 * @modified Rolandas
 */
public class StigmaInfoBlobEntry extends ItemBlobEntry {

	StigmaInfoBlobEntry() {
		super(ItemBlobType.STIGMA_INFO);
	}

	@Override
	public void writeThisBlob(ByteBuffer buf) {
		Item item = ownerItem;
		Stigma stigma = item.getItemTemplate().getStigma();

		writeD(buf, stigma.getSkills().get(0).getSkillId());		// skill id 1
		if (stigma.getSkills().size() >= 2)
			writeD(buf, stigma.getSkills().get(1).getSkillId());	// skill id 2
		else
			writeD(buf, 0);

		writeD(buf, stigma.getKinah());

		skip(buf, 192);
		writeH(buf, 0x1);	// unk
		writeH(buf, 0);
		skip(buf, 96);
		writeH(buf, 0);		// unk
	}

	@Override
	public int getSize() {
		return 8 + 4 + 192 + 4 + 96 + 2;
	}
}
