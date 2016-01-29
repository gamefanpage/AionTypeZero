package ai.siege.katalam;

import ai.ActionItemNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;

@AIName("mine_controller_parades")
public class MineControllerParadesAI2 extends ActionItemNpcAI2 {

	private Race spawnRace;

	@Override
	public void handleUseItemFinish(Player player) {
		if (spawnRace == null) {
			spawnRace = player.getRace();

			switch (getNpcId()) {
				case 701705:
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401694));
					MineSpawn1();
					break;
				case 701706:
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401695));
					MineSpawn2();
					break;
				case 701707:
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401696));
					MineSpawn3();
					break;
				case 701708:
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401697));
					MineSpawn4();
					break;
			}
		}
		AI2Actions.deleteOwner(this);
	}

	private void MineSpawn1() {

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				// Spawn Mine
				final int mine1 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine2 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine3 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine4 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine5 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine6 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine7 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine8 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine9 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine10 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine11 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine12 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine13 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine14 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine15 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				spawn(mine1, 2570.863f, 2773.0264f, 252.875f, (byte) 0);
				spawn(mine2, 2554.0864f, 2759.5188f, 252.875f, (byte) 0);
				spawn(mine3, 2536.9104f, 2748.7827f, 252.875f, (byte) 0);
				spawn(mine4, 2538.2773f, 2739.1667f, 252.875f, (byte) 0);
				spawn(mine5, 2531.8296f, 2727.1538f, 252.875f, (byte) 0);
				spawn(mine6, 2517.5044f, 2728.258f, 252.875f, (byte) 0);
				spawn(mine7, 2502.9688f, 2710.7852f, 252.875f, (byte) 0);
				spawn(mine8, 2493.4626f, 2690.8235f, 252.875f, (byte) 0);
				spawn(mine9, 2504.2659f, 2677.0032f, 252.875f, (byte) 0);
				spawn(mine10, 2508.1863f, 2664.2324f, 252.875f, (byte) 0);
				spawn(mine11, 2521.8843f, 2655.8398f, 252.875f, (byte) 0);
				spawn(mine12, 2530.1567f, 2662.5295f, 252.875f, (byte) 0);
				spawn(mine13, 2540.0867f, 2647.7725f, 252.875f, (byte) 0);
				spawn(mine14, 2547.0059f, 2642.3955f, 252.875f, (byte) 0);
				spawn(mine15, 2554.3787f, 2632.6968f, 252.875f, (byte) 0);
			}
		}, 1000);
	}

	private void MineSpawn2() {

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				// Spawn Mine
				final int mine1 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine2 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine3 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine4 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine5 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine6 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine7 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine8 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine9 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine10 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine11 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine12 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine13 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine14 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine15 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				spawn(mine1, 2593.8743f, 2585.323f, 252.875f, (byte) 0);
				spawn(mine2, 2609.5137f, 2582.4124f, 252.875f, (byte) 0);
				spawn(mine3, 2613.0505f, 2565.1267f, 252.875f, (byte) 0);
				spawn(mine4, 2631.1482f, 2567.4473f, 252.875f, (byte) 0);
				spawn(mine5, 2633.5422f, 2549.674f, 252.875f, (byte) 0);
				spawn(mine6, 2649.4163f, 2538.8484f, 252.875f, (byte) 0);
				spawn(mine7, 2668.13f, 2529.3167f, 252.875f, (byte) 0);
				spawn(mine8, 2680.1084f, 2536.061f, 252.875f, (byte) 0);
				spawn(mine9, 2684.6047f, 2547.855f, 252.875f, (byte) 0);
				spawn(mine10, 2692.7847f, 2549.1062f, 252.875f, (byte) 0);
				spawn(mine11, 2700.1973f, 2553.2407f, 252.875f, (byte) 0);
				spawn(mine12, 2713.4214f, 2580.187f, 252.875f, (byte) 0);
				spawn(mine13, 2719.001f, 2574.0356f, 252.875f, (byte) 0);
				spawn(mine14, 2723.537f, 2589.6692f, 252.875f, (byte) 0);
				spawn(mine15, 2733.814f, 2599.2612f, 252.875f, (byte) 0);
			}
		}, 1000);
	}

	private void MineSpawn3() {

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				// Spawn Mine
				final int mine1 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine2 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine3 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine4 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine5 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine6 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine7 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine8 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine9 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine10 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine11 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine12 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine13 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine14 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine15 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				spawn(mine1, 2752.1206f, 2620.098f, 252.875f, (byte) 0);
				spawn(mine2, 2763.7874f, 2624.0552f, 252.875f, (byte) 0);
				spawn(mine3, 2766.7512f, 2640.1523f, 252.875f, (byte) 0);
				spawn(mine4, 2774.932f, 2644.6814f, 252.875f, (byte) 0);
				spawn(mine5, 2781.675f, 2648.105f, 252.875f, (byte) 0);
				spawn(mine6, 2798.1133f, 2663.308f, 252.875f, (byte) 0);
				spawn(mine7, 2807.3113f, 2675.4294f, 252.875f, (byte) 0);
				spawn(mine8, 2817.5774f, 2681.7166f, 252.875f, (byte) 0);
				spawn(mine9, 2825.052f, 2688.329f, 252.875f, (byte) 0);
				spawn(mine10, 2818.9836f, 2706.6418f, 252.875f, (byte) 0);
				spawn(mine11, 2806.5957f, 2712.042f, 252.875f, (byte) 0);
				spawn(mine12, 2797.7886f, 2721.6516f, 252.875f, (byte) 0);
				spawn(mine13, 2778.3984f, 2739.8289f, 252.875f, (byte) 0);
				spawn(mine14, 2766.7625f, 2744.3071f, 252.875f, (byte) 0);
				spawn(mine15, 2757.6938f, 2754.249f, 252.875f, (byte) 0);
			}
		}, 1000);
	}

	private void MineSpawn4() {

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				// Spawn Mine
				final int mine1 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine2 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine3 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine4 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine5 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine6 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine7 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine8 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine9 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine10 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine11 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine12 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine13 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine14 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				final int mine15 = spawnRace == Race.ASMODIANS ? 701722 : 701721;
				spawn(mine1, 2724.1543f, 2792.4712f, 252.875f, (byte) 0);
				spawn(mine2, 2706.8271f, 2800.5752f, 252.875f, (byte) 0);
				spawn(mine3, 2697.212f, 2817.7178f, 252.875f, (byte) 0);
				spawn(mine4, 2681.9907f, 2825.264f, 252.875f, (byte) 0);
				spawn(mine5, 2671.3225f, 2838.5657f, 252.875f, (byte) 0);
				spawn(mine6, 2660.2576f, 2851.9377f, 252.875f, (byte) 0);
				spawn(mine7, 2639.6152f, 2855.8376f, 252.875f, (byte) 0);
				spawn(mine8, 2631.0688f, 2842.3955f, 252.875f, (byte) 0);
				spawn(mine9, 2623.2888f, 2827.1162f, 252.875f, (byte) 0);
				spawn(mine10, 2614.42f, 2815.0767f, 252.875f, (byte) 0);
				spawn(mine11, 2597.4668f, 2811.5986f, 252.875f, (byte) 0);
				spawn(mine12, 2594.7466f, 2798.7458f, 252.875f, (byte) 0);
				spawn(mine13, 2585.8684f, 2791.7327f, 252.875f, (byte) 0);
				spawn(mine14, 2582.949f, 2781.8704f, 252.875f, (byte) 0);
				spawn(mine15, 2571.5574f, 2774.686f, 252.875f, (byte) 0);
			}
		}, 1000);
	}

}
