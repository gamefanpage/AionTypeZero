package ai.instance.theRunadiumHero;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  Romanz
 */
@AIName("damn_witch_graendal2_hero")
public class DamnWitchGraendal2Hero extends AggressiveNpcAI2 {

    private List<Integer> percents = new ArrayList<Integer>();
    private boolean isIllusionsSpawned;

    @Override
    protected void handleSpawned() {
        NpcShoutsService.getInstance().sendMsg(getOwner(), 1500739, getObjectId(), 0, 1000);
        addPercent();
        isIllusionsSpawned = false;
        super.handleSpawned();
    }

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    private void checkPercentage(int hpPercentage) {
        if (hpPercentage > 63 && percents.size() < 49) {
            addPercent();
        }
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 60:
                        shout1();
                        skill2();
                        spawn_storm();
                        break;
                    case 57:
                        skill2();
                        spawn_storm();
                        break;
                    case 54:
                        skill2();
                        spawn_storm();
                        break;
                    case 50:
                        skill3();
                        shout2();
                        degeneration_skill();
                        graendal_despawn();
                        if (!isIllusionsSpawned) {
                            shout_illusion();
                            spawn_illusion();
                        }
                        break;
                }
                percents.remove(percent);
                break;
            }
        }
    }

    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, new Integer[]{60, 57, 54, 50});
    }

    private void skill2() {
        VisibleObject target = getTarget();
        if (target != null && target instanceof Player) {
            SkillEngine.getInstance().getSkill(getOwner(), 21179, 65, target).useNoAnimationSkill();
        }
    }

    private void skill3() {
        VisibleObject target = getTarget();
        if (target != null && target instanceof Player) {
            SkillEngine.getInstance().getSkill(getOwner(), 21172, 65, target).useNoAnimationSkill();
        }
    }

    private void degeneration_skill() {
        VisibleObject target = getTarget();
        if (target != null && target instanceof Player) {
            SkillEngine.getInstance().getSkill(getOwner(), 21165, 65, target).useNoAnimationSkill();
        }
    }

    private void shout_illusion() {
        NpcShoutsService.getInstance().sendMsg(getOwner(), 1500742, getObjectId(), 0, 1000);
    }

    private void shout1() {
        NpcShoutsService.getInstance().sendMsg(getOwner(), 1500740, getObjectId(), 0, 1000);
    }

    private void shout2() {
        NpcShoutsService.getInstance().sendMsg(getOwner(), 1500739, getObjectId(), 0, 1000);
    }

    private void spawn_illusion() {
        Npc IllusionReal = getPosition().getWorldMapInstance().getNpc(855244);
        Npc IllusionFake = getPosition().getWorldMapInstance().getNpc(855247);
        if (!isIllusionsSpawned) {
            if (IllusionReal == null && IllusionFake == null) {
                isIllusionsSpawned = true;
		switch ((int)Rnd.get(1, 4)) {
			case 1:
                spawn(855244, 284.4135f, 262.8083f, 248.6746f, (byte) 63);
                spawn(855247, 232.5143f, 263.8524f, 248.5539f, (byte) 113);
                spawn(855248, 271.1393f, 230.5098f, 250.8564f, (byte) 44);
                spawn(855249, 240.2434f, 235.1515f, 251.0607f, (byte) 18);
				break;
			case 2:
                spawn(855247, 284.4135f, 262.8083f, 248.6746f, (byte) 63);
                spawn(855244, 232.5143f, 263.8524f, 248.5539f, (byte) 113);
                spawn(855248, 271.1393f, 230.5098f, 250.8564f, (byte) 44);
                spawn(855249, 240.2434f, 235.1515f, 251.0607f, (byte) 18);
				break;
			case 3:
                spawn(855247, 284.4135f, 262.8083f, 248.6746f, (byte) 63);
                spawn(855248, 232.5143f, 263.8524f, 248.5539f, (byte) 113);
                spawn(855244, 271.1393f, 230.5098f, 250.8564f, (byte) 44);
                spawn(855249, 240.2434f, 235.1515f, 251.0607f, (byte) 18);
				break;
			case 4:
                spawn(855247, 284.4135f, 262.8083f, 248.6746f, (byte) 63);
                spawn(855248, 232.5143f, 263.8524f, 248.5539f, (byte) 113);
                spawn(855249, 271.1393f, 230.5098f, 250.8564f, (byte) 44);
                spawn(855244, 240.2434f, 235.1515f, 251.0607f, (byte) 18);
				break;
				}
            }
        }
    }

    private void spawn_storm() {
        spawn(284385, 256.306f, 257.6652f, 241.77f, (byte) 0);
    }

    private void graendal_despawn() {
        AI2Actions.deleteOwner(this);
    }

    @Override
    protected void handleDespawned() {
        percents.clear();
        super.handleDespawned();
        isIllusionsSpawned = false;
    }

    @Override
    protected void handleDied() {
        percents.clear();
        super.handleDied();
        isIllusionsSpawned = false;
    }

    @Override
    protected void handleBackHome() {
        addPercent();
        super.handleBackHome();
    }

}
