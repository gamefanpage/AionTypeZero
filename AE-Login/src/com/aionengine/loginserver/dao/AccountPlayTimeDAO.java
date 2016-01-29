package com.aionengine.loginserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionengine.loginserver.model.AccountTime;

/**
 * @author Antraxx
 */
public abstract class AccountPlayTimeDAO implements DAO {

	/**
	 * Update the playtime for character 
	 * 
	 * @param accountId
	 * @param accountTime
	 * @return
	 */
	public abstract boolean update(Integer accountId, AccountTime accountTime);

	@Override
	public final String getClassName() {
		return AccountPlayTimeDAO.class.getName();
	}

}
