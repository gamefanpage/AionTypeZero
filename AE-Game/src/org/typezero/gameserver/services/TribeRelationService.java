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
import org.typezero.gameserver.model.TribeClass;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.templates.npc.AbyssNpcType;


/**
 * @author Cheatkiller
 *
 */
public class TribeRelationService {

	public static boolean isAggressive(Creature creature1, Creature creature2) {
		switch (creature1.getBaseTribe()) {
            case GUARD_DARK:
                switch (creature2.getBaseTribe()) {
                    case PC:
                    case GUARD:
                    case PROTECTGUARD_LIGHT:
                    case GENERAL:
                    case GUARD_DRAGON:
                        return true;
                    default:
                        break;
                }
                break;
            case PROTECTGUARD_DARK:
                switch (creature2.getBaseTribe()) {
                    case PC:
                    case GUARD:
                    case PROTECTGUARD_LIGHT:
                    case GENERAL:
                    case GUARD_DRAGON:
                        return true;
                    default:
                        break;
                }
                break;
            case GUARD:
                switch (creature2.getBaseTribe()) {
                    case PC_DARK:
                    case GUARD_DARK:
                    case PROTECTGUARD_DARK:
                    case GENERAL_DARK:
                    case GUARD_DRAGON:
                        return true;
                    default:
                        break;
                }
                break;
            case PROTECTGUARD_LIGHT:
                switch (creature2.getBaseTribe()) {
                    case PC_DARK:
                    case GUARD_DARK:
                    case PROTECTGUARD_DARK:
                    case GENERAL_DARK:
                    case GUARD_DRAGON:
                        return true;
                    default:
                        break;
                }
                break;
            case GUARD_DRAGON:
                switch (creature2.getBaseTribe()) {
                    case PC_DARK:
                    case PC:
                    case GUARD:
                    case GUARD_DARK:
                    case GENERAL_DARK:
                    case GENERAL:
                    case PROTECTGUARD_DARK:
                    case PROTECTGUARD_LIGHT:
                        return true;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(creature1.getTribe(), creature2.getTribe());
    }


	public static boolean isFriend(Creature creature1, Creature creature2) {
		if (creature1.getTribe() == creature2.getTribe()) // OR BASE ????
			return true;
		switch (creature1.getBaseTribe()) {
			case USEALL:
			case FIELD_OBJECT_ALL:
				return true;
			case GENERAL_DARK:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
					case GUARD_DARK:
						return true;
				}
				break;
			case GENERAL:
				switch (creature2.getBaseTribe()) {
					case PC:
					case GUARD:
						return true;

				}
				break;
			case FIELD_OBJECT_LIGHT:
				switch (creature2.getBaseTribe()) {
					case PC:
						return true;

				}
			case FIELD_OBJECT_DARK:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
						return true;

				}
				break;
		}
		return DataManager.TRIBE_RELATIONS_DATA.isFriendlyRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean isSupport(Creature creature1, Creature creature2) {
		switch (creature1.getBaseTribe()) {
			case GUARD_DARK:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
						return true;
				}
				break;
			case GUARD:
				switch (creature2.getBaseTribe()) {
					case PC:
						return true;

				}
				break;
		}
		return DataManager.TRIBE_RELATIONS_DATA.isSupportRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean isNone(Creature creature1, Creature creature2) {
		if (DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(creature1.getTribe(), creature2.getTribe())
			|| creature1 instanceof Npc && checkSiegeRelation((Npc) creature1, creature2)
			|| DataManager.TRIBE_RELATIONS_DATA.isHostileRelation(creature1.getTribe(), creature2.getTribe())
			|| DataManager.TRIBE_RELATIONS_DATA.isNeutralRelation(creature1.getTribe(), creature2.getTribe())) {
			return false;
		}
		switch (creature1.getBaseTribe()) {
			case GENERAL_DRAGON:
				return true;
			case GENERAL:
			case FIELD_OBJECT_LIGHT:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
						return true;
				}
				break;
			case GENERAL_DARK:
			case FIELD_OBJECT_DARK:
				switch (creature2.getBaseTribe()) {
					case PC:
						return true;

				}
				break;
		}
		return DataManager.TRIBE_RELATIONS_DATA.isNoneRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean isNeutral(Creature creature1, Creature creature2) {
		return DataManager.TRIBE_RELATIONS_DATA.isNeutralRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean isHostile(Creature creature1, Creature creature2) {
		if (creature1 instanceof Npc && checkSiegeRelation((Npc) creature1, creature2)) {
			return true;
		}
		switch (creature1.getBaseTribe()) {
			case MONSTER:
				switch (creature2.getBaseTribe()) {
					case PC_DARK:
					case PC:
						return true;
				}
				break;
		 }
		return DataManager.TRIBE_RELATIONS_DATA.isHostileRelation(creature1.getTribe(), creature2.getTribe());
	}

	public static boolean checkSiegeRelation(Npc npc, Creature creature) {
		return npc.getObjectTemplate().getAbyssNpcType() != AbyssNpcType.ARTIFACT
			&& npc.getObjectTemplate().getAbyssNpcType() != AbyssNpcType.NONE
			&& ((npc.getBaseTribe() == TribeClass.GENERAL && creature.getTribe() == TribeClass.PC_DARK)
			|| (npc.getBaseTribe() == TribeClass.GENERAL_DARK && creature.getTribe() == TribeClass.PC)
			|| npc.getBaseTribe() == TribeClass.GENERAL_DRAGON);
		}
}
