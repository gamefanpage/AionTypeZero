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

import org.typezero.gameserver.geoEngine.collision.CollisionIntention;
import org.typezero.gameserver.geoEngine.collision.CollisionResult;
import org.typezero.gameserver.geoEngine.collision.CollisionResults;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.geo.GeoService;

/**
 * @author Rolandas
 */
public class Collide extends AdminCommand {

	public Collide() {
		super("collide");
	}

	@Override
	public void execute(Player admin, String... params) {
		VisibleObject target = admin.getTarget();
		if (target == null) {
			PacketSendUtility.sendMessage(admin, "Must select a target!");
			return;
		}
		if (params.length > 1 || params.length == 1 && !"me".equalsIgnoreCase(params[0])) {
			onFail(admin, null);
			return;
		}

		final byte intentions = (byte) (CollisionIntention.ALL.getId());
		float x = target.getX();
		float y = target.getY();
		float z = target.getZ();
		float targetX, targetY, targetZ;

		if (params.length == 0) {
			targetX = x;
			targetY = y;
			targetZ = z - 10;
		}
		else {
			targetX = admin.getX();
			targetY = admin.getY();
			targetZ = admin.getZ() + admin.getObjectTemplate().getBoundRadius().getUpper() / 2;
		}

		if (params.length == 1)
			PacketSendUtility.sendMessage(admin, "From target direction:");
		PacketSendUtility.sendMessage(admin, "Target: X=" + x + "; Y=" + y + "; Z=" + z);

		CollisionResults results = GeoService.getInstance().getCollisions(target, targetX, targetY, targetZ, false, intentions);
		CollisionResult closest = results.getClosestCollision();

		if (results.size() == 0) {
			PacketSendUtility.sendMessage(admin, "Hm... Nothing collidable?");
			if (params.length == 0)
				return;
			else
				closest = null;
		}
		else {
			Iterator<CollisionResult> iter = results.iterator();
			int count = 0;
			int closestId = 0;
			String description = "";
			while (iter.hasNext()) {
				count++;
				CollisionResult result = iter.next();
				if (result.equals(closest))
					closestId = count;
				if (result.getGeometry() == null)
					description += count + ". " + result.getContactPoint().toString() + "\n";
				else {
					if (result.getGeometry().getName() == null) {
						description += count + ". " + result.getContactPoint().toString() + "; parent=" + result.getGeometry().getParent().getName()
							+ "\n";
					}
					else
						description += count + ". " + result.getContactPoint().toString() + "; name=" + result.getGeometry().getName() + "\n";
				}
			}
			description += "-----------------------\nClosest: " + closestId + ". Distance: " + closest.getDistance();
			PacketSendUtility.sendMessage(admin, description);
		}

		CollisionResult closestOpposite = null;

		if (params.length == 1) {
			PacketSendUtility.sendMessage(admin, "From opposite direction:");
			PacketSendUtility.sendMessage(admin, "Admin: X=" + admin.getX() + "; Y=" + admin.getY() + "; Z=" + admin.getZ());

			results = GeoService.getInstance().getCollisions(admin, target.getX(), target.getY(),
				target.getZ() + target.getObjectTemplate().getBoundRadius().getUpper() / 2, false, intentions);
			closestOpposite = results.getClosestCollision();

			if (results.size() == 0) {
				PacketSendUtility.sendMessage(admin, "Hm... Nothing collidable?");
				closestOpposite = null;
			}
			else {
				Iterator<CollisionResult> iter2 = results.iterator();
				int count = 0;
				int closestId = 0;
				String description = "";

				while (iter2.hasNext()) {
					count++;
					CollisionResult result = iter2.next();
					if (result.equals(closestOpposite))
						closestId = count;
					if (result.getGeometry() == null)
						description += count + ". " + result.getContactPoint().toString() + "\n";
					else {
						if (result.getGeometry().getName() == null) {
							description += count + ". " + result.getContactPoint().toString() + "; parent=" + result.getGeometry().getParent().getName()
								+ "\n";
						}
						else
							description += count + ". " + result.getContactPoint().toString() + "; name=" + result.getGeometry().getName() + "\n";
					}
				}
				description += "-----------------------\nClosest: " + closestId + ". Distance: " + closestOpposite.getDistance();
				PacketSendUtility.sendMessage(admin, description);
			}
		}

		if (params.length == 0 && closest.getContactPoint().z + 0.5f < target.getZ()) {
			PacketSendUtility.sendMessage(admin, "Below actual Z!");
		}
		else {
			if (closest != null) {
				SpawnTemplate spawn = SpawnEngine.addNewSpawn(admin.getWorldId(), 200000, closest.getContactPoint().x, closest.getContactPoint().y,
					closest.getContactPoint().z, (byte) 0, 0);
				SpawnEngine.spawnObject(spawn, admin.getInstanceId());
			}
			if (closestOpposite != null) {
				SpawnTemplate spawn = SpawnEngine.addNewSpawn(admin.getWorldId(), 200000, closestOpposite.getContactPoint().x,
					closestOpposite.getContactPoint().y, closestOpposite.getContactPoint().z, (byte) 0, 0);
				SpawnEngine.spawnObject(spawn, admin.getInstanceId());
			}
		}
	}

	@Override
	public void onFail(Player player, String message) {
		String syntax = "Syntax: //collide [me]";
		PacketSendUtility.sendMessage(player, syntax);
	}
}
