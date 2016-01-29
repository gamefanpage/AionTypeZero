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
import com.aionemu.commons.database.ReadStH;
import com.aionengine.loginserver.GameServerInfo;
import com.aionengine.loginserver.dao.GameServersDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * GameServers DAO implementation for MySQL5
 *
 * @author -Nemesiss-
 */
public class MySQL5GameServersDAO extends GameServersDAO {

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Byte, GameServerInfo> getAllGameServers() {

        final Map<Byte, GameServerInfo> result = new HashMap<Byte, GameServerInfo>();
        DB.select("SELECT * FROM gameservers", new ReadStH() {

            @Override
            public void handleRead(ResultSet resultSet) throws SQLException {
                while (resultSet.next()) {
                    byte id = resultSet.getByte("id");
                    String ipMask = resultSet.getString("mask");
                    String password = resultSet.getString("password");
                    boolean gmOnly = resultSet.getInt("gm_only") == 1 ? true : false;
                    GameServerInfo gsi = new GameServerInfo(id, ipMask, password, gmOnly);
                    result.put(id, gsi);
                }
            }
        });
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(String s, int i, int i1) {
        return MySQL5DAOUtils.supports(s, i, i1);
    }
}
