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

package ai.instance.unstableSplinterpath;

import ai.AggressiveNpcAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.utils.MathUtil;


/**
 * @author Cheatkiller
 *
 */
@AIName("pieceofmidnight")
public class PieceOfMidnightAI2 extends AggressiveNpcAI2 {

  @Override
  protected void handleCreatureSee(Creature creature) {
      checkDistance(this, creature);
  }

  @Override
  protected void handleCreatureMoved(Creature creature) {
      checkDistance(this, creature);
  }

  private void checkDistance(NpcAI2 ai, Creature creature) {
  	Npc rukril = getPosition().getWorldMapInstance().getNpc(219551);
  	Npc ebonsoul = getPosition().getWorldMapInstance().getNpc(219552);
     if (creature instanceof Npc) {
    	if (MathUtil.isIn3dRange(getOwner(), rukril, 5) && rukril.getEffectController().hasAbnormalEffect(19266)) {
    		rukril.getEffectController().removeEffect(19266);
    		if(ebonsoul != null && ebonsoul.getEffectController().hasAbnormalEffect(19159))
    			ebonsoul.getEffectController().removeEffect(19159);
    	}
    }
  }
}
