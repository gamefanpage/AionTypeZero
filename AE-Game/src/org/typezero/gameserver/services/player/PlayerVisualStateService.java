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

package org.typezero.gameserver.services.player;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author Source
 */
public class PlayerVisualStateService {

	public static void hideValidate(final Player hiden) {
		hiden.getKnownList().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player observer) {
				boolean canSee = observer.canSee(hiden);
				boolean isSee = observer.isSeePlayer(hiden);

				if (canSee && !isSee)
					observer.getKnownList().addVisualObject(hiden);
				else if (!canSee && isSee)
					observer.getKnownList().delVisualObject(hiden, false);
			}

		});
	}

	public static void seeValidate(final Player search) {
		search.getKnownList().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player hide) {
				boolean canSee = search.canSee(hide);
				boolean isSee = search.isSeePlayer(hide);

				if (canSee && !isSee)
					search.getKnownList().addVisualObject(hide);
				else if (!canSee && isSee)
					search.getKnownList().delVisualObject(hide, false);
			}

		});
	}

}
