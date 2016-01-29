package instance;

import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.controllers.effect.PlayerEffectController;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.model.instance.StageType;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.network.aion.serverpackets.SM_TRANSFORM;
import org.typezero.gameserver.services.player.PlayerReviveService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.WorldMapInstance;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Dision M.O.G. Devs Team
 */

@InstanceID(301160000)
public class IDAsteriaIUParty extends GeneralInstanceHandler {

	protected boolean isInstanceDestroyed = false;
	private int stage;
	private boolean isCancelled;
	private List<Npc> npcs = new ArrayList<Npc>();
	private int skillId;
	private Future<?> cancelSpawnTask;
	private Future<?> cancelMessageTask;
    private Future<?> Vivarokatask;
    private Future<?> Makekiketask;

    @Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		stage = 0;
	}

	@Override
	public void onEnterInstance(Player player) {
		player.getEffectController().removeEffect(218611);
		player.getEffectController().removeEffect(218610);
		IDAsteriaIUBless(player);
	}

	private void IDAsteriaIUBless(final Player player) {
		skillId = player.getRace() == Race.ASMODIANS ? 21332 : 21329;

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				SkillEngine.getInstance().applyEffectDirectly(skillId, player, player, 0);
			}
		}, 1000);
	}

	@Override
	public void onDie(Npc npc) {
		if (npcs.contains(npc)) {
			npcs.remove(npc);
		}
        if(npc.getAggroList().getMostPlayerDamage() != null ){

        Player pl = npc.getAggroList().getMostPlayerDamage();

		PlayerGroup group = pl.getPlayerGroup2();

		switch (npc.getObjectTemplate().getTemplateId()) {

		// Remove Monsters
			case 233144:
			case 831347:
			case 831348:
			case 233149:
			case 233150:
			case 233151:
			case 233152:
			case 230289:
				despawnNpc(npc);
				break;
			case 831572: // Start Event
				if (group != null) {
					PacketSendUtility.sendPacket(pl, new SM_PLAY_MOVIE(0, 0, 983, 0));
				}
				else { // is Solo
					PacketSendUtility.sendPacket(pl, new SM_PLAY_MOVIE(0, 0, 983, 0));
				}
                despawnNpc(getNpc(831741));
                spawn(831741, 521.22f, 565.28f, 199.90f, (byte) 60);
				onChangeStage(StageType.START_STAGE_1_PHASE_1);
                despawnNpc(getNpc(831572));
				break;
			case 233161: // Rukibuki
				if (group != null) {
					PacketSendUtility.sendPacket(pl, new SM_PLAY_MOVIE(0, 0, 984, 0));
				}
				else { // is Solo
					PacketSendUtility.sendPacket(pl, new SM_PLAY_MOVIE(0, 0, 984, 0));
				}
				despawnNpc(getNpc(831572));
				despawnNpc(getNpc(831741));
				spawn(831598, 522.3982f, 564.6901f, 199.0337f, (byte) 60, 14);
				spawn(831744, 516.80f, 565.53f, 198.90f, (byte) 60);
				spawn(831746, 495.1534f, 567.7955f, 199.05292f, (byte) 0);
				SpawnBigChests();
				SpawnChests();
				break;
		}
        }
	}

	private void SpawnBigChests() {
		spawn(831575, 509.96f, 565.48f, 198.75f, (byte) 119);
		spawn(831575, 506.33f, 568.13f, 198.85f, (byte) 119);
		spawn(831575, 502.48f, 568.37f, 198.87f, (byte) 119);
		spawn(831575, 502.02f, 563.34f, 198.87f, (byte) 119);
		spawn(831575, 505.91f, 563.23f, 198.87f, (byte) 119);
		spawn(831575, 502.44f, 572.43f, 198.90f, (byte) 119);
	}

	private void SpawnChests() {
		spawn(831745, 515.24f, 578.78f, 198.74f, (byte) 60);
		spawn(831745, 517.85f, 576.96f, 198.63f, (byte) 60);
		spawn(831745, 518.06f, 580.44f, 198.72f, (byte) 60);
		spawn(831745, 524.86f, 584.65f, 198.95f, (byte) 60);
		spawn(831745, 527.76f, 586.25f, 199.15f, (byte) 60);
		spawn(831745, 527.91f, 582.85f, 198.80f, (byte) 60);
		spawn(831745, 532.62f, 578.71f, 198.75f, (byte) 60);
		spawn(831745, 535.73f, 579.88f, 198.85f, (byte) 60);
		spawn(831745, 535.46f, 577.02f, 198.75f, (byte) 60);
		spawn(831745, 512.36f, 553.06f, 198.87f, (byte) 60);
		spawn(831745, 515.26f, 554.71f, 198.75f, (byte) 60);
		spawn(831745, 515.05f, 551.01f, 198.87f, (byte) 60);
		spawn(831745, 520.91f, 545.35f, 198.83f, (byte) 60);
		spawn(831745, 523.51f, 546.70f, 198.84f, (byte) 60);
		spawn(831745, 523.29f, 543.62f, 198.83f, (byte) 60);
		spawn(831745, 529.84f, 549.56f, 198.75f, (byte) 60);
		spawn(831745, 532.90f, 551.15f, 198.85f, (byte) 60);
		spawn(831745, 532.95f, 548.21f, 198.87f, (byte) 60);
	}

	@Override
	public void onChangeStage(StageType type) {
         switch (type) {
			case START_STAGE_1_PHASE_1:
				stage = 1;
				// 1 Left wave 1
				sp(233149, 524.86f, 621.12f, 206.80f, (byte) 91, 10000, "1_left_301160000", false);
				sp(233149, 524.43f, 623.38f, 207.23f, (byte) 91, 10000, "2_left_301160000", false);
				sp(233149, 524.17f, 625.72f, 207.57f, (byte) 91, 10000, "3_left_301160000", false);
				// 1 Right wave 1
				sp(233149, 522.74f, 621.05f, 206.84f, (byte) 91, 10000, "1_Right_301160000", false);
				sp(233149, 522.38f, 623.29f, 207.11f, (byte) 91, 10000, "2_Right_301160000", false);
				sp(233149, 522.00f, 625.70f, 207.57f, (byte) 91, 10000, "3_Right_301160000", false);
				// 2 Left wave 1
				sp(233149, 520.84f, 502.01f, 198.75f, (byte) 30, 20000, "4_left_301160000", false);
				sp(233149, 520.79f, 499.26f, 198.58f, (byte) 30, 20000, "5_left_301160000", false);
				sp(233149, 520.51f, 496.55f, 198.47f, (byte) 30, 20000, "6_left_301160000", false);
				// 2 Right wave 1
				sp(233149, 523.31f, 501.45f, 198.78f, (byte) 30, 20000, "4_Right_301160000", false);
				sp(233149, 523.31f, 498.50f, 198.61f, (byte) 30, 20000, "5_Right_301160000", false);
				sp(233149, 523.31f, 495.82f, 198.46f, (byte) 30, 20000, "6_Right_301160000", false);

				// 1 Left wave 2
				sp(233144, 524.86f, 621.12f, 206.80f, (byte) 91, 40000, "1_left_301160000", false);
				sp(233152, 524.43f, 623.38f, 207.23f, (byte) 91, 40000, "2_left_301160000", false);
				// 1 Right wave 2
				sp(233144, 522.74f, 621.05f, 206.84f, (byte) 91, 40000, "1_Right_301160000", false);
				sp(233152, 522.38f, 623.29f, 207.11f, (byte) 91, 40000, "2_Right_301160000", false);
				// 2 Left wave 2
				sp(233144, 520.84f, 502.01f, 198.75f, (byte) 30, 50000, "4_left_301160000", false);
				sp(233152, 520.79f, 499.26f, 198.58f, (byte) 30, 50000, "5_left_301160000", false);
				// 2 Right wave 2
				sp(233144, 523.31f, 501.45f, 198.78f, (byte) 30, 50000, "4_Right_301160000", false);
				sp(233152, 523.31f, 498.50f, 198.61f, (byte) 30, 50000, "5_Right_301160000", false);

				// 1 Left wave 3
				sp(233144, 524.86f, 621.12f, 206.80f, (byte) 91, 70000, "1_left_301160000", false);
				sp(233152, 524.43f, 623.38f, 207.23f, (byte) 91, 70000, "2_left_301160000", false);
				// 1 Right wave 3
				sp(233144, 522.74f, 621.05f, 206.84f, (byte) 91, 70000, "1_Right_301160000", false);
				sp(233152, 522.38f, 623.29f, 207.11f, (byte) 91, 70000, "2_Right_301160000", false);
				// 2 Left wave 3
				sp(233144, 520.84f, 502.01f, 198.75f, (byte) 30, 80000, "4_left_301160000", false);
				sp(233152, 520.79f, 499.26f, 198.58f, (byte) 30, 80000, "5_left_301160000", false);
				// 2 Right wave 3
				sp(233144, 523.31f, 501.45f, 198.78f, (byte) 30, 80000, "4_Right_301160000", false);
				sp(233152, 523.31f, 498.50f, 198.61f, (byte) 30, 80000, "5_Right_301160000", false);

				// 1 Left wave 4
				sp(233149, 524.86f, 621.12f, 206.80f, (byte) 91, 130000, "1_left_301160000", false);
				sp(233149, 524.43f, 623.38f, 207.23f, (byte) 91, 130000, "2_left_301160000", false);
				// 1 Right wave 4
				sp(233149, 522.74f, 621.05f, 206.84f, (byte) 91, 130000, "1_Right_301160000", false);
				sp(233149, 522.38f, 623.29f, 207.11f, (byte) 91, 130000, "2_Right_301160000", false);
				// 2 Left wave 4
				sp(233149, 520.84f, 502.01f, 198.75f, (byte) 30, 140000, "4_left_301160000", false);
				sp(233149, 520.79f, 499.26f, 198.58f, (byte) 30, 140000, "5_left_301160000", false);
				// 2 Right wave 4
				sp(233149, 523.31f, 501.45f, 198.78f, (byte) 30, 140000, "4_Right_301160000", false);
				sp(233149, 523.31f, 498.50f, 198.61f, (byte) 30, 140000, "5_Right_301160000", false);

				// 1 Left wave 5
				sp(233149, 524.86f, 621.12f, 206.80f, (byte) 91, 160000, "1_left_301160000", false);
				sp(233149, 524.43f, 623.38f, 207.23f, (byte) 91, 160000, "2_left_301160000", false);
				sp(233149, 524.17f, 625.72f, 207.57f, (byte) 91, 160000, "3_left_301160000", false);
				// 1 Right wave 5
				sp(233149, 522.74f, 621.05f, 206.84f, (byte) 91, 160000, "1_Right_301160000", false);
				sp(233149, 522.38f, 623.29f, 207.11f, (byte) 91, 160000, "2_Right_301160000", false);
				sp(233149, 522.00f, 625.70f, 207.57f, (byte) 91, 160000, "3_Right_301160000", false);
				// 2 Left wave 5
				sp(233149, 520.84f, 502.01f, 198.75f, (byte) 30, 170000, "4_left_301160000", false);
				sp(233149, 520.79f, 499.26f, 198.58f, (byte) 30, 170000, "5_left_301160000", false);
				sp(233149, 520.51f, 496.55f, 198.47f, (byte) 30, 170000, "6_left_301160000", false);
				// 2 Right wave 5
				sp(233149, 523.31f, 501.45f, 198.78f, (byte) 30, 170000, "4_Right_301160000", false);
				sp(233149, 523.31f, 498.50f, 198.61f, (byte) 30, 170000, "5_Right_301160000", false);
				sp(233149, 523.31f, 495.82f, 198.46f, (byte) 30, 170000, "6_Right_301160000", false);

				// 1 Left wave 6
				sp(233144, 524.86f, 621.12f, 206.80f, (byte) 91, 190000, "1_left_301160000", false);
				sp(233144, 524.43f, 623.38f, 207.23f, (byte) 91, 190000, "2_left_301160000", false);
				sp(233152, 524.17f, 625.72f, 207.57f, (byte) 91, 190000, "3_left_301160000", false);
				// 1 Right wave 6
				sp(233144, 522.74f, 621.05f, 206.84f, (byte) 91, 190000, "1_Right_301160000", false);
				sp(233144, 522.38f, 623.29f, 207.11f, (byte) 91, 190000, "2_Right_301160000", false);
				sp(233152, 522.00f, 625.70f, 207.57f, (byte) 91, 190000, "3_Right_301160000", false);
				// 2 Left wave 6
				sp(233144, 520.84f, 502.01f, 198.75f, (byte) 30, 200000, "4_left_301160000", false);
				sp(233144, 520.79f, 499.26f, 198.58f, (byte) 30, 200000, "5_left_301160000", false);
				sp(233152, 520.51f, 496.55f, 198.47f, (byte) 30, 200000, "6_left_301160000", false);
				// 2 Right wave 6
				sp(233144, 523.31f, 501.45f, 198.78f, (byte) 30, 200000, "4_Right_301160000", false);
				sp(233144, 523.31f, 498.50f, 198.61f, (byte) 30, 200000, "5_Right_301160000", false);
				sp(233152, 523.31f, 495.82f, 198.46f, (byte) 30, 200000, "6_Right_301160000", false);

				// 1 Left wave 7
				sp(233144, 524.86f, 621.12f, 206.80f, (byte) 91, 220000, "1_left_301160000", false);
				sp(233144, 524.43f, 623.38f, 207.23f, (byte) 91, 220000, "2_left_301160000", false);
				sp(233152, 524.17f, 625.72f, 207.57f, (byte) 91, 220000, "3_left_301160000", false);
				// 1 Right wave 7
				sp(233149, 522.74f, 621.05f, 206.84f, (byte) 91, 220000, "1_Right_301160000", false);
				sp(233149, 522.38f, 623.29f, 207.11f, (byte) 91, 220000, "2_Right_301160000", false);
				sp(233149, 522.00f, 625.70f, 207.57f, (byte) 91, 220000, "3_Right_301160000", false);
				// 2 Left wave 7
				sp(233144, 520.84f, 502.01f, 198.75f, (byte) 30, 230000, "4_left_301160000", false);
				sp(233144, 520.79f, 499.26f, 198.58f, (byte) 30, 230000, "5_left_301160000", false);
				sp(233152, 520.51f, 496.55f, 198.47f, (byte) 30, 230000, "6_left_301160000", false);
				// 2 Right wave 7
				sp(233144, 523.31f, 501.45f, 198.78f, (byte) 30, 230000, "4_Right_301160000", false);
				sp(233144, 523.31f, 498.50f, 198.61f, (byte) 30, 230000, "5_Right_301160000", false);
				sp(233152, 523.31f, 495.82f, 198.46f, (byte) 30, 230000, "6_Right_301160000", false);

				// 1 Left wave 8
				sp(233149, 524.86f, 621.12f, 206.80f, (byte) 91, 250000, "1_left_301160000", false);
				sp(233149, 524.43f, 623.38f, 207.23f, (byte) 91, 250000, "2_left_301160000", false);
				// 1 Right wave 8
				sp(233149, 522.74f, 621.05f, 206.84f, (byte) 91, 250000, "1_Right_301160000", false);
				sp(233149, 522.38f, 623.29f, 207.11f, (byte) 91, 250000, "2_Right_301160000", false);
				// 2 Left wave 8
				sp(233149, 520.84f, 502.01f, 198.75f, (byte) 30, 260000, "4_left_301160000", false);
				sp(233149, 520.79f, 499.26f, 198.58f, (byte) 30, 260000, "5_left_301160000", false);
				// 2 Right wave 8
				sp(233149, 523.31f, 501.45f, 198.78f, (byte) 30, 260000, "4_Right_301160000", false);
				sp(233149, 523.31f, 498.50f, 198.61f, (byte) 30, 260000, "5_Right_301160000", false);

				// 1 Left wave 9
				sp(233149, 524.86f, 621.12f, 206.80f, (byte) 91, 280000, "1_left_301160000", false);
				sp(233149, 524.43f, 623.38f, 207.23f, (byte) 91, 280000, "2_left_301160000", false);
				// 1 Right wave 9
				sp(233149, 522.74f, 621.05f, 206.84f, (byte) 91, 280000, "1_Right_301160000", false);
				sp(233149, 522.38f, 623.29f, 207.11f, (byte) 91, 280000, "2_Right_301160000", false);
				// 2 Left wave 9
				sp(233149, 520.84f, 502.01f, 198.75f, (byte) 30, 290000, "4_left_301160000", false);
				sp(233149, 520.79f, 499.26f, 198.58f, (byte) 30, 290000, "5_left_301160000", false);
				// 2 Right wave 9
				sp(233149, 523.31f, 501.45f, 198.78f, (byte) 30, 290000, "4_Right_301160000", false);
				sp(233149, 523.31f, 498.50f, 198.61f, (byte) 30, 290000, "5_Right_301160000", false);

				// 1 Left wave 10
				sp(233144, 524.86f, 621.12f, 206.80f, (byte) 91, 310000, "1_left_301160000", false);
				sp(233144, 524.43f, 623.38f, 207.23f, (byte) 91, 310000, "2_left_301160000", false);
				// 1 Right wave 10
				sp(233144, 522.74f, 621.05f, 206.84f, (byte) 91, 310000, "1_Right_301160000", false);
				sp(233144, 522.38f, 623.29f, 207.11f, (byte) 91, 310000, "2_Right_301160000", false);
				// 2 Left wave 10
				sp(233144, 520.84f, 502.01f, 198.75f, (byte) 30, 320000, "4_left_301160000", false);
				sp(233144, 520.79f, 499.26f, 198.58f, (byte) 30, 320000, "5_left_301160000", false);
				// 2 Right wave 10
				sp(233144, 523.31f, 501.45f, 198.78f, (byte) 30, 320000, "4_Right_301160000", false);
				sp(233144, 523.31f, 498.50f, 198.61f, (byte) 30, 320000, "5_Right_301160000", false);

				// 1 Left wave 11
				sp(233144, 524.86f, 621.12f, 206.80f, (byte) 91, 340000, "1_left_301160000", false);
				sp(233144, 524.43f, 623.38f, 207.23f, (byte) 91, 340000, "2_left_301160000", false);
				sp(233152, 524.17f, 625.72f, 207.57f, (byte) 91, 340000, "3_left_301160000", false);
				// 1 Right wave 11
				sp(233144, 522.74f, 621.05f, 206.84f, (byte) 91, 340000, "1_Right_301160000", false);
				sp(233144, 522.38f, 623.29f, 207.11f, (byte) 91, 340000, "2_Right_301160000", false);
				sp(233152, 522.00f, 625.70f, 207.57f, (byte) 91, 340000, "3_Right_301160000", false);
				// 2 Left wave 11
				sp(233144, 520.84f, 502.01f, 198.75f, (byte) 30, 350000, "4_left_301160000", false);
				sp(233144, 520.79f, 499.26f, 198.58f, (byte) 30, 350000, "5_left_301160000", false);
				sp(233152, 520.51f, 496.55f, 198.47f, (byte) 30, 350000, "6_left_301160000", false);
				// 2 Right wave 11
				sp(233144, 523.31f, 501.45f, 198.78f, (byte) 30, 350000, "4_Right_301160000", false);
				sp(233144, 523.31f, 498.50f, 198.61f, (byte) 30, 350000, "5_Right_301160000", false);
				sp(233152, 523.31f, 495.82f, 198.46f, (byte) 30, 350000, "6_Right_301160000", false);

				// 1 Left wave 12
				sp(233149, 524.86f, 621.12f, 206.80f, (byte) 91, 370000, "1_left_301160000", false);
				sp(233149, 524.43f, 623.38f, 207.23f, (byte) 91, 370000, "2_left_301160000", false);
				sp(233149, 524.17f, 625.72f, 207.57f, (byte) 91, 370000, "3_left_301160000", false);
				// 1 Right wave 12
				sp(233149, 522.74f, 621.05f, 206.84f, (byte) 91, 370000, "1_Right_301160000", false);
				sp(233149, 522.38f, 623.29f, 207.11f, (byte) 91, 370000, "2_Right_301160000", false);
				sp(233149, 522.00f, 625.70f, 207.57f, (byte) 91, 370000, "3_Right_301160000", false);
				// 2 Left wave 12
				sp(233149, 520.84f, 502.01f, 198.75f, (byte) 30, 380000, "4_left_301160000", false);
				sp(233149, 520.79f, 499.26f, 198.58f, (byte) 30, 380000, "5_left_301160000", false);
				sp(233149, 520.51f, 496.55f, 198.47f, (byte) 30, 380000, "6_left_301160000", false);
				// 2 Right wave 12
				sp(233149, 523.31f, 501.45f, 198.78f, (byte) 30, 380000, "4_Right_301160000", false);
				sp(233149, 523.31f, 498.50f, 198.61f, (byte) 30, 380000, "5_Right_301160000", false);
				sp(233149, 523.31f, 495.82f, 198.46f, (byte) 30, 380000, "6_Right_301160000", false);

				// 1 Left wave 13
				sp(233149, 524.86f, 621.12f, 206.80f, (byte) 91, 400000, "1_left_301160000", false);
				sp(233149, 524.43f, 623.38f, 207.23f, (byte) 91, 400000, "2_left_301160000", false);
				sp(233149, 524.17f, 625.72f, 207.57f, (byte) 91, 400000, "3_left_301160000", false);
				// 1 Right wave 13
				sp(233149, 522.74f, 621.05f, 206.84f, (byte) 91, 400000, "1_Right_301160000", false);
				sp(233149, 522.38f, 623.29f, 207.11f, (byte) 91, 400000, "2_Right_301160000", false);
				sp(233149, 522.00f, 625.70f, 207.57f, (byte) 91, 400000, "3_Right_301160000", false);
				// 2 Left wave 13
				sp(233149, 520.84f, 502.01f, 198.75f, (byte) 30, 410000, "4_left_301160000", false);
				sp(233149, 520.79f, 499.26f, 198.58f, (byte) 30, 410000, "5_left_301160000", false);
				sp(233149, 520.51f, 496.55f, 198.47f, (byte) 30, 410000, "6_left_301160000", false);
				// 2 Right wave 13
				sp(233149, 523.31f, 501.45f, 198.78f, (byte) 30, 410000, "4_Right_301160000", false);
				sp(233149, 523.31f, 498.50f, 198.61f, (byte) 30, 410000, "5_Right_301160000", false);
				sp(233149, 523.31f, 495.82f, 198.46f, (byte) 30, 410000, "6_Right_301160000", false);
				cancelMessageTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						onChangeStage(StageType.START_STAGE_2_PHASE_1); // 2 Stage Start
					}
				}, 460000);
				break;
			// 2 Stage Phase 1 Start
			case START_STAGE_2_PHASE_1:
				isCancelled = false;
				stage = 2;
				sendMsg(1401797);
				sp(233153, 550.03f, 564.29f, 198.76f, (byte) 61);

                Vivarokatask = ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                            onChangeStage(StageType.START_STAGE_2_PHASE_2);
                    }
                }, 50000);

            // Spawns Vivaroka
				break;
			// 2 Stage Phase 2 Start
			case START_STAGE_2_PHASE_2:
				sendMsg(1401798);
				sp(233147, 550.03f, 564.29f, 198.76f, (byte) 61); // Spawns Makekike
                Makekiketask = ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                            onChangeStage(StageType.START_STAGE_3_PHASE_1);
                    }
                }, 95000);
                break;
			// 3 Stage Start
			case START_STAGE_3_PHASE_1:
				isCancelled = false;
				stage = 3;
				sendMsg(1401799);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						sp(233161, 550.03f, 564.29f, 198.76f, (byte) 61); // Spawns Rukibuki
					}
				}, 10000);

				break;
		}
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

	protected void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player,
			new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		PacketSendUtility.sendPacket(player, new SM_DIE(false, false, 0, 8));
		return true;
	}

	@Override
	public boolean onReviveEvent(Player player) {
		PlayerReviveService.revive(player, 25, 25, false, 0);
		player.getGameStats().updateStatsAndSpeedVisually();
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		TeleportService2.teleportTo(player, mapId, instanceId, 468.27f, 568.18f, 201.68f, (byte) 49);
		return true;
	}

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

	private void cancelTask() {
		if (cancelSpawnTask != null && !cancelSpawnTask.isCancelled()) {
			cancelSpawnTask.cancel(true);
		}
		if (cancelMessageTask != null && !cancelMessageTask.isCancelled()) {
			cancelMessageTask.cancel(true);
		}
        if (Vivarokatask != null && !Vivarokatask.isCancelled()) {
            Vivarokatask.cancel(true);
        }
        if (Makekiketask != null && !Makekiketask.isCancelled()) {
            Makekiketask.cancel(true);
        }
	}

	@Override
	public void onLeaveInstance(Player player) {
		removeEffects(player);
	}

	@Override
	public void onPlayerLogOut(Player player) {
        removeEffects(player);
		player.setTransformed(false);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, false));
	}

	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(19520);
		effectController.removeEffect(21332);
		effectController.removeEffect(21329);
	}

	@Override
	public void onInstanceDestroy() {
		cancelTask();
		isCancelled = true;
		isInstanceDestroyed = true;
		stage = 0;
	}

}
