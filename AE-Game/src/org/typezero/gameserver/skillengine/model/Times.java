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

package org.typezero.gameserver.skillengine.model;

import java.util.HashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.templates.item.WeaponType;


/**
 * @author kecims
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Times")
public class Times {

    @XmlAttribute(required = true)
    protected String times;

    @XmlTransient
    private HashMap<WeaponTypeWrapper, Integer> timeForWeaponType = new HashMap<WeaponTypeWrapper, Integer>();

    public String getTimes() {
        return times;
    }

    /**
     * @param times the times to set
     */
    public void setTimes(String times) {
        this.times = times;
    }

    public int getTimeForWeapon(WeaponTypeWrapper weapon) {
        return timeForWeaponType.get(weapon);
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        String[] tokens = times.split(",");
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.BOOK_2H, null), Integer.parseInt(tokens[0]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.BOW, null), Integer.parseInt(tokens[1]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.DAGGER_1H, null), Integer.parseInt(tokens[2]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.MACE_1H, null), Integer.parseInt(tokens[3]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.ORB_2H, null), Integer.parseInt(tokens[4]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.POLEARM_2H, null), Integer.parseInt(tokens[5]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.STAFF_2H, null), Integer.parseInt(tokens[6]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.SWORD_1H, null), Integer.parseInt(tokens[7]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.SWORD_2H, null), Integer.parseInt(tokens[8]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.SWORD_1H, WeaponType.SWORD_1H), Integer.parseInt(tokens[9]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.DAGGER_1H, WeaponType.DAGGER_1H), Integer.parseInt(tokens[10]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.GUN_1H, null), Integer.parseInt(tokens[11]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.GUN_1H, WeaponType.GUN_1H), Integer.parseInt(tokens[12]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.TOOLHOE_1H, null), Integer.parseInt(tokens[13]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.TOOLHOE_1H, WeaponType.TOOLHOE_1H), Integer.parseInt(tokens[14]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.TOOLPICK_2H, null), Integer.parseInt(tokens[15]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.TOOLROD_2H, null), Integer.parseInt(tokens[16]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.CANNON_2H, null), Integer.parseInt(tokens[17]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.HARP_2H, null), Integer.parseInt(tokens[18]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.GUN_2H, null), Integer.parseInt(tokens[19]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.KEYBLADE_2H, null), Integer.parseInt(tokens[20]));
        timeForWeaponType.put(new WeaponTypeWrapper(WeaponType.KEYHAMMER_2H, null), Integer.parseInt(tokens[21]));
    }
}
