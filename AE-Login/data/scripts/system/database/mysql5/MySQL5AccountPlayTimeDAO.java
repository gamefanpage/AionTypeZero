package mysql5;

import com.aionemu.commons.database.DB;
import com.aionengine.loginserver.dao.AccountPlayTimeDAO;
import com.aionengine.loginserver.model.AccountTime;


/**
 * @author Antraxx
 */
public class MySQL5AccountPlayTimeDAO extends AccountPlayTimeDAO {

	@Override
	public boolean update(final Integer accountId, final AccountTime accountTime) {
		String sql = "INSERT INTO account_playtime (`account_id`,`accumulated_online`) VALUES (" + accountId + ", " + accountTime.getAccumulatedOnlineTime() + ") " +
			"ON DUPLICATE KEY UPDATE `accumulated_online` = `accumulated_online` + " + accountTime.getAccumulatedOnlineTime();
		return DB.insertUpdate(sql);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String database, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(database, majorVersion, minorVersion);
	}

}
