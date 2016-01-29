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
 * @author zdead
 */
public class Petition {

	private final int petitionId;
	private final int playerObjId;
	private final PetitionType type;
	private final String title;
	private final String contentText;
	private final String additionalData;
	private final PetitionStatus status;

	public Petition(int petitionId) {
		this.petitionId = petitionId;
		this.playerObjId = 0;
		this.type = PetitionType.INQUIRY;
		this.title = "";
		this.contentText = "";
		this.additionalData = "";
		this.status = PetitionStatus.PENDING;
	}

	public Petition(int petitionId, int playerObjId, int petitionTypeId, String title, String contentText,
		String additionalData, int petitionStatus) {
		this.petitionId = petitionId;
		this.playerObjId = playerObjId;
		switch (petitionTypeId) {
			case 256:
				type = PetitionType.CHARACTER_STUCK;
				break;
			case 512:
				type = PetitionType.CHARACTER_RESTORATION;
				break;
			case 768:
				type = PetitionType.BUG;
				break;
			case 1024:
				type = PetitionType.QUEST;
				break;
			case 1280:
				type = PetitionType.UNACCEPTABLE_BEHAVIOR;
				break;
			case 1536:
				type = PetitionType.SUGGESTION;
				break;
			case 65280:
				type = PetitionType.INQUIRY;
				break;
			default:
				type = PetitionType.INQUIRY;
				break;
		}
		this.title = title;
		this.contentText = contentText;
		this.additionalData = additionalData;
		switch (petitionStatus) {
			case 0:
				status = PetitionStatus.PENDING;
				break;
			case 1:
				status = PetitionStatus.IN_PROGRESS;
				break;
			case 2:
				status = PetitionStatus.REPLIED;
				break;
			default:
				status = PetitionStatus.PENDING;
				break;
		}
	}

	public int getPlayerObjId() {
		return playerObjId;
	}

	public int getPetitionId() {
		return petitionId;
	}

	public PetitionType getPetitionType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public String getContentText() {
		return contentText;
	}

	public String getAdditionalData() {
		return additionalData;
	}

	public PetitionStatus getStatus() {
		return status;
	}

}
