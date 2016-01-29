package playercommands;

import org.typezero.gameserver.configs.main.RateConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.drop.Drop;
import org.typezero.gameserver.model.drop.DropGroup;
import org.typezero.gameserver.model.drop.NpcDrop;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.PlayerCommand;

/**
 * @author Romanz
 *
 */
public class DropList  extends PlayerCommand
{
    public DropList() { super("droplist"); }

    @Override
    public void execute(Player player, String... params) {
        NpcDrop npcDrop = null;
        if (params.length > 0) {
            int npcId = Integer.parseInt(params[0]);
            NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);
            if (npcTemplate == null){
                PacketSendUtility.sendMessage(player, "\u041d\u0435 \u0432\u0435\u0440\u043d\u043e\u0435 id: "+ npcId);
                return;
            }
            npcDrop = npcTemplate.getNpcDrop();
        }
        else {
            VisibleObject visibleObject = player.getTarget();

            if (visibleObject == null) {
                PacketSendUtility.sendMessage(player, "\u0412\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u0432\u044b\u0431\u0440\u0430\u0442\u044c \u0446\u0435\u043b\u044c !");
                return;
            }

            if (visibleObject instanceof Npc) {
                npcDrop = ((Npc)visibleObject).getNpcDrop();
            }
        }
        if (npcDrop == null){
            PacketSendUtility.sendMessage(player, "\u0412 \u0434\u0430\u043d\u043d\u043e\u043c \u041d\u041f\u0421 \u043e\u0442\u0441\u0443\u0442\u0441\u0442\u0432\u0443\u044e\u0442 \u0438\u0442\u0435\u043c\u044b (.");
            return;
        }

        int count = 0;
		PacketSendUtility.sendMessage(player, "\n[color:\u0412\u0430\u0448\u0438 ;1 0 0][color:\u0440\u0435\u0439\u0442\u044b;1 0 0][color::;1 0 0] x " + (player.getRates().getDropRate() + (player.getGameStats().getStat(StatEnum.DR_BOOST, 0).getCurrent() / 10f) + (player.getGameStats().getStat(StatEnum.BOOST_DROP_RATE, 0).getCurrent() / 10f)));
        PacketSendUtility.sendMessage(player, "[color:\u041d\u041f\u0421 \u0441;0 1 1][color:\u043e\u0434\u0435\u0440\u0436;0 1 1][color:\u0438\u0442 \u0441\u043b;0 1 1][color:\u0435\u0434\u0443\u044e\u0449;0 1 1][color:\u0438\u0435 \u0438\u0442;0 1 1][color:\u0435\u043c\u044b :;0 1 1] ");
        for (DropGroup dropGroup: npcDrop.getDropGroup()){
            PacketSendUtility.sendMessage(player, "\u0413\u0440\u0443\u043f\u043f\u0430 \u0438\u0442\u0435\u043c\u043e\u0432: "+ dropGroup.getGroupName());
            for (Drop drop : dropGroup.getDrop()){
                PacketSendUtility.sendMessage(player, "[item:" + drop.getItemId() + "]" + " [color:\u0428\u0430\u043d\u0441:;0 1 0] " + drop.getChance() * (player.getRates().getDropRate() + (player.getGameStats().getStat(StatEnum.DR_BOOST, 0).getCurrent() / 10f) + (player.getGameStats().getStat(StatEnum.BOOST_DROP_RATE, 0).getCurrent() / 10f)));
                count ++;
            }
        }
        PacketSendUtility.sendMessage(player, " [color:\u0412\u0441\u0435\u0433\u043e;0 1 0][color: :;0 1 0] " + count);
    }

    @Override
    public void onFail(Player player, String message) {
        // TODO Auto-generated method stub
    }
}


