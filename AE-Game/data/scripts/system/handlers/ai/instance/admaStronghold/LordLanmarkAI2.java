package ai.instance.admaStronghold;

import ai.AggressiveNpcAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

@AIName("lordlanmark")
public class LordLanmarkAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isAlreadyDead()) {
					getOwner().getSpawn().setWalkerId("lordlanmark");
					WalkManager.startWalking((NpcAI2) getOwner().getAi2());
					PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
				}
			}
		}, 1000);
	}

}
