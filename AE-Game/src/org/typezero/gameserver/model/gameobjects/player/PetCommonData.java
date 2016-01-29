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
import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.PlayerPetsDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.IExpirable;
import org.typezero.gameserver.model.templates.VisibleObjectTemplate;
import org.typezero.gameserver.model.templates.pet.PetDopingBag;
import org.typezero.gameserver.model.templates.pet.PetFunctionType;
import org.typezero.gameserver.model.templates.pet.PetTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.toypet.PetAdoptionService;
import org.typezero.gameserver.services.toypet.PetFeedProgress;
import org.typezero.gameserver.services.toypet.PetHungryLevel;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.utils.idfactory.IDFactory;

/**
 * @author ATracer
 */
public class PetCommonData extends VisibleObjectTemplate implements IExpirable {

   private int decoration;
   private String name;
   private final int petId;
   private Timestamp birthday;
   PetFeedProgress feedProgress = null;
   PetDopingBag dopingBag = null;
   private volatile boolean cancelFeed = false;
   private boolean feedingTime = true;
   private long refeedTime;
   private final int petObjectId;
   private final int masterObjectId;
   private long startMoodTime;
   private int shuggleCounter;
   private int lastSentPoints;
   private long moodCdStarted;
   private long giftCdStarted;
   private int expireTime;
   private Timestamp despawnTime;
   private boolean isLooting = false;
   private boolean isBuffing = false;

   public PetCommonData(int petId, int masterObjectId, int expireTime) {
	  this.petObjectId = IDFactory.getInstance().nextId();
	  this.petId = petId;
	  this.masterObjectId = masterObjectId;
	  this.expireTime = expireTime;
	  PetTemplate template = DataManager.PET_DATA.getPetTemplate(petId);
	  if (template.ContainsFunction(PetFunctionType.FOOD)) {
		 int flavourId = template.getPetFunction(PetFunctionType.FOOD).getId();
		 int lovedLimit = DataManager.PET_FEED_DATA.getFlavourById(flavourId).getLovedFoodLimit();
		 feedProgress = new PetFeedProgress((byte) (lovedLimit & 0xFF));
	  }
	  if (template.ContainsFunction(PetFunctionType.DOPING)) {
		 dopingBag = new PetDopingBag();
	  }
   }

   public final int getDecoration() {
	  return decoration;
   }

   public final void setDecoration(int decoration) {
	  this.decoration = decoration;
   }

   @Override
   public final String getName() {
	  return name;
   }

   public final void setName(String name) {
	  this.name = name;
   }

   public final int getPetId() {
	  return petId;
   }

   public int getBirthday() {
	  if (birthday == null)
		 return 0;

	  return (int) (birthday.getTime() / 1000);
   }

   public Timestamp getBirthdayTimestamp() {
	  return birthday;
   }

   public void setBirthday(Timestamp birthday) {
	  this.birthday = birthday;
   }

   public long getRefeedTime() {
	  return refeedTime;
   }

   public void setRefeedTime(long curentTime) {
	  this.refeedTime = curentTime;
   }

   public void setIsFeedingTime(boolean food) {
	  this.feedingTime = food;
   }

   public boolean isFeedingTime() {
	  return feedingTime;
   }

   public boolean getCancelFeed() {
	  return cancelFeed;
   }

   public void setCancelFeed(boolean cancelFeed) {
	  this.cancelFeed = cancelFeed;
   }

   public void scheduleRefeed(final long reFoodTime) {
	  setIsFeedingTime(false);
	  ThreadPoolManager.getInstance().schedule(new Runnable() {
		 @Override
		 public void run() {
			feedingTime = true;
			refeedTime = 0;
			feedProgress.setHungryLevel(PetHungryLevel.HUNGRY);
		 }
	  }, reFoodTime);
   }

   public long getRefeedDelay() {
	  long time = refeedTime - System.currentTimeMillis();
	  if (time < 0) {
		 refeedTime = 0;
		 time = 0;
	  }

	  return time;
   }

   public int getObjectId() {
	  return petObjectId;
   }

   public int getMasterObjectId() {
	  return masterObjectId;
   }

   @Override
   public int getTemplateId() {
	  return petId;
   }

   @Override
   public int getNameId() {
	  // TODO Auto-generated method stub
	  return 0;
   }

   public final long getMoodStartTime() {
	  return startMoodTime;
   }

   public final int getShuggleCounter() {
	  return shuggleCounter;
   }

   public final void setShuggleCounter(int shuggleCounter) {
	  this.shuggleCounter = shuggleCounter;
   }

   public final int getMoodPoints(boolean forPacket) {
	  if (startMoodTime == 0)
		 startMoodTime = System.currentTimeMillis();
	  int points = Math.round((System.currentTimeMillis() - startMoodTime) / 1000f) + shuggleCounter * 1000;
	  if (forPacket && points > 9000)
		 return 9000;
	  return points;
   }

   public final int getLastSentPoints() {
	  return lastSentPoints;
   }

   public final void setLastSentPoints(int points) {
	  lastSentPoints = points;
   }

   public final boolean increaseShuggleCounter() {
	  if (getMoodRemainingTime() > 0)
		 return false;
	  this.moodCdStarted = System.currentTimeMillis();
	  this.shuggleCounter++;
	  return true;
   }

   public final void clearMoodStatistics() {
	  this.startMoodTime = 0;
	  this.shuggleCounter = 0;
   }

   public final void setStartMoodTime(long startMoodTime) {
	  this.startMoodTime = startMoodTime;
   }

   /**
    * @return moodCdStarted
    */
   public long getMoodCdStarted() {
	  return moodCdStarted;
   }

   /**
    * @param moodCdStarted the moodCdStarted to set
    */
   public void setMoodCdStarted(long moodCdStarted) {
	  this.moodCdStarted = moodCdStarted;
   }

   public int getMoodRemainingTime() {
	  long stop = moodCdStarted + 600000;
	  long remains = stop - System.currentTimeMillis();
	  if (remains <= 0) {
		 setMoodCdStarted(0);
		 return 0;
	  }
	  return (int) (remains / 1000);
   }

   /**
    * @return the giftCdStarted
    */
   public long getGiftCdStarted() {
	  return giftCdStarted;
   }

   /**
    * @param giftCdStarted the giftCdStarted to set
    */
   public void setGiftCdStarted(long giftCdStarted) {
	  this.giftCdStarted = giftCdStarted;
   }

   public int getGiftRemainingTime() {
	  long stop = giftCdStarted + 3600 * 1000;
	  long remains = stop - System.currentTimeMillis();
	  if (remains <= 0) {
		 setGiftCdStarted(0);
		 return 0;
	  }
	  return (int) (remains / 1000);
   }

   /**
    * @return the despawnTime
    */
   public Timestamp getDespawnTime() {
	  return despawnTime;
   }

   /**
    * @param despawnTime the despawnTime to set
    */
   public void setDespawnTime(Timestamp despawnTime) {
	  this.despawnTime = despawnTime;
   }

   /**
    * Saves mood data to DB
    */
   public void savePetMoodData() {
	  DAOManager.getDAO(PlayerPetsDAO.class).savePetMoodData(this);
   }

   /**
    * @return feedProgress, null if pet has no feed function
    */
   public PetFeedProgress getFeedProgress() {
	  return feedProgress;
   }

   public void setIsLooting(boolean isLooting) {
	  this.isLooting = isLooting;
   }

   public boolean isLooting() {
	  return this.isLooting;
   }

   public PetDopingBag getDopingBag() {
	  return dopingBag;
   }

   public void setIsBuffing(boolean isBuffing) {
	  this.isBuffing = isBuffing;
   }

   public boolean isBuffing() {
	  return this.isBuffing;
   }

   //pet id is not unique for adopt action, this not explicit
   /**
   public AdoptPetAction getAdoptAction() {
	  ItemTemplate eggTemplate = DataManager.ITEM_DATA.getPetEggTemplate(petId);
	  if (eggTemplate == null || eggTemplate.getActions() == null)
		 return null;
	  return eggTemplate.getActions().getAdoptPetAction();
   }
   */

   @Override
   public int getExpireTime() {
	  return expireTime;
   }

   @Override
   public void expireEnd(Player player) {
	  if (player == null)
		 return;
	  PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_PET_ABANDON_EXPIRE_TIME_COMPLETE(name));
	  PetAdoptionService.surrenderPet(player, petId);
   }

   @Override
   public boolean canExpireNow() {
	  return true;
   }

   @Override
   public void expireMessage(Player player, int time) {
   }
}
