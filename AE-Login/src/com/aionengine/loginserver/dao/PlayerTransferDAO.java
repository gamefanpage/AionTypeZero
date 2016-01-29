package com.aionengine.loginserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionengine.loginserver.service.ptransfer.PlayerTransferTask;
import javolution.util.FastList;

/**
 * @author KID
 */
public abstract class PlayerTransferDAO implements DAO {
    public abstract FastList<PlayerTransferTask> getNew();

    public abstract boolean update(PlayerTransferTask task);

    @Override
    public final String getClassName() {
        return PlayerTransferDAO.class.getName();
    }
}
