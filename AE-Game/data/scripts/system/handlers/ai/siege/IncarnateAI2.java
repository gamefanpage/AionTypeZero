package ai.siege;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.configs.main.SiegeConfig;
import org.typezero.gameserver.controllers.effect.EffectController;
import org.typezero.gameserver.model.ChatType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_MESSAGE;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.utils.stats.AbyssRankEnum;
import org.typezero.gameserver.world.knownlist.Visitor;
import java.util.concurrent.Future;

@AIName("incarnate")
public class IncarnateAI2 extends AggressiveNpcAI2 {

	Future<?> avatar_scan;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		avatar_scan = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (SiegeConfig.SIEGE_IDA_ENABLED) {
					getOwner().getKnownList().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							if (player.getAbyssRank().getRank().getId() > AbyssRankEnum.STAR4_OFFICER.getId()) {
								boolean inform = false;
								EffectController controller = player.getEffectController();
								for (Effect eff : controller.getAbnormalEffects()) {
									if (eff.isDeityAvatar()) {
										eff.endEffect();
										getOwner().getEffectController().clearEffect(eff);
										inform = true;
									}
								}

								if (inform) {
									String message = "The power of incarnation removes " + player.getName() + " morph state.";
									PacketSendUtility.broadcastPacket(getOwner(),
											new SM_MESSAGE(getObjectId(), getOwner().getName(), message, ChatType.BRIGHT_YELLOW_CENTER));
								}
							}
						}

					});
				}
			}

		}, 10000, 10000);
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		avatar_scan.cancel(true);
	}

}
