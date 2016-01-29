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
@AIName("southern_shield_generator")
public class Southern_Shield_GeneratorAI2 extends NpcAI2 {

    private boolean isInstanceDestroyed;
    private boolean isChargingS;

    @Override
    protected void handleSpawned() {
        isChargingS = false;
        super.handleSpawned();
    }

    @Override
    protected void handleDialogStart(Player player) {
            if (isChargingS) {
            PacketSendUtility.sendMessage(player, "Charging in progress");
        } else if (player.getInventory().getFirstItemByItemId(164000289) != null) {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
        } else {
            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402211));
        }
    }

    @Override
    public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
        if (dialogId == 10000 && player.getInventory().getItemCountByItemId(164000289) >= 3 && !isChargingS) {
            startCharging();
            startWaveSouthernShieldGenerator1();
            player.getInventory().decreaseByItemId(164000289, 3);
            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402226));
            spawn(702016, 343.12021f, 254.10585f, 291.62302f, (byte) 0, 34);
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
        }
        if (dialogId == 10000 && player.getInventory().getItemCountByItemId(164000289) < 3) {
            PacketSendUtility.sendMessage(player, "You need 3 idium to start charging");
        }

        return true;
    }

    private void startWaveSouthernShieldGenerator1() {
        sp(233729, 337.93338f, 257.88702f, 292.43845f, (byte) 60, 1000, "SouthernShieldGenerator1");
        sp(233730, 338.05304f, 254.6424f, 292.3325f, (byte) 60, 1000, "SouthernShieldGenerator2");
        sp(233731, 338.13315f, 251.34738f, 292.48932f, (byte) 59, 1000, "SouthernShieldGenerator3");
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
        isChargingS = true;
    }

    public void onInstanceDestroy() {
        isInstanceDestroyed = true;
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        isChargingS = false;
    }

    protected void handleDespawned() {
        super.handleDespawned();
        isChargingS = false;
    }
}
