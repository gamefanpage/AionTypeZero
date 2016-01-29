/*
 * Copyright (c) 2015, TypeZero Engine (game.developpers.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of TypeZero Engine nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package admincommands;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.model.stats.container.CreatureLifeStats;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import org.typezero.gameserver.network.aion.serverpackets.SM_STATUPDATE_EXP;
import org.typezero.gameserver.skillengine.model.SkillTargetSlot;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mrakobes, Loxo
 */
public class Heal extends AdminCommand {

   public Heal() {
	  super("heal");
   }

   @Override
   public void execute(Player player, String... params) {
	  VisibleObject target = player.getTarget();
	  if (target == null) {
		 PacketSendUtility.sendMessage(player, "No target selected");
		 return;
	  }
	  if (!(target instanceof Creature)) {
		 PacketSendUtility.sendMessage(player, "Target has to be Creature!");
		 return;
	  }

	  Creature creature = (Creature) target;

	  if (params == null || params.length < 1) {
		 creature.getLifeStats().increaseHp(TYPE.HP, creature.getLifeStats().getMaxHp() + 1);
		 creature.getLifeStats().increaseMp(TYPE.MP, creature.getLifeStats().getMaxMp() + 1);
		 creature.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
		 PacketSendUtility.sendMessage(player, creature.getName() + " has been refreshed !");
	  }
	  else if (params[0].equals("dp") && creature instanceof Player) {
		 Player targetPlayer = (Player) creature;
		 targetPlayer.getCommonData().setDp(targetPlayer.getGameStats().getMaxDp().getCurrent());
		 PacketSendUtility.sendMessage(player, targetPlayer.getName() + " is now full of DP !");
	  }
	  else if (params[0].equals("fp") && creature instanceof Player) {
		 Player targetPlayer = (Player) creature;
		 targetPlayer.getLifeStats().setCurrentFp(targetPlayer.getLifeStats().getMaxFp());
		 PacketSendUtility.sendMessage(player, targetPlayer.getName() + " FP has been fully refreshed !");
	  }
	  else if (params[0].equals("repose") && creature instanceof Player) {
		 Player targetPlayer = (Player) creature;
		 PlayerCommonData pcd = targetPlayer.getCommonData();
		 pcd.setCurrentReposteEnergy(pcd.getMaxReposteEnergy());
		 PacketSendUtility.sendMessage(player, targetPlayer.getName() + " Reposte Energy has been fully refreshed !");
		 PacketSendUtility.sendPacket(targetPlayer,
				 new SM_STATUPDATE_EXP(pcd.getExpShown(), pcd.getExpRecoverable(), pcd.getExpNeed(), pcd
				 .getCurrentReposteEnergy(), pcd.getMaxReposteEnergy()));
	  }
	  else {
		 int hp;
		 try {
			String percent = params[0];
			CreatureLifeStats<?> cls = creature.getLifeStats();
			Pattern heal = Pattern.compile("([^%]+)%");
			Matcher result = heal.matcher(percent);
			int value;

			if (result.find()) {
			   hp = Integer.parseInt(result.group(1));

			   if (hp < 100)
				  value = (int) (hp / 100f * cls.getMaxHp());
			   else
				  value = cls.getMaxHp();
			}
			else
			   value = Integer.parseInt(params[0]);
			cls.increaseHp(TYPE.HP, value);
			PacketSendUtility.sendMessage(player, creature.getName() + " has been healed for " + value +" health points!");
		 }
		 catch (Exception ex) {
			onFail(player, null);
		 }
	  }
   }

   @Override
   public void onFail(Player player, String message) {
	  String syntax = "//heal : Full HP and MP\n"
			  + "//heal dp : Full DP, must be used on a player !\n"
			  + "//heal fp : Full FP, must be used on a player\n"
			  + "//heal repose : Full repose energy, must be used on a player\n"
			  + "//heal <hp | hp%> : Heal given amount/percentage of HP";
	  PacketSendUtility.sendMessage(player, syntax);
   }
}
