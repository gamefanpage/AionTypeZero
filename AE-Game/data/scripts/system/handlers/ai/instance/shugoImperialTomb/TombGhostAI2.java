package ai.instance.shugoImperialTomb;

import ai.AggressiveNpcAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.utils.PacketSendUtility;

@AIName("tombghost")
public class TombGhostAI2 extends AggressiveNpcAI2
{

	@Override
	public boolean canThink()
	{
		return false;
	}


	@Override
	protected void handleSpawned()
	{
		super.handleSpawned();
		getOwner().setState(1);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0,
			getObjectId()));
	}

}
