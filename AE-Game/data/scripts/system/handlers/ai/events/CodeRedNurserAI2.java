package ai.events;

import ai.GeneralNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.joda.time.DateTime;

/**
 * @author xTz, modified bobobear
 */
@AIName("code_red_nurse")
public class CodeRedNurserAI2 extends GeneralNpcAI2 {

  	@Override
	protected void handleDialogStart(Player player) {
        switch (getNpcId()) {
            case 831435: //Jorpine (MON-THU)
            case 831436: //Yennu (MON-THU)
            case 831437: //Dalloren (FRI-SAT)
            case 831518: //Dalliea (FRI-SAT)
            case 831441: //Hylian (MON-THU)
            case 831442: //Rordah (MON-THU)
            case 831443: //Mazka (FRI-SAT)
            case 831524: { //Desha (FRI-SAT)
				super.handleDialogStart(player);
				break;
			}
			default: {
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
				break;
			}
		}
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		QuestEnv env = new QuestEnv(getOwner(), player, questId, dialogId);
		env.setExtendedRewardIndex(extendedRewardIndex);
		if (QuestEngine.getInstance().onDialog(env) && dialogId != DialogAction.SETPRO1.id()) {
			return true;
		}
		if (dialogId == DialogAction.SETPRO1.id()) {
			int skillId = 0;
			int RemoveSkillId = 0;
			switch (getNpcId()) {
				case 831435:  //Jorpine (MON-THU)
				case 831441: {//Hylian (MON-THU)
					RemoveSkillId =21281;
					skillId = 21280;
					break;
				}
				case 831436: //Yennu (MON-THU)
				case 831442: { //Rordah (MON-THU)
					RemoveSkillId =21280;
					skillId = 21281;
					break;
				}
				case 831518: //Dalliea (FRI-SAT)
				case 831524: { //Desha (FRI-SAT)
					RemoveSkillId =21283;
					skillId = 21309;
					break;
				}
				case 831437: //Dalloren (FRI-SAT)
				case 831443: { //Mazka (FRI-SAT)
					RemoveSkillId =21309;
					skillId = 21283;
					break;
				}
			}
			//only one buff at the same time
			player.getEffectController().removeEffect(RemoveSkillId);
			SkillEngine.getInstance().getSkill(getOwner(), skillId, 1, player).useWithoutPropSkill();
		}
		else if (dialogId == DialogAction.QUEST_SELECT.id() && questId != 0) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), dialogId, questId));
		}
		return true;
	}

	/*@Override
	protected void handleSpawned() {
		DateTime now = DateTime.now();
		int currentDay = now.getDayOfWeek();
		switch (getNpcId()) {
			case 831435: //Jorpine (MON-THU)
			case 831436: //Yennu (MON-THU)
			case 831441: //Hylian (MON-THU)
			case 831442:  {//Rordah (MON-THU)
					super.handleSpawned();
				break;
			}
			case 831437: // (MON-THU) 10% drop
			case 831443:  {// (MON-THU) 10% drop
				if (currentDay >= 1 && currentDay <= 4)
					super.handleSpawned();
				else
					if (!isAlreadyDead())
						getOwner().getController().onDelete();
				break;
			}
			case 831518: // (FRI-SAT) 3% pvp def
			case 831524: { // (FRI-SAT) 3% pvp def
				if (currentDay >= 5 && currentDay <= 7)
					super.handleSpawned();
				else
					if (!isAlreadyDead())
						getOwner().getController().onDelete();
				break;
			}
		}
	}*/
}
