package ai.instance.unstableSplinterpath;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.manager.EmoteManager;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Luzien
 * @edit Cheatkiller
 */
@AIName("unstablekaluva")
public class UnstableKaluvaAI2 extends AggressiveNpcAI2 {
	private boolean canThink = true;
	private boolean isInMove = false;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (Rnd.get(0, 100) < 3) {
			if (!isInMove) {
				isInMove = true;
				moveToSpawner(randomEgg());
			}
		}
	}


	private void moveToSpawner(int egg) {
		Npc spawner = getPosition().getWorldMapInstance().getNpc(egg);
		if (spawner != null) {
			SkillEngine.getInstance().getSkill(getOwner(), 19152, 55, getOwner()).useNoAnimationSkill();
			canThink = false;
			EmoteManager.emoteStopAttacking(getOwner());
			setStateIfNot(AIState.FOLLOWING);
			getOwner().setState(1);
			PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
			AI2Actions.targetCreature(this, getPosition().getWorldMapInstance().getNpc(egg));
			getMoveController().moveToTargetObject();
		}
	}

	@Override
	protected void handleMoveArrived() {
		if (canThink == false) {
			if (getOwner().getTarget() instanceof Npc) {
				Npc spawner = (Npc) getOwner().getTarget();
				if (spawner != null) {
					spawner.getEffectController().removeEffect(19222);
					SkillEngine.getInstance().getSkill(getOwner(), 19223, 55, spawner).useNoAnimationSkill();
					getEffectController().removeEffect(19152);
				}
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
			isInMove = false;
		}
		super.handleMoveArrived();
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		isInMove = false;
	}

	private int randomEgg() {
		int [] npcId = {219583, 219582, 219564, 219581};
		return npcId [Rnd.get(0, npcId.length -1)];
	}

	@Override
	public boolean canThink() {
		return canThink;
	}

}
