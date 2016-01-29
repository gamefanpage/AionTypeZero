package ai.instance.idAsteriaIUParty;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Dision M.O.G. Devs Team
 */
@AIName("blessing_jester_light")
public class BlessingJesterLightAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		if (dialogId == 10000) {
			SkillEngine.getInstance().applyEffectDirectly(21331, player, player, 1200000 * 3);
			Despawn_Jester();
		}
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		return true;
	}

	private void Despawn_Jester() {

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				Npc npc = getOwner();
				NpcActions.delete(npc);
			}
		}, 5000);
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
	}
}
