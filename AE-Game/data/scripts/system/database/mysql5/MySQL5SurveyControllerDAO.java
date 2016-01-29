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

import javolution.util.FastList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.SurveyControllerDAO;
import org.typezero.gameserver.model.templates.survey.SurveyItem;


/**
 *
 * @author KID
 *
 */
public class MySQL5SurveyControllerDAO extends SurveyControllerDAO {
	private static final Logger log = LoggerFactory.getLogger(MySQL5SurveyControllerDAO.class);
	public static final String UPDATE_QUERY = "UPDATE `surveys` SET `used`=?, used_time=NOW() WHERE `unique_id`=?";
	public static final String SELECT_QUERY = "SELECT * FROM `surveys` WHERE `used`=?";

	@Override
	public boolean supports(String arg0, int arg1, int arg2) {
		return MySQL5DAOUtils.supports(arg0, arg1, arg2);
	}

	@Override
	public FastList<SurveyItem> getAllNew() {
		FastList<SurveyItem> list = FastList.newInstance();

		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, 0);

			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				SurveyItem item = new SurveyItem();
				item.uniqueId = rset.getInt("unique_id");
				item.ownerId = rset.getInt("owner_id");
				item.itemId = rset.getInt("item_id");
				item.count = rset.getLong("item_count");
				item.html = rset.getString("html_text");
				item.radio = rset.getString("html_radio");
				list.add(item);
			}
			rset.close();
			stmt.close();
		}
		catch (Exception e) {
			log.warn("getAllNew() from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}

		return list;
	}

	@Override
	public boolean useItem(int id) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt;
			stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, 1);
			stmt.setInt(2, id);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("useItem", e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}

		return true;
	}
}
