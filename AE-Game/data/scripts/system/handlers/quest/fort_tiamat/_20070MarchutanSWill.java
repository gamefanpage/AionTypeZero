package quest.fort_tiamat;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.SystemMessageId;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMapInstance;


/**
 * @author Romanz
 *
 */
public class _20070MarchutanSWill extends QuestHandler {

	private final static int questId = 20070;

	public _20070MarchutanSWill() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 205579, 798800, 205864, 730628, 730691, 800386, 800431, 800358};
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 205579) {
				switch (dialog) {
					case QUEST_SELECT: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
						else if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					}
					case SETPRO1: {
						return defaultCloseDialog(env, 0, 1); // 1
					}
					case SETPRO3: {
						removeQuestItem(env, 182213248, 1);
						return defaultCloseDialog(env, 2, 3);
					}
				}
			}
			else if (targetId == 798800) {
				switch (dialog) {
					case QUEST_SELECT: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					}
					case SETPRO2: {
						giveQuestItem(env, 182213248, 1);
						return defaultCloseDialog(env, 1, 2);
						}
					}
				}
			else if (targetId == 205864) {
				switch (dialog) {
					case QUEST_SELECT: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					}
					case SETPRO4: {
						return defaultCloseDialog(env, 3, 4);
					}
				}
			}
			else if (targetId == 730628) {
				switch (dialog) {
					case USE_OBJECT: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					}
					case SETPRO5: {
						WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(300490000);
						InstanceService.registerPlayerWithInstance(newInstance, player);
						TeleportService2.teleportTo(player, 300490000, newInstance.getInstanceId(), 501.95316f, 519.2258f, 240.26651f);
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					}
				}
			}
			else if (targetId == 730691) {
				switch (dialog) {
					case USE_OBJECT: {
						TeleportService2.teleportTo(player, 300490000, 495, 528, 417);
						changeQuestStep(env, 5, 6, false);
                        QuestService.spawnQuestNpc(300490000, player.getInstanceId(), 800386, 459.24f, 514.55f, 417.40436f, (byte) 0);
						return closeDialogWindow(env);
					}
				}
			}
			else if (targetId == 800386) {
				switch (dialog) {
					case QUEST_SELECT: {
						if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					}
					case SETPRO7: {
						playQuestMovie(env, 494);
						QuestService.spawnQuestNpc(300490000, player.getInstanceId(), 800431, 502.125f, 510.26f, 417.40436f, (byte) 0);
						QuestService.spawnQuestNpc(300490000, player.getInstanceId(), 800358, 504.438f, 514.822f, 417.40436f, (byte) 0);
						if (env.getVisibleObject() != null && env.getVisibleObject() instanceof Npc) {
							Npc npc = (Npc) env.getVisibleObject();
							if (npc.getNpcId() == 800386) {
								World.getInstance().getWorldMap(300490000).getWorld().despawn(npc);
							}
						}
						QuestService.spawnQuestNpc(300490000, player.getInstanceId(), 800387, 450.883331f, 514.55f, 417.40436f, (byte) 0);
						return defaultCloseDialog(env, 6, 7);
					}
				}
			}
			else if (targetId == 800431) {
				switch (dialog) {
					case QUEST_SELECT: {
						if (var == 7) {
							return sendQuestDialog(env, 3398);
						}
					}
					case SETPRO8: {
						return defaultCloseDialog(env, 7, 8);
					}
				}
			}
			else if (targetId == 800358) {
				switch (dialog) {
					case QUEST_SELECT: {
						if (var == 8) {
							return sendQuestDialog(env, 3739);
						}
					}
					case SET_SUCCEED: {
						TeleportService2.teleportTo(player, 600030000, 100.1574f, 1860.46f, 295.455f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
						return defaultCloseDialog(env, 8, 9, true, false);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205864) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs != null && qs.getStatus() == QuestStatus.START) {
		  if (player.getWorldId() != 300490000) {
				int var = qs.getQuestVarById(0);
				if (var >= 5) {
					qs.setQuestVar(1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
						DataManager.QUEST_DATA.getQuestById(questId).getName()));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 20064);
	}
}
