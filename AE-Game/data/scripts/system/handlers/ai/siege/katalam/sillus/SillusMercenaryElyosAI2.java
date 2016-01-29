package ai.siege.katalam.sillus;

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
@AIName("sillus_mercenary_elyos")
public class SillusMercenaryElyosAI2 extends NpcAI2 {

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
            if (player.getInventory().getItemCountByItemId(186000236) >= 16) {
                if ((getPosition().getWorldMapInstance().getNpc(272072) == null &&
                getPosition().getWorldMapInstance().getNpc(272064)  == null) &&
                player.getInventory().decreaseByItemId(186000236, 16)) {
                spawn(272072, 1998.688f, 1799.0376f, 331.733f, (byte) 90); // Archer
                spawn(272072, 2101.2119f, 1870.0081f, 331.733f, (byte) 105); // Archer
                spawn(272072, 2165.853f, 1855.36f, 315.46f, (byte) 30); // Archer
                spawn(272072, 2000.1692f, 1722.8898f, 318.268f, (byte) 0); // Archer
                spawn(272072, 2000.1825f, 1725.9467f, 318.268f, (byte) 0); // Archer
                spawn(272072, 2007.1035f, 1801.4924f, 331.733f, (byte) 90); // Archer
                spawn(272072, 2105.3423f, 1875.0712f, 331.733f, (byte) 105); // Archer
                spawn(272064, 2201.9319f, 1870.5378f, 288.83f, (byte) 105); // Fisher
                spawn(272064, 2199.9441f, 1868.1339f, 288.43f, (byte) 105); // Fisher
                spawn(272064, 2025.4611f, 1722.5242f, 307.8534f, (byte) 105); // Fisher
                spawn(272064, 2018.953f, 1722.464f, 307.645f, (byte) 105); // Fisher
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401864));
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401828));
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
            if (player.getInventory().getItemCountByItemId(186000236) >= 8) {
                if ((getPosition().getWorldMapInstance().getNpc(272086) == null) &&
                player.getInventory().decreaseByItemId(186000236, 8)) {
                spawn(272086, 2014.9697f, 1794.6371f, 331.7571f, (byte) 60); // Archmagus
                spawn(272086, 1995.3406f, 1790.2651f, 332.03f, (byte) 15); // Archmagus
                spawn(272086, 2115.437f, 1876.0839f, 332.05f, (byte) 80); // Archmagus
                spawn(272086, 2101.9697f, 1861.1672f, 332.03f, (byte) 30); // Archmagus
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401865));
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401829));
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
            if (player.getInventory().getItemCountByItemId(186000236) >= 16) {
                if ((getPosition().getWorldMapInstance().getNpc(272079) == null) &&
                player.getInventory().decreaseByItemId(186000236, 16)) {
                spawn(272079, 2001.835f, 1799.8464f, 331.733f, (byte) 60); // Sniper
                spawn(272079, 2161.5847f, 1875.2308f, 311.008f, (byte) 60); // Sniper
                spawn(272079, 2003.9126f, 1800.4058f, 331.733f, (byte) 60); // Sniper
                spawn(272079, 2162.4954f, 1878.149f, 311.008f, (byte) 60); // Sniper
                spawn(272079, 2103.9932f, 1873.4806f, 331.733f, (byte) 60); // Sniper
                spawn(272079, 2041.8623f, 1724.8845f, 318.548f, (byte) 60); // Sniper
                spawn(272079, 2042.1536f, 1721.9293f, 318.548f, (byte) 60); // Sniper
                spawn(272079, 2167.4968f, 1853.9686f, 315.46f, (byte) 15); // Sniper
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401866));
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401830));
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
        deleteMobs(instance.getNpcs(272064));
        deleteMobs(instance.getNpcs(272072));
        deleteMobs(instance.getNpcs(272079));
        deleteMobs(instance.getNpcs(272086));
        super.handleDespawned();
    }
}
