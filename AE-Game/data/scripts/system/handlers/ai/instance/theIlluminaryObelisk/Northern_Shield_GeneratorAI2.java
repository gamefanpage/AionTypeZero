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
@AIName("northern_shield_generator")
public class Northern_Shield_GeneratorAI2 extends NpcAI2 {

    private boolean isInstanceDestroyed;
    private boolean isChargingN;

    @Override
    protected void handleSpawned() {
        isChargingN = false;
        super.handleSpawned();
    }

    @Override
    protected void handleDialogStart(Player player) {
        if (isChargingN) {
            PacketSendUtility.sendMessage(player, "Charging in progress");
        } else if (player.getInventory().getFirstItemByItemId(164000289) != null) {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
        } else {
            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402211));
        }
    }

    @Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
        if (dialogId == 10000 && player.getInventory().getItemCountByItemId(164000289) >= 3 && !isChargingN) {
            startCharging();
            startWaveNorthernShieldGenerator1();
            player.getInventory().decreaseByItemId(164000289, 3);
            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402227));
            spawn(702017, 169.55626f, 254.52907f, 293.04276f, (byte) 0, 17);
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
        }
        if (dialogId == 10000 && player.getInventory().getItemCountByItemId(164000289) < 3) {
            PacketSendUtility.sendMessage(player, "You need 3 idium to start charging");
        }

        return true;
    }

    private void startWaveNorthernShieldGenerator1() {
        sp(233720, 174.50981f, 251.38982f, 292.43088f, (byte) 0, 1000, "NorthernShieldGenerator1");
        sp(233721, 174.9973f, 254.4739f, 292.3325f, (byte) 0, 1000, "NorthernShieldGenerator2");
        sp(233722, 174.84029f, 257.80832f, 292.4389f, (byte) 0, 1000, "NorthernShieldGenerator3");
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
        isChargingN = true;
    }
    public void onInstanceDestroy() {
        isInstanceDestroyed = true;
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        isChargingN = false;
    }

    protected void handleDespawned() {
        super.handleDespawned();
        isChargingN = false;
    }
}
