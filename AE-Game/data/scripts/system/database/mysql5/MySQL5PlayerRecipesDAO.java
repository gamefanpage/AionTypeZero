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
import java.util.HashSet;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.PlayerRecipesDAO;
import org.typezero.gameserver.model.gameobjects.player.RecipeList;

/**
 * @author lord_rex
 */
public class MySQL5PlayerRecipesDAO extends PlayerRecipesDAO {

	private static final String SELECT_QUERY = "SELECT `recipe_id` FROM player_recipes WHERE `player_id`=?";
	private static final String ADD_QUERY = "INSERT INTO player_recipes (`player_id`, `recipe_id`) VALUES (?, ?)";
	private static final String DELETE_QUERY = "DELETE FROM player_recipes WHERE `player_id`=? AND `recipe_id`=?";

	@Override
	public RecipeList load(final int playerId) {
		final HashSet<Integer> recipeList = new HashSet<Integer>();
		DB.select(SELECT_QUERY, new ParamReadStH() {

			@Override
			public void setParams(PreparedStatement ps) throws SQLException {
				ps.setInt(1, playerId);
			}

			@Override
			public void handleRead(ResultSet rs) throws SQLException {
				while (rs.next()) {
					recipeList.add(rs.getInt("recipe_id"));
				}
			}
		});
		return new RecipeList(recipeList);
	}

	@Override
	public boolean addRecipe(final int playerId, final int recipeId) {
		return DB.insertUpdate(ADD_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
				ps.setInt(1, playerId);
				ps.setInt(2, recipeId);
				ps.execute();
			}
		});
	}

	@Override
	public boolean delRecipe(final int playerId, final int recipeId) {
		return DB.insertUpdate(DELETE_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
				ps.setInt(1, playerId);
				ps.setInt(2, recipeId);
				ps.execute();
			}
		});
	}

	@Override
	public boolean supports(String s, int i, int i1) {
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
