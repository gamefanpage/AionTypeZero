package playercommands;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.SkillLearnService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.PlayerCommand;

/**
 * @author ATracer
 */

public class GiveMissingSkills extends PlayerCommand {

	public GiveMissingSkills() {
		super("skills");
	}

	@Override
	public void execute(Player player, String... params) {
		SkillLearnService.addMissingSkills(player);
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax : .skills");
	}
}
