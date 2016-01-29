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

package org.typezero.gameserver.model.instance.instanceposition;

import org.typezero.gameserver.model.gameobjects.player.Player;

/**
 *
 * @author xTz
 */
public class DisciplineInstancePosition extends GenerealInstancePosition {

	@Override
	public void port(Player player, int zone, int position) {
		switch (position) {
			case 1:
				switch (zone) {
					case 1:
						teleport(player, 1841.294f, 1041.223f, 338.20056f, (byte) 15);
						break;
					case 2:
						teleport(player, 278.18478f, 1265.8389f, 263.1712f, (byte) 73);
						break;
					case 3:
						teleport(player, 709.78845f, 1766.1855f, 183.43953f, (byte) 60);
						break;
					case 4:
						teleport(player, 1817.1067f, 1737.4899f, 311.49692f, (byte) 1);
						break;
				}
				break;
			case 2:
				switch (zone) {
					case 1:
						teleport(player, 1869.4803f, 1041.8444f, 337.9918f, (byte) 43);
						break;
					case 2:
						teleport(player, 251.03516f, 1297.7039f, 248.11426f, (byte) 105);
						break;
					case 3:
						teleport(player, 693.93176f, 1761.0234f, 196.12753f, (byte) 21);
						break;
					case 4:
						teleport(player, 1851.6932f, 1765.4813f, 305.23187f, (byte) 90);
						break;
				}
				break;
			case 3:
				switch (zone) {
					case 1:
						teleport(player, 1869.0569f, 1069.1344f, 337.6657f, (byte) 71);
						break;
					case 2:
						teleport(player, 315.8269f, 1221.0648f, 263.4517f, (byte) 51);
						break;
					case 3:
						teleport(player, 686.09247f, 1756.8987f, 163.4386f, (byte) 25);
						break;
					case 4:
						teleport(player, 1851.7856f, 1709.3085f, 305.23566f, (byte) 31);
						break;
				}
				break;
			case 4:
				switch (zone) {
					case 1:
						teleport(player, 1841.7906f, 1069.6471f, 338.10706f, (byte) 107);
						break;
					case 2:
						teleport(player, 346.1267f, 1185.1802f, 244.43742f, (byte) 44);
						break;
					case 3:
						teleport(player, 693.11945f, 1771.6886f, 236.5583f, (byte) 17);
						break;
					case 4:
						teleport(player, 1887.0206f, 1737.6492f, 311.49692f, (byte) 62);
						break;
				}
				break;
		}
	}

}
