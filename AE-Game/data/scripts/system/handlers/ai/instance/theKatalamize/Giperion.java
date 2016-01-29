package ai.instance.theKatalamize;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.WorldPosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

@AIName("hyperion")
public class Giperion extends AggressiveNpcAI2 {

    private AtomicBoolean isHome = new AtomicBoolean(true);
    private Future<?> skillTask;
    private Future<?> BlasterTask;
    private Future<?> EnergyTask;
    protected List<Integer> percents = new ArrayList<Integer>();

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isHome.compareAndSet(true, false)) {
            startSkillTask();
            startBlasterTask();
            startEnergyTask();
        }
        checkPercentage(getLifeStats().getHpPercentage());
    }

    private synchronized void checkPercentage(int hpPercentage) {
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 98:
                        AI2Actions.useSkill(this, 21241);
                        break;
                    case 95:
                        AI2Actions.useSkill(this, 21241);
						spawn_support();
						shout();
                        break;
                    case 80:
                        AI2Actions.useSkill(this, 21245);
                        spawnHyperionEasy();
						spawn_support();
						shout2();
                        break;
                    case 70:
                        AI2Actions.useSkill(this, 21242);
                        spawnHyperionNormal1();
						spawn_support();
						shout3();
                        break;
                    case 60:
                        AI2Actions.useSkill(this, 21243);
                        spawnHyperionNormal1();
						spawn_support();
                        break;
                    case 50:
                        AI2Actions.useSkill(this, 21244);
                        spawnHyperionHard();
						spawn_support();
						shout4();
                        break;
                    case 40:
                        AI2Actions.useSkill(this, 21244);
						spawn_support();
						shout5();
                        break;
                    case 30:
                        cancelEnergyTask();
                        AI2Actions.useSkill(this, 21248);
                        spawnHyperionHard();
                        break;
                    case 20:
                        AI2Actions.useSkill(this, 21253);
                        spawnHyperionNormal();
						spawn_support();
						shout2();
                        break;
                    case 10:
                        AI2Actions.useSkill(this, 21246);
                        spawnHyperionNormal();
						shout4();
                        break;
                    case 5:
                        AI2Actions.useSkill(this, 21249);
						shout_died();
                        break;
                }
                percents.remove(percent);
                break;
            }
        }
    }

	private void shout() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500808, getObjectId(), 0, 1000);
	}

	private void shout2() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500809, getObjectId(), 0, 1000);
	}

	private void shout3() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500810, getObjectId(), 0, 1000);
	}

	private void shout4() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500811, getObjectId(), 0, 1000);
	}

	private void shout5() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500812, getObjectId(), 0, 1000);
	}

	private void shout_died() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500818, getObjectId(), 0, 1000);
	}

	private void spawn_support() {
		float direction = Rnd.get(0, 199) / 100f;
		int distance = Rnd.get(0, 2);
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		WorldPosition p = getPosition();
	  spawn(231103, p.getX() + x1,  p.getY() + y1,  p.getZ(),  p.getHeading());
	}

    private void spawnHyperionEasy() {
        spawn(231096, 148.12894f, 148.34091f, 124.03375f, (byte) 105);
        spawn(233292, 108.5921f, 145.41702f, 114.03043f, (byte) 20);
        spawn(233289, 110.090965f, 128.28905f, 124.15179f, (byte) 43);
    }

    private void spawnHyperionNormal() {
        spawn(233288, 148.12894f, 148.34091f, 124.03375f, (byte) 105);
        spawn(233294, 108.5921f, 145.41702f, 114.03043f, (byte) 20);
        spawn(233296, 110.090965f, 128.28905f, 124.15179f, (byte) 43);
    }

    private void spawnHyperionNormal1() {
        spawn(233292, 148.12894f, 148.34091f, 124.03375f, (byte) 105);
        spawn(233294, 108.5921f, 145.41702f, 114.03043f, (byte) 20);
        spawn(233295, 150.05635f, 128.56758f, 114.49583f, (byte) 16);
        spawn(233295, 110.090965f, 128.28905f, 124.15179f, (byte) 43);
    }

    private void spawnHyperionHard() {
        spawn(233288, 148.12894f, 148.34091f, 124.03375f, (byte) 105);
        spawn(233299, 148.12894f, 148.34091f, 124.03375f, (byte) 105);
        spawn(233294, 108.5921f, 145.41702f, 114.03043f, (byte) 20);
        spawn(233298, 150.05635f, 128.56758f, 114.49583f, (byte) 16);
        spawn(233298, 110.090965f, 128.28905f, 124.15179f, (byte) 43);
    }

    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, new Integer[]{80, 75, 60, 55, 52, 50, 40, 35, 30, 25, 20, 10, 5, 2});
    }

    private void startSkillTask() {
        skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelskillTask();
                } else {
                    Throw();
                }
            }
        }, 30000, 120000);
    }

    private void startBlasterTask() {
        BlasterTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelBlasterTask();
                } else {
                    Blaster();
                }
            }
        }, 2000, 90000);
    }

    private void startEnergyTask() {
        EnergyTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelEnergyTask();
                } else {
                    Energy();
                }
            }
        }, 10000, 160000);
    }

    private void cancelskillTask() {
        if (skillTask != null && !skillTask.isCancelled()) {
            skillTask.cancel(true);
        }
    }

    private void cancelBlasterTask() {
        if (BlasterTask != null && !BlasterTask.isCancelled()) {
            BlasterTask.cancel(true);
        }
    }

    private void cancelEnergyTask() {
        if (EnergyTask != null && !EnergyTask.isCancelled()) {
            EnergyTask.cancel(true);
        }
    }

    private void Throw() {
        SkillEngine.getInstance().getSkill(getOwner(), 21250, 55, getOwner()).useNoAnimationSkill();
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                SkillEngine.getInstance().getSkill(getOwner(), Rnd.get(21251, 21252), 55, getOwner()).useNoAnimationSkill();
            }
        }, 5000);
    }

    private void Blaster() {
        SkillEngine.getInstance().getSkill(getOwner(), 21241, 60, getOwner().getTarget()).useNoAnimationSkill();
        Player target = getRandomTarget();
        if (target == null) {
        }
    }

    private void Energy() {
        SkillEngine.getInstance().getSkill(getOwner(), 21247, 60, getOwner().getTarget()).useNoAnimationSkill();
        Player target = getRandomTarget();
        if (target == null) {
        }
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        addPercent();
    }

    private void deleteNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            if (npc != null) {
                npc.getController().onDelete();
            }
        }
    }

    private void despawnAdds() {
        WorldMapInstance instance = getPosition().getWorldMapInstance();
        deleteNpcs(instance.getNpcs(231096));
        deleteNpcs(instance.getNpcs(233292));
        deleteNpcs(instance.getNpcs(231103));
        deleteNpcs(instance.getNpcs(233289));
        deleteNpcs(instance.getNpcs(233288));
        deleteNpcs(instance.getNpcs(233294));
        deleteNpcs(instance.getNpcs(233296));
        deleteNpcs(instance.getNpcs(233295));
        deleteNpcs(instance.getNpcs(233299));
        deleteNpcs(instance.getNpcs(233298));
        deleteNpcs(instance.getNpcs(231104));
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
        addPercent();
        cancelskillTask();
        cancelBlasterTask();
        cancelEnergyTask();
        isHome.set(true);
        despawnAdds();
    }

    @Override
    protected void handleDespawned() {
        super.handleDespawned();
        percents.clear();
        despawnAdds();
        cancelskillTask();
        cancelBlasterTask();
        cancelEnergyTask();
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        percents.clear();
        cancelskillTask();
        cancelBlasterTask();
        cancelEnergyTask();
        despawnAdds();
    }
}
