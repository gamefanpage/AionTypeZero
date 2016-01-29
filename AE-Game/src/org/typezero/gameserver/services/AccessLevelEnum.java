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

import org.typezero.gameserver.configs.administration.AdminConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;

public enum AccessLevelEnum {
    AccessLevel1(1, AdminConfig.CUSTOMTAG_ACCESS1, "\ue042Supporter\ue043", new int[]{240, 241}),
    AccessLevel2(2, AdminConfig.CUSTOMTAG_ACCESS2, "\ue042Game Master\ue043", new int[]{240, 241, 277}),
    AccessLevel3(3, AdminConfig.CUSTOMTAG_ACCESS3, "\ue042Event Game Master\ue043", new int[]{240, 241, 277}),
    AccessLevel4(4, AdminConfig.CUSTOMTAG_ACCESS4, "\ue042Head Game Master\ue043", new int[]{240, 241, 277}),
    AccessLevel5(5, AdminConfig.CUSTOMTAG_ACCESS5, "\ue042Administrator\ue043", new int[]{240, 241, 277, 282}),
    AccessLevel6(6, AdminConfig.CUSTOMTAG_ACCESS6, "\ue042Developer\ue043", new int[]{240, 241, 277, 282});

    private final int level;
    private final String nameLevel;
    private String status;
    private int[] skills;

    AccessLevelEnum(int id, String name, String status, int[] skills) {
        this.level = id;
        this.nameLevel = name;
        this.status = status;
        this.skills = skills;
    }

    public String getName() {
        return nameLevel;
    }

    public int getLevel() {
        return level;
    }

    public String getStatusName() {
        return status;
    }

    public int[] getSkills() {
        return skills;
    }

    public static AccessLevelEnum getAlType(int level) {
        for (AccessLevelEnum al : AccessLevelEnum.values()) {
            if (level == al.getLevel()) {
                return al;
            }
        }
        return null;
    }

    public static String getAlName(int level) {
        for (AccessLevelEnum al : AccessLevelEnum.values()) {
            if (level == al.getLevel()) {
                return al.getName();
            }
        }
        return "%s";
    }

    public static String getStatusName(Player player) {
        return player.getAccessLevel() > 0 ? AccessLevelEnum.getAlType(player.getAccessLevel()).getStatusName() : player.getLegion().getLegionName();
    }
}
