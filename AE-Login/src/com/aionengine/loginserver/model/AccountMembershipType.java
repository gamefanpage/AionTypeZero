/* Copyright (C) 2013 Dr2co
 *
 * Created with IntelliJ IDEA.
 * User: Dr2co
 * Date: 04.07.13
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


public enum AccountMembershipType {

    REGULAR(2),
    AP(3),
    CRAFT(4),
    COLLECTION(5);

    private int id;

    AccountMembershipType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
