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
@AIName("western_shield_generator")
public class Western_Shield_GeneratorAI2 extends NpcAI2 {

    private boolean isInstanceDestroyed;
    private boolean isChargingW;

    @Override
    protected void handleSpawned() {
        isChargingW = false;
        super.handleSpawned();
    }

        @Override
    protected void handleDialogStart(Player player) {
        if (isChargingW) {
            PacketSendUtility.sendMessage(player, "Charging in progress");
        } else if (player.getInventory().getFirstItemByItemId(164000289) != null) {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
        } else {
            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402211));
        }
    }

    @Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
        if (dialogId == 10000 && player.getInventory().getItemCountByItemId(164000289) >= 3 && !isChargingW) {
            startCharging();
            startWaveWesternShieldGenerator1();
            player.getInventory().decreaseByItemId(164000289, 3);
            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402225));
            spawn(702015, 255.7034f, 171.83853f, 325.81653f, (byte) 0, 18);
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
        }
        if (dialogId == 10000 && player.getInventory().getItemCountByItemId(164000289) < 3) {
            PacketSendUtility.sendMessage(player, "You need 3 idium to start charging");
        }

        return true;
    }

    private void startWaveWesternShieldGenerator1() {
        sp(233726, 258.37912f, 176.03621f, 325.59268f, (byte) 30, 1000, "WesternShieldGenerator1");
        sp(233727, 255.55922f, 176.17963f, 325.49332f, (byte) 29, 1000, "WesternShieldGenerator2");
        sp(233728, 252.49738f, 176.27466f, 325.52942f, (byte) 29, 1000, "WesternShieldGenerator3");
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
        isChargingW = true;
    }
    public void onInstanceDestroy() {
        isInstanceDestroyed = true;
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        isChargingW = false;
    }

    protected void handleDespawned() {
        super.handleDespawned();
        isChargingW = false;
    }
}
