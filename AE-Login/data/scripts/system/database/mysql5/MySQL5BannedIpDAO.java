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

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.commons.database.ReadStH;
import com.aionengine.loginserver.dao.BannedIpDAO;
import com.aionengine.loginserver.model.BannedIP;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * BannedIP DAO implementation for MySQL5
 *
 * @author SoulKeeper
 */
public class MySQL5BannedIpDAO extends BannedIpDAO {

    @Override
    public BannedIP insert(String mask) {
        return insert(mask, null);
    }

    @Override
    public BannedIP insert(final String mask, final Timestamp expireTime) {
        BannedIP result = new BannedIP();
        result.setMask(mask);
        result.setTimeEnd(expireTime);

        if (insert(result)) {
            return result;
        }
        return null;
    }

    @Override
    public boolean insert(final BannedIP bannedIP) {
        boolean insert = DB.insertUpdate("INSERT INTO banned_ip(mask, time_end) VALUES (?, ?)", new IUStH() {

            @Override
            public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, bannedIP.getMask());
                if (bannedIP.getTimeEnd() == null)
                    preparedStatement.setNull(2, Types.TIMESTAMP);
                else
                    preparedStatement.setTimestamp(2, bannedIP.getTimeEnd());
                preparedStatement.execute();
            }
        });

        if (!insert)
            return false;

        final BannedIP result = new BannedIP();
        DB.select("SELECT * FROM banned_ip WHERE mask = ?", new ParamReadStH() {

            @Override
            public void setParams(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, bannedIP.getMask());
            }

            @Override
            public void handleRead(ResultSet resultSet) throws SQLException {
                resultSet.next(); // mask is unique, only one result allowed
                result.setId(resultSet.getInt("id"));
                result.setMask(resultSet.getString("mask"));
                result.setTimeEnd(resultSet.getTimestamp("time_end"));
            }
        });
        return true;
    }

    @Override
    public boolean update(final BannedIP bannedIP) {
        return DB.insertUpdate("UPDATE banned_ip SET mask = ?, time_end = ? WHERE id = ?", new IUStH() {

            @Override
            public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, bannedIP.getMask());
                if (bannedIP.getTimeEnd() == null)
                    preparedStatement.setNull(2, Types.TIMESTAMP);
                else
                    preparedStatement.setTimestamp(2, bannedIP.getTimeEnd());
                preparedStatement.setInt(3, bannedIP.getId());
                preparedStatement.execute();
            }
        });
    }

    @Override
    public boolean remove(final String mask) {
        return DB.insertUpdate("DELETE FROM banned_ip WHERE mask = ?", new IUStH() {

            @Override
            public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setString(1, mask);
                preparedStatement.execute();
            }
        });
    }

    @Override
    public boolean remove(final BannedIP bannedIP) {
        return DB.insertUpdate("DELETE FROM banned_ip WHERE mask = ?", new IUStH() {

            @Override
            public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
                // Changed from id to mask because we don't get id of last inserted ban
                preparedStatement.setString(1, bannedIP.getMask());
                preparedStatement.execute();
            }
        });
    }

    @Override
    public Set<BannedIP> getAllBans() {

        final Set<BannedIP> result = new HashSet<BannedIP>();
        DB.select("SELECT * FROM banned_ip", new ReadStH() {

            @Override
            public void handleRead(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    BannedIP ip = new BannedIP();
                    ip.setId(resultSet.getInt("id"));
                    ip.setMask(resultSet.getString("mask"));
                    ip.setTimeEnd(resultSet.getTimestamp("time_end"));
                    result.add(ip);
                }
            }
        });
        return result;
    }

    @Override
    public void cleanExpiredBans() {
        DB.insertUpdate("DELETE FROM banned_ip WHERE time_end < current_timestamp AND time_end IS NOT NULL");
    }

    @Override
    public boolean supports(String s, int i, int i1) {
        return MySQL5DAOUtils.supports(s, i, i1);
    }
}
