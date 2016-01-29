package ai.worlds.kaldor;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.siege.SiegeNpc;
import org.typezero.gameserver.model.siege.SiegeLocation;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author Romanz
 */
@AIName("anoha_sword")
public class AnohaSwordAI2 extends NpcAI2 {



    @Override
    protected void handleDialogStart(Player player) {
        if(player.getLegion() != null) {
      int SiegeId = ((SiegeNpc) this.getOwner()).getSiegeId();
      SiegeLocation Location = SiegeService.getInstance().getSiegeLocation(SiegeId);
      if(Location != null) {
          if(Location.getLegionId()== player.getLegion().getLegionId()) {
             PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 10));
             return;
              }
          }
        }
       PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 1011));
        }

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        switch (dialogId) {
            case 10000:
				if (player.getInventory().getItemCountByItemId(185000215) >= 1) {
					player.getInventory().decreaseByItemId(185000215, 1);
					spawnBoss();
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 2375));
					AI2Actions.deleteOwner(this);
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402483));
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402584));
						}
					});
                }
				else {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
                }
                break;
        }
        return true;
    }

	private void spawnBoss() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				spawn(855263, 791.27985f, 489.02f, 142.9f, (byte) 0);
			}
		}, 30*60*1000);//30 min
	}
}
