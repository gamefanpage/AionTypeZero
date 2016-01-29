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

import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;


/**
 * @author Alcapwnd
 *         this fatigue system isnt implemented on official servers
 *         its inside but not enabled
 */
public class SM_FATIGUE extends AionServerPacket {

    private int effectEnabled;
    private int isFull;
    private int fatigueRecover;
    private int iconSet;

    /**
	 * @param fatigueRecover
	 */
    public SM_FATIGUE(int effectEnabled, int isFull, int fatigueRecover, int iconSet) {
        this.effectEnabled = effectEnabled;
        this.isFull = isFull;
        this.fatigueRecover = 0;//fatigueRecover
        this.iconSet = iconSet;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(0);//unk
        writeC(0);//unk
        writeC(effectEnabled);// 1=effect enabled | 0=effect disabled //VERIFIED!
        writeH(iconSet);//icon
        writeC(isFull);//isFull 1=100% | 0=0% //VERIFIED!
        writeC(fatigueRecover);//fatigue recovery //VERIFIED! //seems it isnt implemented
    }
}
