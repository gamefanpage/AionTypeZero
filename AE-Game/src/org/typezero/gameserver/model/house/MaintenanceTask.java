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

package org.typezero.gameserver.model.house;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

import javolution.util.FastList;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.configs.main.HousingConfig;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_ACQUIRE;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_OWNER_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.HousingBidService;
import org.typezero.gameserver.services.HousingService;
import org.typezero.gameserver.services.mail.MailFormatter;
import org.typezero.gameserver.taskmanager.AbstractCronTask;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

/**
 * @author Rolandas
 */
public class MaintenanceTask extends AbstractCronTask {

	private static final Logger log = LoggerFactory.getLogger(MaintenanceTask.class);

	private static final FastList<House> maintainedHouses;

	private static MaintenanceTask instance;

	static {
		maintainedHouses = FastList.newInstance();
		try {
			instance = new MaintenanceTask(HousingConfig.HOUSE_MAINTENANCE_TIME);
		}
		catch (ParseException pe) {
		}
	}

	public static final MaintenanceTask getInstance() {
		return instance;
	}

	private MaintenanceTask(String maintainTime) throws ParseException {
		super(maintainTime);
	}

	@Override
	protected long getRunDelay() {
		int left = (int) (getRunTime() - System.currentTimeMillis() / 1000);
		if (left < 0)
			return 0;
		return left * 1000;
	}

	@Override
	protected String getServerTimeVariable() {
		return "houseMaintainTime";
	}

    @Override
    protected boolean canRunOnInit() {
      return false;
    }

	public boolean isMaintainTime() {
		return (getRunTime() - System.currentTimeMillis() / 1000) <= 0;
	}

	@Override
	protected void preInit() {
		log.info("Initializing House maintenance task...");
	}

    @Override
    protected void preRun() {
        updateMaintainedHouses();
        log.info("Executing House maintenance. Maintained Houses: " + maintainedHouses.size());
    }

	private void updateMaintainedHouses() {
		maintainedHouses.clear();

		if (!HousingConfig.ENABLE_HOUSE_PAY)
			return;

		Date now = new Date();
		FastList<House> houses = HousingService.getInstance().getCustomHouses();
		for (House house : houses) {
			if (house.getStatus() == HouseStatus.INACTIVE)
				continue;
			if (house.getOwnerId() == 0)
				continue;
			if (house.isFeePaid()) {
				if (house.getNextPay() == null || house.getNextPay().before(now)) {
					house.setFeePaid(false);
					// if never paid, just set time to the next period
					if (house.getNextPay() == null)
						house.setNextPay(new Timestamp((long) getRunTime() * 1000));
					house.save();
				}
				else
					continue;
			}
			maintainedHouses.add(house);
		}
	}

	@Override
	protected void executeTask() {
		if (!HousingConfig.ENABLE_HOUSE_PAY)
			return;

		// Get times based on configuration values
		DateTime now = new DateTime();
		DateTime previousRun = now.minus(getPeriod()); // usually week ago
		DateTime beforePreviousRun = previousRun.minus(getPeriod()); // usually two weeks ago

		for (House house : maintainedHouses) {
			if (house.isFeePaid())
				continue; // player already paid, don't check

			long payTime = house.getNextPay().getTime();
			long impoundTime = 0;
			int warnCount = 0;

			PlayerCommonData pcd = null;
			Player player = World.getInstance().findPlayer(house.getOwnerId());
			if (player == null)
				pcd = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(house.getOwnerId());
			else
				pcd = player.getCommonData();

			if (pcd == null) {
				// player doesn't exist already for some reasons
				log.warn("House " + house.getAddress().getId() + " had player assigned but no player exists. Auctioned.");
				putHouseToAuction(house, null);
				continue;
			}

			if (payTime <= beforePreviousRun.getMillis()) {
				DateTime plusDay = beforePreviousRun.minusDays(1);
				if (payTime <= plusDay.getMillis()) {
					// player didn't pay after the second warning and one day passed
					impoundTime = now.getMillis();
					warnCount = 3;
					putHouseToAuction(house, pcd);
				}
				else {
					impoundTime = now.plusDays(1).getMillis();
					warnCount = 2;
				}
			}
			else if (payTime <= previousRun.getMillis()) {
				// player did't pay 1 period
				impoundTime = now.plus(getPeriod()).plusDays(1).getMillis();
				warnCount = 1;
			}
			else {
				continue; // should not happen
			}

			if (pcd.isOnline()) {
				if (warnCount == 3)
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_SEQUESTRATE);
				else
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OVERDUE);
			}
			MailFormatter.sendHouseMaintenanceMail(house, warnCount, impoundTime);
		}
	}

	private void putHouseToAuction(House house, PlayerCommonData playerCommonData) {
		house.revokeOwner();
		HousingBidService.getInstance().addHouseToAuction(house);
		house.save();
		log.info("House " + house.getAddress().getId() + " overdued and put to auction.");
		if (playerCommonData == null)
			return;
		if (playerCommonData.isOnline()) {
			Player player = playerCommonData.getPlayer();
			player.getHouses().remove(house);
			player.setHouseRegistry(null);
			// TODO: check this
			PacketSendUtility.sendPacket(player, new SM_HOUSE_ACQUIRE(player.getObjectId(), house.getAddress().getId(), false));
			PacketSendUtility.sendPacket(player, new SM_HOUSE_OWNER_INFO(player, null));
		}
	}

}
