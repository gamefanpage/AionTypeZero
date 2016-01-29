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

package org.typezero.gameserver.dataholders;

import org.typezero.gameserver.model.siege.*;
import org.typezero.gameserver.model.templates.siegelocation.SiegeLocationTemplate;
import java.util.List;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javolution.util.FastMap;

/**
 * @author Sarynth, antness
 */
@XmlRootElement(name = "siege_locations")
@XmlAccessorType(XmlAccessType.FIELD)
public class SiegeLocationData {

	@XmlElement(name = "siege_location")
	private List<SiegeLocationTemplate> siegeLocationTemplates;
	/**
	 * Map that contains skillId - SkillTemplate key-value pair
	 */
	@XmlTransient
	private FastMap<Integer, ArtifactLocation> artifactLocations = new FastMap<Integer, ArtifactLocation>();
	@XmlTransient
	private FastMap<Integer, FortressLocation> fortressLocations = new FastMap<Integer, FortressLocation>();
	@XmlTransient
	private FastMap<Integer, OutpostLocation> outpostLocations = new FastMap<Integer, OutpostLocation>();
	@XmlTransient
	private FastMap<Integer, SourceLocation> sourceLocations = new FastMap<Integer, SourceLocation>();
	@XmlTransient
	private FastMap<Integer, SiegeLocation> siegeLocations = new FastMap<Integer, SiegeLocation>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		artifactLocations.clear();
		fortressLocations.clear();
		outpostLocations.clear();
		sourceLocations.clear();
		siegeLocations.clear();
		for (SiegeLocationTemplate template : siegeLocationTemplates)
			switch (template.getType()) {
				case FORTRESS:
					FortressLocation fortress = new FortressLocation(template);
					fortressLocations.put(template.getId(), fortress);
					siegeLocations.put(template.getId(), fortress);
					artifactLocations.put(template.getId(), new ArtifactLocation(template));
					break;
				case ARTIFACT:
					ArtifactLocation artifact = new ArtifactLocation(template);
					artifactLocations.put(template.getId(), artifact);
					siegeLocations.put(template.getId(), artifact);
					break;
				case BOSSRAID_LIGHT:
				case BOSSRAID_DARK:
					OutpostLocation protector = new OutpostLocation(template);
					outpostLocations.put(template.getId(), protector);
					siegeLocations.put(template.getId(), protector);
					break;
				case SOURCE:
					SourceLocation source = new SourceLocation(template);
					sourceLocations.put(template.getId(), source);
					siegeLocations.put(template.getId(), source);
					break;
				default:
					break;
			}
	}

	public int size() {
		return siegeLocations.size();
	}

	public FastMap<Integer, ArtifactLocation> getArtifacts() {
		return artifactLocations;
	}

	public FastMap<Integer, FortressLocation> getFortress() {
		return fortressLocations;
	}

	public FastMap<Integer, OutpostLocation> getOutpost() {
		return outpostLocations;
	}

	public FastMap<Integer, SourceLocation> getSource() {
		return sourceLocations;
	}

	public FastMap<Integer, SiegeLocation> getSiegeLocations() {
		return siegeLocations;
	}

}
