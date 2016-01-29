package com.aionengine.loginserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionengine.loginserver.model.AccountToll;

/**
 * @author KID, Dr2co
 */
public abstract class PremiumDAO implements DAO {

    public abstract AccountToll getTolls(int accountId);

    public abstract boolean updateTolls(int accountId, AccountToll tolls, long cost, byte type);

    @Override
    public final String getClassName() {
        return PremiumDAO.class.getName();
    }
}
