/*
 * Eternal Bastion Defence Weapon
 */
package ai.instance.eternalBastion;

import ai.GeneralNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.MuiService;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import java.util.List;

/**
 * M.O.G. Devs Team
 * @author Dision
 */
@AIName("defence_bastion")
public class DefenceBastionAI2 extends GeneralNpcAI2 {
	protected int rewardDialogId = 5;
	protected int startingDialogId = 10;
	protected int questDialogId = 10;

	@Override
	protected void handleDialogStart(Player player) {
		checkDialog(player);
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 10));
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
			  if (player.getInventory().decreaseByItemId(185000136, 1)) {
				switch (player.getRace()) {
					case ASMODIANS:
					SkillEngine.getInstance().applyEffectDirectly(21139, player, player, 160000 * 3);
					break;
				  case ELYOS:
					SkillEngine.getInstance().applyEffectDirectly(21138, player, player, 160000 * 3);
					break;
					}
				PacketSendUtility.sendBrightYellowMessageOnCenter(player, MuiService.getInstance().getMessage("DEFENCE_BASTION"));
				stopMove(player);
			  AI2Actions.deleteOwner(this);
			  } else {
		    PacketSendUtility.sendBrightYellowMessageOnCenter(player, MuiService.getInstance().getMessage("DEFENCE_BASTION_NO"));
			  PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0, 0));
		    }
		} else {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), dialogId, questId));
		}
	}

	private void stopMove(Player player) {
		if (player != null) {
			player.getController().onStopMove();
		}
	}
}
