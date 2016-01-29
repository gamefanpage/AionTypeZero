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

package org.typezero.gameserver.model.templates.npcshout;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rolandas
 */

@XmlType(name = "ShoutEventType")
@XmlEnum
public enum ShoutEventType {

    IDLE,
    ATTACKED,					// NPC was being attacked (the same as aggro)
    ATTACK_BEGIN,			// NPC starts an attack (the same as aggro)
    ATTACK_END,				// NPC leaves FIGHT state
    ATTACK_K,					// Numeric hit shouts
    SUMMON_ATTACK,		// Summon attack
    CASTING,
    CAST_K,						// Numeric cast shouts
    DIED,							// Npc died
    HELP,							// Calls help without running away
    HELPCALL,					// Calls help and runs away
    WALK_WAYPOINT,		// Reached the walk point
    START,
    WAKEUP,
    SLEEP,
    RESET_HATE,
    UNK_ACC,					// Not clear but seems the same as ATTACKED
    WALK_DIRECTION,		// NPC reached the 0 walk point
    STATUP,						// Skill statup shouts
    SWITCH_TARGET,		// NPC switched the target
    SEE,							// NPC sees a player from aggro range
    PLAYER_MAGIC,			// Player uses magic attack (merge with attacked?)
    PLAYER_SNARE,
    PLAYER_DEBUFF,
    PLAYER_SKILL,
    PLAYER_SLAVE,
    PLAYER_BLOW,
    PLAYER_PULL,
    PLAYER_PROVOKE,
    PLAYER_CAST,
    GOD_HELP,
    LEAVE,						// when player leaves an attack
    BEFORE_DESPAWN,		// NPC despawns
    ATTACK_DEADLY,
    WIN,
    ENEMY_DIED,				// NPC's enemy died
    ENTER_BATTLE,
    LEAVE_BATTLE,
    DEFORM_SKILL,
    ATTACK_HITPOINT;

    public String value() {
        return name();
    }

    public static ShoutEventType fromValue(String v) {
        return valueOf(v);
    }

}
