package ai.instance.kamarBattlefield;

import ai.ActionItemNpcAI2;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

/**
 *
 * @author Steve
 */
@AIName("kamarsiegeweapon")
public class KamarSiegeWeaponAI2 extends ActionItemNpcAI2
{

    @Override
    protected void handleDialogStart(Player player)
    {
        super.handleDialogStart(player);
    }

    @Override
    protected void handleUseItemFinish(Player player)
    {
        switch (getNpcId())
        {
            case 701807:
                SkillEngine.getInstance().getSkill(player, 21403, 1, player).useNoAnimationSkill();
                if (player.getInventory().getItemCountByItemId(164000262) < 1)
                {
                    return;
                }
                break;
            case 701808:
                SkillEngine.getInstance().getSkill(player, 21404, 1, player).useNoAnimationSkill();
                if (player.getInventory().getItemCountByItemId(164000262) < 1)
                {
                    return;
                }
                break;
            case 701902:
            case 701806:
                World.getInstance().updatePosition(player, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
                PacketSendUtility.broadcastPacketAndReceive(player, new SM_FORCED_MOVE(player, player));
                SkillEngine.getInstance().getSkill(player, 21409, 1, player).useNoAnimationSkill();
                break;
        }
        AI2Actions.dieSilently(this, player);
        AI2Actions.deleteOwner(this);
    }
}
