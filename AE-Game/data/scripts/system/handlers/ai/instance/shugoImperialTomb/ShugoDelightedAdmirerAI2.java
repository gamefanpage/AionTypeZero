package ai.instance.shugoImperialTomb;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;

@AIName("shugodelightedadmirer")
public class ShugoDelightedAdmirerAI2 extends NpcAI2
{

	@Override
	protected void handleDialogStart(Player player)
	{
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex)
	{
		int instanceId = player.getInstanceId();

		if (dialogId == 10001)
		{
			switch (getNpcId())
			{
				case 831114:
				case 831306:
					skillId = player.getRace() == Race.ASMODIANS ? 21104 : 21095;
					SkillEngine.getInstance().applyEffectDirectly(skillId, player, player, 0);
					break;
				case 831115:
				case 831195:
					skillId = player.getRace() == Race.ASMODIANS ? 21105 : 21096;
					SkillEngine.getInstance().applyEffectDirectly(skillId, player, player, 0);
					break;
			}
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1012));
		}
		else if (dialogId == 10000)
		{
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
			switch (getNpcId())
			{
				case 831114:
				case 831306:
					TeleportService2.teleportTo(player, 300560000, instanceId, 346.27332f, 424.07101f, 294.75793f, (byte) 90, TeleportAnimation.BEAM_ANIMATION);
					break;
				case 831115:
				case 831195:
					TeleportService2.teleportTo(player, 300560000, instanceId, 450.8527f, 105.94637f, 212.20023f, (byte) 90, TeleportAnimation.BEAM_ANIMATION);
					break;
			}
		}
		return true;
	}
}
