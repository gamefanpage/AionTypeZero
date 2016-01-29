package ai.siege;

import ai.ActionItemNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.handler.TalkEventHandler;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.siege.SiegeLocation;
import org.typezero.gameserver.model.siege.SiegeRace;
import org.typezero.gameserver.services.MuiService;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.utils.PacketSendUtility;

//By Evil_dnk
@AIName("fortress_portal")
public class FortressPortalsAI2 extends ActionItemNpcAI2 {


	@Override
	protected void handleDialogStart(Player player) {
        int SiegeId = 0;
        if (player != null){
            switch (getNpcId())    {
                case  801710:
                case  801711:
                case  801712:
                case  801713:
                case  801740:
                case  801741: {
                    SiegeId = 6011; //Базен
                    break;
                }
                case  801716:
                case  801717:
                case  801718:
                case  801719:
                case  801756:
                case  801757:{
                    SiegeId = 6021; //Парадес
                    break;
                }
                case  730658:
                case  730659:
                case  730660:
                case  730661:
                case  730666:
                case  730667:
                    SiegeId = 5011; //Силус
                    break;

            }
        }

        SiegeLocation Location = SiegeService.getInstance().getSiegeLocation(SiegeId);
        if (Location.getRace() == SiegeRace.BALAUR){
            PacketSendUtility.sendBrightYellowMessageOnCenter(player, MuiService.getInstance().getMessage("SIEGE_RACE"));
            return;
        }

        if(player.getRace().getRaceId() != Location.getRace().getRaceId() ){
            PacketSendUtility.sendBrightYellowMessageOnCenter(player, MuiService.getInstance().getMessage("SIEGE_RACE"));
            return;
        }

        if (SiegeService.getInstance().isSiegeInProgress(SiegeId)){
            PacketSendUtility.sendBrightYellowMessageOnCenter(player, MuiService.getInstance().getMessage("SIEGE_START"));
                return;
        }

        TalkEventHandler.onTalk(this, player);

    }

}
