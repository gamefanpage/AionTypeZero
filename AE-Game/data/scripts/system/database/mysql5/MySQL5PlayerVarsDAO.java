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
import java.util.Map;

import javolution.util.FastMap;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.PlayerVarsDAO;

/**
 * @author KID
 */
public class MySQL5PlayerVarsDAO extends PlayerVarsDAO {

	@Override
	public Map<String, Object> load(final int playerId) {
		final Map<String, Object> map = FastMap.newInstance();
		DB.select("SELECT param,value FROM player_vars WHERE player_id=?", new ParamReadStH() {

			@Override
			public void handleRead(ResultSet rset) throws SQLException {
				while (rset.next()) {
					String key = rset.getString("param");
					String value = rset.getString("value");
					map.put(key, value);
				}
			}

			@Override
			public void setParams(PreparedStatement st) throws SQLException {
				st.setInt(1, playerId);
			}
		});

		return map;
	}

	@Override
	public boolean set(final int playerId, final String key, final Object value) {
		boolean result = DB.insertUpdate(
			"INSERT INTO player_vars (`player_id`, `param`, `value`, `time`) VALUES (?,?,?,NOW())", new IUStH() {

				@Override
				public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
					stmt.setInt(1, playerId);
					stmt.setString(2, key);
					stmt.setString(3, value.toString());
					stmt.execute();
				}
			});

		return result;
	}

    @Override
    public boolean upd(final int playerId, final String key, final Object value) {
        boolean result = DB.insertUpdate(
                "UPDATE player_vars set param = ?, value = ? WHERE player_id = ?", new IUStH() {

            @Override
            public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
                stmt.setInt(1, playerId);
                stmt.setString(2, key);
                stmt.setString(3, value.toString());
                stmt.execute();
            }
        });

        return result;
    }


    @Override
	public boolean remove(final int playerId, final String key) {
		boolean result = DB.insertUpdate("DELETE FROM player_vars WHERE player_id=? AND param=?", new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				stmt.setInt(1, playerId);
				stmt.setString(2, key);
				stmt.execute();
			}
		});

		return result;
	}

	@Override
	public boolean supports(String s, int i, int i1) {
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
