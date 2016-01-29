package ai.instance.shugoImperialTomb;

import static ch.lambdaj.Lambda.maxFrom;

import java.util.Collection;
import java.util.HashSet;

import ai.ActionItemNpcAI2;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.configs.main.GroupConfig;
import org.typezero.gameserver.model.ChatType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.network.aion.serverpackets.SM_MESSAGE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.drop.DropRegistrationService;
import org.typezero.gameserver.services.drop.DropService;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.audit.AuditLogger;

@AIName("shugo_relic")
public class ShugoRelicsAI2 extends ActionItemNpcAI2
{

	@Override
	protected void handleUseItemFinish(Player player)
	{
		int npcId = getOwner().getNpcId();
		if ((npcId == 831122 || npcId == 831123 || npcId == 831124) && (player.getInventory().decreaseByItemId(185000129, 1) || player.getInventory().decreaseByItemId(185000128, 1)))
		{
			analyzeOpening(player);
			return;
		}
		else if (npcId == 831373 && (player.getInventory().decreaseByItemId(185000129, 3) || player.getInventory().decreaseByItemId(185000128, 3)))
		{
			analyzeOpening(player);
			return;
		}
		else
		{
			if (npcId == 831373)
				PacketSendUtility.broadcastPacket(player,new SM_MESSAGE(player, "\u0427\u0442\u043e \u0431\u044b \u043e\u0442\u043a\u0440\u044b\u0442\u044c \u0441\u0443\u043d\u0434\u0443\u043a \u043d\u0443\u0436\u043d\u044b 3 \u043a\u043b\u044e\u0447\u0430.", ChatType.NORMAL), true);
			else
				PacketSendUtility.broadcastPacket(player,new SM_MESSAGE(player, "\u0427\u0442\u043e \u0431\u044b \u043e\u0442\u043a\u0440\u044b\u0442\u044c \u0441\u0443\u043d\u0434\u0443\u043a \u043d\u0443\u0436\u0435\u043d 1 \u043a\u043b\u044e\u0447.", ChatType.NORMAL), true);
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401587));
			return;
		}
	}

	private void analyzeOpening(Player player)
	{
		if (getOwner().isInState(CreatureState.DEAD))
		{
			AuditLogger.info(player, "Attempted multiple Chest looting!");
			return;
		}

		AI2Actions.dieSilently(this, player);
		Collection<Player> players = new HashSet<Player>();
		if (player.isInGroup2()) {
			for (Player member : player.getPlayerGroup2().getOnlineMembers())
			{
				if (MathUtil.isIn3dRange(member, getOwner(), GroupConfig.GROUP_MAX_DISTANCE))
				{
					players.add(member);
				}
			}
		}
		else if (player.isInAlliance2())
		{
			for (Player member : player.getPlayerAlliance2().getOnlineMembers())
			{
				if (MathUtil.isIn3dRange(member, getOwner(), GroupConfig.GROUP_MAX_DISTANCE))
				{
					players.add(member);
				}
			}
		}
		else
		{
			players.add(player);
		}
		DropRegistrationService.getInstance().registerDrop(getOwner(), player, maxFrom(players).getLevel(), players);
		DropService.getInstance().requestDropList(player, getObjectId());
	}
}
