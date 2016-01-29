package ai.siege.kaldor;

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
@AIName("7011_mercenary_asmo2")
public class MercenaryAsmodians_7011_2_AI2 extends NpcAI2 {

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
            if (player.getInventory().getItemCountByItemId(186000236) >= 29) {
                if ((getPosition().getWorldMapInstance().getNpc(252159) == null &&
                getPosition().getWorldMapInstance().getNpc(252160) == null &&
                getPosition().getWorldMapInstance().getNpc(252161) == null &&
                getPosition().getWorldMapInstance().getNpc(252162)  == null) &&
                player.getInventory().decreaseByItemId(186000236, 29)) {
                spawn(252159, 740.65f, 995.59f, 151.72f, (byte) 35); // Kn
                spawn(252160, 736.98f, 997.09f, 152.01f, (byte) 35); // Fi
                spawn(252160, 746.17f, 1001.14f, 151.81f, (byte) 35); // Fi
                spawn(252160, 748.52f, 6997.15f, 152.03f, (byte) 35); // Fi
                spawn(252160, 738.2f, 1001.1f, 151.86f, (byte) 35); // Fi
                spawn(252161, 732.92f, 999.77f, 161.68f, (byte) 35); // Ra
                spawn(252161, 752.73f, 999.88f, 161.69f, (byte) 35); // Ra
                spawn(252161, 731.29f, 1001.4f, 161.72f, (byte) 35); // Ra
                spawn(252161, 754.28f, 1001.44f, 161.67f, (byte) 35); // Ra
                spawn(252162, 745.f, 995.68f, 151.8f, (byte) 35); // Wi
                spawn(252429, 742.76f, 992.41f, 151.62f, (byte) 35); // trap
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402487));
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402494));
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
            if (player.getInventory().getItemCountByItemId(186000236) >= 29) {
                if ((getPosition().getWorldMapInstance().getNpc(251921) == null &&
                getPosition().getWorldMapInstance().getNpc(252264) == null &&
                getPosition().getWorldMapInstance().getNpc(251926) == null &&
                getPosition().getWorldMapInstance().getNpc(251951)  == null) &&
                player.getInventory().decreaseByItemId(186000236, 29)) {
                spawn(251921, 790.22f, 571.79f, 149.76f, (byte) 35); // Kn
                spawn(252264, 792.59f, 574.92f, 150.09f, (byte) 35); // Fi
                spawn(252264, 777.59f, 571.92f, 149.77f, (byte) 35); // Fi
                spawn(252264, 780.59f, 574.92f, 150.09f, (byte) 35); // Fi
                spawn(252264, 794.59f, 571.92f, 149.77f, (byte) 35); // Fi
                spawn(251926, 796.25f, 569.41f, 156.16f, (byte) 35); // Ra
                spawn(251926, 795.05f, 568.05f, 156.13f, (byte) 35); // Ra
                spawn(251926, 776.71f, 568.4f, 155.99f, (byte) 35); // Ra
                spawn(251926, 777.91f, 567.03f, 155.96f, (byte) 35); // Ra
                spawn(251951, 782.79f, 571.83f, 149.77f, (byte) 35); // Wi
                spawn(252429, 786.58f, 568.86f, 149.45f, (byte) 35); // trap
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402488));
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402495));
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 2375));
                } else {
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401837));
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
                }
            } else {
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
            }
        }
	else {
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
        deleteMobs(instance.getNpcs(252159));
        deleteMobs(instance.getNpcs(252160));
        deleteMobs(instance.getNpcs(252161));
        deleteMobs(instance.getNpcs(252162));
		deleteMobs(instance.getNpcs(251921));
		deleteMobs(instance.getNpcs(251926));
		deleteMobs(instance.getNpcs(251951));
		deleteMobs(instance.getNpcs(252264));
		deleteMobs(instance.getNpcs(252429));
        super.handleDespawned();
    }
}
