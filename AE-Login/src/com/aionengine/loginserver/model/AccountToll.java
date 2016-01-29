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

/**
 * @author Dr2co
 */


public class AccountToll {

    private long tollCount;
    private long bonusTollCount;

    public void setToll(long toll) {
        this.tollCount = toll;
    }

    public void setBonusToll(long toll) {
        this.bonusTollCount = toll;
    }

    public long getToll() {
        return tollCount;
    }

    public long getBonusToll() {
        return bonusTollCount;
    }

    public long getTollsByType(byte type) {
        switch (type) {
            case 0:
                return tollCount;
            case 1:
                return bonusTollCount;
            default:
                return tollCount;
        }
    }

    public long updateTolls(long cost) {
        this.tollCount -= cost;
        return tollCount;
    }

    public long updateBonusTolls(long cost) {
        this.bonusTollCount -= cost;
        return bonusTollCount;
    }
}
