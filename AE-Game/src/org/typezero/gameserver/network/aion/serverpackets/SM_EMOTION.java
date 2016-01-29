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
package org.typezero.gameserver.network.aion.serverpackets;


import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.stats.calc.Stat2;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * Emotion packet
 *
 * @author SoulKeeper
 * @modified -Enomine- 4.0
 */
public class SM_EMOTION extends AionServerPacket {

	/**
	 * Object id of emotion sender
	 */
	private int senderObjectId;

	/**
	 * Some unknown variable
	 */
	private EmotionType emotionType;

	/**
	 * ID of emotion
	 */
	private int emotion;

	/**
	 * Object id of emotion target
	 */
	private int targetObjectId;

	/**
	 * Temporary Speed..
	 */
	private float speed;
	private int state;
	private int baseAttackSpeed;
	private int currentAttackSpeed;

	/**
	 * Coordinates of player
	 */
	private float x;
	private float y;
	private float z;
	private byte heading;

	/**
	 * This constructor should be used when emotion and targetid is 0
	 *
	 * @param creature
	 * @param emotionType
	 */
	public SM_EMOTION(Creature creature, EmotionType emotionType) {
		this(creature, emotionType, 0, 0);
	}

	/**
	 * Constructs new server packet with specified opcode
	 *
	 * @param senderObjectId
	 *          who sended emotion
	 * @param unknown
	 *          Dunno what it is, can be 0x10 or 0x11
	 * @param emotionId
	 *          emotion to play
	 * @param emotionId
	 *          who target emotion
	 */
	public SM_EMOTION(Creature creature, EmotionType emotionType, int emotion, int targetObjectId) {
		this.senderObjectId = creature.getObjectId();
		this.emotionType = emotionType;
		this.emotion = emotion;
		this.targetObjectId = targetObjectId;
		this.state = creature.getState();
		Stat2 aSpeed = creature.getGameStats().getAttackSpeed();
		this.baseAttackSpeed = aSpeed.getBase();
		this.currentAttackSpeed = aSpeed.getCurrent();
		this.speed = creature.getGameStats().getMovementSpeedFloat();
	}

	/**
	 * @param Obj
	 * @param doorId
	 * @param state
	 */
	public SM_EMOTION(int Objid, EmotionType emotionType, int state) {
		this.senderObjectId = Objid;
		this.emotionType = emotionType;
		this.state = state;
	}

	/**
	 * New
	 */
	public SM_EMOTION(Player player, EmotionType emotionType, int emotion, float x, float y, float z, byte heading, int targetObjectId) {
		this.senderObjectId = player.getObjectId();
		this.emotionType = emotionType;
		this.emotion = emotion;
		this.x = x;
		this.y = y;
		this.z = z;
		this.heading = heading;
		this.targetObjectId = targetObjectId;

		this.state = player.getState();
		this.speed = player.getGameStats().getMovementSpeedFloat();
		Stat2 aSpeed = player.getGameStats().getAttackSpeed();
		this.baseAttackSpeed = aSpeed.getBase();
		this.currentAttackSpeed = aSpeed.getCurrent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(senderObjectId);
		writeC(emotionType.getTypeId());
		writeH(state);
		writeF(speed);
		switch (emotionType) {
			case LAND_FLYTELEPORT: // fly teleport (land)
			case FLY: // toggle flight mode
			case LAND: // toggle land mode
			case SELECT_TARGET: // select target
			case JUMP:
			case SIT: // sit
			case STAND: // stand
			case ATTACKMODE: // toggle attack mode
			case NEUTRALMODE: // toggle normal mode
			case WALK: // toggle walk
			case RUN: // toggle run
			case OPEN_PRIVATESHOP: // private shop open
			case CLOSE_PRIVATESHOP:	// private shop close
			case POWERSHARD_ON: // powershard on
			case POWERSHARD_OFF: // powershard off
			case ATTACKMODE2: // toggle attack mode
			case NEUTRALMODE2: // toggle normal mode
			case START_FEEDING:
			case END_FEEDING:
			case WINDSTREAM_START_BOOST:
			case WINDSTREAM_END_BOOST:
			case WINDSTREAM_END:
			case WINDSTREAM_EXIT:
			case OPEN_DOOR:
			case CLOSE_DOOR:
			case WINDSTREAM_STRAFE:
				break;
			case DIE: // die
			case START_LOOT: // looting start
			case END_LOOT: // looting end
			case START_QUESTLOOT: // looting start (quest)
			case END_QUESTLOOT: // looting end (quest);
				writeD(targetObjectId);
				break;
			case CHAIR_SIT: // sit (chair)
			case CHAIR_UP: // stand (chair)
				writeF(x);
				writeF(y);
				writeF(z);
				writeC(heading);
				break;
			case START_FLYTELEPORT:
				// fly teleport (start)
				writeD(emotion); // teleport Id
				break;
			case WINDSTREAM:
				// entering windstream
				writeD(emotion); // teleport Id
				writeD(targetObjectId); // distance
				break;
			case RIDE:
			case RIDE_END:
				if (targetObjectId != 0) {
					writeD(targetObjectId);//rideId
				}
				writeH(0);//emotion?
				writeC(0);//type
				writeD(0x3F);//unk
				writeD(0x3F);//unk
				writeC(0x40);//unk
				break;
			case RESURRECT:
				// resurrect
				writeD(0);
				break;
			case EMOTE:
				// emote
				writeD(targetObjectId);
				writeH(emotion);
				writeC(1);
				break;
			case START_EMOTE2:
				// emote startloop
				writeH(baseAttackSpeed);
				writeH(currentAttackSpeed);
				writeC(0);//new 4.0
				break;
			default:
				if (targetObjectId != 0) {
					writeD(targetObjectId);
				}
		}
	}
}
