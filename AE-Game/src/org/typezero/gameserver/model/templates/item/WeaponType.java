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

package org.typezero.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author ATracer
 */
@XmlType(name = "weapon_type")
@XmlEnum
public enum WeaponType {
    DAGGER_1H(new int[]{66, 9}, 1),
    MACE_1H(new int[]{39, 46}, 1),
    SWORD_1H(new int[]{37, 44}, 1),
    TOOLHOE_1H(new int[]{}, 1),
    GUN_1H(new int[]{117, 112}, 1),
    BOOK_2H(new int[]{100}, 2),
    ORB_2H(new int[]{100}, 2),
    POLEARM_2H(new int[]{52}, 2),
    STAFF_2H(new int[]{89}, 2),
    SWORD_2H(new int[]{51}, 2),
    TOOLPICK_2H(new int[]{}, 2),
    TOOLROD_2H(new int[]{}, 2),
    BOW(new int[]{53}, 2),
    CANNON_2H(new int[]{113}, 2),
    HARP_2H(new int[]{124, 114}, 2),
    GUN_2H(new int[]{}, 2),
    KEYBLADE_2H(new int[]{115}, 2),
    KEYHAMMER_2H(new int[]{}, 2);

    private int[] requiredSkill;
    private int slots;

    private WeaponType(int[] requiredSkills, int slots) {
        this.requiredSkill = requiredSkills;
        this.slots = slots;
    }

    public int[] getRequiredSkills() {
        return requiredSkill;
    }

    public int getRequiredSlots() {
        return slots;
    }

    /**
     * @return int
     */
    public int getMask() {
        return 1 << this.ordinal();
    }
}
