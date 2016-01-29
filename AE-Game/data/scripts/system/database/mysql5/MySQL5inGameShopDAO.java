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

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.InGameShopDAO;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.model.ingameshop.IGItem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xTz
 */
public class MySQL5inGameShopDAO extends InGameShopDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5inGameShopDAO.class);
	public static final String SELECT_QUERY = "SELECT `object_id`, `item_id`, `item_count`, `item_price`, `category`, `sub_category`, `list`, `sales_ranking`, `item_type`, `gift`, `title_description`, `description` FROM `ingameshop`";
	public static final String DELETE_QUERY = "DELETE FROM `ingameshop` WHERE `item_id`=? AND `category`=? AND `sub_category`=? AND `list`=?";
	public static final String UPDATE_SALES_QUERY = "UPDATE `ingameshop` SET `sales_ranking`=? WHERE `object_id`=?";

	@Override
	public FastMap<Byte, List<IGItem>> loadInGameShopItems() {
		FastMap<Byte, List<IGItem>> items = FastMap.newInstance();
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				byte category = rset.getByte("category");
				byte subCategory = rset.getByte("sub_category");
				if (subCategory < 3)
					continue;

				int objectId = rset.getInt("object_id");
				int itemId = rset.getInt("item_id");
				long itemCount = rset.getLong("item_count");
				long itemPrice = rset.getLong("item_price");
				int list = rset.getInt("list");
				int salesRanking = rset.getInt("sales_ranking");
				byte itemType = rset.getByte("item_type");
				byte gift = rset.getByte("gift");
				String titleDescription = rset.getString("title_description");
				String description = rset.getString("description");
				if (!items.containsKey(category)) {
					items.put(category, new ArrayList<IGItem>());
				}
				items.get(category).add(new IGItem(objectId, itemId, itemCount, itemPrice,
						category, subCategory, list, salesRanking, itemType, gift, titleDescription, description));
			}
			rset.close();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not restore inGameShop data for all from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
		return items;
	}

	@Override
	public boolean deleteIngameShopItem(int itemId, byte category, byte subCategory, int list) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, itemId);
			stmt.setInt(2, category);
			stmt.setInt(3, subCategory);
			stmt.setInt(4, list);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error delete ingameshopItem: " + itemId, e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return true;
	}

	@Override
	public void saveIngameShopItem(int objectId, int itemId, long itemCount, long itemPrice, byte category, byte subCategory, int list, int salesRanking,
			byte itemType, byte gift, String titleDescription, String description) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con
				.prepareStatement("INSERT INTO ingameshop(object_id, item_id, item_count, item_price, category, sub_category, list, sales_ranking, item_type, gift, title_description, description)"
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			stmt.setInt(1, objectId);
			stmt.setInt(2, itemId);
			stmt.setLong(3, itemCount);
			stmt.setLong(4, itemPrice);
			stmt.setByte(5, category);
			stmt.setByte(6, subCategory);
			stmt.setInt(7, list);
			stmt.setInt(8, salesRanking);
			stmt.setByte(9, itemType);
			stmt.setByte(10, gift);
			stmt.setString(11, titleDescription);
			stmt.setString(12, description);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error saving Item: " + objectId, e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public boolean increaseSales(int object, int current) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_SALES_QUERY);
			stmt.setInt(1, current);
			stmt.setInt(2, object);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error increaseSales Item: " + object, e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}

		return true;
	}

	@Override
	public boolean supports(String s, int i, int i1) {
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
