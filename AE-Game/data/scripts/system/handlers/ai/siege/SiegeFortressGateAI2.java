package ai.siege;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AI2Request;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.PositionUtil;

/**
 * @author Romanz
 */
@AIName("fortressgate")
public class SiegeFortressGateAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		AI2Actions.addRequest(this, player, 160017, 0, new AI2Request() {

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				if (MathUtil.isInRange(requester, responder, 10))
					TeleportService2.moveToTargetWithDistance(requester, responder,
						PositionUtil.isBehind(requester, responder) ? 0 : 1, 3);
				else
					PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_FAR_FROM_NPC);
			}
		});
	}

	@Override
	protected void handleDialogFinish(Player player) {
	}

	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}
}
