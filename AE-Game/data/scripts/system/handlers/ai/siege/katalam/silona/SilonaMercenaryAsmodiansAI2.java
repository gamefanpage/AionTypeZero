package ai.siege.katalam.silona;

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
@AIName("silona_mercenary_asmo")
public class SilonaMercenaryAsmodiansAI2 extends NpcAI2 {

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
            if (player.getInventory().getItemCountByItemId(186000236) >= 14) {
                if ((getPosition().getWorldMapInstance().getNpc(272656) == null) &&
                player.getInventory().decreaseByItemId(186000236, 14)) {
                spawn(272656, 1599.2748f, 910.92035f, 53.5f, (byte) 23); // Cannoneer
                spawn(272656, 1585.2769f, 921.29706f, 53.581066f, (byte) 18); // Cannoneer
                spawn(272656, 1535.5685f, 804.60486f, 93.432f, (byte) 30); // Cannoneer
                spawn(272656, 1512.3474f, 821.4114f, 93.44656f, (byte) 9); // Cannoneer
                spawn(272821, 1496.2777f, 901.2098f, 68.430786f, (byte) 42); // Mine
                spawn(272821, 1551.4484f, 903.64246f, 70.36379f, (byte) 8); // Mine
                spawn(272821, 1514.3641f, 863.8583f, 107.63131f, (byte) 92); // Mine
                spawn(272821, 1513.1644f, 919.59576f, 80.2405f, (byte) 36); // Mine
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401867));
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401831));
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
            if (player.getInventory().getItemCountByItemId(186000236) >= 10) {
                if ((getPosition().getWorldMapInstance().getNpc(272666) == null) &&
                player.getInventory().decreaseByItemId(186000236, 10)) {
                spawn(272666, 1639.0613f, 982.8706f, 55.894577f, (byte) 37); // Archmagus
                spawn(272666, 1634.7057f, 981.8072f, 55.5f, (byte) 35); // Archmagus
                spawn(272666, 1636.3159f, 984.11444f, 55.50544f, (byte) 35); // Archmagus
                spawn(272666, 1521.8398f, 920.99414f, 53.75f, (byte) 35); // Archmagus
                spawn(272666, 1516.6506f, 919.58185f, 53.51453f, (byte) 35); // Archmagus
                spawn(272666, 1518.8091f, 922.89966f, 53.675568f, (byte) 35); // Archmagus
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401868));
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401832));
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
            if (player.getInventory().getItemCountByItemId(186000236) >= 10) {
                if ((getPosition().getWorldMapInstance().getNpc(272616) == null) &&
                player.getInventory().decreaseByItemId(186000236, 10)) {
                spawn(272616, 1623.2288f, 1014.32184f, 54.903088f, (byte) 46); // Gunner
                spawn(272616, 1616.7952f, 1006.62463f, 55.38566f, (byte) 46); // Gunner
                spawn(272616, 1618.6987f, 1014.48865f, 55.05079f, (byte) 46); // Gunner
                spawn(272616, 1615.7753f, 1011.0701f, 55.183117f, (byte) 46); // Gunner
                spawn(272616, 1511.2913f, 953.0544f, 55.146603f, (byte) 55); // Gunner
                spawn(272616, 1483.1779f, 976.3054f, 52.570473f, (byte) 64); // Gunner
                spawn(272616, 1496.0297f, 906.3323f, 52.992584f, (byte) 41); // Gunner
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401869));
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401833));
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
        deleteMobs(instance.getNpcs(272656));
        deleteMobs(instance.getNpcs(272821));
        deleteMobs(instance.getNpcs(272666));
        deleteMobs(instance.getNpcs(272616));
        super.handleDespawned();
    }
}
