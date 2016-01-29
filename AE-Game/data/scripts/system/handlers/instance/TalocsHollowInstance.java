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

package instance;

import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.flyring.FlyRing;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.StaticDoor;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.templates.flyring.FlyRingTemplate;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.model.utils3d.Point3D;
import org.typezero.gameserver.network.aion.serverpackets.*;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.knownlist.Visitor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xTz
 */
@InstanceID(300190000)
public class TalocsHollowInstance extends GeneralInstanceHandler {

	private List<Integer> movies = new ArrayList<Integer>();
	private Map<Integer, StaticDoor> doors;

	@Override
	public void onEnterInstance(Player player) {
		addItems(player);
	}

	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
	}

	private void addItems(Player player) {
		QuestState qs1 = player.getQuestStateList().getQuestState(10021);
		QuestState qs2 = player.getQuestStateList().getQuestState(20021);
		if ((qs1 != null && qs1.getStatus() == QuestStatus.START) || (qs2 != null && qs2.getStatus() == QuestStatus.START)) {
			return;
		}
		switch (player.getRace()) {
			case ELYOS:
				ItemService.addItem(player, 160001286, 1);
				ItemService.addItem(player, 164000099, 1);
				break;
			case ASMODIANS:
				ItemService.addItem(player, 160001287, 1);
				ItemService.addItem(player, 164000099, 1);
				break;
		}
	}

	private void removeItems(Player player) {
		Storage storage = player.getInventory();
		storage.decreaseByItemId(164000099, storage.getItemCountByItemId(164000099));
		storage.decreaseByItemId(160001286, storage.getItemCountByItemId(160001286));
		storage.decreaseByItemId(160001287, storage.getItemCountByItemId(160001287));
		player.getEffectController().removeEffect(10251);
		player.getEffectController().removeEffect(10252);
	}

	@Override
	public void onDie(Npc npc) {
		switch (npc.getNpcId()) {
			case 215467:
				openDoor(48);
				openDoor(49);
				break;
			case 215457:
				Npc newNpc = getNpc(700633);
				if (newNpc != null) {
					newNpc.getController().onDelete();
				}
				break;
			case 700739:
				npc.getKnownList().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400477));
					}
				});
				SpawnTemplate template = npc.getSpawn();
				spawn(281817, template.getX(), template.getY(), template.getZ(), template.getHeading(), 9);
				npc.getController().onDelete();
				break;
			case 215488:
				Player player = npc.getAggroList().getMostPlayerDamage();
				if (player != null) {
					PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 10021, 437, 0));
				}
				Npc newNpc2 = getNpc(700740);
				if (newNpc2 != null) {
					SpawnTemplate template2 = newNpc2.getSpawn();
					spawn(700741, template2.getX(), template2.getY(), template2.getZ(), template2.getHeading(), 92);
					newNpc2.getController().onDelete();
				}
				spawn(799503, 548f, 811f, 1375f, (byte) 0);
				break;
		}
	}

	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 700940:
				player.getLifeStats().increaseHp(SM_ATTACK_STATUS.TYPE.HP, 20000);
				NpcActions.delete(npc);
				break;
			case 700941:
				player.getLifeStats().increaseHp(SM_ATTACK_STATUS.TYPE.HP, 30000);
				NpcActions.delete(npc);
				break;
		}
	}

	@Override
	public void onInstanceDestroy() {
		movies.clear();
		doors.clear();
	}

	private void sendMovie(Player player, int movie) {
		if (!movies.contains(movie)) {
			movies.add(movie);
			PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, movie));
		}
	}

	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
	}

	private void openDoor(int doorId) {
		StaticDoor door = doors.get(doorId);
		if (door != null)
			door.setOpen(true);
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		doors.get(48).setOpen(true);
		doors.get(7).setOpen(true);
		spawnRings();
	}

	private void spawnRings() {
		FlyRing f1 = new FlyRing(new FlyRingTemplate("TALOCS_1", mapId,
				new Point3D(253.85039, 649.23535, 1171.8772),
				new Point3D(253.85039, 649.23535, 1177.8772),
				new Point3D(262.84872, 649.4091, 1171.8772), 8), instanceId);
		f1.spawn();
		FlyRing f2 = new FlyRing(new FlyRingTemplate("TALOCS_2", mapId,
				new Point3D(592.32275, 844.056, 1295.0966),
				new Point3D(592.32275, 844.056, 1301.0966),
				new Point3D(595.2305, 835.5387, 1295.0966), 8), instanceId);
		f2.spawn();
	}

	@Override
	public boolean onPassFlyingRing(Player player, String flyingRing) {
		if (flyingRing.equals("TALOCS_1")) {
			sendMovie(player, 463);
		}
		else if(flyingRing.equals("TALOCS_2")) {
			sendMovie(player, 464);
		}
		return false;
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0
				: lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}

}
