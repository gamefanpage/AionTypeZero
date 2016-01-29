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

import java.util.Iterator;

import org.slf4j.LoggerFactory;

import org.typezero.gameserver.ai2.AI2Engine;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.AISubState;
import org.typezero.gameserver.ai2.AbstractAI;
import org.typezero.gameserver.ai2.event.AIEventLog;
import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.configs.main.AIConfig;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;

/**
 * @author ATracer
 */
public class Ai2Command extends AdminCommand {

	public Ai2Command() {
		super("ai2");
	}

	@Override
	public void execute(Player player, String... params) {
		/**
		 * Non target commands
		 */
		String param0 = params[0];

		if (param0.equals("createlog")) {
			boolean oldValue = AIConfig.ONCREATE_DEBUG;
			AIConfig.ONCREATE_DEBUG = !oldValue;
			PacketSendUtility.sendMessage(player, "New createlog value: " + !oldValue);
			return;
		}

		if (param0.equals("eventlog")) {
			boolean oldValue = AIConfig.EVENT_DEBUG;
			AIConfig.EVENT_DEBUG = !oldValue;
			PacketSendUtility.sendMessage(player, "New eventlog value: " + !oldValue);
			return;
		}

		if (param0.equals("movelog")) {
			boolean oldValue = AIConfig.MOVE_DEBUG;
			AIConfig.MOVE_DEBUG = !oldValue;
			PacketSendUtility.sendMessage(player, "New movelog value: " + !oldValue);
			return;
		}

		if (param0.equals("say")) {
			LoggerFactory.getLogger(Ai2Command.class).info("[AI2] marker: " + params[1]);
		}

		/**
		 * Target commands
		 */
		VisibleObject target = player.getTarget();

		if (target == null || !(target instanceof Npc)) {
			PacketSendUtility.sendMessage(player, "Select target first (Npc only)");
			return;
		}
		Npc npc = (Npc) target;

		if (param0.equals("info")) {
			PacketSendUtility.sendMessage(player, "Ai name: " + npc.getAi2().getName());
			PacketSendUtility.sendMessage(player, "Ai state: " + npc.getAi2().getState());
			PacketSendUtility.sendMessage(player, "Ai substate: " + npc.getAi2().getSubState());
			return;
		}

		if (param0.equals("log")) {
			boolean oldValue = npc.getAi2().isLogging();
			((AbstractAI) npc.getAi2()).setLogging(!oldValue);
			PacketSendUtility.sendMessage(player, "New log value: " + !oldValue);
			return;
		}

		if (param0.equals("print")) {
			AIEventLog eventLog = ((AbstractAI) npc.getAi2()).getEventLog();
			Iterator<AIEventType> iterator = eventLog.iterator();
			while (iterator.hasNext()) {
				PacketSendUtility.sendMessage(player, "EVENT: " + iterator.next().name());
			}
			return;
		}

		String param1 = params[1];
		if (param0.equals("set")) {
			String aiName = param1;
			AI2Engine.getInstance().setupAI(aiName, npc);
		}
		else if (param0.equals("event")) {
			AIEventType eventType = AIEventType.valueOf(param1.toUpperCase());
			if (eventType != null) {
				npc.getAi2().onGeneralEvent(eventType);
			}
		}
		else if (param0.equals("event2")) {
			AIEventType eventType = AIEventType.valueOf(param1.toUpperCase());
			Creature creature = (Creature) World.getInstance().findVisibleObject(Integer.valueOf(params[2]));
			if (eventType != null) {
				npc.getAi2().onCreatureEvent(eventType, creature);
			}
		}
		else if (param0.equals("state")) {
			AIState state = AIState.valueOf(param1.toUpperCase());
			((NpcAI2) npc.getAi2()).setStateIfNot(state);
			if (params.length > 2) {
				AISubState substate = AISubState.valueOf(params[2]);
				((NpcAI2) npc.getAi2()).setSubStateIfNot(substate);
			}
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //ai2 <set|event|event2|info|log|print|createlog|eventlog|movelog>");
	}

}
