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
@AIName("2021_mercenary_asmo")
public class MercenaryAsmodians_2021AI2 extends NpcAI2 {

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
            if (player.getInventory().getItemCountByItemId(186000236) >= 11) {
                if ((getPosition().getWorldMapInstance().getNpc(252363) == null &&
                getPosition().getWorldMapInstance().getNpc(252365) == null &&
                getPosition().getWorldMapInstance().getNpc(252366) == null &&
                getPosition().getWorldMapInstance().getNpc(252367)  == null) &&
                player.getInventory().decreaseByItemId(186000236, 11)) {
                spawn(252363, 832.67f, 1860.01f, 334.01f, (byte) 80); // Fisher
                spawn(252363, 871.25f, 1947.14f, 338.61f, (byte) 80); // Fisher
                spawn(252363, 833.43f, 1813.07f, 330.12f, (byte) 60); // Fisher
                spawn(252365, 861.06f, 1861.91f, 331.29f, (byte) 60); // Wizard
                spawn(252365, 773.83f, 1923.53f, 333.26f, (byte) 60); // Wizard
                spawn(252366, 794.15f, 1899.71f, 331.29f, (byte) 100); // Priest
                spawn(252366, 892.72f, 1851.14f, 333.44f, (byte) 60); // Priest
                spawn(252367, 822.45f, 1978.29f, 337.39f, (byte) 100); // Assasin Hide
                spawn(252367, 795.47f, 1831.08f, 330.12f, (byte) 100); // Assasin Hide
                spawn(252367, 895.34f, 1886.82f, 333.25f, (byte) 80); // Assasin Hide
                spawn(252367, 921.14f, 1936.07f, 337.42f, (byte) 80); // Assasin Hide
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402319));
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402310));
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 2716));
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
        deleteMobs(instance.getNpcs(252363));
        deleteMobs(instance.getNpcs(252365));
        deleteMobs(instance.getNpcs(252366));
        deleteMobs(instance.getNpcs(252367));
        super.handleDespawned();
    }
}
