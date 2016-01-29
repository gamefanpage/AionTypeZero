package ai.instance.voidCube;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.world.WorldMapInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//By Evil_dnk

@AIName("barukan")
public class barukanAI2 extends AggressiveNpcAI2 {
    protected List<Integer> percents = new ArrayList<Integer>();

    private AtomicBoolean isHome = new AtomicBoolean(true);
    private boolean canThink = true;

    @Override
    public boolean canThink() {
        return canThink;
    }

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());

    }

    private synchronized void checkPercentage(int hpPercentage) {
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 50:

                        Npc npc = getOwner();
                        float direction = Rnd.get(0, 199) / 100f;
                        int distance = Rnd.get(1, 3);
                        float x1 = (float) (Math.cos(Math.PI * direction) * distance);
                        float y1 = (float) (Math.sin(Math.PI * direction) * distance);
                        spawn(230092, npc.getX() + x1, npc.getY() + y1, npc.getZ(), (byte) 0);
                        spawn(230092, npc.getX() + y1, npc.getY() + x1, npc.getZ(), (byte) 0);

            }
            percents.remove(percent);
            break;
            }
        }
    }

    private void despawnAdds() {
        WorldMapInstance instance = getPosition().getWorldMapInstance();
        deleteNpcs(instance.getNpcs(230092));
    }

    private void deleteNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            if (npc != null) {
                npc.getController().onDelete();
            }
        }
    }

    @Override
    protected void handleDied() {
        percents.clear();
        despawnAdds();
        super.handleDied();
    }
    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, new Integer[]{50});
    }

    @Override
    protected void handleBackHome() {
        addPercent();
        super.handleBackHome();
        despawnAdds();
        isHome.set(true);
    }

}
