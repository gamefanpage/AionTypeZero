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

package org.typezero.gameserver.services;

import com.aionemu.commons.database.dao.DAOManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.dao.PetitionDAO;
import org.typezero.gameserver.model.Petition;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_PETITION;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

import java.util.*;

/**
 * @author zdead
 */
public class PetitionService {

	private static Logger log = LoggerFactory.getLogger(PetitionService.class);

	private static SortedMap<Integer, Petition> registeredPetitions = new TreeMap<Integer, Petition>();

	public static final PetitionService getInstance() {
		return SingletonHolder.instance;
	}

	public PetitionService() {
		log.info("Loading PetitionService ...");
		Set<Petition> petitions = DAOManager.getDAO(PetitionDAO.class).getPetitions();
		for (Petition p : petitions) {
			registeredPetitions.put(p.getPetitionId(), p);
		}
		log.info("Successfully loaded " + registeredPetitions.size() + " database petitions");
	}

	public Collection<Petition> getRegisteredPetitions() {
		return registeredPetitions.values();
	}

	public void deletePetition(int playerObjId) {
		Set<Petition> petitions = new HashSet<Petition>();
		for (Petition p : registeredPetitions.values()) {
			if (p.getPlayerObjId() == playerObjId)
				petitions.add(p);
		}
		for (Petition p : petitions)
			if(registeredPetitions.containsKey(p.getPetitionId()))
				registeredPetitions.remove(p.getPetitionId());

		DAOManager.getDAO(PetitionDAO.class).deletePetition(playerObjId);
		if (playerObjId > 0 && World.getInstance().findPlayer(playerObjId) != null) {
			Player p = World.getInstance().findPlayer(playerObjId);
			PacketSendUtility.sendPacket(p, new SM_PETITION());
		}
		rebroadcastPlayerData();
	}

	public void setPetitionReplied(int petitionId) {
		int playerObjId = registeredPetitions.get(petitionId).getPlayerObjId();
		DAOManager.getDAO(PetitionDAO.class).setReplied(petitionId);
		registeredPetitions.remove(petitionId);
		rebroadcastPlayerData();
		if (playerObjId > 0 && World.getInstance().findPlayer(playerObjId) != null) {
			Player p = World.getInstance().findPlayer(playerObjId);
			PacketSendUtility.sendPacket(p, new SM_PETITION());
		}
	}

	public synchronized Petition registerPetition(Player sender, int typeId, String title, String contentText,
		String additionalData) {
		int id = DAOManager.getDAO(PetitionDAO.class).getNextAvailableId();
		Petition ptt = new Petition(id, sender.getObjectId(), typeId, title, contentText, additionalData, 0);
		DAOManager.getDAO(PetitionDAO.class).insertPetition(ptt);
		registeredPetitions.put(ptt.getPetitionId(), ptt);
		broadcastMessageToGM(sender, ptt.getPetitionId());
		return ptt;
	}

	private void rebroadcastPlayerData() {
		for (Petition p : registeredPetitions.values()) {
			Player player = World.getInstance().findPlayer(p.getPlayerObjId());
			if (player != null)
				PacketSendUtility.sendPacket(player, new SM_PETITION(p));
		}
	}

	private void broadcastMessageToGM(Player sender, int petitionId) {
		Iterator<Player> players = World.getInstance().getPlayersIterator();
		while (players.hasNext()) {
			Player p = players.next();
			if (p.getAccessLevel() > 0) {
				PacketSendUtility
					.sendBrightYellowMessageOnCenter(p, "New Support Petition from: " + sender.getName() + " (#" + petitionId + ")");
			}
		}
	}

	public boolean hasRegisteredPetition(Player player) {
		return hasRegisteredPetition(player.getObjectId());
	}

	public boolean hasRegisteredPetition(int playerObjId) {
		boolean result = false;
		for (Petition p : registeredPetitions.values()) {
			if (p.getPlayerObjId() == playerObjId)
				result = true;
		}
		return result;
	}

	public Petition getPetition(int playerObjId) {
		for (Petition p : registeredPetitions.values()) {
			if (p.getPlayerObjId() == playerObjId)
				return p;
		}
		return null;
	}

	public synchronized int getNextAvailablePetitionId() {
		return 0;
	}

	public int getWaitingPlayers(int playerObjId) {
		int counter = 0;
		for (Petition p : registeredPetitions.values()) {
			if (p.getPlayerObjId() == playerObjId)
				break;
			counter++;
		}
		return counter;
	}

	public int calculateWaitTime(int playerObjId) {
		int timePerPetition = 15;
		int timeBetweenPetition = 30;
		int result = timeBetweenPetition;
		for (Petition p : registeredPetitions.values()) {
			if (p.getPlayerObjId() == playerObjId)
				break;
			result += timePerPetition;
			result += timeBetweenPetition;
		}
		return result;
	}

	public void onPlayerLogin(Player player) {
		if (hasRegisteredPetition(player))
			PacketSendUtility.sendPacket(player, new SM_PETITION(getPetition(player.getObjectId())));
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final PetitionService instance = new PetitionService();
	}

}
