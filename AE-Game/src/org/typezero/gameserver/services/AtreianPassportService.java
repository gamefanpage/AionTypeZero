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

package org.typezero.gameserver.services;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.AttendType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.model.templates.event.AtreianPassport;
import org.typezero.gameserver.network.aion.serverpackets.SM_ATREIAN_PASSPORT;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.item.ItemPacketService.ItemAddType;
import org.typezero.gameserver.services.item.ItemPacketService.ItemUpdateType;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.services.item.ItemService.ItemUpdatePredicate;
import org.typezero.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author Alcapwnd
 *         <p/>
 *         TODO
 *         Check on reward taking the multiple taking
 *         I think we need to rework this shit in the future, maybe it cant handle it long...
 */
public class AtreianPassportService {
    private static final Logger log = LoggerFactory.getLogger(AtreianPassportService.class);
    private Timestamp t = new Timestamp(Calendar.getInstance().getTime().getTime() - 20);
    private Map<Integer, AtreianPassport> cumu = new HashMap<Integer, AtreianPassport>(1);
    private Map<Integer, AtreianPassport> daily = new HashMap<Integer, AtreianPassport>(1);
    private Map<Integer, AtreianPassport> anny = new HashMap<Integer, AtreianPassport>(1);
    public Map<Integer, AtreianPassport> data = new HashMap<Integer, AtreianPassport>(1);
    private Calendar calendar = Calendar.getInstance();
    private int year = 0;
    private int arrival = 0;
    private int currentPassport = 0;
    private int cachedPassport = 0;
    private int check = 0;
    private boolean cumuIsActive = false;

    public void onLogin(Player player) {
        check = 0;
        year = 0;
        arrival = 0;
        currentPassport = 0;
        cachedPassport = 0;
        cumuIsActive = false;
        if (player == null)
            return;
        PlayerCommonData pcd = player.getCommonData();
        arrival = getArrival();
        checkForNewMonth(pcd);
        if (checkOnlineDate(pcd)) {
            int stamps = pcd.getPassportStamps();
            int newStamps = stamps + 1;
            pcd.setPassportStamps(newStamps);
        }
        for (AtreianPassport atp : cumu.values()) {
            if (atp.getPeriodStart().isBeforeNow() && atp.getPeriodEnd().isAfterNow()) {
                if (year == 0)
                    year = atp.getPeriodStart().getYear();
                if (atp.getAttendNum() == pcd.getPassportStamps() && checkOnlineDate(pcd)) {
                    currentPassport = atp.getId();
                    check = 1;
                    atp.setRewardId(1);
                    pcd.setPassportReward(0);
                    pcd.playerPassports.put(atp.getId(), atp);
                    cumuIsActive = true;
                } else {
                    atp.setRewardId(0);
                    pcd.playerPassports.put(atp.getId(), atp);
                    cumuIsActive = false;
                    if (currentPassport == 0)
                        currentPassport = atp.getId();
                }
            }
        }
        for (AtreianPassport atp : daily.values()) {
            if (atp.getPeriodStart().isBeforeNow() && atp.getPeriodEnd().isAfterNow()) {
                if (year == 0)
                    year = atp.getPeriodStart().getYear();
                if (checkOnlineDate(pcd)) {
                    if (currentPassport != 0 && cumuIsActive) {
                        setCachedPassport(currentPassport);
                    } else {
                        currentPassport = atp.getId();
                        check = 1;
                        atp.setRewardId(1);
                        pcd.setPassportReward(0);
                        pcd.playerPassports.put(atp.getId(), atp);
                    }
                } else if (isCached()) {
                    currentPassport = getCachedPassport();
                    check = 1;
                    atp.setRewardId(1);
                    pcd.setPassportReward(0);
                    pcd.playerPassports.put(atp.getId(), atp);
                } else {
                    atp.setRewardId(0);
                    break;
                }
            }
        }
        /*for (AtreianPassport atp : anny.values()) { //TODO
			if (atp.getPeriodStart().isBeforeNow() && atp.getPeriodEnd().isAfterNow()) {
				if (year == 0)
					year = atp.getPeriodStart().getYear();
				if (checkOnlineDate(pcd)) {
					setCachedPassport(currentPassport);
					currentPassport = atp.getId();// anni also must go first, also if there is a cumu!
					check = 1;
					atp.setRewardId(1);
					pcd.setPassportReward(0);
					pcd.playerPassports.put(atp.getId(), atp);
				} else {
					break;
				}
			}
		}*/
        pcd.setLastStamp(t);
        checkCompletedPassports(pcd);
        if (checkOnlineDate(pcd)) {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NEW_PASSPORT_AVAIBLE);
        }
        PacketSendUtility.sendPacket(player, new SM_ATREIAN_PASSPORT(format(pcd.getPlayerPassports()), pcd.getPassportStamps(), currentPassport, t, year, arrival, check));
        check = 0;
        currentPassport = 0;
        year = 0;
        arrival = 0;
        cachedPassport = 0;
        cumuIsActive = false;
        pcd.playerPassports.clear();
    }

    @SuppressWarnings("deprecation")
	private void checkForNewMonth(PlayerCommonData pcd) {
    	if (pcd.getLastStamp() == null)
    		return;

    	if (pcd.getLastStamp().getMonth() != calendar.getTime().getMonth()) {
    		pcd.setPassportStamps(0);
    	}
    }

    private void checkCompletedPassports(PlayerCommonData pcd) {
        for (AtreianPassport pp : pcd.getCompletedPassports().getAllPassports()) {
            if (pcd.getPlayerPassports().containsValue(pp)) {
                pcd.playerPassports.remove(pp);
            }
            if (pp.getRewardId() == 0) {
                if (pp.getPeriodEnd().isBeforeNow() && pp.getAttendType() == AttendType.ANNIVERSARY || pp.getPeriodEnd().isBeforeNow() && pp.getAttendType() == AttendType.DAILY) {
                    continue;
                } else {
                    pp.setRewardId(3);
                }
            } else if (pp.getRewardId() == 3 && pp.getAttendType() == AttendType.CUMULATIVE) {
                this.check = 0;
            } else if (pp.getRewardId() == 1 && pp.getAttendType() == AttendType.CUMULATIVE) {
                this.check = 1;
            } else {
                this.check = 0;
            }

            if (pp.getAttendType() == AttendType.CUMULATIVE) {
                if (pp.getAttendNum() != pcd.getPassportStamps()) {
                    pp.setRewardId(0);
                    this.check = 0;
                }
            }
            pcd.playerPassports.put(pp.getId(), pp);
        }
    }

    private Map<Integer, AtreianPassport> format(Map<Integer, AtreianPassport> atp) {
        Map<Integer, AtreianPassport> finalPassports = new TreeMap<Integer, AtreianPassport>(atp);
        return finalPassports;
    }

    private boolean checkOnlineDate(PlayerCommonData pcd) {
        long lastOnline = pcd.getLastStamp().getTime();
        long secondsOffline = (System.currentTimeMillis() / 1000) - lastOnline / 1000;
        double hours = secondsOffline / 3600d;
        if (hours > 24)
            hours = 24;

        if (hours == 24) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCached() {
        if (cachedPassport != 0) {
            return true;
        } else {
            return false;
        }
    }

    public void onStart() {
        Map<Integer, AtreianPassport> raw = DataManager.ATREIAN_PASSPORT_DATA.getAll();
        if (!raw.isEmpty()) {
            getPassports(raw);
        } else {
            log.warn("[ATREIAN PASSPORT] passports from static data = 0");
        }
        log.info("AtreianPassportService initialized");
    }

    /**
     * @param player
     * @param timestamp
     */
    public void onGetReward(Player player, int timestamp, List<Integer> passportId) {
    	for (Integer i : passportId) {
    		AtreianPassport atp = data.get(i);
    		ItemService.addItem(player, atp.getRewardItem(), atp.getRewardItemNum(), new ItemUpdatePredicate(ItemAddType.ITEM_COLLECT, ItemUpdateType.INC_PASSPORT_ADD));
            player.getCommonData().setPassportReward(1);
            if (atp.getAttendType() != AttendType.DAILY)
                player.getCommonData().addToCompletedPassports(atp);
    	}
        onLogin(player);
    }

    public void getPassports(Map<Integer, AtreianPassport> raw) {
        data.putAll(raw);
        for (AtreianPassport atp : data.values()) {
            switch (atp.getAttendType()) {
                case DAILY:
                    getDailyPassports(atp.getId(), atp);
                    break;
                case CUMULATIVE:
                    getCumulativePassports(atp.getId(), atp);
                    break;
                case ANNIVERSARY:
                    getAnniversaryPassports(atp.getId(), atp);
                    break;
            }
        }
        log.info("[ATREIAN PASSPORT] Loaded " + daily.size() + " daily passports");
        log.info("[ATREIAN PASSPORT] Loaded " + cumu.size() + " cumulative passports");
        log.info("[ATREIAN PASSPORT] Loaded " + anny.size() + " anniversary passports");
    }

    public void getDailyPassports(int id, AtreianPassport atp) {
        if (daily.containsValue(id))
            return;
        daily.put(id, atp);
    }

    public void getCumulativePassports(int id, AtreianPassport atp) {
        if (cumu.containsValue(id))
            return;
        cumu.put(id, atp);
    }

    public void getAnniversaryPassports(int id, AtreianPassport atp) {
        if (anny.containsValue(id))
            return;
        anny.put(id, atp);
    }

    public int getArrival() {
        switch (calendar.get(Calendar.MONTH)) {
            case Calendar.NOVEMBER:
                return 1;
            case Calendar.DECEMBER:
                return 2;
            case Calendar.JANUARY:
                return 3;
            case Calendar.FEBRUARY:
                return 4;
            case Calendar.MARCH:
                return 5;
            case Calendar.APRIL:
                return 6;
            case Calendar.MAY:
                return 7;
            case Calendar.JUNE:
                return 8;
            case Calendar.JULY:
                return 9;
            case Calendar.AUGUST:
                return 10;
            case Calendar.SEPTEMBER:
                return 11;
            case Calendar.OCTOBER:
                return 12;
            default:
                return 0;
        }
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {

        protected static final AtreianPassportService instance = new AtreianPassportService();
    }

    public static final AtreianPassportService getInstance() {
        return SingletonHolder.instance;
    }

    public int getCachedPassport() {
        return cachedPassport;
    }

    public void setCachedPassport(int cachedPassport) {
        this.cachedPassport = cachedPassport;
    }

}
