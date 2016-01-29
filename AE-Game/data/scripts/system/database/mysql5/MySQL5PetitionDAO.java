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

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.PetitionDAO;
import org.typezero.gameserver.model.Petition;
import org.typezero.gameserver.model.PetitionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zdead
 */
public class MySQL5PetitionDAO extends PetitionDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5PetitionDAO.class);

	@Override
	public synchronized int getNextAvailableId() {
		Connection con = null;
		int result = 0;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT MAX(id) as nextid FROM petitions");
			ResultSet rset = stmt.executeQuery();
			rset.next();
			result = rset.getInt("nextid") + 1;
			stmt.close();
		}
		catch (Exception e) {
			log.error("Cannot get next available petition id", e);
			return 0;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return result;
	}

	@Override
	public Petition getPetitionById(int petitionId) {
		String query = "SELECT * FROM petitions WHERE id = ?";
		Connection con = null;
		Petition result = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, petitionId);
			ResultSet rset = stmt.executeQuery();
			if (!rset.next()) {
				return null;
			}

			String statusValue = rset.getString("status");
			PetitionStatus status;
			if (statusValue.equals("PENDING"))
				status = PetitionStatus.PENDING;
			else if (statusValue.equals("IN_PROGRESS"))
				status = PetitionStatus.IN_PROGRESS;
			else
				status = PetitionStatus.PENDING;

			result = new Petition(rset.getInt("id"), rset.getInt("player_id"), rset.getInt("type"), rset.getString("title"),
				rset.getString("message"), rset.getString("add_data"), status.getElementId());

			stmt.close();
		}
		catch (Exception e) {
			log.error("Cannot get petition #" + petitionId, e);
		}
		finally {
			DatabaseFactory.close(con);
		}
		return result;
	}

	@Override
	public Set<Petition> getPetitions() {
		String query = "SELECT * FROM petitions WHERE status = 'PENDING' OR status = 'IN_PROGRESS' ORDER BY id ASC";
		Connection con = null;
		Set<Petition> results = new HashSet<Petition>();
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(query);
			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				String statusValue = rset.getString("status");
				PetitionStatus status;
				if (statusValue.equals("PENDING"))
					status = PetitionStatus.PENDING;
				else if (statusValue.equals("IN_PROGRESS"))
					status = PetitionStatus.IN_PROGRESS;
				else
					status = PetitionStatus.PENDING;

				Petition p = new Petition(rset.getInt("id"), rset.getInt("player_id"), rset.getInt("type"),
					rset.getString("title"), rset.getString("message"), rset.getString("add_data"), status.getElementId());
				results.add(p);
			}
			stmt.close();
		}
		catch (Exception e) {
			log.error("Cannot get next available petition id", e);
			return null;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return results;
	}

	@Override
	public void deletePetition(int playerObjId) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con
				.prepareStatement("DELETE FROM petitions WHERE player_id = ? AND (status = 'PENDING' OR status='IN_PROGRESS')");
			stmt.setInt(1, playerObjId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Cannot delete petition", e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public void insertPetition(Petition petition) {
		Connection con = null;
		String query = "INSERT INTO petitions (id, player_id, type, title, message, add_data, time, status) VALUES(?,?,?,?,?,?,?,?)";
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, petition.getPetitionId());
			stmt.setInt(2, petition.getPlayerObjId());
			stmt.setInt(3, petition.getPetitionType().getElementId());
			stmt.setString(4, petition.getTitle());
			stmt.setString(5, petition.getContentText());
			stmt.setString(6, petition.getAdditionalData());
			stmt.setLong(7, new Date().getTime() / 1000);
			stmt.setString(8, petition.getStatus().toString());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Cannot insert petition", e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public void setReplied(int petitionId) {
		Connection con = null;
		String query = "UPDATE petitions SET status = 'REPLIED' WHERE id = ?";
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, petitionId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Cannot set petition replied", e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public boolean supports(String s, int i, int i1) {
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
