package ai.siege.katalam.pradeth;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.siege.SiegeNpc;
import org.typezero.gameserver.model.siege.SiegeLocation;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import java.util.List;

/**
 * @author Romanz
 */
@AIName("pradeth_mercenary_elyos")
public class PradethMercenaryElyosAI2 extends NpcAI2 {

    protected int rewardDialogId = 5;
    protected int startingDialogId = 10;
    protected int questDialogId = 10;

    @Override
    protected void handleDialogStart(Player player) {
        checkDialog(player);
        if(player.getLegion() != null) {
      int SiegeId = ((SiegeNpc) this.getOwner()).getSiegeId();
      SiegeLocation Location = SiegeService.getInstance().getSiegeLocation(SiegeId);
      if(Location != null) {
          if(Location.getLegionId()== player.getLegion().getLegionId()) {
             PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 10));
             return;
              }
          }
        }
       PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 1011));
        }

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        QuestEnv env = new QuestEnv(getOwner(), player, questId, dialogId);
        env.setExtendedRewardIndex(extendedRewardIndex);
        checkEntryConditions(player, dialogId, questId);
        if (QuestEngine.getInstance().onDialog(env)) {
            return true;
        }
        return true;
    }

    private void checkDialog(Player player) {
        int npcId = getNpcId();
        List<Integer> relatedQuests = QuestEngine.getInstance().getQuestNpc(npcId).getOnTalkEvent();
        boolean playerHasQuest = false;
        boolean playerCanStartQuest = false;
        if (!relatedQuests.isEmpty()) {
            for (int questId : relatedQuests) {
                QuestState qs = player.getQuestStateList().getQuestState(questId);
                if (qs != null && (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.REWARD)) {
                    playerHasQuest = true;
                    break;
                }
                else if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
                    if (QuestService.checkStartConditions(new QuestEnv(getOwner(), player, questId, 0), true)) {
                        playerCanStartQuest = true;
                        continue;
                    }
                }
            }
        }

        if (playerHasQuest) {
            boolean isRewardStep = false;
            for (int questId : relatedQuests) {
                QuestState qs = player.getQuestStateList().getQuestState(questId);
                if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), rewardDialogId, questId));
                    isRewardStep = true;
                    break;
                }
            }
            if (!isRewardStep) {
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), questDialogId));
            }
        }
        else if (playerCanStartQuest) {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), startingDialogId));
        } else {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011, 0));
        }
    }

    private void checkEntryConditions(Player player, int dialogId, int questId) {
        if (dialogId == 10000) {
            if (player.getInventory().getItemCountByItemId(186000236) >= 12) {
                if ((getPosition().getWorldMapInstance().getNpc(273016) == null &&
                getPosition().getWorldMapInstance().getNpc(273046) == null &&
                getPosition().getWorldMapInstance().getNpc(273031)  == null) &&
                player.getInventory().decreaseByItemId(186000236, 12)) {
                spawn(273016, 2628.9766f, 2791.371f, 253.86087f, (byte) 61);  // Stalwart
                spawn(273016, 2611.1191f, 2772.6743f, 253.83484f, (byte) 30);  // Stalwart
                spawn(273016, 2689.5513f, 2598.0747f, 253.83484f, (byte) 119);  // Stalwart
                spawn(273016, 2705.4612f, 2614.9175f, 253.83865f, (byte) 90);  // Stalwart
                spawn(273046, 2575.6929f, 2610.3667f, 253.65619f, (byte) 75); // Archmagus
                spawn(273046, 2581.2778f, 2605.1582f, 253.65619f, (byte) 75); // Archmagus
                spawn(273046, 2739.7441f, 2775.4817f, 253.72882f, (byte) 15); // Archmagus
                spawn(273046, 2734.1306f, 2780.7148f, 253.72882f, (byte) 15); // Archmagus
                spawn(273031, 2717.3767f, 2738.2834f, 265.75882f, (byte) 33); // Sniper
                spawn(273031, 2698.919f, 2755.3105f, 265.75632f, (byte) 1); // Sniper
                spawn(273031, 2599.8613f, 2648.6938f, 265.75552f, (byte) 92); // Sniper
                spawn(273031, 2639.0042f, 2612.565f, 265.77414f, (byte) 59); // Sniper
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401870));
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401834));
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 2375));
                } else {
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401837));
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
                }
            } else {
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
            }
        }
        else if (dialogId == 10001) {
            if (player.getInventory().getItemCountByItemId(186000236) >= 20) {
                if ((getPosition().getWorldMapInstance().getNpc(273036) == null) &&
                player.getInventory().decreaseByItemId(186000236, 20)) {
                spawn(273036, 2667.2795f, 2592.56f, 268.32513f, (byte) 106); // Sniper
                spawn(273036, 2629.799f, 2770.936f, 265.8121f, (byte) 45); // Sniper
                spawn(273036, 2579.392f, 2672.4946f, 265.81476f, (byte) 75); // Sniper
                spawn(273036, 2621.6184f, 2632.5005f, 265.81476f, (byte) 76); // Sniper
                spawn(273036, 2736.454f, 2669.858f, 265.80414f, (byte) 106); // Sniper
                spawn(273036, 2775.1433f, 2654.893f, 262.82587f, (byte) 106); // Sniper
                spawn(273036, 2684.618f, 2557.8318f, 262.82587f, (byte) 106); // Sniper
                spawn(273036, 2627.3955f, 2578.351f, 262.82587f, (byte) 76); // Sniper
                spawn(273036, 2522.8257f, 2675.4504f, 262.82587f, (byte) 76); // Sniper
                spawn(273036, 2522.1343f, 2711.6804f, 262.82587f, (byte) 47); // Sniper
                spawn(273036, 2541.5078f, 2731.549f, 259.10098f, (byte) 41); // Sniper
                spawn(273036, 2632.497f, 2829.7847f, 262.82587f, (byte) 46); // Sniper
                spawn(273036, 2793.8599f, 2712.1128f, 262.82587f, (byte) 15); // Sniper
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401871));
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401835));
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 2375));
                } else {
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401837));
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
                }
            } else {
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
            }
        }
        else if (dialogId == 10002) {
            if (player.getInventory().getItemCountByItemId(186000236) >= 12) {
                if ((getPosition().getWorldMapInstance().getNpc(273314) == null) &&
                player.getInventory().decreaseByItemId(186000236, 12)) {
                spawn(273314, 2596.982f, 2737.708f, 266.19125f, (byte) 46); // Empty Aetheric Bombard
                spawn(273314, 2611.173f, 2752.241f, 266.19125f, (byte) 45); // Empty Aetheric Bombard
                spawn(273314, 2705.3672f, 2636.0967f, 266.20126f, (byte) 106); // Empty Aetheric Bombard
                spawn(273314, 2718.9968f, 2651.4675f, 266.20126f, (byte) 105); // Empty Aetheric Bombard
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401872));
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401836));
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
                } else {
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401837));
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 2375));
                }
            } else {
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
            }
        } else {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), dialogId, questId));
        }
    }
    public void deleteMobs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            if (npc != null) {
                npc.getController().onDelete();
            }
        }
    }

    @Override
    protected void handleDespawned() {
        WorldMapInstance instance = getPosition().getWorldMapInstance();
        deleteMobs(instance.getNpcs(273314));
        deleteMobs(instance.getNpcs(273036));
        deleteMobs(instance.getNpcs(273016));
        deleteMobs(instance.getNpcs(273046));
        deleteMobs(instance.getNpcs(273031));
        super.handleDespawned();
    }
}
