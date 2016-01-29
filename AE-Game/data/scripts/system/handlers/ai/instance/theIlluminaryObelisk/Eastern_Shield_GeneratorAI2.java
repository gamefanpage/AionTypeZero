/**
 * This file is part of Aion Eternity Core <Ver:4.5>.
 *
 * Aion Eternity Core is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Aion Eternity Core is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Aion Eternity Core. If not, see <http:// www.gnu.org/licenses/>.
 *
 */
package ai.instance.theIlluminaryObelisk;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Rinzler
 * @rework Ever
 */
@AIName("eastern_shield_generator")
public class Eastern_Shield_GeneratorAI2 extends NpcAI2 {

    private boolean isInstanceDestroyed;
    private boolean isChargingE;

    @Override
    protected void handleSpawned() {
        isChargingE = false;
        super.handleSpawned();
    }

    @Override
    protected void handleDialogStart(Player player) {
        if (isChargingE) {
            PacketSendUtility.sendMessage(player, "Charging in progress");
        } else if (player.getInventory().getFirstItemByItemId(164000289) != null) {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
        } else {
            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402211));
        }
    }

    @Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
        if (dialogId == 10000 && player.getInventory().getItemCountByItemId(164000289) >= 3 && !isChargingE) {
            startCharging();
            startWaveEasternShieldGenerator1();
            player.getInventory().decreaseByItemId(164000289, 3);
            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402224));
            spawn(702014, 255.7926f, 338.22058f, 325.56473f, (byte) 0, 60);
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
        }
        if (dialogId == 10000 && player.getInventory().getItemCountByItemId(164000289) < 3) {
            PacketSendUtility.sendMessage(player, "You need 3 idium to start charging");
        }

        return true;
    }

    private void startWaveEasternShieldGenerator1() {
        sp(233724, 252.68709f, 333.483f, 325.59268f, (byte) 90, 1000, "EasternShieldGenerator1");
        sp(233725, 255.74022f, 333.2762f, 325.49332f, (byte) 90, 1000, "EasternShieldGenerator2");
        sp(233723, 258.72256f, 333.27713f, 325.58722f, (byte) 90, 1000, "EasternShieldGenerator3");
    }

    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final String walkerId) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    Npc npc = (Npc) spawn(npcId, x, y, z, h);
                    npc.getSpawn().setWalkerId(walkerId);
                    WalkManager.startWalking((NpcAI2) npc.getAi2());
                }
            }
        }, time);
    }

    public void startCharging() {
        isChargingE = true;
    }

    public void onInstanceDestroy() {
        isInstanceDestroyed = true;
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        isChargingE = false;
    }

    protected void handleDespawned() {
        super.handleDespawned();
        isChargingE = false;
    }
}
