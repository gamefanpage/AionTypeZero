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

package org.typezero.gameserver.model.gameobjects.player;

import org.typezero.gameserver.model.stats.container.StatEnum;

/**
 * @author antness
 */
public enum RewardType {
	AP_PLAYER {

		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.AP_BOOST, 100).getCurrent() / 100f;
			return (long) (reward * player.getRates().getApPlayerGainRate() * statRate);
		}
	},
	AP_NPC {

		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.AP_BOOST, 100).getCurrent() / 100f;
			return (long) (reward * player.getRates().getApNpcRate() * statRate);
		}
	},
	HUNTING {

		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_HUNTING_XP_RATE, 100).getCurrent() / 100f;
            long legionOnlineBonus = 0;
            if (player.isLegionMember() && player.getLegion().getOnlineMembersCount() >= 10) {
                legionOnlineBonus = (long) (reward * player.getRates().getXpRate() * statRate) / 100 * 10;
            }
            return (long) (reward * player.getRates().getXpRate() * statRate + legionOnlineBonus);
        }
	},
	GROUP_HUNTING {

		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_GROUP_HUNTING_XP_RATE, 100).getCurrent() / 100f;
            long legionOnlineBonus = 0;
            if (player.isLegionMember() && player.getLegion().getOnlineMembersCount() >= 10) {
                legionOnlineBonus = (long) (reward * player.getRates().getXpRate() * statRate) / 100 * 10;
            }
            return (long) (reward * player.getRates().getGroupXpRate() * statRate + legionOnlineBonus);
        }
	},
	PVP_KILL {

		@Override
		public long calcReward(Player player, long reward) {
			return (reward);
		}
	},
	QUEST {

		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_QUEST_XP_RATE, 100).getCurrent() / 100f;
			return (long) (reward * player.getRates().getQuestXpRate() * statRate);
		}
	},
	CRAFTING {

		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_CRAFTING_XP_RATE, 100).getCurrent() / 100f;
            long legionOnlineBonus = 0;
            if (player.isLegionMember() && player.getLegion().getOnlineMembersCount() >= 10) {
                legionOnlineBonus = (long) (reward * player.getRates().getXpRate() * statRate) / 100 * 10;
            }
            return (long) (reward * player.getRates().getCraftingXPRate() * statRate + legionOnlineBonus);
        }
	},
	GATHERING {

		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_GATHERING_XP_RATE, 100).getCurrent() / 100f;
            long legionOnlineBonus = 0;
            if (player.isLegionMember() && player.getLegion().getOnlineMembersCount() >= 10) {
                legionOnlineBonus = (long) (reward * player.getRates().getXpRate() * statRate) / 100 * 10;
            }
            return (long) (reward * player.getRates().getGatheringXPRate() * statRate + legionOnlineBonus);
        }
	};

	public abstract long calcReward(Player player, long reward);
}
