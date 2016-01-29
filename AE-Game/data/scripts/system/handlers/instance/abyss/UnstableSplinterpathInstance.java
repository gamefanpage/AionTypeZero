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

package instance.abyss;

import java.util.List;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldPosition;


/**
 * @author zhkchi
 * @reworked vlog, Luzien
 * @edit Cheatkiller
 */
@InstanceID(300600000)
public class UnstableSplinterpathInstance extends GeneralInstanceHandler {

	private int destroyedFragments;
	private int killedPazuzuWorms = 0;
	private boolean bossSpawned = false;

	@Override
	public void onDie(Npc npc) {
		final int npcId = npc.getNpcId();
		switch (npcId) {
			case 219554: // Pazuzu the Life Current
				spawnPazuzuHugeAetherFragment();
				spawnPazuzuGenesisTreasureBoxes();
				spawnPazuzuAbyssalTreasureBox();
				spawnPazuzusTreasureBox();
				npc.getController().onDelete();
				break;
			case 219553: // Kaluva the Fourth Fragment
				spawnKaluvaHugeAetherFragment();
				spawnKaluvaGenesisTreasureBoxes();
				spawnKaluvaAbyssalTreasureBox();
				npc.getController().onDelete();
				break;
			case 219551: //rukril
			case 219552: //ebonsoul
				if (getNpc(npcId == 219552 ? 219551 : 219552) == null) {
					spawnDayshadeAetherFragment();
					spawnDayshadeGenesisTreasureBoxes();
					spawnDayshadeAbyssalTreasureChest();
				}
				else {
					sendMsg(npcId == 219551 ? 1400634 : 1400635); //Defeat Rukril/Ebonsoul in 1 min!
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {

							if (getNpc(npcId == 219552 ? 219551 : 219552) != null) {
								switch (npcId) {
									case 219551:
										spawn(219552, 447.1937f, 683.72217f, 433.1805f, (byte) 108); // rukril
										break;
									case 219552:
										spawn(219551, 455.5502f, 702.09485f, 433.13727f, (byte) 108); // ebonsoul
										break;
								}
							}
						}

					}, 60000);
				}
				npc.getController().onDelete();
				break;
			case 219568:
				Npc ebonsoul = getNpc(219552);
				if (ebonsoul != null && !ebonsoul.getLifeStats().isAlreadyDead()) {
					if (MathUtil.isIn3dRange(npc, ebonsoul, 5)) {
						ebonsoul.getEffectController().removeEffect(19159);
						deleteNpcs(instance.getNpcs(281907));
						break;
					}
				}
				npc.getController().onDelete();
				break;
			case 219569:
				Npc rukril = getNpc(219551);
				if (rukril != null && !rukril.getLifeStats().isAlreadyDead()) {
					if (MathUtil.isIn3dRange(npc, rukril, 5)) {
						rukril.getEffectController().removeEffect(19266);
						deleteNpcs(instance.getNpcs(281908));
						break;
					}
				}
				npc.getController().onDelete();
				break;
			case 219563: // unstable Yamennes Painflare
			case 219555: // strengthened Yamennes Blindsight
				spawnYamennesGenesisTreasureBoxes();
				spawnYamennesAbyssalTreasureBox(npcId == 219563 ? 701579 : 701580);
				deleteNpcs(instance.getNpcs(219586));
				spawn(730828, 328.476f, 762.585f, 197.479f, (byte) 90); //Exit
				npc.getController().onDelete();
				break;
			case 701588: // HugeAetherFragment
				destroyedFragments++;
				onFragmentKill();
				npc.getController().onDelete();
				break;

			case 283206:
				if (++killedPazuzuWorms == 4) {
					killedPazuzuWorms = 0;
					Npc pazuzu = getNpc(219554);
					if (pazuzu != null && !pazuzu.getLifeStats().isAlreadyDead()) {
						pazuzu.getEffectController().removeEffect(19145);
						pazuzu.getEffectController().removeEffect(19291);
					}
				}
				npc.getController().onDelete();
				break;
			case 219567:
			case 219579:
			case 219580:// Spawn Gate
				removeSummoned();
				npc.getController().onDelete();
				break;
			case 701587:
				npc.getController().onDelete();
				break;
		}
	}

	private boolean isSpawned(int npcId) {
		return !instance.getNpcs(npcId).isEmpty();
	}

	@Override
	public void onInstanceDestroy() {
		destroyedFragments = 0;
	}

	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		if (npc.getNpcId() == 700957) {
			QuestEnv env = new QuestEnv(npc, player, 0, 0);
			QuestEngine.getInstance().onDialog(env);
			if (!isSpawned(219563) && !isSpawned(219555) && !bossSpawned) { // No bosses spawned
				if (!isSpawned(700957) && destroyedFragments == 3) { // No Huge Aether Fragments spawned (all destroyed)
					sendMsg(1400732);
					spawn(219555, 329.70886f, 733.8744f, 197.60938f, (byte) 0);
				}
				else {
					sendMsg(1400731);
					spawn(219563, 329.70886f, 733.8744f, 197.60938f, (byte) 0);
				}
				bossSpawned = true;
			}
		}
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0
			: lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}

	private void spawnPazuzuHugeAetherFragment() {
		spawn(701588, 669.576f, 335.135f, 465.895f, (byte) 0);
	}

	private void spawnPazuzuGenesisTreasureBoxes() {
		spawn(701574, 360.1270f, 684.1302f, 390.9752f, (byte) 33);
		spawn(701574, 347.3535f, 735.8599f, 419.6725f, (byte) 66);
		spawn(701574, 346.9677f, 742.3782f, 365.1365f, (byte) 0);
		spawn(701574, 344.2876f, 778.7310f, 397.5779f, (byte) 33);
		spawn(701576, 651.53204f, 357.085f, 465.9203f, (byte) 66);
		spawn(701576, 647.00446f, 357.2484f, 465.32486f, (byte) 0);
		spawn(701576, 653.8384f, 360.39508f, 465.98065f, (byte) 100);
	}

	private void spawnPazuzuAbyssalTreasureBox() {
		spawn(701575, 649.24286f, 361.33755f, 465.9203f, (byte) 33);
	}

	private void spawnPazuzusTreasureBox() {
		if (Rnd.get(0, 100) >= 80) { // 20% chance, not retail
			spawn(700861, 649.243f, 362.338f, 466.0451f, (byte) 0);
		}
	}

	private void spawnKaluvaHugeAetherFragment() {
		spawn(701588, 633.7498f, 557.8822f, 424.99347f, (byte) 6);
	}

	private void spawnKaluvaGenesisTreasureBoxes() {
		spawn(701574, 306.5070f, 715.2716f, 368.3994f, (byte) 6);
		spawn(701574, 368.2921f, 771.3032f, 347.4754f, (byte) 0);
		spawn(701574, 311.6450f, 768.0681f, 343.8409f, (byte) 106);
		spawn(701574, 350.4549f, 788.7392f, 320.6760f, (byte) 106);
		spawn(701576, 601.2931f, 584.66705f, 423.3362f, (byte) 6);
		spawn(701576, 597.2156f, 583.95416f, 423.55383f, (byte) 66);
		spawn(701576, 602.9586f, 589.2678f, 423.29767f, (byte) 100);
	}

	private void spawnKaluvaAbyssalTreasureBox() {
		spawn(701577, 598.82776f, 588.25946f, 422.2649f, (byte) 113);
	}

	private void spawnDayshadeAetherFragment() {
		spawn(701588, 452.89706f, 692.36084f, 433.96838f, (byte) 6);
	}

	private void spawnDayshadeGenesisTreasureBoxes() {
		spawn(701574, 270.4710f, 743.9896f, 319.3749f, (byte) 106);
		spawn(701574, 389.8283f, 763.1230f, 274.2615f, (byte) 40);
		spawn(701574, 280.9925f, 767.5568f, 287.4333f, (byte) 66);
		spawn(701574, 290.2162f, 729.6007f, 253.7787f, (byte) 16);
		spawn(701576, 408.10938f, 650.9015f, 439.28332f, (byte) 66);
		spawn(701576, 402.40375f, 655.55237f, 439.26288f, (byte) 33);
		spawn(701576, 406.74445f, 655.5914f, 439.2548f, (byte) 100);
	}

	private void spawnDayshadeAbyssalTreasureChest() {
		sendMsg(1400636); //A Treasure Box Appeared
		spawn(701578, 404.891f, 650.2943f, 439.2548f, (byte) 130);
	}

	private void spawnYamennesGenesisTreasureBoxes() {
		spawn(701576, 326.978f, 729.8414f, 197.71234f, (byte) 16);
		spawn(701576, 326.5296f, 735.13324f, 197.65479f, (byte) 66);
		spawn(701576, 329.8462f, 738.41095f, 197.65213f, (byte) 3);
	}

	private void spawnYamennesAbyssalTreasureBox(int npcId) {
		spawn(npcId, 330.891f, 733.2943f, 197.7373f, (byte) 113);
	}

	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}

	private void removeSummoned(){
		Npc gate1 = getNpc(219567);
		Npc gate2 = getNpc(219579);
		Npc gate3 = getNpc(219580);
		if((gate1 == null || gate1.getLifeStats().isAlreadyDead())
			&& (gate2 == null || gate2.getLifeStats().isAlreadyDead())
			&& (gate3 == null || gate3.getLifeStats().isAlreadyDead())){
			deleteNpcs(instance.getNpcs(219565));// Summoned Orkanimum
			deleteNpcs(instance.getNpcs(219566));// Summoned Lapilima
		}
	}

	private void onFragmentKill() {
		switch (destroyedFragments) {
			case 1:
				// The destruction of the Huge Aether Fragment has destabilized the artifact!
				sendMsg(1400689);
				break;
			case 2:
				// The destruction of the Huge Aether Fragment has put the artifact protector on alert!
				sendMsg(1400690);
				break;
			case 3:
				// The destruction of the Huge Aether Fragment has caused abnormality on the artifact. The artifact protector is
				// furious!
				sendMsg(1400691);
				break;
		}
	}

}
