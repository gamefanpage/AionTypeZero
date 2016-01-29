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

package org.typezero.gameserver.model.templates.siegelocation;

import org.typezero.gameserver.model.siege.SiegeType;

import javax.xml.bind.annotation.*;
import java.util.Collections;
import java.util.List;

/**
 * @author Sarynth modified by antness & Source & Wakizashi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "siegelocation")
public class SiegeLocationTemplate {

	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "type")
	protected SiegeType type;
	@XmlAttribute(name = "world")
	protected int world;
	@XmlElement(name = "artifact_activation")
	protected ArtifactActivation artifactActivation;
	@XmlElement(name = "siege_reward")
	protected List<SiegeReward> siegeRewards;
	@XmlElement(name = "legion_reward")
	protected List<SiegeLegionReward> siegeLegionRewards;
	@XmlAttribute(name = "name_id")
	protected int nameId = 0;
	@XmlAttribute(name = "siege_duration")
	protected int siegeDuration;
	@XmlAttribute(name = "influence")
	protected int influenceValue;
	@XmlList
	@XmlAttribute(name = "fortress_dependency")
	protected List<Integer> fortressDependency;
	/**
	 * @return the location id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @return the location type
	 */
	public SiegeType getType() {
		return this.type;
	}

	/**
	 * @return the world id
	 */
	public int getWorldId() {
		return this.world;
	}

	public ArtifactActivation getActivation() {
		return this.artifactActivation;
	}

	/**
	 * @return the reward list
	 */
	public List<SiegeReward> getSiegeRewards() {
		return this.siegeRewards;
	}

	/**
	 * @return the siege zone
	 */
	public List<SiegeLegionReward> getSiegeLegionRewards() {
		return this.siegeLegionRewards;
	}


	/**
	 * @return the nameId
	 */
	public int getNameId() {
		return nameId;
	}


	/**
	 * @return the fortressDependency
	 */
	public List<Integer> getFortressDependency() {
		if (fortressDependency == null)
			return Collections.emptyList();
		return fortressDependency;
	}

	/**
	 * @return the Duration in Seconds
	 */
	public int getSiegeDuration() {
		return this.siegeDuration;
	}

	/**
	 * @return the influence Points
	 */
	public int getInfluenceValue() {
		return this.influenceValue;
	}
}
