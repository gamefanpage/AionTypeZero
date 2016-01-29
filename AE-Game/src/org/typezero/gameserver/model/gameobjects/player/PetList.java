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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import javolution.util.FastMap;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.PlayerPetsDAO;
import org.typezero.gameserver.taskmanager.tasks.ExpireTimerTask;

/**
 * @author ATracer
 */
public class PetList {

   private final Player player;
   private int lastUsedPetId;
   private FastMap<Integer, PetCommonData> pets = new FastMap<Integer, PetCommonData>();

   PetList(Player player) {
	  this.player = player;
	  loadPets();
   }

   public void loadPets() {
	  List<PetCommonData> playerPets = DAOManager.getDAO(PlayerPetsDAO.class).getPlayerPets(player);
	  PetCommonData lastUsedPet = null;
	  for (PetCommonData pet : playerPets) {
		 if (pet.getExpireTime() > 0) {
			ExpireTimerTask.getInstance().addTask(pet, player);
		 }
		 pets.put(pet.getPetId(), pet);
		 if (lastUsedPet == null || pet.getDespawnTime().after(lastUsedPet.getDespawnTime()))
			lastUsedPet = pet;
	  }

	  if (lastUsedPet != null)
		 lastUsedPetId = lastUsedPet.getPetId();
   }

   public Collection<PetCommonData> getPets() {
	  return pets.values();
   }

   /**
    * @param petId
    * @return
    */
   public PetCommonData getPet(int petId) {
	  return pets.get(petId);
   }

   public PetCommonData getLastUsedPet() {
	  return getPet(lastUsedPetId);
   }

   public void setLastUsedPetId(int lastUsedPetId) {
	  this.lastUsedPetId = lastUsedPetId;
   }

   /**
    * @param player
    * @param petId
    * @param decorationId
    * @param name
    * @return
    */
   public PetCommonData addPet(Player player, int petId, int decorationId, String name, int expireTime) {
	  return addPet(player, petId, decorationId, System.currentTimeMillis(), name, expireTime);
   }

   public PetCommonData addPet(Player player, int petId, int decorationId, long birthday, String name, int expireTime) {
	  PetCommonData petCommonData = new PetCommonData(petId, player.getObjectId(), expireTime);
	  petCommonData.setDecoration(decorationId);
	  petCommonData.setName(name);
	  petCommonData.setBirthday(new Timestamp(birthday));
	  petCommonData.setDespawnTime(new Timestamp(System.currentTimeMillis()));
	  DAOManager.getDAO(PlayerPetsDAO.class).insertPlayerPet(petCommonData);
	  pets.put(petId, petCommonData);
	  return petCommonData;
   }

   /**
    * @param petId
    * @return
    */
   public boolean hasPet(int petId) {
	  return pets.containsKey(petId);
   }

   /**
    * @param petId
    */
   public void deletePet(int petId) {
	  if (hasPet(petId)) {
		 pets.remove(petId);
		 DAOManager.getDAO(PlayerPetsDAO.class).removePlayerPet(player, petId);
	  }
   }
}
