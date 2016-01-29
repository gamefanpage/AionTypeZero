package ai.siege.balaurea.inggison;

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
@AIName("2021_mercenary_ely2")
public class MercenaryElyos2_2021AI2 extends NpcAI2 {

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
            if (player.getInventory().getItemCountByItemId(186000236) >= 6) {
                if ((getPosition().getWorldMapInstance().getNpc(252359) == null) &&
                player.getInventory().decreaseByItemId(186000236, 6)) {
                spawn(252359, 817.61f, 1927.62f, 346.09f, (byte) 80); // Ranger
                spawn(252359, 795.53f, 1870.39f, 343.5f, (byte) 15); // Ranger
                spawn(252359, 795.19f, 1908.57f, 343.1f, (byte) 80); // Ranger
                spawn(252359, 861.67f, 1839.65f, 343.58f, (byte) 30); // Ranger
                spawn(252359, 860.73f, 1880.82f, 345.13f, (byte) 80); // Ranger
                spawn(252359, 896.5f, 1892.48f, 345.95f, (byte) 80); // Ranger
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402323));
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402314));
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 2375));
                } else {
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401837));
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
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
        deleteMobs(instance.getNpcs(252359));
        super.handleDespawned();
    }
}
