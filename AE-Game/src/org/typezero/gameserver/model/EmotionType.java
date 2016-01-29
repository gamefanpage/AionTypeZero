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

package org.typezero.gameserver.model;

/**
 * @author lyahim
 */
public enum EmotionType {
	UNK(-1),
	SELECT_TARGET(0),
	JUMP(1),
	SIT(2),
	STAND(3),
	CHAIR_SIT(4),
	CHAIR_UP(5),
	START_FLYTELEPORT(6),
	LAND_FLYTELEPORT(7),
	WINDSTREAM(8),
	WINDSTREAM_END(9),
	WINDSTREAM_EXIT(10),
	WINDSTREAM_START_BOOST(11),
	WINDSTREAM_END_BOOST(12),
	FLY(13),
	LAND(14),
	RIDE(15),
	RIDE_END(16),
	DIE(18),
	RESURRECT(19),
	EMOTE(21),
	END_DUEL(22), // What? Duel? It's the end of a emote
	ATTACKMODE(24), // Attack mode, by game
	NEUTRALMODE(25), // Attack mode, by game
	WALK(26),
	RUN(27),
	OPEN_DOOR(31),
	CLOSE_DOOR(32),
	OPEN_PRIVATESHOP(33),
	CLOSE_PRIVATESHOP(34),
	START_EMOTE2(35), // It's not "emote". Triggered after Attack Mode of npcs
	POWERSHARD_ON(36),
	POWERSHARD_OFF(37),
	ATTACKMODE2(38), // It's the Attack toggled by player
	NEUTRALMODE2(39), // It's Neutral toggled by player
	START_LOOT(40),
	END_LOOT(41),
	START_QUESTLOOT(42),
	END_QUESTLOOT(43),
	START_FEEDING(50),
	END_FEEDING(51),
	WINDSTREAM_STRAFE(52),
	START_SPRINT(53),
	END_SPRINT(54);
	private int id;

	private EmotionType(int id) {
		this.id = id;
	}

	public int getTypeId() {
		return id;
	}

	public static EmotionType getEmotionTypeById(int id) {
		for (EmotionType emotionType : values()) {
			if (emotionType.getTypeId() == id)
				return emotionType;
		}
		return UNK;
	}

}
