package instance;

import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.model.instance.StageType;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_TRANSFORM;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.WorldMapInstance;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Ritsu
 * @author Dision M.O.G. Devs Team TODO: Rework of spawn scheduler
 */
@InstanceID(300560000)
public class ShugoImperialTombInstance extends GeneralInstanceHandler {

	private int skillId;
	private int stage;
	private int destroyedTowers;
	private boolean isCancelled;
	private List<Npc> npcs = new ArrayList<Npc>();
	protected boolean isInstanceDestroyed = false;
	private Future<?> cancelSpawnTask;
	private Future<?> cancelMessageTask;

	@Override
	public void onExitInstance(Player player) {
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}

	protected List<Npc> getNpcs(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpcs(npcId);
		}
		return null;
	}

	protected void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}

	private void cancelTask() {
		if (cancelSpawnTask != null && !cancelSpawnTask.isCancelled()) {
			cancelSpawnTask.cancel(true);
		}
		if (cancelMessageTask != null && !cancelMessageTask.isCancelled()) {
			cancelMessageTask.cancel(true);
		}
	}

	@Override
	public void onDie(Npc npc) {
		if (npcs.contains(npc)) {
			npcs.remove(npc);
		}
		switch (npc.getNpcId()) {
			case 219508:
			case 219509:
			case 219513:
			case 219516:
			case 219517:
			case 219519:
			case 219520:
			case 219521:
			case 219524:
			case 219525:
			case 219527:
			case 219528:
			case 219529:
				despawnNpc(npc);
				break;
			case 219514:
				onChangeStage(StageType.START_STAGE_1_PHASE_2);
				break;
			case 219522:
				onChangeStage(StageType.START_STAGE_2_PHASE_2);
				break;
			case 219530:
				onChangeStage(StageType.START_STAGE_3_PHASE_2);
				break;
			case 219523:
				spawnBonusStage2();
				deleteNpcs(instance.getNpcs(831304));
				deleteNpcs(instance.getNpcs(831250));
				deleteNpcs(instance.getNpcs(831251));
				sp(831115, 342.11075f, 426.13712f, 294.75793f, (byte) 80);
				break;
			case 219515:
				deleteNpcs(instance.getNpcs(831130));
				deleteNpcs(instance.getNpcs(831250));
				deleteNpcs(instance.getNpcs(831251));
				sp(831114, 177.87117f, 233.5641f, 536.16974f, (byte) 80);
				break;
			case 219531:
				// Delete Imperial Tower
				deleteNpcs(instance.getNpcs(831305));
				deleteNpcs(instance.getNpcs(831250));
				deleteNpcs(instance.getNpcs(831251));
				sp(831350, 451.58344f, 105.55769f, 212.20023f, (byte) 0);
				sp(831116, 436.186f, 99.208008f, 212.20023f, (byte) 80);
				spawnChest();
				spawnExitPortal();
				break;
			case 831250:
			case 831251:
			case 831130:
			case 831304:
			case 831305:
				destroyedTowers++;
				if (destroyedTowers == 3 && stage == 1) {
					sp(831306, 177.87117f, 233.5641f, 536.16974f, (byte) 80);
					cancelTask();
					isCancelled = true;
					despawnNpcs();
				}
				if (destroyedTowers == 3 && stage == 2) {
					sp(831195, 342.11075f, 426.13712f, 294.75793f, (byte) 80);
					cancelTask();
					isCancelled = true;
					despawnNpcs();
				}
				if (destroyedTowers == 3 && stage == 3) {
					deleteNpcs(instance.getNpcs(831305));
					sp(831350, 451.58344f, 105.55769f, 212.20023f, (byte) 0);
					sp(831307, 436.186f, 99.208008f, 212.20023f, (byte) 80);
					// spawnChest();
					// spawnExitPortal();
					cancelTask();
					isCancelled = true;
					despawnNpcs();
				}
				break;
		}

	}

	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			if (npc != null && !npc.getLifeStats().isAlreadyDead()) {
				npc.getController().onDelete();
			}
		}
	}

	private void despawnNpcs() {
		deleteNpcs(instance.getNpcs(219543));
		deleteNpcs(instance.getNpcs(219544));
		deleteNpcs(instance.getNpcs(219505));
		deleteNpcs(instance.getNpcs(219630));
		deleteNpcs(instance.getNpcs(219507));
		deleteNpcs(instance.getNpcs(219508));
		deleteNpcs(instance.getNpcs(219509));
		deleteNpcs(instance.getNpcs(219505));
		deleteNpcs(instance.getNpcs(219514));
		deleteNpcs(instance.getNpcs(219543));
		deleteNpcs(instance.getNpcs(219515));
		deleteNpcs(instance.getNpcs(219516));
		deleteNpcs(instance.getNpcs(219517));
		deleteNpcs(instance.getNpcs(219519));
		deleteNpcs(instance.getNpcs(219520));
		deleteNpcs(instance.getNpcs(219521));
		deleteNpcs(instance.getNpcs(219522));
		deleteNpcs(instance.getNpcs(219523));
		deleteNpcs(instance.getNpcs(219524));
		deleteNpcs(instance.getNpcs(219525));
		deleteNpcs(instance.getNpcs(219527));
		deleteNpcs(instance.getNpcs(219528));
		deleteNpcs(instance.getNpcs(219529));
		deleteNpcs(instance.getNpcs(219530));
		deleteNpcs(instance.getNpcs(219531));
		deleteNpcs(instance.getNpcs(219461));
	}

	@Override
	public void onEnterInstance(final Player player) {
		if (stage == 0 || stage == 1)
			skillId = player.getRace() == Race.ASMODIANS ? 21103 : 21094;
		else if (stage == 2)
			skillId = player.getRace() == Race.ASMODIANS ? 21104 : 21095;
		else if (stage == 3)
			skillId = player.getRace() == Race.ASMODIANS ? 21105 : 21096;
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				SkillEngine.getInstance().applyEffectDirectly(skillId, player, player, 0);
			}

		}, 1000);
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		stage = 0;
		destroyedTowers = 0;
		sp(831110, 177.84564f, 233.56879f, 536.16974f, (byte) 80);
		sp(831111, 342.11075f, 426.13712f, 294.75793f, (byte) 80);
		sp(831112, 437.05875f, 99.71092f, 212.20023f, (byte) 10);
	}

	@Override
	public void onChangeStage(StageType type) {
		switch (type) {
			case START_STAGE_1_PHASE_1:
				stage = 1;
				// Defense towers
				sp(831251, 186.37216f, 226.88597f, 535.81213f, (byte) 0);
				sp(831250, 169.97723f, 239.81743f, 535.81213f, (byte) 0);
				sp(831130, 170.58615f, 224.70198f, 535.81213f, (byte) 0);
				sendMsg(1401582);
				// Kobolds wave 1
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 5000, "stage1_300560000", false);
				sp(219509, 217.59f, 265.39f, 550.49f, (byte) 77, 5000, "stage1_300560004", false);
				// Kobolds Left wave 2
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 10000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 10000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 10000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 10000, "stage1_300560003", false);
				// Kobolds Right wave 2
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 10000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 10000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 10000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 10000, "stage1_300560007", false);
				// Kobolds Left wave 3
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 50000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 50000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 50000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 50000, "stage1_300560003", false);
				// Kobolds Right wave 3
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 50000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 50000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 50000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 50000, "stage1_300560007", false);
				// Kobolds Left wave 4
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 80000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 80000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 80000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 80000, "stage1_300560003", false);
				// Kobolds Right wave 4
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 80000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 80000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 80000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 80000, "stage1_300560007", false);
				// Kobolds Left wave 5
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 110000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 110000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 110000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 110000, "stage1_300560003", false);
				// Kobolds Right wave 5
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 110000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 110000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 110000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 110000, "stage1_300560007", false);
				// Kobolds Left wave 6
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 200000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 200000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 200000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 200000, "stage1_300560003", false);
				// Kobolds Right wave 6
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 200000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 200000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 200000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 200000, "stage1_300560007", false);
				// Kobolds Left wave 7
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 220000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 220000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 220000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 220000, "stage1_300560003", false);
				// Kobolds Right wave 7
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 220000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 220000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 220000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 220000, "stage1_300560007", false);
				// Kobolds Left wave 8
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 250000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 250000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 250000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 250000, "stage1_300560003", false);
				// Kobolds Right wave 8
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 250000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 250000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 250000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 250000, "stage1_300560007", false);
				// Kobolds Left wave 9
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 300000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 300000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 300000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 300000, "stage1_300560003", false);
				// Kobolds Right wave 9
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 300000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 300000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 300000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 300000, "stage1_300560007", false);
				// Bonus spawn wave 1
				cancelMessageTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						startBonusStage1();
					}
				}, 350000);
				// Kobolds Left wave 12
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 380000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 380000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 380000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 380000, "stage1_300560003", false);
				// Kobolds Right wave 12
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 380000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 380000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 380000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 380000, "stage1_300560007", false);
				// Kobolds Left wave 13
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 400000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 400000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 400000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 400000, "stage1_300560003", false);
				// Kobolds Right wave 13
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 400000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 400000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 400000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 400000, "stage1_300560007", false);
				// Kobolds Left wave 14
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 450000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 450000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 450000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 450000, "stage1_300560003", false);
				// Kobolds Right wave 14
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 450000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 450000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 450000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 450000, "stage1_300560007", false);
				// Kobolds Left wave 15
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 480000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 480000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 480000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 480000, "stage1_300560003", false);
				// Kobolds Right wave 15
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 480000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 480000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 480000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 480000, "stage1_300560007", false);
				// Kobolds Left wave 16
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 500000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 500000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 500000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 500000, "stage1_300560003", false);
				// Kobolds Right wave 16
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 500000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 500000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 500000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 500000, "stage1_300560007", false);
				// Kobolds Left wave 17
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 550000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 550000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 550000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 550000, "stage1_300560003", false);
				// Kobolds Right wave 17
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 550000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 550000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 550000, "stage1_300560006", false);
				sp(219514, 214.71f, 265.34f, 550.49f, (byte) 77, 550000, "stage1_300560007", false); // Boss 1
				break;
			case START_STAGE_1_PHASE_2:
				sendMsg(1401664); // The 3rd looting begins in 10 seconds!
				sp(219505, 199.23898f, 280.70059f, 550.49426f, (byte) 0);
				sp(219505, 217.23492f, 266.69803f, 550.49426f, (byte) 0);
				sp(219544, 210.49338f, 275.42099f, 550.49426f, (byte) 0);
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						sendMsg(1401665); // Grave Robbers will attack again in 5 seconds!
						ThreadPoolManager.getInstance().schedule(new Runnable() {

							@Override
							public void run() {
								deleteNpcs(instance.getNpcs(219544));
								deleteNpcs(instance.getNpcs(219505));
							}
						}, 5000);
					}
				}, 5000);
				// Kobolds Left wave 2
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 10000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 10000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 10000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 10000, "stage1_300560003", false);
				// Kobolds Right wave 2
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 10000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 10000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 10000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 10000, "stage1_300560007", false);
				// Kobolds Left wave 3
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 50000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 50000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 50000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 50000, "stage1_300560003", false);
				// Kobolds Right wave 3
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 50000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 50000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 50000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 50000, "stage1_300560007", false);
				// Kobolds Left wave 4
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 80000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 80000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 80000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 80000, "stage1_300560003", false);
				// Kobolds Right wave 4
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 80000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 80000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 80000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 80000, "stage1_300560007", false);
				// Kobolds Left wave 5
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 120000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 120000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 120000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 120000, "stage1_300560003", false);
				// Kobolds Right wave 5
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 120000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 120000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 120000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 120000, "stage1_300560007", false);
				// Kobolds Left wave 6
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 150000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 150000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 150000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 150000, "stage1_300560003", false);
				// Kobolds Right wave 6
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 150000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 150000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 150000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 150000, "stage1_300560007", false);
				// Kobolds Left wave 7
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 200000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 200000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 200000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 200000, "stage1_300560003", false);
				// Kobolds Right wave 7
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 200000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 200000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 200000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 200000, "stage1_300560007", false);
				// Kobolds Left wave 8
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 230000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 230000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 230000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 230000, "stage1_300560003", false);
				// Kobolds Right wave 8
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 230000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 230000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 230000, "stage1_300560006", false);
				sp(219509, 214.71f, 265.34f, 550.49f, (byte) 77, 230000, "stage1_300560007", false);
				// Kobolds Left wave 9
				sp(219508, 197.99f, 281.85f, 550.49f, (byte) 77, 280000, "stage1_300560000", false);
				sp(219509, 199.93f, 280.58f, 550.49f, (byte) 77, 280000, "stage1_300560001", false);
				sp(219508, 196.53f, 280.06f, 550.49f, (byte) 77, 280000, "stage1_300560002", false);
				sp(219509, 198.64f, 278.73f, 550.49f, (byte) 77, 280000, "stage1_300560003", false);
				// Kobolds Right wave 9
				sp(219508, 217.59f, 265.39f, 550.49f, (byte) 77, 280000, "stage1_300560004", false);
				sp(219509, 216.07f, 266.99f, 550.49f, (byte) 77, 280000, "stage1_300560005", false);
				sp(219508, 216.38f, 263.92f, 550.49f, (byte) 77, 280000, "stage1_300560006", false);
				sp(219515, 214.71f, 265.34f, 550.49f, (byte) 77, 280000, "stage1_300560007", false); // Boss 2
				break;
			case START_STAGE_2_PHASE_1:
				isCancelled = false;
				stage = 2;
				destroyedTowers = 0;
				// Defense towers
				sp(831251, 342.2613f, 436.7085f, 294.75647f, (byte) 0);
				sp(831304, 340.3823f, 426.19424f, 294.75742f, (byte) 0);
				sp(831250, 338.8206f, 415.69327f, 294.76065f, (byte) 0);
				// Transformation device
				sp(831096, 307.86002f, 433.87598f, 298.31903f, (byte) 0);
				sendMsg(1401583);
				// wave 1
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 5000, "stage2_300560000", false);
				sp(219517, 315.88f, 445.00f, 296.40f, (byte) 13, 5000, "stage2_300560006", false);
				// wave 2 left_4
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 10000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 10000, "stage2_300560001", false);
				sp(219516, 309.56f, 420.92f, 296.40f, (byte) 100, 10000, "stage2_300560002", false);
				sp(219517, 307.84f, 420.67f, 296.40f, (byte) 100, 10000, "stage2_300560003", false);
				// wave 2 right_4
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 10000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 10000, "stage2_300560007", false);
				sp(219516, 314.33f, 444.00f, 296.40f, (byte) 13, 10000, "stage2_300560008", false);
				sp(219517, 313.50f, 445.74f, 296.40f, (byte) 13, 10000, "stage2_300560009", false);
				// wave 3 left_6
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 50000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 50000, "stage2_300560001", false);
				sp(219513, 309.56f, 420.92f, 296.40f, (byte) 100, 50000, "stage2_300560002", false);
				sp(219521, 307.84f, 420.67f, 296.40f, (byte) 100, 50000, "stage2_300560003", false);
				sp(219513, 309.04f, 422.65f, 296.40f, (byte) 100, 50000, "stage2_300560004", false);
				sp(219521, 307.43f, 422.54f, 296.60f, (byte) 100, 50000, "stage2_300560005", false);
				// wave 3 right_6
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 50000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 50000, "stage2_300560007", false);
				sp(219513, 314.33f, 444.00f, 296.40f, (byte) 13, 50000, "stage2_300560008", false);
				sp(219521, 313.50f, 445.74f, 296.40f, (byte) 13, 50000, "stage2_300560009", false);
				sp(219513, 313.15f, 443.01f, 296.57f, (byte) 13, 50000, "stage2_300560010", false);
				sp(219521, 312.30f, 444.33f, 296.47f, (byte) 13, 50000, "stage2_300560011", false);
				// wave 3 center_2
				sp(219513, 318.10f, 429.95f, 294.58f, (byte) 100, 50000, "stage2_center_300560000", true);
				sp(219513, 337.40f, 425.50f, 294.75f, (byte) 13, 50000, "stage2_center_300560001", true);
				// wave 4 left_6
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 80000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 80000, "stage2_300560001", false);
				sp(219513, 309.56f, 420.92f, 296.40f, (byte) 100, 80000, "stage2_300560002", false);
				sp(219521, 307.84f, 420.67f, 296.40f, (byte) 100, 80000, "stage2_300560003", false);
				sp(219513, 309.04f, 422.65f, 296.40f, (byte) 100, 80000, "stage2_300560004", false);
				sp(219521, 307.43f, 422.54f, 296.60f, (byte) 100, 80000, "stage2_300560005", false);
				// wave 4 right_6
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 80000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 80000, "stage2_300560007", false);
				sp(219513, 314.33f, 444.00f, 296.40f, (byte) 13, 80000, "stage2_300560008", false);
				sp(219521, 313.50f, 445.74f, 296.40f, (byte) 13, 80000, "stage2_300560009", false);
				sp(219513, 313.15f, 443.01f, 296.57f, (byte) 13, 80000, "stage2_300560010", false);
				sp(219521, 312.30f, 444.33f, 296.47f, (byte) 13, 80000, "stage2_300560011", false);
				// wave 4 center_4
				sp(219513, 318.10f, 429.95f, 294.58f, (byte) 100, 80000, "stage2_center_300560000", true);
				sp(219513, 337.40f, 425.50f, 294.75f, (byte) 13, 80000, "stage2_center_300560001", true);
				sp(219513, 316.90f, 430.52f, 294.58f, (byte) 100, 80000, "stage2_center_300560002", true);
				sp(219513, 317.53f, 432.64f, 294.58f, (byte) 13, 80000, "stage2_center_300560003", true);
				// wave 5 left_6
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 120000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 120000, "stage2_300560001", false);
				sp(219513, 309.56f, 420.92f, 296.40f, (byte) 100, 120000, "stage2_300560002", false);
				sp(219521, 307.84f, 420.67f, 296.40f, (byte) 100, 120000, "stage2_300560003", false);
				sp(219513, 309.04f, 422.65f, 296.40f, (byte) 100, 120000, "stage2_300560004", false);
				sp(219521, 307.43f, 422.54f, 296.60f, (byte) 100, 120000, "stage2_300560005", false);
				// wave 5 right_6
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 120000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 120000, "stage2_300560007", false);
				sp(219513, 314.33f, 444.00f, 296.40f, (byte) 13, 120000, "stage2_300560008", false);
				sp(219521, 313.50f, 445.74f, 296.40f, (byte) 13, 120000, "stage2_300560009", false);
				sp(219513, 313.15f, 443.01f, 296.57f, (byte) 13, 120000, "stage2_300560010", false);
				sp(219521, 312.30f, 444.33f, 296.47f, (byte) 13, 120000, "stage2_300560011", false);
				// wave 6 left_6
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 150000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 150000, "stage2_300560001", false);
				sp(219513, 309.56f, 420.92f, 296.40f, (byte) 100, 150000, "stage2_300560002", false);
				sp(219521, 307.84f, 420.67f, 296.40f, (byte) 100, 150000, "stage2_300560003", false);
				sp(219513, 309.04f, 422.65f, 296.40f, (byte) 100, 150000, "stage2_300560004", false);
				sp(219521, 307.43f, 422.54f, 296.60f, (byte) 100, 150000, "stage2_300560005", false);
				// wave 6 right_6
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 150000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 150000, "stage2_300560007", false);
				sp(219513, 314.33f, 444.00f, 296.40f, (byte) 13, 150000, "stage2_300560008", false);
				sp(219521, 313.50f, 445.74f, 296.40f, (byte) 13, 150000, "stage2_300560009", false);
				sp(219513, 313.15f, 443.01f, 296.57f, (byte) 13, 150000, "stage2_300560010", false);
				sp(219521, 312.30f, 444.33f, 296.47f, (byte) 13, 150000, "stage2_300560011", false);
				// wave 7 left_6
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 180000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 180000, "stage2_300560001", false);
				sp(219513, 309.56f, 420.92f, 296.40f, (byte) 100, 180000, "stage2_300560002", false);
				sp(219521, 307.84f, 420.67f, 296.40f, (byte) 100, 180000, "stage2_300560003", false);
				sp(219513, 309.04f, 422.65f, 296.40f, (byte) 100, 180000, "stage2_300560004", false);
				sp(219521, 307.43f, 422.54f, 296.60f, (byte) 100, 180000, "stage2_300560005", false);
				// wave 7 right_6
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 180000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 180000, "stage2_300560007", false);
				sp(219513, 314.33f, 444.00f, 296.40f, (byte) 13, 180000, "stage2_300560008", false);
				sp(219521, 313.50f, 445.74f, 296.40f, (byte) 13, 180000, "stage2_300560009", false);
				sp(219513, 313.15f, 443.01f, 296.57f, (byte) 13, 180000, "stage2_300560010", false);
				sp(219521, 312.30f, 444.33f, 296.47f, (byte) 13, 180000, "stage2_300560011", false);
				// wave 7 center_4
				sp(219513, 318.10f, 429.95f, 294.58f, (byte) 100, 180000, "stage2_center_300560000", true);
				sp(219513, 337.40f, 425.50f, 294.75f, (byte) 13, 180000, "stage2_center_300560001", true);
				sp(219513, 316.90f, 430.52f, 294.58f, (byte) 100, 180000, "stage2_center_300560002", true);
				sp(219513, 317.53f, 432.64f, 294.58f, (byte) 13, 180000, "stage2_center_300560003", true);
				// wave 8 left_6
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 220000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 220000, "stage2_300560001", false);
				sp(219513, 309.56f, 420.92f, 296.40f, (byte) 100, 220000, "stage2_300560002", false);
				sp(219521, 307.84f, 420.67f, 296.40f, (byte) 100, 220000, "stage2_300560003", false);
				sp(219513, 309.04f, 422.65f, 296.40f, (byte) 100, 220000, "stage2_300560004", false);
				sp(219521, 307.43f, 422.54f, 296.60f, (byte) 100, 220000, "stage2_300560005", false);
				// vawe 8 right_6
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 220000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 220000, "stage2_300560007", false);
				sp(219513, 314.33f, 444.00f, 296.40f, (byte) 13, 220000, "stage2_300560008", false);
				sp(219521, 313.50f, 445.74f, 296.40f, (byte) 13, 220000, "stage2_300560009", false);
				sp(219513, 313.15f, 443.01f, 296.57f, (byte) 13, 220000, "stage2_300560010", false);
				sp(219521, 312.30f, 444.33f, 296.47f, (byte) 13, 220000, "stage2_300560011", false);
				// wave 9 left_6
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 250000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 250000, "stage2_300560001", false);
				sp(219513, 309.56f, 420.92f, 296.40f, (byte) 100, 250000, "stage2_300560002", false);
				sp(219521, 307.84f, 420.67f, 296.40f, (byte) 100, 250000, "stage2_300560003", false);
				sp(219513, 309.04f, 422.65f, 296.40f, (byte) 100, 250000, "stage2_300560004", false);
				sp(219521, 307.43f, 422.54f, 296.60f, (byte) 100, 250000, "stage2_300560005", false);
				// wave 9 right_6
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 250000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 250000, "stage2_300560007", false);
				sp(219513, 314.33f, 444.00f, 296.40f, (byte) 13, 250000, "stage2_300560008", false);
				sp(219521, 313.50f, 445.74f, 296.40f, (byte) 13, 250000, "stage2_300560009", false);
				sp(219513, 313.15f, 443.01f, 296.57f, (byte) 13, 250000, "stage2_300560010", false);
				sp(219522, 312.30f, 444.33f, 296.47f, (byte) 13, 250000, "stage2_300560011", false); // Boss 1
				// wave 9 center_4
				sp(219513, 318.10f, 429.95f, 294.58f, (byte) 100, 250000, "stage2_center_300560000", true);
				sp(219513, 337.40f, 425.50f, 294.75f, (byte) 13, 250000, "stage2_center_300560001", true);
				sp(219513, 316.90f, 430.52f, 294.58f, (byte) 100, 250000, "stage2_center_300560002", true);
				sp(219513, 317.53f, 432.64f, 294.58f, (byte) 13, 250000, "stage2_center_300560003", true);
				break;

			case START_STAGE_2_PHASE_2:
				sendMsg(1401586); // The 2nd looting begins in 10 seconds!
				sp(219544, 316.4301f, 431.30615f, 294.58875f, (byte) 0);
				sp(219505, 319.37946f, 438.7644f, 294.58875f, (byte) 0);
				sp(219505, 316.30917f, 422.88278f, 294.58875f, (byte) 0);
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						sendMsg(1401665); // Grave Robbers will attack again in 5 seconds!
						ThreadPoolManager.getInstance().schedule(new Runnable() {

							@Override
							public void run() {
								deleteNpcs(instance.getNpcs(219544));
								deleteNpcs(instance.getNpcs(219505));
							}
						}, 5000);
					}
				}, 5000);
				// wave 1 left_4
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 10000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 10000, "stage2_300560001", false);
				sp(219516, 309.56f, 420.92f, 296.40f, (byte) 100, 10000, "stage2_300560002", false);
				sp(219517, 307.84f, 420.67f, 296.40f, (byte) 100, 10000, "stage2_300560003", false);
				// wave 1 right_4
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 10000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 10000, "stage2_300560007", false);
				sp(219516, 314.33f, 444.00f, 296.40f, (byte) 13, 10000, "stage2_300560008", false);
				sp(219517, 313.50f, 445.74f, 296.40f, (byte) 13, 10000, "stage2_300560009", false);
				// wave 2 left_6
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 50000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 50000, "stage2_300560001", false);
				sp(219513, 309.56f, 420.92f, 296.40f, (byte) 100, 50000, "stage2_300560002", false);
				sp(219521, 307.84f, 420.67f, 296.40f, (byte) 100, 50000, "stage2_300560003", false);
				sp(219513, 309.04f, 422.65f, 296.40f, (byte) 100, 50000, "stage2_300560004", false);
				sp(219521, 307.43f, 422.54f, 296.60f, (byte) 100, 50000, "stage2_300560005", false);
				// wave 2 right_6
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 50000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 50000, "stage2_300560007", false);
				sp(219513, 314.33f, 444.00f, 296.40f, (byte) 13, 50000, "stage2_300560008", false);
				sp(219521, 313.50f, 445.74f, 296.40f, (byte) 13, 50000, "stage2_300560009", false);
				sp(219513, 313.15f, 443.01f, 296.57f, (byte) 13, 50000, "stage2_300560010", false);
				sp(219521, 312.30f, 444.33f, 296.47f, (byte) 13, 50000, "stage2_300560011", false);
				// wave 2 center_2
				sp(219513, 318.10f, 429.95f, 294.58f, (byte) 100, 50000, "stage2_center_300560000", true);
				sp(219513, 337.40f, 425.50f, 294.75f, (byte) 13, 50000, "stage2_center_300560001", true);
				// wave 3 left_6
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 80000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 80000, "stage2_300560001", false);
				sp(219513, 309.56f, 420.92f, 296.40f, (byte) 100, 80000, "stage2_300560002", false);
				sp(219521, 307.84f, 420.67f, 296.40f, (byte) 100, 80000, "stage2_300560003", false);
				sp(219513, 309.04f, 422.65f, 296.40f, (byte) 100, 80000, "stage2_300560004", false);
				sp(219521, 307.43f, 422.54f, 296.60f, (byte) 100, 80000, "stage2_300560005", false);
				// wave 3 right_6
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 80000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 80000, "stage2_300560007", false);
				sp(219513, 314.33f, 444.00f, 296.40f, (byte) 13, 80000, "stage2_300560008", false);
				sp(219521, 313.50f, 445.74f, 296.40f, (byte) 13, 80000, "stage2_300560009", false);
				sp(219513, 313.15f, 443.01f, 296.57f, (byte) 13, 80000, "stage2_300560010", false);
				sp(219521, 312.30f, 444.33f, 296.47f, (byte) 13, 80000, "stage2_300560011", false);
				// wave 3 center_4
				sp(219513, 318.10f, 429.95f, 294.58f, (byte) 100, 80000, "stage2_center_300560000", true);
				sp(219513, 337.40f, 425.50f, 294.75f, (byte) 13, 80000, "stage2_center_300560001", true);
				sp(219513, 316.90f, 430.52f, 294.58f, (byte) 100, 80000, "stage2_center_300560002", true);
				sp(219513, 317.53f, 432.64f, 294.58f, (byte) 13, 80000, "stage2_center_300560003", true);
				// wave 4 left_6
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 120000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 120000, "stage2_300560001", false);
				sp(219513, 309.56f, 420.92f, 296.40f, (byte) 100, 120000, "stage2_300560002", false);
				sp(219521, 307.84f, 420.67f, 296.40f, (byte) 100, 120000, "stage2_300560003", false);
				sp(219513, 309.04f, 422.65f, 296.40f, (byte) 100, 120000, "stage2_300560004", false);
				sp(219521, 307.43f, 422.54f, 296.60f, (byte) 100, 120000, "stage2_300560005", false);
				// wave 4 right_6
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 120000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 120000, "stage2_300560007", false);
				sp(219513, 314.33f, 444.00f, 296.40f, (byte) 13, 120000, "stage2_300560008", false);
				sp(219521, 313.50f, 445.74f, 296.40f, (byte) 13, 120000, "stage2_300560009", false);
				sp(219513, 313.15f, 443.01f, 296.57f, (byte) 13, 120000, "stage2_300560010", false);
				sp(219521, 312.30f, 444.33f, 296.47f, (byte) 13, 120000, "stage2_300560011", false);
				// wave 5 left_6
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 150000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 150000, "stage2_300560001", false);
				sp(219513, 309.56f, 420.92f, 296.40f, (byte) 100, 150000, "stage2_300560002", false);
				sp(219521, 307.84f, 420.67f, 296.40f, (byte) 100, 150000, "stage2_300560003", false);
				sp(219513, 309.04f, 422.65f, 296.40f, (byte) 100, 150000, "stage2_300560004", false);
				sp(219521, 307.43f, 422.54f, 296.60f, (byte) 100, 150000, "stage2_300560005", false);
				// wave 5 right_6
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 150000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 150000, "stage2_300560007", false);
				sp(219513, 314.33f, 444.00f, 296.40f, (byte) 13, 150000, "stage2_300560008", false);
				sp(219521, 313.50f, 445.74f, 296.40f, (byte) 13, 150000, "stage2_300560009", false);
				sp(219513, 313.15f, 443.01f, 296.57f, (byte) 13, 150000, "stage2_300560010", false);
				sp(219521, 312.30f, 444.33f, 296.47f, (byte) 13, 150000, "stage2_300560011", false);
				// wave 6 left_6
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 180000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 180000, "stage2_300560001", false);
				sp(219513, 309.56f, 420.92f, 296.40f, (byte) 100, 180000, "stage2_300560002", false);
				sp(219521, 307.84f, 420.67f, 296.40f, (byte) 100, 180000, "stage2_300560003", false);
				sp(219513, 309.04f, 422.65f, 296.40f, (byte) 100, 180000, "stage2_300560004", false);
				sp(219521, 307.43f, 422.54f, 296.60f, (byte) 100, 180000, "stage2_300560005", false);
				// wave 6 right_6
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 180000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 180000, "stage2_300560007", false);
				sp(219513, 314.33f, 444.00f, 296.40f, (byte) 13, 180000, "stage2_300560008", false);
				sp(219521, 313.50f, 445.74f, 296.40f, (byte) 13, 180000, "stage2_300560009", false);
				sp(219513, 313.15f, 443.01f, 296.57f, (byte) 13, 180000, "stage2_300560010", false);
				sp(219521, 312.30f, 444.33f, 296.47f, (byte) 13, 180000, "stage2_300560011", false);
				// wave 6 center_4
				sp(219513, 318.10f, 429.95f, 294.58f, (byte) 100, 180000, "stage2_center_300560000", true);
				sp(219513, 337.40f, 425.50f, 294.75f, (byte) 13, 180000, "stage2_center_300560001", true);
				sp(219513, 316.90f, 430.52f, 294.58f, (byte) 100, 180000, "stage2_center_300560002", true);
				sp(219513, 317.53f, 432.64f, 294.58f, (byte) 13, 180000, "stage2_center_300560003", true);
				// wave 7 left_6
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 220000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 220000, "stage2_300560001", false);
				sp(219513, 309.56f, 420.92f, 296.40f, (byte) 100, 220000, "stage2_300560002", false);
				sp(219521, 307.84f, 420.67f, 296.40f, (byte) 100, 220000, "stage2_300560003", false);
				sp(219513, 309.04f, 422.65f, 296.40f, (byte) 100, 220000, "stage2_300560004", false);
				sp(219521, 307.43f, 422.54f, 296.60f, (byte) 100, 220000, "stage2_300560005", false);
				// wave 7 right_6
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 220000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 220000, "stage2_300560007", false);
				sp(219513, 314.33f, 444.00f, 296.40f, (byte) 13, 220000, "stage2_300560008", false);
				sp(219521, 313.50f, 445.74f, 296.40f, (byte) 13, 220000, "stage2_300560009", false);
				sp(219513, 313.15f, 443.01f, 296.57f, (byte) 13, 220000, "stage2_300560010", false);
				sp(219521, 312.30f, 444.33f, 296.47f, (byte) 13, 220000, "stage2_300560011", false);
				// vawe 8 left_6
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 250000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 250000, "stage2_300560001", false);
				sp(219513, 309.56f, 420.92f, 296.40f, (byte) 100, 250000, "stage2_300560002", false);
				sp(219521, 307.84f, 420.67f, 296.40f, (byte) 100, 250000, "stage2_300560003", false);
				sp(219513, 309.04f, 422.65f, 296.40f, (byte) 100, 250000, "stage2_300560004", false);
				sp(219521, 307.43f, 422.54f, 296.60f, (byte) 100, 250000, "stage2_300560005", false);
				// wave 8 right_6
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 250000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 250000, "stage2_300560007", false);
				sp(219513, 314.33f, 444.00f, 296.40f, (byte) 13, 250000, "stage2_300560008", false);
				sp(219521, 313.50f, 445.74f, 296.40f, (byte) 13, 250000, "stage2_300560009", false);
				sp(219513, 313.15f, 443.01f, 296.57f, (byte) 13, 250000, "stage2_300560010", false);
				sp(219521, 312.30f, 444.33f, 296.47f, (byte) 13, 250000, "stage2_300560011", false);
				// wave 8 center_4
				sp(219513, 318.10f, 429.95f, 294.58f, (byte) 100, 250000, "stage2_center_300560000", true);
				sp(219513, 337.40f, 425.50f, 294.75f, (byte) 13, 250000, "stage2_center_300560001", true);
				sp(219513, 316.90f, 430.52f, 294.58f, (byte) 100, 250000, "stage2_center_300560002", true);
				sp(219513, 317.53f, 432.64f, 294.58f, (byte) 13, 250000, "stage2_center_300560003", true);
				// wave 9 left_6
				sp(219516, 310.15f, 419.25f, 296.40f, (byte) 100, 280000, "stage2_300560000", false);
				sp(219517, 308.33f, 418.85f, 296.40f, (byte) 100, 280000, "stage2_300560001", false);
				sp(219513, 309.56f, 420.92f, 296.40f, (byte) 100, 280000, "stage2_300560002", false);
				sp(219521, 307.84f, 420.67f, 296.40f, (byte) 100, 280000, "stage2_300560003", false);
				sp(219513, 309.04f, 422.65f, 296.40f, (byte) 100, 280000, "stage2_300560004", false);
				sp(219521, 307.43f, 422.54f, 296.60f, (byte) 100, 280000, "stage2_300560005", false);
				// wave 9 right_6
				sp(219516, 315.88f, 445.00f, 296.40f, (byte) 13, 280000, "stage2_300560006", false);
				sp(219517, 315.13f, 446.95f, 296.40f, (byte) 13, 280000, "stage2_300560007", false);
				sp(219513, 314.33f, 444.00f, 296.40f, (byte) 13, 280000, "stage2_300560008", false);
				sp(219521, 313.50f, 445.74f, 296.40f, (byte) 13, 280000, "stage2_300560009", false);
				sp(219513, 313.15f, 443.01f, 296.57f, (byte) 13, 280000, "stage2_300560010", false);
				sp(219523, 312.30f, 444.33f, 296.47f, (byte) 13, 280000, "stage2_300560011", false); // Boss 2
				break;
			case START_STAGE_3_PHASE_1:
				isCancelled = false;
				stage = 3;
				destroyedTowers = 0;
				// Defense towers
				sp(831251, 452.33456f, 86.14286f, 214.3362f, (byte) 0);
				sp(831305, 451.58344f, 105.55769f, 212.20023f, (byte) 0);
				sp(831250, 437.19818f, 119.67343f, 214.33812f, (byte) 0);
				// Transformation device
				sp(831097, 410.5438f, 86.79955f, 222.13731f, (byte) 0);
				sendMsg(1401584);
				// wave 1
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 5000, "stage3_300560000", false);
				sp(219525, 391.56f, 121.12f, 222.14f, (byte) 13, 5000, "stage3_300560006", false);
				// wave 2 left_4
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 10000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 10000, "stage3_300560001", false);
				sp(219524, 419.02f, 49.02f, 222.15f, (byte) 100, 10000, "stage3_300560002", false);
				sp(219525, 420.37f, 47.53f, 222.15f, (byte) 100, 10000, "stage3_300560003", false);
				// wave 2 right_4
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 10000, "stage3_300560006", false);
				sp(219525, 391.25f, 119.11f, 222.14f, (byte) 13, 10000, "stage3_300560007", false);
				sp(219524, 389.61f, 121.50f, 222.15f, (byte) 13, 10000, "stage3_300560008", false);
				sp(219525, 389.34f, 119.47f, 222.14f, (byte) 13, 10000, "stage3_300560009", false);
				// wave 2 center
				sp(219529, 421.93f, 93.76f, 214.33f, (byte) 100, 10000, "stage3_center_300560000", true);
				sp(219529, 423.02f, 91.67f, 214.33f, (byte) 13, 10000, "stage3_center_300560001", true);
				// wave 3 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 50000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 50000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 50000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 50000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 50000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 50000, "stage3_300560005", false);
				// wave 3 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 50000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 50000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 50000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 50000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 50000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 50000, "stage3_300560011", false);
				// wave 3 center_4
				sp(219529, 421.93f, 93.76f, 214.33f, (byte) 100, 50000, "stage3_center_300560000", true);
				sp(219529, 423.02f, 91.67f, 214.33f, (byte) 13, 50000, "stage3_center_300560001", true);
				sp(219529, 420.62f, 93.14f, 214.33f, (byte) 100, 50000, "stage3_center_300560002", true);
				sp(219529, 421.87f, 91.19f, 214.33f, (byte) 13, 50000, "stage3_center_300560003", true);
				// wave 4 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 80000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 80000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 80000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 80000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 80000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 80000, "stage3_300560005", false);
				// wave 4 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 80000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 80000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 80000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 80000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 80000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 80000, "stage3_300560011", false);
				// wave 5 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 120000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 120000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 120000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 120000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 120000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 120000, "stage3_300560005", false);
				// wave 5 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 120000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 120000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 120000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 120000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 120000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 120000, "stage3_300560011", false);
				// wave 5 center_4
				sp(219529, 421.93f, 93.76f, 214.33f, (byte) 100, 120000, "stage3_center_300560000", true);
				sp(219529, 423.02f, 91.67f, 214.33f, (byte) 13, 120000, "stage3_center_300560001", true);
				sp(219529, 420.62f, 93.14f, 214.33f, (byte) 100, 120000, "stage3_center_300560002", true);
				sp(219529, 421.87f, 91.19f, 214.33f, (byte) 13, 120000, "stage3_center_300560003", true);
				// wave 6 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 150000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 150000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 150000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 150000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 150000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 150000, "stage3_300560005", false);
				// wave 6 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 150000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 150000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 150000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 150000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 150000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 150000, "stage3_300560011", false);
				// wave 7 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 180000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 180000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 180000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 180000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 180000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 180000, "stage3_300560005", false);
				// wave 7 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 180000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 180000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 180000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 180000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 180000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 180000, "stage3_300560011", false);
				// wave 8 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 220000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 220000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 220000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 220000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 220000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 220000, "stage3_300560005", false);
				// wave 8 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 220000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 220000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 220000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 220000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 220000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 220000, "stage3_300560011", false);
				// wave 8 center_4
				sp(219529, 421.93f, 93.76f, 214.33f, (byte) 100, 220000, "stage3_center_300560000", true);
				sp(219529, 423.02f, 91.67f, 214.33f, (byte) 13, 220000, "stage3_center_300560001", true);
				sp(219529, 420.62f, 93.14f, 214.33f, (byte) 100, 220000, "stage3_center_300560002", true);
				sp(219529, 421.87f, 91.19f, 214.33f, (byte) 13, 220000, "stage3_center_300560003", true);
				// wave 9 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 250000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 250000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 250000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 250000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 250000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 250000, "stage3_300560005", false);
				// wave 9 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 250000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 250000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 250000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 250000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 250000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 250000, "stage3_300560011", false);
				// Bonus spawn wave 1
				cancelMessageTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						startBonusStage3();
					}
				}, 280000);
				// wave 12 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 320000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 320000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 320000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 320000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 320000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 320000, "stage3_300560005", false);
				// wave 12 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 320000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 320000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 320000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 320000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 320000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 320000, "stage3_300560011", false);
				// wave 12 center_4
				sp(219529, 421.93f, 93.76f, 214.33f, (byte) 100, 320000, "stage3_center_300560000", true);
				sp(219529, 423.02f, 91.67f, 214.33f, (byte) 13, 320000, "stage3_center_300560001", true);
				sp(219529, 420.62f, 93.14f, 214.33f, (byte) 100, 320000, "stage3_center_300560002", true);
				sp(219529, 421.87f, 91.19f, 214.33f, (byte) 13, 320000, "stage3_center_300560003", true);
				// wave 13 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 350000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 350000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 350000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 350000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 350000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 350000, "stage3_300560005", false);
				// wave 13 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 350000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 350000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 350000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 350000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 350000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 350000, "stage3_300560011", false);
				// wave 14 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 380000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 380000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 380000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 380000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 380000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 380000, "stage3_300560005", false);
				// wave 14 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 380000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 380000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 380000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 380000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 380000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 380000, "stage3_300560011", false);
				// wave 14 center_4
				sp(219529, 421.93f, 93.76f, 214.33f, (byte) 100, 380000, "stage3_center_300560000", true);
				sp(219529, 423.02f, 91.67f, 214.33f, (byte) 13, 380000, "stage3_center_300560001", true);
				sp(219529, 420.62f, 93.14f, 214.33f, (byte) 100, 380000, "stage3_center_300560002", true);
				sp(219529, 421.87f, 91.19f, 214.33f, (byte) 13, 380000, "stage3_center_300560003", true);
				// wave 15 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 420000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 420000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 420000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 420000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 420000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 420000, "stage3_300560005", false);
				// wave 15 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 420000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 420000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 420000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 420000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 420000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 420000, "stage3_300560011", false);
				// wave 16 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 450000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 450000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 450000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 450000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 450000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 450000, "stage3_300560005", false);
				// wave 16 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 450000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 450000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 450000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 450000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 450000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 450000, "stage3_300560011", false);
				// wave 17 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 480000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 480000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 480000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 480000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 480000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 480000, "stage3_300560005", false);
				// wave 17 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 480000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 480000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 480000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 480000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 480000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 480000, "stage3_300560011", false);
				// wave 17 center_4
				sp(219529, 421.93f, 93.76f, 214.33f, (byte) 100, 480000, "stage3_center_300560000", true);
				sp(219529, 423.02f, 91.67f, 214.33f, (byte) 13, 480000, "stage3_center_300560001", true);
				sp(219529, 420.62f, 93.14f, 214.33f, (byte) 100, 480000, "stage3_center_300560002", true);
				sp(219529, 421.87f, 91.19f, 214.33f, (byte) 13, 480000, "stage3_center_300560003", true);
				// wave 18 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 520000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 520000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 520000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 520000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 520000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 520000, "stage3_300560005", false);
				// wave 18 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 520000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 520000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 520000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 520000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 520000, "stage3_300560010", false);
				sp(219530, 387.59f, 119.64f, 222.15f, (byte) 13, 520000, "stage3_300560011", false); // Boss 1
				break;
			case START_STAGE_3_PHASE_2:
				sendMsg(1401670); // The last looting begins in 10 seconds!
				sp(219543, 425.69678f, 85.350716f, 214.338f, (byte) 0);
				sp(219544, 418.70923f, 100.76572f, 214.33798f, (byte) 0);
				sp(219544, 421.16943f, 92.78562f, 214.33856f, (byte) 0);
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						sendMsg(1401671); // Grave Robbers will attack in again in 5 seconds!
						ThreadPoolManager.getInstance().schedule(new Runnable() {

							@Override
							public void run() {
								deleteNpcs(instance.getNpcs(219543));
								deleteNpcs(instance.getNpcs(219544));
								deleteNpcs(instance.getNpcs(219507));
							}
						}, 5000);
					}
				}, 5000);
				// wave 2 left_4
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 10000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 10000, "stage3_300560001", false);
				sp(219524, 419.02f, 49.02f, 222.15f, (byte) 100, 10000, "stage3_300560002", false);
				sp(219525, 420.37f, 47.53f, 222.15f, (byte) 100, 10000, "stage3_300560003", false);
				// wave 2 right_4
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 10000, "stage3_300560006", false);
				sp(219525, 391.25f, 119.11f, 222.14f, (byte) 13, 10000, "stage3_300560007", false);
				sp(219524, 389.61f, 121.50f, 222.15f, (byte) 13, 10000, "stage3_300560008", false);
				sp(219525, 389.34f, 119.47f, 222.14f, (byte) 13, 10000, "stage3_300560009", false);
				// wave 2 center
				sp(219529, 421.93f, 93.76f, 214.33f, (byte) 100, 10000, "stage3_center_300560000", true);
				sp(219529, 423.02f, 91.67f, 214.33f, (byte) 13, 10000, "stage3_center_300560001", true);
				// wave 3 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 50000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 50000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 50000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 50000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 50000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 50000, "stage3_300560005", false);
				// wave 3 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 50000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 50000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 50000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 50000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 50000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 50000, "stage3_300560011", false);
				// wave 3 center_4
				sp(219529, 421.93f, 93.76f, 214.33f, (byte) 100, 50000, "stage3_center_300560000", true);
				sp(219529, 423.02f, 91.67f, 214.33f, (byte) 13, 50000, "stage3_center_300560001", true);
				sp(219529, 420.62f, 93.14f, 214.33f, (byte) 100, 50000, "stage3_center_300560002", true);
				sp(219529, 421.87f, 91.19f, 214.33f, (byte) 13, 50000, "stage3_center_300560003", true);
				// wave 4 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 80000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 80000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 80000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 80000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 80000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 80000, "stage3_300560005", false);
				// wave 4 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 80000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 80000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 80000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 80000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 80000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 80000, "stage3_300560011", false);
				// wave 5 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 120000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 120000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 120000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 120000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 120000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 120000, "stage3_300560005", false);
				// wave 5 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 120000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 120000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 120000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 120000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 120000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 120000, "stage3_300560011", false);
				// wave 5 center_4
				sp(219529, 421.93f, 93.76f, 214.33f, (byte) 100, 120000, "stage3_center_300560000", true);
				sp(219529, 423.02f, 91.67f, 214.33f, (byte) 13, 120000, "stage3_center_300560001", true);
				sp(219529, 420.62f, 93.14f, 214.33f, (byte) 100, 120000, "stage3_center_300560002", true);
				sp(219529, 421.87f, 91.19f, 214.33f, (byte) 13, 120000, "stage3_center_300560003", true);
				// wave 6 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 150000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 150000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 150000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 150000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 150000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 150000, "stage3_300560005", false);
				// wave 6 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 150000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 150000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 150000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 150000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 150000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 150000, "stage3_300560011", false);
				// wave 7 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 180000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 180000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 180000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 180000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 180000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 180000, "stage3_300560005", false);
				// wave 7 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 180000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 180000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 180000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 180000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 180000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 180000, "stage3_300560011", false);
				// wave 8 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 220000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 220000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 220000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 220000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 220000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 220000, "stage3_300560005", false);
				// wave 8 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 220000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 220000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 220000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 220000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 220000, "stage3_300560010", false);
				sp(219529, 387.59f, 119.64f, 222.15f, (byte) 13, 220000, "stage3_300560011", false);
				// wave 8 center_4
				sp(219529, 421.93f, 93.76f, 214.33f, (byte) 100, 220000, "stage3_center_300560000", true);
				sp(219529, 423.02f, 91.67f, 214.33f, (byte) 13, 220000, "stage3_center_300560001", true);
				sp(219529, 420.62f, 93.14f, 214.33f, (byte) 100, 220000, "stage3_center_300560002", true);
				sp(219529, 421.87f, 91.19f, 214.33f, (byte) 13, 220000, "stage3_center_300560003", true);
				// wave 9 left_6
				sp(219524, 420.19f, 50.58f, 222.14f, (byte) 100, 250000, "stage3_300560000", false);
				sp(219525, 421.44f, 49.18f, 222.15f, (byte) 100, 250000, "stage3_300560001", false);
				sp(219529, 419.02f, 49.02f, 222.15f, (byte) 100, 250000, "stage3_300560002", false);
				sp(219529, 420.37f, 47.53f, 222.15f, (byte) 100, 250000, "stage3_300560003", false);
				sp(219529, 417.99f, 47.47f, 222.15f, (byte) 100, 250000, "stage3_300560004", false);
				sp(219529, 419.17f, 46.11f, 222.15f, (byte) 100, 250000, "stage3_300560005", false);
				// wave 9 right_6
				sp(219524, 391.56f, 121.12f, 222.14f, (byte) 13, 250000, "stage3_300560006", false);
				sp(219524, 391.25f, 119.11f, 222.14f, (byte) 13, 250000, "stage3_300560007", false);
				sp(219529, 389.61f, 121.50f, 222.15f, (byte) 13, 250000, "stage3_300560008", false);
				sp(219529, 389.34f, 119.47f, 222.14f, (byte) 13, 250000, "stage3_300560009", false);
				sp(219529, 388.08f, 121.71f, 222.15f, (byte) 13, 250000, "stage3_300560010", false);
				sp(219531, 387.59f, 119.64f, 222.15f, (byte) 13, 250000, "stage3_300560011", false); // Boss 2
				break;
		}
	}

	private void startBonusStage1() {
		sp(219505, 199.23898f, 280.70059f, 550.49426f, (byte) 0);
		sp(219505, 217.23492f, 266.69803f, 550.49426f, (byte) 0);
		sp(219543, 210.49338f, 275.42099f, 550.49426f, (byte) 0);
		sendMsg(1401586); // The 2nd looting will begin in 10 seconds!
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				sendMsg(1401607); // Grave Robbers will attack again in 5 seconds!
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						deleteNpcs(instance.getNpcs(219543));
						deleteNpcs(instance.getNpcs(219505));
					}
				}, 5000);
			}
		}, 5000);
	}

	private void startBonusStage3() {
		sp(219507, 425.69678f, 85.350716f, 214.338f, (byte) 0);
		sp(219507, 418.70923f, 100.76572f, 214.33798f, (byte) 0);
		sp(219544, 421.16943f, 92.78562f, 214.33856f, (byte) 0);
		sendMsg(1401586); // The 2nd looting will begin in 10 seconds!
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				sendMsg(1401607); // Grave Robbers will attack again in 5 seconds!
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						deleteNpcs(instance.getNpcs(219507));
						deleteNpcs(instance.getNpcs(219544));
					}
				}, 5000);
			}
		}, 5000);
	}

	private void spawnBonusStage2() {
		sp(219543, 318.77524f, 430.0789f, 294.58875f, (byte) 0);
		sp(219630, 318.58594f, 426.85278f, 294.58875f, (byte) 0);
		sp(219630, 319.8232f, 432.50577f, 294.58875f, (byte) 0);
		sp(219630, 316.298f, 426.3792f, 294.58875f, (byte) 0);
		sp(219630, 324.12003f, 431.1665f, 294.58875f, (byte) 0);
		sp(219630, 321.50192f, 430.2314f, 294.58875f, (byte) 0);
		sp(219630, 320.20047f, 424.94888f, 294.58875f, (byte) 0);
		sp(219630, 319.10046f, 435.9833f, 294.58875f, (byte) 0);
		sp(219630, 323.14096f, 426.51614f, 294.58875f, (byte) 0);
		sp(219630, 322.4489f, 434.38107f, 294.58875f, (byte) 0);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				deleteNpcs(instance.getNpcs(219543));
				deleteNpcs(instance.getNpcs(219630));
			}
		}, 5000);
	}

	private void spawnExitPortal() {
		sp(831118, 64.34126f, 331.66925f, 287.28214f, (byte) 0);
		sp(831118, 48.955025f, 241.34631f, 421.61823f, (byte) 0);
		sp(831118, 54.88896f, 353.18854f, 287.28244f, (byte) 0);
		sp(831118, 51.961323f, 256.28143f, 421.61823f, (byte) 0);
		sp(831118, 332.69638f, 54.43414f, 358.22095f, (byte) 0);
		sp(831118, 443.98517f, 138.95134f, 214.81421f, (byte) 80);
		sp(831118, 339.50348f, 169.87396f, 306.4693f, (byte) 0);
		sp(831118, 46.9357f, 435.19156f, 455.65244f, (byte) 0);
		sp(831118, 450.11063f, 239.98364f, 516.4457f, (byte) 0);
		sp(831118, 289.1735f, 329.22617f, 458.2218f, (byte) 0);
		sp(831118, 224.21411f, 33.678886f, 466.1734f, (byte) 0);
		sp(831118, 329.32675f, 36.599545f, 358.22098f, (byte) 0);
		sp(831118, 298.6258f, 307.70688f, 458.22186f, (byte) 0);
		sp(831118, 167.14172f, 76.387856f, 466.1734f, (byte) 0);
		sp(831118, 50.074734f, 452.7495f, 455.6524f, (byte) 0);
		sp(831118, 330.05118f, 191.39325f, 306.46924f, (byte) 0);
		sp(831118, 394.43204f, 282.6926f, 516.4457f, (byte) 0);
		sp(831118, 469.83466f, 76.66318f, 214.76643f, (byte) 0);
		sp(831118, 164.9796f, 421.7813f, 616.1734f, (byte) 0);
		sp(831118, 222.05199f, 379.07236f, 616.1734f, (byte) 0);
	}

	private void spawnChest() {
		sp(831122, 316.1566f, 333.21307f, 456.08493f, (byte) 0);
		sp(831122, 369.00742f, 169.64816f, 306.46603f, (byte) 0);
		sp(831122, 298.8276f, 335.4352f, 458.22232f, (byte) 0);
		sp(831122, 310.1692f, 309.53632f, 458.22226f, (byte) 0);
		sp(831122, 80.64051f, 345.50037f, 285.14545f, (byte) 0);
		sp(831122, 328.34308f, 307.45993f, 458.22153f, (byte) 0);
		sp(831122, 339.49194f, 197.62344f, 306.46964f, (byte) 0);
		sp(831122, 369.3519f, 178.85245f, 306.46866f, (byte) 0);
		sp(831122, 344.67303f, 184.942f, 304.3324f, (byte) 0);
		sp(831122, 328.68756f, 316.6642f, 458.22092f, (byte) 0);
		sp(831122, 356.82092f, 195.4013f, 304.33533f, (byte) 0);
		sp(831122, 94.62696f, 331.95328f, 287.27887f, (byte) 0);
		sp(831122, 319.6168f, 309.49963f, 458.22095f, (byte) 0);
		sp(831122, 94.97144f, 341.15756f, 287.28137f, (byte) 0);
		sp(831122, 314.35663f, 321.00702f, 456.08493f, (byte) 0);
		sp(831122, 350.83353f, 171.72455f, 306.46967f, (byte) 0);
		sp(831122, 310.8757f, 348.5869f, 458.22256f, (byte) 0);
		sp(831122, 82.44047f, 357.70642f, 285.14545f, (byte) 0);
		sp(831122, 65.11149f, 359.92856f, 287.2829f, (byte) 0);
		sp(831122, 351.54004f, 210.77515f, 306.4696f, (byte) 0);
		sp(831122, 76.45307f, 334.02966f, 287.2826f, (byte) 0);
		sp(831122, 360.28113f, 171.0f, 306.4681f, (byte) 0);
		sp(831122, 77.159584f, 373.08026f, 287.2831f, (byte) 0);
		sp(831122, 355.02097f, 183.19527f, 304.3324f, (byte) 0);
		sp(831122, 305.15875f, 329.49237f, 456.08493f, (byte) 0);
		sp(831122, 71.442635f, 353.98572f, 285.14545f, (byte) 0);
		sp(831122, 304.0087f, 322.75378f, 456.0895f, (byte) 0);
		sp(831122, 345.8231f, 191.6806f, 304.3324f, (byte) 0);
		sp(831122, 85.90067f, 333.99298f, 287.28128f, (byte) 0);
		sp(831122, 70.29258f, 347.24713f, 285.14545f, (byte) 0);

		sp(831123, 359.7552f, 58.446693f, 358.22098f, (byte) 0);
		sp(831123, 66.76055f, 239.5818f, 421.7896f, (byte) 0);
		sp(831123, 356.41553f, 45.183434f, 358.38928f, (byte) 0);
		sp(831123, 62.997505f, 227.72667f, 421.61823f, (byte) 0);
		sp(831123, 62.569805f, 268.48575f, 423.43756f, (byte) 0);
		sp(831123, 332.38266f, 22.128136f, 360.0403f, (byte) 0);
		sp(831123, 51.071453f, 422.49442f, 457.47177f, (byte) 0);
		sp(831123, 68.73515f, 225.56467f, 421.61823f, (byte) 0);
		sp(831123, 343.7064f, 23.771324f, 358.22098f, (byte) 0);
		sp(831123, 77.04496f, 235.73196f, 421.78915f, (byte) 0);
		sp(831123, 61.485966f, 257.10004f, 421.61823f, (byte) 0);
		sp(831123, 342.19485f, 53.14469f, 358.22095f, (byte) 0);
		sp(831123, 51.673756f, 226.08348f, 423.43756f, (byte) 0);
		sp(831123, 350.34528f, 46.59707f, 358.3922f, (byte) 0);
		sp(831123, 68.13286f, 421.9756f, 455.65244f, (byte) 0);
		sp(831123, 357.19852f, 38.951546f, 358.39038f, (byte) 0);
		sp(831123, 76.44264f, 432.14288f, 455.82336f, (byte) 0);
		sp(831123, 357.75385f, 31.776619f, 358.39203f, (byte) 0);
		sp(831123, 69.63638f, 250.55241f, 421.78964f, (byte) 0);
		sp(831123, 69.03407f, 446.96335f, 455.82376f, (byte) 0);
		sp(831123, 71.916115f, 237.41977f, 421.78894f, (byte) 0);
		sp(831123, 343.2787f, 64.5304f, 360.0403f, (byte) 0);
		sp(831123, 75.70664f, 249.13878f, 421.78638f, (byte) 0);
		sp(831123, 79.046295f, 262.40204f, 421.61823f, (byte) 0);
		sp(831123, 335.87512f, 39.341072f, 358.22098f, (byte) 0);
		sp(831123, 71.3138f, 433.8307f, 455.82294f, (byte) 0);
		sp(831123, 62.395184f, 424.1376f, 455.65244f, (byte) 0);
		sp(831123, 347.46945f, 35.626457f, 358.39227f, (byte) 0);
		sp(831123, 75.887314f, 439.3178f, 455.82196f, (byte) 0);
		sp(831123, 349.44406f, 21.609322f, 358.22098f, (byte) 0);
		sp(831123, 84.264786f, 455.56992f, 455.65244f, (byte) 0);
		sp(831123, 84.867096f, 259.159f, 421.61823f, (byte) 0);
		sp(831123, 365.576f, 55.20365f, 358.22098f, (byte) 0);
		sp(831123, 55.166237f, 243.29642f, 421.61823f, (byte) 0);
		sp(831123, 66.15825f, 435.99274f, 455.82352f, (byte) 0);
		sp(831123, 54.563915f, 439.70734f, 455.65515f, (byte) 0);
		sp(831123, 61.96748f, 464.89667f, 457.47177f, (byte) 0);
		sp(831123, 75.104324f, 445.5497f, 455.821f, (byte) 0);
		sp(831123, 76.48961f, 242.90689f, 421.78745f, (byte) 0);
		sp(831123, 78.443985f, 458.81296f, 455.65244f, (byte) 0);
		sp(831123, 352.625f, 33.464424f, 358.3917f, (byte) 0);
		sp(831123, 60.883648f, 453.51096f, 455.6524f, (byte) 0);

		sp(831124, 211.09508f, 404.05515f, 614.131f, (byte) 0);
		sp(831124, 435.39233f, 259.91364f, 514.4033f, (byte) 0);
		sp(831124, 439.626f, 265.3722f, 514.4033f, (byte) 0);
		sp(831124, 197.74788f, 404.6274f, 614.48865f, (byte) 0);
		sp(831124, 225.05302f, 390.13104f, 616.1752f, (byte) 0);
		sp(831124, 457.62457f, 263.59116f, 516.4457f, (byte) 0);
		sp(831124, 410.6319f, 261.6957f, 514.4033f, (byte) 0);
		sp(831124, 196.15181f, 386.54306f, 614.4412f, (byte) 0);
		sp(831124, 418.15936f, 255.10327f, 514.4033f, (byte) 0);
		sp(831124, 417.06314f, 293.49078f, 516.4457f, (byte) 0);
		sp(831124, 176.92763f, 431.9935f, 616.1734f, (byte) 0);
		sp(831124, 193.36438f, 399.0128f, 614.48865f, (byte) 0);
		sp(831124, 414.81146f, 267.5063f, 514.4033f, (byte) 0);
		sp(831124, 216.92516f, 385.02884f, 616.1734f, (byte) 0);
		sp(831124, 435.518f, 237.70782f, 516.4457f, (byte) 0);
		sp(831124, 427.114f, 266.71103f, 514.7609f, (byte) 0);
		sp(831124, 194.1109f, 416.6819f, 614.131f, (byte) 0);
		sp(831124, 229.9601f, 405.0212f, 616.1734f, (byte) 0);
		sp(831124, 452.7175f, 248.70099f, 516.4457f, (byte) 0);
		sp(831124, 404.5921f, 290.56342f, 516.4457f, (byte) 0);
		sp(831124, 202.80295f, 410.5095f, 614.48865f, (byte) 0);
		sp(831124, 444.58963f, 243.59877f, 516.4457f, (byte) 0);
		sp(831124, 425.51794f, 248.6267f, 514.4995f, (byte) 0);
		sp(831124, 400.0553f, 267.39f, 516.4457f, (byte) 0);
		sp(831124, 206.02621f, 397.83002f, 614.131f, (byte) 0);
		sp(831124, 402.65216f, 279.38605f, 516.4457f, (byte) 0);
		sp(831124, 181.26578f, 399.61206f, 614.30817f, (byte) 0);
		sp(831124, 207.85353f, 379.13788f, 616.1734f, (byte) 0);
		sp(831124, 189.39867f, 434.92084f, 616.1734f, (byte) 0);
		sp(831124, 422.7305f, 261.09644f, 514.7609f, (byte) 0);
		sp(831124, 172.39082f, 408.8201f, 616.1734f, (byte) 0);
		sp(831124, 188.79323f, 393.01965f, 614.131f, (byte) 0);
		sp(831124, 174.98769f, 420.8161f, 616.1734f, (byte) 0);
		sp(831124, 423.47702f, 278.7655f, 514.4033f, (byte) 0);
		sp(831124, 185.44536f, 405.42267f, 614.131f, (byte) 0);
		sp(831124, 191.12608f, 91.33438f, 466.1734f, (byte) 0);
		sp(831124, 431.77502f, 271.9439f, 514.7609f, (byte) 0);

		sp(831373, 355.68063f, 20.112541f, 358.22098f, (byte) 0);
		sp(831373, 353.3523f, 59.361397f, 358.22095f, (byte) 0);
		sp(831373, 315.51196f, 327.70633f, 456.08493f, (byte) 0);
		sp(831373, 337.12244f, 31.857185f, 358.22098f, (byte) 0);
		sp(831373, 309.1074f, 320.32703f, 456.08536f, (byte) 0);
		sp(831373, 56.413555f, 235.81253f, 421.61823f, (byte) 0);
		sp(831373, 310.3064f, 326.59393f, 456.08493f, (byte) 0);
		sp(831373, 337.95398f, 47.32389f, 358.22095f, (byte) 0);
		sp(831373, 57.245094f, 251.27924f, 421.61823f, (byte) 0);
		sp(831373, 356.1763f, 189.89456f, 304.3324f, (byte) 0);
		sp(831373, 350.83453f, 194.99779f, 304.3324f, (byte) 0);
		sp(831373, 76.45408f, 357.30292f, 285.14545f, (byte) 0);
		sp(831373, 55.81123f, 432.22345f, 455.65244f, (byte) 0);
		sp(831373, 84.0355f, 368.70935f, 287.28445f, (byte) 0);
		sp(831373, 345.88846f, 212.87036f, 306.46487f, (byte) 0);
		sp(831373, 323.69205f, 313.83783f, 458.22195f, (byte) 0);
		sp(831373, 86.40266f, 348.33115f, 285.14545f, (byte) 0);
		sp(831373, 80.04646f, 443.97028f, 455.8237f, (byte) 0);
		sp(831373, 361.35767f, 43.603996f, 358.39203f, (byte) 0);
		sp(831373, 360.7831f, 186.02603f, 304.3324f, (byte) 0);
		sp(831373, 89.97593f, 338.33118f, 287.28238f, (byte) 0);
		sp(831373, 70.66881f, 244.4879f, 421.78815f, (byte) 0);
		sp(831373, 344.90024f, 204.8159f, 306.46793f, (byte) 0);
		sp(831373, 322.08545f, 330.18976f, 456.08493f, (byte) 0);
		sp(831373, 310.1702f, 332.80957f, 456.08493f, (byte) 0);
		sp(831373, 72.04109f, 459.72766f, 455.6524f, (byte) 0);
		sp(831373, 72.6434f, 263.31674f, 421.61823f, (byte) 0);
		sp(831373, 80.64878f, 247.55934f, 421.78918f, (byte) 0);
		sp(831373, 56.642773f, 447.69016f, 455.6524f, (byte) 0);
		sp(831373, 74.36943f, 420.47882f, 455.65244f, (byte) 0);
		sp(831373, 362.7498f, 192.378f, 304.3324f, (byte) 0);
		sp(831373, 74.971725f, 224.06789f, 421.61823f, (byte) 0);
		sp(831373, 70.51978f, 367.12103f, 287.28125f, (byte) 0);
		sp(831373, 364.35638f, 176.02605f, 306.46954f, (byte) 0);
		sp(831373, 88.36933f, 354.6831f, 285.14545f, (byte) 0);
		sp(831373, 305.22412f, 350.68213f, 458.21747f, (byte) 0);
		sp(831373, 358.41595f, 206.40424f, 306.47455f, (byte) 0);
		sp(831373, 76.59028f, 351.08728f, 285.14545f, (byte) 0);
		sp(831373, 71.508f, 375.17548f, 287.27805f, (byte) 0);
		sp(831373, 189.74734f, 411.19003f, 614.131f, (byte) 0);
		sp(831373, 317.75162f, 344.216f, 458.22403f, (byte) 0);
		sp(831373, 349.77173f, 182.51524f, 304.3324f, (byte) 0);
		sp(831373, 350.97073f, 188.78217f, 304.3324f, (byte) 0);
		sp(831373, 75.39127f, 344.82037f, 285.14545f, (byte) 0);
		sp(831373, 304.2359f, 342.6277f, 458.2206f, (byte) 0);
		sp(831373, 419.11346f, 273.27368f, 514.4041f, (byte) 0);
		sp(831373, 430.85223f, 255.02826f, 514.4033f, (byte) 0);
		sp(831373, 320.11877f, 323.8378f, 456.08493f, (byte) 0);
		sp(831373, 70.06651f, 440.89883f, 455.82208f, (byte) 0);
		sp(831373, 201.48613f, 392.9446f, 614.131f, (byte) 0);
		sp(831373, 351.37772f, 40.532555f, 358.3907f, (byte) 0);
		sp(831373, 81.795845f, 352.19968f, 285.14545f, (byte) 0);
	}

	private void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final String walkerId,
		final boolean isRun) {
		cancelSpawnTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isInstanceDestroyed && isCancelled == false) {
					Npc npc = (Npc) spawn(npcId, x, y, z, h);
					npcs.add(npc);
					npc.getSpawn().setWalkerId(walkerId);
					WalkManager.startWalking((NpcAI2) npc.getAi2());
					if (isRun) {
						npc.setState(1);
					}
					else {
						npc.setState(CreatureState.WALKING);
					}
					PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
				}
			}
		}, time);
	}

	private void sp(int npcId, float x, float y, float z, byte h) {
		if (!isInstanceDestroyed) {
			Npc npc = (Npc) spawn(npcId, x, y, z, h);
			npcs.add(npc);
		}
	}

	@Override
	public void onLeaveInstance(Player player) {
		player.getEffectController().removeEffect(skillId);
		// Delete Keys and Tags after leaving instance
		player.getInventory().decreaseByItemId(185000129, player.getInventory().getItemCountByItemId(185000129));
		player.getInventory().decreaseByItemId(185000129, player.getInventory().getItemCountByItemId(182006989));
		player.getInventory().decreaseByItemId(185000129, player.getInventory().getItemCountByItemId(182006990));
		player.getInventory().decreaseByItemId(185000129, player.getInventory().getItemCountByItemId(182006991));
	}

	@Override
	public void onPlayerLogOut(Player player) {
		player.setTransformed(false);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, false));
	}

	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		destroyedTowers = 0;
		stage = 0;
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player,
			new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}
}
