/* Copyright (C) 2013 Dr2co
 *
 * Created with IntelliJ IDEA.
 * User: Dr2co
 * Date: 01.07.13
 *
 *  pt-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  pt-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with pt-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionengine.loginserver.model;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

/*
 * @author Dr2co
 *
 */

public class AccountMembership {

    /**
     * Membership of this account
     */
    private byte membership;
    /**
     * Craftship of this account
     */
    private byte craftship;
    /**
     * Apship of this account
     */
    private byte apship;
    /**
     * Collectionship of this account
     */
    private byte collectionship;
    /**
     * The Membership expire time
     */
    private Date membershipExpire;
    /**
     * The Craftship expire time
     */
    private Date craftshipExpire;
    /**
     * The Apship expire time
     */
    private Date apshipExpire;
    /**
     * The Collectionship expire time
     */
    private Date collectionshipExpire;

    /**
     * @return the membership
     */
    public byte getMembership() {
        return membership;
    }

    /**
     * @return the membership
     */
    public byte getCraftship() {
        return craftship;
    }

    /**
     * @return the membership
     */
    public byte getApship() {
        return apship;
    }

    /**
     * @return the membership
     */
    public byte getCollectionship() {
        return collectionship;
    }

    /**
     * @param membership the membership to set
     */
    public void setMembership(byte membership) {
        this.membership = membership;
    }

    /**
     * @param craftship the membership to set
     */
    public void setCraftship(byte craftship) {
        this.craftship = craftship;
    }

    /**
     * @param apship the membership to set
     */
    public void setApship(byte apship) {
        this.apship = apship;
    }

    /**
     * @param apship the membership to set
     */
    public void setCollectionship(byte collectionship) {
        this.collectionship = collectionship;
    }

    public Date getMembershipExpire() {
        return membershipExpire;
    }

    public Date getCraftshipExpire() {
        return craftshipExpire;
    }

    public Date getApshipExpire() {
        return apshipExpire;
    }

    public Date getCollectionshipExpire() {
        return collectionshipExpire;
    }

    /**
     * Sets premium expire time
     *
     * @param premiEnd sets premium expire time
     */
    public void setMembershipExpire(Date expire) {
        this.membershipExpire = expire;
    }

    /**
     * Sets premium expire time
     *
     * @param premiEnd sets premium expire time
     */
    public void setCraftshipExpire(Date expire) {
        this.craftshipExpire = expire;
    }

    /**
     * Sets premium expire time
     *
     * @param premiEnd sets premium expire time
     */
    public void setApshipExpire(Date expire) {
        this.apshipExpire = expire;
    }

    /**
     * Sets premium expire time
     *
     * @param premiEnd sets premium expire time
     */
    public void setCollectionshipExpire(Date expire) {
        this.collectionshipExpire = expire;
    }

    public void setMemberShipByType(AccountMembershipType type, byte ship) {
        switch (type) {
            case REGULAR:
                setMembership(ship);
                break;
            case CRAFT:
                setCraftship(ship);
                break;
            case AP:
                setApship(ship);
                break;
            case COLLECTION:
                setCollectionship(ship);
                break;
        }

    }

    public void updateMemberShipExpire(AccountMembershipType type, int day) {
        if (day < 1) {
            return;
        }
        Calendar calendar = GregorianCalendar.getInstance();
        switch (type) {
            case REGULAR:
                calendar.setTimeInMillis(membershipExpire != null ? membershipExpire.getTime() > 0 ? membershipExpire.getTime() : System.currentTimeMillis() : System.currentTimeMillis());
                break;
            case AP:
                calendar.setTimeInMillis(apshipExpire != null ? apshipExpire.getTime() > 0 ? apshipExpire.getTime() : System.currentTimeMillis() : System.currentTimeMillis());
                break;
            case CRAFT:
                calendar.setTimeInMillis(craftshipExpire != null ? craftshipExpire.getTime() > 0 ? craftshipExpire.getTime() : System.currentTimeMillis() : System.currentTimeMillis());
                break;
            case COLLECTION:
                calendar.setTimeInMillis(collectionshipExpire != null ? collectionshipExpire.getTime() > 0 ? collectionshipExpire.getTime() : System.currentTimeMillis() : System.currentTimeMillis());
                break;
        }

        calendar.add(Calendar.DATE, day);
        switch (type) {
            case REGULAR:
                setMembershipExpire(new Date(calendar.getTimeInMillis()));
                break;
            case AP:
                setApshipExpire(new Date(calendar.getTimeInMillis()));
                break;
            case CRAFT:
                setCraftshipExpire(new Date(calendar.getTimeInMillis()));
                break;
            case COLLECTION:
                setCollectionshipExpire(new Date(calendar.getTimeInMillis()));
                break;
        }
    }
}
