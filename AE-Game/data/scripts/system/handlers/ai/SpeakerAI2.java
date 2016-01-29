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

package ai;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.siege.SiegeRace;
import org.typezero.gameserver.model.templates.npcshout.ShoutEventType;
import org.typezero.gameserver.services.SiegeService;

/**
 * @author Rolandas
 */
@AIName("speaker")
public class SpeakerAI2 extends GeneralNpcAI2 {

	@Override
	public boolean onPatternShout(ShoutEventType event, String pattern, int skillNumber) {
		if (getOwner().getWorldId() != 210050000 && getOwner().getWorldId() != 220070000)
			return false;
		if (event != ShoutEventType.IDLE || pattern == null)
			return false;

		final SiegeService srv = SiegeService.getInstance();

		Race npcRace = getOwner().getRace();
		if (npcRace == Race.ASMODIANS) {
			if ("1".equals(pattern)) {
				// TODO: find if Dredgion Ship is spawned
			}
			else if ("2".equals(pattern)) {
				return srv.isSiegeInProgress(1142) && srv.getSiegeLocation(1142).getRace() == SiegeRace.ASMODIANS
					|| srv.isSiegeInProgress(1143) && srv.getSiegeLocation(1143).getRace() == SiegeRace.ASMODIANS
					|| srv.isSiegeInProgress(1144) && srv.getSiegeLocation(1144).getRace() == SiegeRace.ASMODIANS
					|| srv.isSiegeInProgress(1145) && srv.getSiegeLocation(1145).getRace() == SiegeRace.ASMODIANS;
			}
			else if ("3".equals(pattern)) {
				return srv.isSiegeInProgress(1132) && srv.getSiegeLocation(1132).getRace() == SiegeRace.ASMODIANS
					|| srv.isSiegeInProgress(1251) && srv.getSiegeLocation(1251).getRace() == SiegeRace.ASMODIANS;
			}
			else if ("4".equals(pattern)) {
				return srv.isSiegeInProgress(1221) && srv.getSiegeLocation(1221).getRace() == SiegeRace.BALAUR;
			}
			else if ("5".equals(pattern)) {
				return srv.isSiegeInProgress(1131);
			}
			else if ("6".equals(pattern)) {
				return srv.getSiegeLocation(3011) != null && srv.getSiegeLocation(3011).getRace() == SiegeRace.ELYOS
					&& srv.getSiegeLocation(3021) != null && srv.getSiegeLocation(3021).getRace() == SiegeRace.ELYOS;
			}
		}
		else if (npcRace == Race.ELYOS) {
			if ("1".equals(pattern)) {
				// TODO: find if Dredgion Ship is spawned
			}
			else if ("2".equals(pattern)) {
				return srv.isSiegeInProgress(1142) && srv.getSiegeLocation(1142).getRace() == SiegeRace.ELYOS
					|| srv.isSiegeInProgress(1143) && srv.getSiegeLocation(1143).getRace() == SiegeRace.ELYOS
					|| srv.isSiegeInProgress(1144) && srv.getSiegeLocation(1144).getRace() == SiegeRace.ELYOS
					|| srv.isSiegeInProgress(1145) && srv.getSiegeLocation(1145).getRace() == SiegeRace.ELYOS;
			}
			else if ("3".equals(pattern)) {
				return srv.isSiegeInProgress(1141) && srv.getSiegeLocation(1141).getRace() == SiegeRace.ELYOS
					|| srv.isSiegeInProgress(1211) && srv.getSiegeLocation(1211).getRace() == SiegeRace.ELYOS;
			}
			else if ("4".equals(pattern)) {
				return srv.isSiegeInProgress(1241) && srv.getSiegeLocation(1241).getRace() == SiegeRace.BALAUR;
			}
			else if ("5".equals(pattern)) {
				return srv.isSiegeInProgress(1141);
			}
			else if ("6".equals(pattern)) {
				return srv.getSiegeLocation(2011) != null && srv.getSiegeLocation(2011).getRace() == SiegeRace.ASMODIANS
					&& srv.getSiegeLocation(2021) != null && srv.getSiegeLocation(2021).getRace() == SiegeRace.ASMODIANS;
			}
		}

		return false;
	}
}
