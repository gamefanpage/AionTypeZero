package com.aionengine.loginserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionengine.loginserver.model.base.BannedMacEntry;

import java.util.Map;

/**
 * @author KID
 */
public abstract class BannedMacDAO implements DAO {
    public abstract boolean update(BannedMacEntry entry);

    public abstract boolean remove(String address);

    public abstract Map<String, BannedMacEntry> load();

    public abstract void cleanExpiredBans();

    @Override
    public final String getClassName() {
        return BannedMacDAO.class.getName();
    }
}
