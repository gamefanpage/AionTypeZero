package quest.sillus_danuar_mysticarium;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;


/**
 * @author Romanz
 *
 */
public class _18003WithGreatPowerComesGreatIdiocy extends QuestHandler {

	private final static int questId = 18003;

	private final static int mob = 230082;

	public _18003WithGreatPowerComesGreatIdiocy() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(mob).addOnKillEvent(questId);
		qe.registerQuestNpc(mob).addOnAddAggroListEvent(getQuestId());
		qe.registerQuestNpc(800525).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env){
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();
		 if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 800525) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 1352);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (env.getTargetId() == mob) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
			}
		}
		return false;
	}

	@Override
	public boolean onAddAggroListEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			QuestService.startQuest(env);
			return true;
		}
		return false;
	}
}
