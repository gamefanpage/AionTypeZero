package quest.verteron;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.teleport.TeleportService2;

/**
 *
 * @authorRomanz
 */
public class _14016AGateAgape extends QuestHandler {

	private final static int questId = 14016;

	public _14016AGateAgape() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 203098, 700142 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerOnDie(questId);
		qe.registerQuestNpc(210753).addOnKillEvent(questId);
		qe.registerOnMovieEndQuest(153, questId);
		for (int npcId : npcs) {
			qe.registerQuestNpc(npcId).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203098: { // Spatalos
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case SETPRO1: {
							TeleportService2.teleportTo(player, 210030000, 2683.2085f, 1068.8977f, 199.375f);
							changeQuestStep(env, 0, 1, false); // 1
							return closeDialogWindow(env);
						}
					}
					break;
				}
				case 700142: { // Abyss Gate Guardian Stone
					if (dialog == DialogAction.USE_OBJECT) {
						if (QuestService.collectItemCheck(env, true)) {
                            changeQuestStep(env, 2, 2, true); // reward
							return playQuestMovie(env, 153);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203098) { // Spatalos
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 2034);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var == 2) {
				changeQuestStep(env, 2, 1, false);
				removeQuestItem(env, 182215317, 1);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var == 2 && player.getWorldId() != 310030000) {
				changeQuestStep(env, 2, 1, false);
				removeQuestItem(env, 182215317, 1);
				return true;
			}
			else if (var == 1 && player.getWorldId() == 310030000) {
				changeQuestStep(env, 1, 2, false); // 2
				QuestService.addNewSpawn(310030000, player.getInstanceId(), 233873, (float) 258.89917, (float) 237.20166,
					(float) 217.06035, (byte) 0);
				return true;
			}
		}
		return false;
	}


	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		if (movieId == 153) {
			TeleportService2.teleportTo(env.getPlayer(), 210030000, 2683.2085f, 1068.8977f, 199.375f);
			return true;
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] verteronQuests = { 14010, 14011, 14012, 14013, 14014, 14015 };
		return defaultOnLvlUpEvent(env, verteronQuests, true);
	}
}
