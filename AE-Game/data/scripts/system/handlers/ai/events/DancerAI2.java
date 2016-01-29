package ai.events;

import ai.GeneralNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.utils.PacketSendUtility;


/**
 * @author Alcapwnd
 *
 */
@AIName("dancer")
public class DancerAI2 extends GeneralNpcAI2 {

	@Override
	protected void handleSpawned() {
		switch(getNpcId()) {
			case 831633:
			case 831634:
			case 831635:
			case 831637:
			case 831638:
			case 831639:
				StartDancing1(this.getOwner());
				break;
			case 831640:
			case 831641:
			case 831642:
			case 831643:
			case 831644:
			case 831645:
			case 831646:
			case 831647:
				StartDancing2(this.getOwner());
				break;
			case 831648:
			case 831649:
			case 831650:
			case 831651:
			case 831652:
			case 831653:
				StartDancing3(this.getOwner());
				break;
			case 831617:
			case 831618:
				StartDancing4(this.getOwner());
				break;
		}
	}

    public static void StartDancing1(Npc owner) {
    	owner.unsetState(CreatureState.NPC_IDLE);
    	owner.setState(CreatureState.ACTIVE);
        PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.EMOTE, 134, 0));
    }

    public static void StartDancing2(Npc owner) {
    	owner.unsetState(CreatureState.NPC_IDLE);
    	owner.setState(CreatureState.ACTIVE);
        PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.EMOTE, 133, 0));
    }

    public static void StartDancing3(Npc owner) {
    	owner.unsetState(CreatureState.NPC_IDLE);
    	owner.setState(CreatureState.ACTIVE);
        PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.EMOTE, 142, 0));
    }

    public static void StartDancing4(Npc owner) {
    	owner.unsetState(CreatureState.NPC_IDLE);
    	owner.setState(CreatureState.ACTIVE);
        PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.EMOTE, 19, 0));
    }

	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			case SHOULD_REWARD:
				return AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}
}
