package ai.instance.beshmundirTemple;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;


@AIName("theplaguebearer")
public class ThePlaguebearerAI2 extends AggressiveNpcAI2 {

    private boolean isStart = false;

    private void checkPercentage(int hpPercentage) {
        if (hpPercentage == 90) {
            isStart = true;
            summons();
        }
        if (hpPercentage == 70) {
            isStart = true;
            summons();
        }
        if (hpPercentage == 50) {
            isStart = true;
            summons();
        }
        if (hpPercentage == 30) {
            isStart = true;
            summons();
        }
    }

    private void summons() {
        if (getPosition().isSpawned() && !isAlreadyDead() && isStart) {
            for (int i = 0; i < 1; i++) {
                int distance = Rnd.get(4, 10);
                int nrNpc = Rnd.get(1, 2);
                switch (nrNpc) {
                    case 1:
                        nrNpc = 281808; //Plaguebearer Fragment.
                        break;
                    case 2:
                        nrNpc = 281809; //Plaguebearer Fragment.
                        break;
                }
                rndSpawnInRange(nrNpc, distance);
            }
        }
    }

    private void rndSpawnInRange(int npcId, float distance) {
        float direction = Rnd.get(0, 199) / 100f;
        float x1 = (float) (Math.cos(Math.PI * direction) * distance);
        float y1 = (float) (Math.sin(Math.PI * direction) * distance);
        spawn(npcId, getPosition().getX() + x1, getPosition().getY() + y1, getPosition().getZ(), (byte) 0);
    }

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    @Override
    protected void handleBackHome() {
        isStart = false;
        super.handleBackHome();
    }
}
