package ai.siege.katalam;

import ai.GeneralNpcAI2;
import org.typezero.gameserver.ai2.AIName;
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
import java.util.List;

@AIName("merc_sillus")
public class MercenariesSillusAI2 extends GeneralNpcAI2 {
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
		int entryCount = 0;
		if (dialogId == 10000) {
			if (player.getInventory().decreaseByItemId(186000236, 16) && entryCount == 16) {
				entryCount ++;
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401828));
				// Spawn Merc
			} else {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300319));
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0, 0));
			}
		}
		else if (dialogId == 10001) {
			if (player.getInventory().decreaseByItemId(186000236, 8) && entryCount == 8) {
				entryCount ++;
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401829));
			  // Spawn Merc
			} else {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300319));
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0, 0));
			}
		}
		else if (dialogId == 10002) {
			if (player.getInventory().decreaseByItemId(186000236, 16) && entryCount == 16) {
				entryCount ++;
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401830));
			  // Spawn Merc
			} else {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300319));
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0, 0));
			}
		} else {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), dialogId, questId));
		}
	}
}
