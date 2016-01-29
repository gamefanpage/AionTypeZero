package ai.instance.varuna_lab;

import ai.ActionItemNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.player.Player;


/**
 * @author Romanz
 */
@AIName("varuna_door")
public class Varuna_DoorAI2 extends ActionItemNpcAI2 {

	@Override
	protected void handleUseItemFinish(Player player) {
        AI2Actions.dieSilently(this, player);
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
		delete();
            }
        }, 500);
    }
        	private void delete() {
                AI2Actions.deleteOwner(this);
	}
}
