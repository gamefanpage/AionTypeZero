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

package mysql5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.PlayerPetsDAO;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PetCommonData;
import org.typezero.gameserver.model.templates.pet.PetDopingBag;
import org.typezero.gameserver.services.toypet.PetHungryLevel;

/**
 * @author M@xx, xTz, Rolandas
 */
public class MySQL5PlayerPetsDAO extends PlayerPetsDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerPetsDAO.class);

	@Override
	public void saveFeedStatus(Player player, int petId, int hungryLevel, int feedProgress, long reuseTime) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con
				.prepareStatement("UPDATE player_pets SET hungry_level = ?, feed_progress = ?, reuse_time = ? WHERE player_id = ? AND pet_id = ?");
			stmt.setInt(1, hungryLevel);
			stmt.setInt(2, feedProgress);
			stmt.setLong(3, reuseTime);
			stmt.setInt(4, player.getObjectId());
			stmt.setInt(5, petId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error update pet #" + petId, e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public void saveDopingBag(Player player, int petId, PetDopingBag bag) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con
				.prepareStatement("UPDATE player_pets SET dopings = ? WHERE player_id = ? AND pet_id = ?");
			String itemIds = bag.getFoodItem() + "," + bag.getDrinkItem();
			for (int itemId : bag.getScrollsUsed())
				itemIds += "," + Integer.toString(itemId);
			stmt.setString(1, itemIds);
			stmt.setInt(2, player.getObjectId());
			stmt.setInt(3, petId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error update doping for pet #" + petId, e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public void setTime(Player player, int petId, long time) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con
				.prepareStatement("UPDATE player_pets SET reuse_time = ? WHERE player_id = ? AND pet_id = ?");
			stmt.setLong(1, time);
			stmt.setInt(2, player.getObjectId());
			stmt.setInt(3, petId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error update pet #" + petId, e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public void insertPlayerPet(PetCommonData petCommonData) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con
				.prepareStatement("INSERT INTO player_pets(player_id, pet_id, decoration, name, despawn_time, expire_time) VALUES(?, ?, ?, ?, ?, ?)");
			stmt.setInt(1, petCommonData.getMasterObjectId());
			stmt.setInt(2, petCommonData.getPetId());
			stmt.setInt(3, petCommonData.getDecoration());
			stmt.setString(4, petCommonData.getName());
			stmt.setTimestamp(5, petCommonData.getDespawnTime());
			stmt.setInt(6, petCommonData.getExpireTime());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error inserting new pet #" + petCommonData.getPetId() + "[" + petCommonData.getName() + "]", e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public void removePlayerPet(Player player, int petId) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("DELETE FROM player_pets WHERE player_id = ? AND pet_id = ?");
			stmt.setInt(1, player.getObjectId());
			stmt.setInt(2, petId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error removing pet #" + petId, e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public List<PetCommonData> getPlayerPets(Player player) {
		List<PetCommonData> pets = new ArrayList<PetCommonData>();
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM player_pets WHERE player_id = ?");
			stmt.setInt(1, player.getObjectId());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				PetCommonData petCommonData = new PetCommonData(rs.getInt("pet_id"), player.getObjectId(), rs.getInt("expire_time"));
				petCommonData.setName(rs.getString("name"));
				petCommonData.setDecoration(rs.getInt("decoration"));
				if (petCommonData.getFeedProgress() != null) {
					petCommonData.getFeedProgress().setHungryLevel(PetHungryLevel.fromId(rs.getInt("hungry_level")));
					petCommonData.getFeedProgress().setData(rs.getInt("feed_progress"));
					petCommonData.setRefeedTime(rs.getLong("reuse_time"));
				}
				if (petCommonData.getDopingBag() != null) {
					String dopings = rs.getString("dopings");
					if (dopings != null) {
						String[] ids = dopings.split(",");
						for (int i = 0; i < ids.length; i++)
							petCommonData.getDopingBag().setItem(Integer.parseInt(ids[i]), i);
					}
				}
				petCommonData.setBirthday(rs.getTimestamp("birthday"));
				if (petCommonData.getRefeedDelay() > 0) {
					petCommonData.setIsFeedingTime(false);
					petCommonData.scheduleRefeed(petCommonData.getRefeedDelay());
				}
				else if (petCommonData.getFeedProgress() != null)
				   petCommonData.getFeedProgress().setHungryLevel(PetHungryLevel.HUNGRY);
				petCommonData.setStartMoodTime(rs.getLong("mood_started"));
				petCommonData.setShuggleCounter(rs.getInt("counter"));
				petCommonData.setMoodCdStarted(rs.getLong("mood_cd_started"));
				petCommonData.setGiftCdStarted(rs.getLong("gift_cd_started"));
				Timestamp ts = null;
				try {
					ts = rs.getTimestamp("despawn_time");
				}
				catch (Exception e) {
				}
				if (ts == null)
					ts = new Timestamp(System.currentTimeMillis());
				petCommonData.setDespawnTime(ts);
				pets.add(petCommonData);
			}
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error getting pets for " + player.getObjectId(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
		return pets;
	}

	@Override
	public void updatePetName(PetCommonData petCommonData) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con
				.prepareStatement("UPDATE player_pets SET name = ? WHERE player_id = ? AND pet_id = ?");
			stmt.setString(1, petCommonData.getName());
			stmt.setInt(2, petCommonData.getMasterObjectId());
			stmt.setInt(3, petCommonData.getPetId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error update pet #" + petCommonData.getPetId(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public boolean savePetMoodData(PetCommonData petCommonData) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con
				.prepareStatement("UPDATE player_pets SET mood_started = ?, counter = ?, mood_cd_started = ?, gift_cd_started = ?, despawn_time = ? WHERE player_id = ? AND pet_id = ?");
			stmt.setLong(1, petCommonData.getMoodStartTime());
			stmt.setInt(2, petCommonData.getShuggleCounter());
			stmt.setLong(3, petCommonData.getMoodCdStarted());
			stmt.setLong(4, petCommonData.getGiftCdStarted());
			stmt.setTimestamp(5, petCommonData.getDespawnTime());
			stmt.setInt(6, petCommonData.getMasterObjectId());
			stmt.setInt(7, petCommonData.getPetId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error updating mood for pet #" + petCommonData.getPetId(), e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return true;
	}

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

}
