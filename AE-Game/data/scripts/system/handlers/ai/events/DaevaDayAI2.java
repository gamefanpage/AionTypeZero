package ai.events;

import ai.GeneralNpcAI2;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.services.MuiService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.joda.time.DateTime;

/**
 * @author Romanz
 */
@AIName("daeva_day")
public class DaevaDayAI2 extends GeneralNpcAI2 {

  	@Override
	protected void handleDialogStart(Player player) {
        switch (getNpcId()) {
            case 831857:
            case 831858:
			{
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
				case 831857:
				case 831858:
				{
			 switch (Rnd.get(1, 4)) {
				case 1:
                    if (player.getEffectController().hasAbnormalEffect(10821) || player.getEffectController().hasAbnormalEffect(10822) || player.getEffectController().hasAbnormalEffect(10823) || player.getEffectController().hasAbnormalEffect(10824))
                    {
						PacketSendUtility.sendBrightYellowMessageOnCenter(player, MuiService.getInstance().getMessage("DAEVA_DAY"));
                        break;
                    }

					player.getEffectController().removeEffect(10822);
					player.getEffectController().removeEffect(10823);
					player.getEffectController().removeEffect(10824);
					SkillEngine.getInstance().getSkill(getOwner(), 10821 , 1, player).useWithoutPropSkill();
				break;
				case 2:
                    if (player.getEffectController().hasAbnormalEffect(10821) || player.getEffectController().hasAbnormalEffect(10822) || player.getEffectController().hasAbnormalEffect(10823) || player.getEffectController().hasAbnormalEffect(10824))
                    {
                        PacketSendUtility.sendBrightYellowMessageOnCenter(player, MuiService.getInstance().getMessage("DAEVA_DAY"));
                        break;
                    }
					player.getEffectController().removeEffect(10821);
					player.getEffectController().removeEffect(10823);
					player.getEffectController().removeEffect(10824);
					SkillEngine.getInstance().getSkill(getOwner(), 10822 , 1, player).useWithoutPropSkill();
				break;
				case 3:
                    if (player.getEffectController().hasAbnormalEffect(10821) || player.getEffectController().hasAbnormalEffect(10822) || player.getEffectController().hasAbnormalEffect(10823) || player.getEffectController().hasAbnormalEffect(10824))
                    {
                        PacketSendUtility.sendBrightYellowMessageOnCenter(player, MuiService.getInstance().getMessage("DAEVA_DAY"));
                        break;
                    }
					player.getEffectController().removeEffect(10822);
					player.getEffectController().removeEffect(10821);
					player.getEffectController().removeEffect(10824);
					SkillEngine.getInstance().getSkill(getOwner(), 10823 , 1, player).useWithoutPropSkill();
				break;
				case 4:
                    if (player.getEffectController().hasAbnormalEffect(10821) || player.getEffectController().hasAbnormalEffect(10822) || player.getEffectController().hasAbnormalEffect(10823) || player.getEffectController().hasAbnormalEffect(10824))
                    {
                        PacketSendUtility.sendBrightYellowMessageOnCenter(player, MuiService.getInstance().getMessage("DAEVA_DAY"));
                        break;
                    }

                    player.getEffectController().removeEffect(10822);
					player.getEffectController().removeEffect(10823);
					player.getEffectController().removeEffect(10821);
					SkillEngine.getInstance().getSkill(getOwner(), 10824 , 1, player).useWithoutPropSkill();
				break;
			 }

					break;
				}
			}
		}
		else if (dialogId == DialogAction.QUEST_SELECT.id() && questId != 0) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), dialogId, questId));
		}
		return true;
	}

	@Override
	protected void handleSpawned() {
		DateTime now = DateTime.now();
		int currentDay = now.getDayOfWeek();
		switch (getNpcId()) {
            case 831857:
            case 831858:
			{
				if (currentDay >= 5 && currentDay <= 7)
					super.handleSpawned();
				else
					if (!isAlreadyDead())
						getOwner().getController().onDelete();
				break;
			}
		}
	}
}
