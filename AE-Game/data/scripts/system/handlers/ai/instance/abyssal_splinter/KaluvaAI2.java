package ai.instance.abyssal_splinter;

import ai.SummonerAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.manager.EmoteManager;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.ai.Percentage;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Luzien
 */
@AIName("kaluva")
public class KaluvaAI2 extends SummonerAI2 {
	private boolean canThink = true;

	@Override
	protected void handleIndividualSpawnedSummons(Percentage percent) {
		spawn();
		canThink = false;
		EmoteManager.emoteStopAttacking(getOwner());
		setStateIfNot(AIState.FOLLOWING);
		getOwner().setState(1);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
		AI2Actions.targetCreature(this, getPosition().getWorldMapInstance().getNpc(281902));
		getMoveController().moveToTargetObject();
	}

	@Override
	protected void handleMoveArrived() {
		if (canThink == false) {
			Npc egg = getPosition().getWorldMapInstance().getNpc(281902);
			if (egg != null) {
				SkillEngine.getInstance().getSkill(getOwner(), 19223, 55, egg).useNoAnimationSkill();
			}

			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run(){
					canThink = true;
					Creature creature = getAggroList().getMostHated();
					if (creature == null || !getOwner().canSee(creature) || NpcActions.isAlreadyDead(creature)) {
						setStateIfNot(AIState.FIGHT);
						think();
					}
					else {
						getOwner().setTarget(creature);
						getOwner().getGameStats().renewLastAttackTime();
						getOwner().getGameStats().renewLastAttackedTime();
						getOwner().getGameStats().renewLastChangeTargetTime();
						getOwner().getGameStats().renewLastSkillTime();
						setStateIfNot(AIState.FIGHT);
						think();
					}
				}
			}, 2000);
		}
		super.handleMoveArrived();
	}

	private void spawn() {
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(281902, 663.322021f, 556.731995f, 424.295013f, (byte) 64);
				break;
			case 2:
				spawn(281902, 644.0224f, 523.9641f, 423.09103f, (byte) 32);
				break;
			case 3:
				spawn(281902, 611.008f, 539.73395f, 423.25034f, (byte) 119);
				break;
			case 4:
				spawn(281902, 628.4426f, 585.4443f, 424.31854f, (byte) 93);
				break;
		}
	}

	@Override
	public boolean canThink() {
		return canThink;
	}

}
