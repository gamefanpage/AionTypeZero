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
import java.sql.SQLException;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.InGameShopLogDAO;
import org.typezero.gameserver.dao.MySQL5DAOUtils;


/**
 * @author ViAl
 *
 */
public class MySQL5InGameShopLogDAO extends InGameShopLogDAO {

	private static final Logger log = LoggerFactory.getLogger(InGameShopLogDAO.class);
	private static final String INSERT_QUERY = "INSERT INTO `ingameshop_log` (`transaction_type`, `transaction_date`, `payer_name`, `payer_account_name`, `receiver_name`, `item_id`, `item_count`, `item_price`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

	@Override
	public void log(String transactionType, Timestamp transactionDate, String payerName, String payerAccountName, String receiverName,
		int itemId, long itemCount, long itemPrice) {
		Connection conn = null;
		try {
			conn = DatabaseFactory.getConnection();
			PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY);
			stmt.setString(1, transactionType);
			stmt.setTimestamp(2, transactionDate);
			stmt.setString(3, payerName);
			stmt.setString(4, payerAccountName);
			stmt.setString(5, receiverName);
			stmt.setInt(6, itemId);
			stmt.setLong(7, itemCount);
			stmt.setLong(8, itemPrice);
			stmt.executeUpdate();
			stmt.close();
		}
		catch (SQLException e) {
			log.error("Error while inserting ingameshop log. " + e);
		}
		finally {
			DatabaseFactory.close(conn);
		}
	}

	@Override
	public boolean supports(String s, int i, int i1) {
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
