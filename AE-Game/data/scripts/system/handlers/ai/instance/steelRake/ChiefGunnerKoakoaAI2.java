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


package ai.instance.steelRake;

import ai.SummonerAI2;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.ai.Percentage;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author xTz
 */
@AIName("gunnerkoakoa")
public class ChiefGunnerKoakoaAI2 extends SummonerAI2 {

	@Override
	protected void handleIndividualSpawnedSummons(Percentage percent) {
		if (getEffectController().hasAbnormalEffect(18552)) {
			checkAbnormalEffect();
		}
		randomSpawn(Rnd.get(1, 3));
	}

	private void checkAbnormalEffect() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				getEffectController().removeEffect(18552);
				// to do remove pause
			}
		}, 21000);
	}

	private void randomSpawn(int i) {
		// to do pause boss
		spawn(281212, 757.39746f, 508.70383f, 1012.30084f, (byte) 0);
		switch (i) {
			case 1:
				spawn(281212, 726.1167f, 503.28836f, 1012.6846f, (byte) 0);
				spawn(281212, 736.4446f, 505.3141f, 1012.1576f, (byte) 0);
				spawn(281212, 746.9261f, 503.50122f, 1012.68335f, (byte) 0);
				spawn(281212, 728.9705f, 492.59402f, 1012.68335f, (byte) 0);
				spawn(281212, 739.9526f, 491.54123f, 1011.692f, (byte) 0);
				spawn(281212, 749.754f, 491.74677f, 1011.8663f, (byte) 0);
				spawn(281212, 756.9996f, 500.01736f, 1011.692f, (byte) 0);
				spawn(281213, 736.9722f, 514.6446f, 1011.8599f, (byte) 0);
				spawn(281213, 747.5162f, 514.51715f, 1011.692f, (byte) 0);
				spawn(281213, 726.8303f, 514.5155f, 1012.6845f, (byte) 0);
				spawn(281213, 727.9019f, 524.578f, 1012.68365f, (byte) 0);
				spawn(281213, 738.52844f, 525.0482f, 1011.692f, (byte) 0);
				spawn(281213, 758.3127f, 520.59143f, 1011.692f, (byte) 0);
				spawn(281213, 748.7474f, 525.84f, 1011.859f, (byte) 0);
				break;
			case 2:
				spawn(281213, 726.1167f, 503.28836f, 1012.6846f, (byte) 0);
				spawn(281213, 736.4446f, 505.3141f, 1012.1576f, (byte) 0);
				spawn(281212, 746.9261f, 503.50122f, 1012.68335f, (byte) 0);
				spawn(281213, 728.9705f, 492.59402f, 1012.68335f, (byte) 0);
				spawn(281213, 739.9526f, 491.54123f, 1011.692f, (byte) 0);
				spawn(281212, 749.754f, 491.74677f, 1011.8663f, (byte) 0);
				spawn(281212, 756.9996f, 500.01736f, 1011.692f, (byte) 0);
				spawn(281212, 736.9722f, 514.6446f, 1011.8599f, (byte) 0);
				spawn(281213, 747.5162f, 514.51715f, 1011.692f, (byte) 0);
				spawn(281212, 726.8303f, 514.5155f, 1012.6845f, (byte) 0);
				spawn(281212, 727.9019f, 524.578f, 1012.68365f, (byte) 0);
				spawn(281212, 738.52844f, 525.0482f, 1011.692f, (byte) 0);
				spawn(281213, 758.3127f, 520.59143f, 1011.692f, (byte) 0);
				spawn(281213, 748.7474f, 525.84f, 1011.859f, (byte) 0);
				break;
			case 3:
				spawn(281212, 726.1167f, 503.28836f, 1012.6846f, (byte) 0);
				spawn(281212, 736.4446f, 505.3141f, 1012.1576f, (byte) 0);
				spawn(281213, 746.9261f, 503.50122f, 1012.68335f, (byte) 0);
				spawn(281212, 728.9705f, 492.59402f, 1012.68335f, (byte) 0);
				spawn(281212, 739.9526f, 491.54123f, 1011.692f, (byte) 0);
				spawn(281213, 749.754f, 491.74677f, 1011.8663f, (byte) 0);
				spawn(281213, 756.9996f, 500.01736f, 1011.692f, (byte) 0);
				spawn(281213, 736.9722f, 514.6446f, 1011.8599f, (byte) 0);
				spawn(281212, 747.5162f, 514.51715f, 1011.692f, (byte) 0);
				spawn(281213, 726.8303f, 514.5155f, 1012.6845f, (byte) 0);
				spawn(281213, 727.9019f, 524.578f, 1012.68365f, (byte) 0);
				spawn(281213, 738.52844f, 525.0482f, 1011.692f, (byte) 0);
				spawn(281212, 758.3127f, 520.59143f, 1011.692f, (byte) 0);
				spawn(281212, 748.7474f, 525.84f, 1011.859f, (byte) 0);
				break;
		}
	}
}
