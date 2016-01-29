package ai.worlds.gerha;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.handler.DiedEventHandler;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.controllers.attack.AggroInfo;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_ABYSS_RANK;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Romanz
 */
@AIName("god_race_protector")
public class GodRaceProtectorAI2 extends AggressiveNpcAI2 {

    @Override
    protected void handleDied() {
            for (AggroInfo damager : this.getAggroList().getList()) {
            if (damager.getAttacker() instanceof Player) {
                ((Player) damager.getAttacker()).getAbyssRank().addAGp(0,200);
                PacketSendUtility.sendPacket((Player) damager.getAttacker(), SM_SYSTEM_MESSAGE.STR_MSG_GLORY_POINT_GAIN(200));
                PacketSendUtility.sendPacket((Player) damager.getAttacker(), new SM_ABYSS_RANK(((Player) damager.getAttacker()).getAbyssRank()));
                }
            }
            DiedEventHandler.onDie(this);
			despawnNpc(235065);
        }

	private void despawnNpc(int npcId) {
		Npc npc = getPosition().getWorldMapInstance().getNpc(npcId);
		if (npc != null) {
			npc.getController().onDelete();
		}
	}

	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
				return AIAnswers.POSITIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			case SHOULD_REWARD:
				return AIAnswers.POSITIVE;
			default:
				return null;
		}
	}
}
