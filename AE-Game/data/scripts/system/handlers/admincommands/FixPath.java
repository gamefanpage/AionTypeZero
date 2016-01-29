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

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.dataholders.WalkerData;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.model.templates.walker.RouteStep;
import org.typezero.gameserver.model.templates.walker.WalkerTemplate;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Rolandas
 */
public class FixPath extends AdminCommand {

	static volatile boolean canceled = false;
	static volatile boolean isRunning = false;
	static Player runner = null;

	public FixPath() {
		super("fixpath");
	}

	@Override
	public void execute(final Player admin, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(admin, "Syntax : //fixpath <route id> <jump height> | <cancel>");
			return;
		}

		String routeId = "";
		final float z = admin.getZ();
		float jumpHeight = 0;

		try {
			if (isRunning && runner != null && !admin.equals(runner)) {
				PacketSendUtility.sendMessage(admin, "Someone is already running this command!");
				return;
			}
			if ("cancel".equals(params[0])) {
				if (isRunning) {
					PacketSendUtility.sendMessage(admin, "Canceled.");
					canceled = true;
				}
				return;
			}
			else if (params.length < 2) {
				PacketSendUtility.sendMessage(admin, "Syntax : //fixpath <route id> <jump height> | <cancel>");
				return;
			}
			else {
				routeId = params[0];
				jumpHeight = Float.parseFloat(params[1]);
			}
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(admin, "Only numbers please!!!");
		}

		final WalkerTemplate template = DataManager.WALKER_DATA.getWalkerTemplate(routeId);
		if (template == null) {
			PacketSendUtility.sendMessage(admin, "Invalid route id");
			return;
		}

		PacketSendUtility.sendMessage(admin, "Make sure you are at NPC spawn position. If not use cancel!");

		isRunning = true;
		runner = admin;
		final float height = jumpHeight;

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				boolean wasInvul = admin.isInvul();
				admin.setInvul(true);

				float zDelta = 0;
				HashMap<Integer, Float> corrections = new HashMap<Integer, Float>();

				try {
					int i = 1;
					for (RouteStep step : template.getRouteSteps()) {
						if (canceled || admin.isInState(CreatureState.DEAD)) {
							corrections.clear();
							return;
						}
						if (step.getX() == 0 || step.getY() == 0) {
							corrections.put(i++, admin.getZ());
							PacketSendUtility.sendMessage(admin, "Skipping zero coordinate...");
							continue;
						}
						if (zDelta == 0)
							zDelta = z - step.getZ() + height;
						PacketSendUtility.sendMessage(admin, "Teleporting to step " + i + "...");
						TeleportService2.teleportTo(admin, admin.getWorldId(), step.getX(), step.getY(), step.getZ() + zDelta);
						admin.getController().stopProtectionActiveTask();
						PacketSendUtility.sendMessage(admin, "Waiting to get Z...");
						Thread.sleep(5000);
						step.setZ(admin.getZ());
						corrections.put(i++, admin.getZ());
					}

					PacketSendUtility.sendMessage(admin, "Saving corrections...");

					WalkerData data = new WalkerData();
					WalkerTemplate newTemplate = new WalkerTemplate(template.getRouteId());

					i = 1;
					ArrayList<RouteStep> newSteps = new ArrayList<RouteStep>();

					int lastStep = template.isReversed() ? (template.getRouteSteps().size() + 2) / 2
																							 : template.getRouteSteps().size();
					for (int s = 0; s < lastStep; s++) {
						RouteStep step = template.getRouteSteps().get(s);
						RouteStep fixedStep = new RouteStep(step.getX(), step.getY(), corrections.get(i), 0);
						fixedStep.setRouteStep(i++);
						newSteps.add(fixedStep);
					}

					newTemplate.setRouteSteps(newSteps);
					if (template.isReversed())
						newTemplate.setIsReversed(true);
					newTemplate.setPool(template.getPool());
					data.AddTemplate(newTemplate);
					data.saveData(template.getRouteId());

					PacketSendUtility.sendMessage(admin, "Done.");
				}
				catch (Exception e) {
				}
				finally {
					runner = null;
					isRunning = false;
					canceled = false;
					if (!wasInvul)
						admin.setInvul(false);
				}
			}
		}, 5000);
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax : //fixpath <route id> <jump height> | <cancel>");
	}

}
