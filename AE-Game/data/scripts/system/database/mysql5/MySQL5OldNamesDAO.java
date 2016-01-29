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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.OldNamesDAO;

/**
 * @author synchro2
 */

public class MySQL5OldNamesDAO extends OldNamesDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5OldNamesDAO.class);

	private static final String INSERT_QUERY = "INSERT INTO `old_names` (`player_id`, `old_name`, `new_name`) VALUES (?,?,?)";

	@Override
	public boolean isOldName(final String name) {
		PreparedStatement s = DB
			.prepareStatement("SELECT count(player_id) as cnt FROM old_names WHERE ? = old_names.old_name");
		try {
			s.setString(1, name);
			ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getInt("cnt") > 0;
		}
		catch (SQLException e) {
			log.error("Can't check if name " + name + ", is used, returning possitive result", e);
			return true;
		}
		finally {
			DB.close(s);
		}
	}

	@Override
	public void insertNames(final int id, final String oldname, final String newname) {
		DB.insertUpdate(INSERT_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				stmt.setInt(1, id);
				stmt.setString(2, oldname);
				stmt.setString(3, newname);
				stmt.execute();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String s, int i, int i1) {
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
