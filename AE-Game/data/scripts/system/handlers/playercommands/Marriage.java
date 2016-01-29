package playercommands;

import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_TOLL_INFO;
import org.typezero.gameserver.services.WeddingService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.PlayerCommand;
import org.typezero.gameserver.world.World;

/**
 * @author ViAl
 *
 */
public class Marriage extends PlayerCommand
{
    public String MarryName = "MarryName";

    public Marriage()
	{
		super("marriage");
	}

	@Override
	public void execute(final Player player, String... params)
	{
		String syntax = "marriage <on|off|heart>";
		if(params.length == 0)
		{
			PacketSendUtility.sendMessage(player, syntax);
			return;
		}
		try
		{
			if(!player.isMarried())
			{
				PacketSendUtility.sendMessage(player, "\u0412\u044b \u043d\u0435 \u0441\u043e\u0441\u0442\u043e\u0438\u0442\u0435 \u0432 \u0431\u0440\u0430\u043a\u0435.");
				return;
			}
            if(params.length == 1)
            {

			if(params[0].equals("on"))
				{
                        if (player.hasVar("MarryName"))
                        {
                            player.delVar(MarryName, true);
                        }
                            player.setVar(MarryName, "on", true);
						PacketSendUtility.broadcastPacketAndReceive(player, new SM_PLAYER_INFO(player, false));
                        player.clearKnownlist();
                        player.updateKnownlist();
				}
				if(params[0].equals("off"))
				{
                        if (player.hasVar("MarryName"))
                        {
                            player.delVar(MarryName, true);

                        }
                        player.setVar(MarryName,"off", true);
						PacketSendUtility.broadcastPacketAndReceive(player, new SM_PLAYER_INFO(player, false));
                        player.clearKnownlist();
                        player.updateKnownlist();
				}
				if (params[0].equals("heart"))
				{

                        if (player.hasVar("MarryName"))
                        {
                            player.delVar(MarryName, true);
                        }
                        player.setVar(MarryName, "heart", true);
                        player.getClientConnection().getAccount().setToll(player.getPlayerAccount().getToll()-1);
                        PacketSendUtility.sendPacket(player, new SM_TOLL_INFO(player.getPlayerAccount().getToll()));

                        PacketSendUtility.broadcastPacketAndReceive(player, new SM_PLAYER_INFO(player, false));
                        player.clearKnownlist();
                        player.updateKnownlist();

				}
            }
				else
				{
					PacketSendUtility.sendMessage(player, "\u0423\u043a\u0430\u0437\u0430\u043d \u043d\u0435 \u0432\u0435\u0440\u043d\u044b\u0439 \u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440.");
					return;
		}
        }
		catch(Exception e)
		{
			e.printStackTrace();
			PacketSendUtility.sendMessage(player, syntax);
			return;
		}

    }

}
