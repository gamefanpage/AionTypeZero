package ai.instance.theRunadium;

import ai.AggressiveNpcAI2;
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
 * M.O.G. Devs Team , fix Romanz
 */
@AIName("damn_witch_graendal2")
public class DamnWitchGraendal2 extends AggressiveNpcAI2 {

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
        if (hpPercentage > 63 && percents.size() < 4) {
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
        Npc Illusion = getPosition().getWorldMapInstance().getNpc(284384);
        if (!isIllusionsSpawned) {
            if (Illusion == null) {
                isIllusionsSpawned = true;
                spawn(284384, 284.4135f, 262.8083f, 248.6746f, (byte) 63);
                spawn(284384, 232.5143f, 263.8524f, 248.5539f, (byte) 113);
                spawn(284384, 271.1393f, 230.5098f, 250.8564f, (byte) 44);
                spawn(284384, 240.2434f, 235.1515f, 251.0607f, (byte) 18);
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
