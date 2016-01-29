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
package org.typezero.gameserver.services.siegeservice;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.configs.main.LoggingConfig;
import org.typezero.gameserver.configs.main.SiegeConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.assemblednpc.AssembledNpc;
import org.typezero.gameserver.model.assemblednpc.AssembledNpcPart;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.siege.ArtifactLocation;
import org.typezero.gameserver.model.siege.FortressLocation;
import org.typezero.gameserver.model.siege.Influence;
import org.typezero.gameserver.model.siege.SiegeRace;
import org.typezero.gameserver.model.templates.assemblednpc.AssembledNpcTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_NPC_ASSEMBLER;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.idfactory.IDFactory;
import org.typezero.gameserver.world.World;
import java.util.Iterator;
import java.util.Map;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author synchro2 @reworked Luzien TODO: Send Peace Dredgion without assault
 * TODO: Artifact Siege
 */
public class BalaurAssaultService {

	private static final BalaurAssaultService instance = new BalaurAssaultService();
	private Logger log = LoggerFactory.getLogger("SIEGE_LOG");
	private final Map<Integer, FortressAssault> fortressAssaults = new FastMap<Integer, FortressAssault>().shared();
	//private final Map<Integer, ArtifactAssault> artifactAssaults = new FastMap<Integer, ArtifactAssault>().shared();

	public static BalaurAssaultService getInstance() {
		return instance;
	}

	public void onSiegeStart(final Siege<?> siege) {
		if (siege instanceof FortressSiege) {
			if (!calculateFortressAssault(((FortressSiege) siege).getSiegeLocation()))
				return;
		}
		else if (siege instanceof ArtifactSiege) {
			if (!calculateArtifactAssault(((ArtifactSiege) siege).getSiegeLocation()))
				return;
		}
		else
			return;
		newAssault(siege, Rnd.get(1, 600));
		if (LoggingConfig.LOG_SIEGE)
			log.info("[SIEGE] Balaur Assault scheduled on Siege ID: " + siege.getSiegeLocationId() + "!");
	}

	public void onSiegeFinish(Siege<?> siege) {
		int locId = siege.getSiegeLocationId();
		if (fortressAssaults.containsKey(locId)) {
			Boolean bossIsKilled = siege.isBossKilled();
			fortressAssaults.get(locId).finishAssault(bossIsKilled);
			if (bossIsKilled && siege.getSiegeLocation().getRace().equals(SiegeRace.BALAUR))
				log.info("[SIEGE] > [FORTRESS:" + siege.getSiegeLocationId() + "] has been captured by Balaur Assault!");
			else
				log.info("[SIEGE] > [FORTRESS:" + siege.getSiegeLocationId() + "] Balaur Assault finished without capture!");
			fortressAssaults.remove(locId);
		}
	}

	private boolean calculateFortressAssault(FortressLocation fortress) {
		boolean isBalaurea = fortress.getWorldId() != 400010000;
		boolean isBalaurea_Katalam = fortress.getWorldId() != 600050000;
		boolean isBalaurea_Danaria = fortress.getWorldId() != 600060000;
		int locationId = fortress.getLocationId();

		if (fortressAssaults.containsKey(locationId))
			return false;

		if (!calcFortressInfluence(isBalaurea, fortress))
			return false;
		if (!calcFortressInfluence(isBalaurea_Katalam, fortress))
			return false;
		if (!calcFortressInfluence(isBalaurea_Danaria, fortress))
			return false;

		int count = 0; //Allow only 2 Balaur attacks per map, 1 per Balaurea map
		for (FortressAssault fa : fortressAssaults.values()) {
			if (fa.getWorldId() == fortress.getWorldId()) {
				count++;
			}
		}

		if (count >= (isBalaurea ? 1 : 2))
			return false;
		if (count >= (isBalaurea_Katalam ? 1 : 2))
			return false;
		if (count >= (isBalaurea_Danaria ? 1 : 2))
			return false;

		return true;
	}

	private boolean calculateArtifactAssault(ArtifactLocation artifact) {
		//TODO
		return false;
	}

	public void startAssault(Player player, int location, int delay) {
		if (fortressAssaults.containsKey(location) /* || artifactAssaults.containsKey(location)*/) {
			PacketSendUtility.sendMessage(player, "Assault on " + location + " was already started");
			return;
		}

		newAssault(SiegeService.getInstance().getSiege(location), delay);
	}

	private void newAssault(Siege<?> siege, int delay) {
		if (siege instanceof FortressSiege) {
			FortressAssault assault = new FortressAssault((FortressSiege) siege);
			assault.startAssault(delay);
			fortressAssaults.put(siege.getSiegeLocationId(), assault);
		}
		else if (siege instanceof ArtifactSiege) {
			ArtifactAssault assault = new ArtifactAssault((ArtifactSiege) siege);
			assault.startAssault(delay);
		}
	}

	private boolean calcFortressInfluence(boolean isBalaurea, FortressLocation fortress) {
		SiegeRace locationRace = fortress.getRace();
		float influence;

		if (locationRace.equals(SiegeRace.BALAUR) || !fortress.isVulnerable())
			return false;

		int ownedForts = 0;
		if (isBalaurea) {
			for (FortressLocation fl : SiegeService.getInstance().getFortresses().values()) {
				if (fl.getWorldId() != 400010000 && !fortressAssaults.containsKey(fl.getLocationId()) && fl.getRace().equals(locationRace))
					ownedForts++;
				if (fl.getWorldId() != 600050000 && !fortressAssaults.containsKey(fl.getLocationId()) && fl.getRace().equals(locationRace))
					ownedForts++;
				if (fl.getWorldId() != 600060000 && !fortressAssaults.containsKey(fl.getLocationId()) && fl.getRace().equals(locationRace))
					ownedForts++;
			}
			influence = ownedForts >= 2 ? 0.25f : 0.1f;
		}
		else
			influence = locationRace.equals(SiegeRace.ASMODIANS) ? Influence.getInstance().getGlobalAsmodiansInfluence() : Influence.getInstance().getGlobalElyosInfluence();

		return Rnd.get() < influence * SiegeConfig.BALAUR_ASSAULT_RATE;
	}

	public void spawnDredgion(int spawnId) {
		AssembledNpcTemplate template = DataManager.ASSEMBLED_NPC_DATA.getAssembledNpcTemplate(spawnId);
		FastList<AssembledNpcPart> assembledPatrs = new FastList<AssembledNpcPart>();
		for (AssembledNpcTemplate.AssembledNpcPartTemplate npcPart : template.getAssembledNpcPartTemplates()) {
			assembledPatrs.add(new AssembledNpcPart(IDFactory.getInstance().nextId(), npcPart));
		}

		AssembledNpc npc = new AssembledNpc(template.getRouteId(), template.getMapId(), template.getLiveTime(), assembledPatrs);
		Iterator<Player> iter = World.getInstance().getPlayersIterator();
		Player findedPlayer;
		while (iter.hasNext()) {
			findedPlayer = iter.next();
			PacketSendUtility.sendPacket(findedPlayer, new SM_NPC_ASSEMBLER(npc));
			PacketSendUtility.sendPacket(findedPlayer, SM_SYSTEM_MESSAGE.STR_ABYSS_CARRIER_SPAWN);
		}
	}

}
