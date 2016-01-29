package ai.instance.elementisForest;

import ai.SummonerAI2;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;

/**
 * @author Romanz
 */
@AIName("jurdin")
public class JurdinTheCursedAI2 extends SummonerAI2 {

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (Rnd.get(1, 100) < 3) {
			spawnJurdinShadow();
			spawnSourceOfNightmares();
			spawnSparkys();
		}
	}

	private void spawnJurdinShadow(){
		Npc JurdinShadow = getPosition().getWorldMapInstance().getNpc(282201);
		if (JurdinShadow == null){
			spawn(282201, getOwner().getX() + 5, getOwner().getY() - 5, getOwner().getZ() + 2, (byte) 98);
			spawn(282201, getOwner().getX() + 10, getOwner().getY() + 10, getOwner().getZ() + 2, (byte) 98);
			spawn(282201, getOwner().getX() + 2, getOwner().getY() +2, getOwner().getZ() + 2, (byte) 98);
			spawn(282201, getOwner().getX() + 5, getOwner().getY() - 7, getOwner().getZ() + 2, (byte) 98);

		}
	}

	private void spawnSourceOfNightmares(){
		Npc SourceOfNightmares = getPosition().getWorldMapInstance().getNpc(282440);
		if (SourceOfNightmares == null){
		spawn(282440, 475.93594f, 820.456f, 131.44f, (byte) 14);
		spawn(282440, 486.7738f, 810.451f, 130.979f, (byte) 100);
		spawn(282440, 489.2139f, 794.9857f, 130.06161f, (byte) 80);
		spawn(282440, 478.87366f, 781.4378f, 129.4f, (byte) 67);
		spawn(282440, 463.21036f, 778.285f, 129.88025f, (byte) 53);
		spawn(282440, 448.5734f, 785.9875f, 131.644f, (byte) 42);
		spawn(282440, 447.2025f, 796.51654f, 131.9641f, (byte) 117);
		}
	}

	private void spawnSparkys(){
		Npc Sparkyexplode = getPosition().getWorldMapInstance().getNpc(282248);
		Npc Sparkyboss = getPosition().getWorldMapInstance().getNpc(282249);
		if (Sparkyexplode == null && Sparkyboss == null && Rnd.get(1, 100) < 1 ){
			spawn(282190, getOwner().getX() + 5, getOwner().getY() - 5, getOwner().getZ() + 2, (byte) 98);
			spawn(282190, getOwner().getX() + 10, getOwner().getY() + 10, getOwner().getZ() + 2, (byte) 98);
			spawn(282190, getOwner().getX() + 2, getOwner().getY() +2, getOwner().getZ() + 2, (byte) 98);
			spawn(282190, getOwner().getX() + 5, getOwner().getY() - 7, getOwner().getZ() + 2, (byte) 98);
			spawn(282190, getOwner().getX() -5, getOwner().getY() - 5, getOwner().getZ() + 2, (byte) 98);
			spawn(282190, getOwner().getX() -6, getOwner().getY() + 10, getOwner().getZ() + 2, (byte) 98);
			spawn(282190, getOwner().getX() + 4, getOwner().getY() +6, getOwner().getZ() + 2, (byte) 98);
			spawn(282190, getOwner().getX() + 2, getOwner().getY() - 8, getOwner().getZ() + 2, (byte) 98);
			AI2Actions.useSkill(this, 19454);
		}
	}

}
