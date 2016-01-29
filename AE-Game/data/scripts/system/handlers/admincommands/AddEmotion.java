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

package admincommands;

import org.typezero.gameserver.cache.HTMLCache;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.HTMLService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author ginho1, Damon
 *
 */
public class AddEmotion extends AdminCommand {

	public AddEmotion() {
		super("addemotion");
	}

	@Override
	public void execute(Player admin, String... params) {

		long expireMinutes = 0;
		int emotionId = 0;
		VisibleObject target = null;
		Player finalTarget = null;

		if((params.length < 1) || (params.length > 2)) {
			PacketSendUtility.sendMessage(admin, "syntax: //addemotion <emotion id [expire time] || html>\nhtml to show html with names.");
			return;
		}

		try {
			emotionId = Integer.parseInt(params[0]);
			if(params.length == 2)
				expireMinutes = Long.parseLong(params[1]);
		}
		catch (NumberFormatException ex) {
			if(params[0].equalsIgnoreCase("html"))
				HTMLService.showHTML(admin, HTMLCache.getInstance().getHTML("emote.xhtml"));
				return;
		}

		if(emotionId < 1 || (emotionId > 35 && emotionId < 64) || emotionId > 129) {
			PacketSendUtility.sendMessage(admin, "Invalid <emotion id>, must be in intervals : [1-35]U[64-129]");
			return;
		}

		target = admin.getTarget();

		if (target == null) {
			finalTarget = admin;
		}
		else if (target instanceof Player) {
			finalTarget = (Player) target;
		}

		if(finalTarget.getEmotions().contains(emotionId)) {
			PacketSendUtility.sendMessage(admin, "Target has aldready this emotion !");
			return;
		}

		if(params.length == 2) {
			finalTarget.getEmotions().add(emotionId, (int)((System.currentTimeMillis()/1000)+expireMinutes*60), true);
		}
		else {
			finalTarget.getEmotions().add(emotionId, 0, true);
		}
	}
}
