package ai.instance.dragonLordsRefuge_Hard;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.actions.PlayerActions;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.utils.MathUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Romanz
 *
 */
@AIName("calindiflamelord65")
public class CalindiFlamelord_HardAI2 extends AggressiveNpcAI2 {

   private AtomicBoolean isHome = new AtomicBoolean(true);
   private Future<?> trapTask;
   private boolean isFinalBuff;

   @Override
   protected void handleAttack(Creature creature) {
	  super.handleAttack(creature);
	  if (isHome.compareAndSet(true, false))
		 startSkillTask();
	  if (!isFinalBuff) {
		 blazeEngraving();
		 if (getOwner().getLifeStats().getHpPercentage() <= 12) {
			isFinalBuff = true;
			cancelTask();
			AI2Actions.useSkill(this, 20915);
		 }
	  }
   }

   private void startSkillTask() {
	  trapTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
		 @Override
		 public void run() {
			if (isAlreadyDead())
			   cancelTask();
			else {
			   startHallucinatoryVictoryEvent();
			}
		 }
	  }, 5000, 80000);
   }

   private void cancelTask() {
	  if (trapTask != null && !trapTask.isCancelled()) {
		 trapTask.cancel(true);
	  }
   }

   private void startHallucinatoryVictoryEvent() {
	  if (getPosition().getWorldMapInstance().getNpc(731629) == null
			  && getPosition().getWorldMapInstance().getNpc(731630) == null) {
		 AI2Actions.useSkill(this, 20911);
		 SkillEngine.getInstance().applyEffectDirectly(20590, getOwner(), getOwner(), 0);
		 SkillEngine.getInstance().applyEffectDirectly(20591, getOwner(), getOwner(), 0);
		 spawn(731629, 482.21f, 458.06f, 427.42f, (byte) 98);
		 spawn(731630, 482.21f, 571.16f, 427.42f, (byte) 22);
		 rndSpawn(283132, 10);
	  }
   }

   private void blazeEngraving() {
	  if (Rnd.get(0, 100) < 2 && getPosition().getWorldMapInstance().getNpc(283130) == null) {
		 SkillEngine.getInstance().getSkill(getOwner(), 20913, 60, getOwner().getTarget()).useNoAnimationSkill();
		 Player target = getRandomTarget();
		 if (target == null)
			return;
		 spawn(283130, target.getX(), target.getY(), target.getZ(), (byte) 0);
	  }
   }

   private void rndSpawn(int npcId, int count) {
	  for (int i = 0; i < count; i++) {
		 SpawnTemplate template = rndSpawnInRange(npcId);
		 SpawnEngine.spawnObject(template, getPosition().getInstanceId());
	  }
   }

   private SpawnTemplate rndSpawnInRange(int npcId) {
	  float direction = Rnd.get(0, 199) / 100f;
	  int range = Rnd.get(5, 20);
	  float x1 = (float) (Math.cos(Math.PI * direction) * range);
	  float y1 = (float) (Math.sin(Math.PI * direction) * range);
	  return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x1, getPosition().getY()
			  + y1, getPosition().getZ(), getPosition().getHeading());
   }

   protected Player getRandomTarget() {
	  List<Player> players = new ArrayList<Player>();
	  for (Player player : getKnownList().getKnownPlayers().values()) {
		 if (!PlayerActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 50)) {
			players.add(player);
		 }
	  }

	  if (players.isEmpty())
		 return null;
	  return players.get(Rnd.get(players.size()));
   }

   @Override
   protected void handleDied() {
	  super.handleDied();
	  cancelTask();
   }

   @Override
   protected void handleDespawned() {
	  super.handleDespawned();
	  cancelTask();
   }

   @Override
   protected void handleBackHome() {
	  super.handleBackHome();
	  cancelTask();
	  isFinalBuff = false;
	  isHome.set(true);
   }
}
