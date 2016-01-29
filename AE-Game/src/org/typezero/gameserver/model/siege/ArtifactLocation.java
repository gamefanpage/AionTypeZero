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

package org.typezero.gameserver.model.siege;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.templates.siegelocation.ArtifactActivation;
import org.typezero.gameserver.model.templates.siegelocation.SiegeLocationTemplate;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.skillengine.model.SkillTemplate;

/**
 * @author Source
 */
public class ArtifactLocation extends SiegeLocation {

	private ArtifactStatus status;

	public ArtifactLocation() {
		this.status = ArtifactStatus.IDLE;
	}

	public ArtifactLocation(SiegeLocationTemplate template) {
		super(template);
		// Artifacts Always Vulnerable
		setVulnerable(true);
	}

	@Override
	public int getNextState() {
		return STATE_VULNERABLE;
	}

	public long getLastActivation() {
		return this.lastArtifactActivation;
	}

	public void setLastActivation(long paramLong) {
		this.lastArtifactActivation = paramLong;
	}

	public int getCoolDown() {
		long i = this.template.getActivation().getCd();
		long l = System.currentTimeMillis() - this.lastArtifactActivation;
		if (l > i)
			return 0;
		else
			return (int) ((i - l) / 1000);
	}

	/**
	 * Returns DescriptionId that describes name of this artifact.<br>
	 *
	 * @return DescriptionId with name
	 */
	public DescriptionId getNameAsDescriptionId() {
		// Get Skill id, item, count and target defined for each artifact.
		ArtifactActivation activation = getTemplate().getActivation();
		int skillId = activation.getSkillId();
		SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		return new DescriptionId(skillTemplate.getNameId());
	}

	public boolean isStandAlone() {
		return !SiegeService.getInstance().getFortresses().containsKey(getLocationId());
	}

	public FortressLocation getOwningFortress() {
		return SiegeService.getInstance().getFortress(getLocationId());
	}

	/**
	 * @return the status
	 */
	public ArtifactStatus getStatus() {
		return status != null ? status : ArtifactStatus.IDLE;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ArtifactStatus status) {
		this.status = status;
	}

}