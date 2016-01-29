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

package org.typezero.gameserver.configs.shedule;

import com.aionemu.commons.utils.xml.JAXBUtil;
import java.io.File;
import java.util.List;
import javax.xml.bind.annotation.*;
import org.apache.commons.io.FileUtils;

/**
 * @author SoulKeeper, Source
 */
@XmlRootElement(name = "siege_schedule")
@XmlAccessorType(XmlAccessType.FIELD)
public class SiegeSchedule {

	@XmlElement(name = "fortress", required = true)
	private List<Fortress> fortressesList;
	@XmlElement(name = "source", required = true)
	private List<Source> sourcesList;

	public List<Fortress> getFortressesList() {
		return fortressesList;
	}

	public void setFortressesList(List<Fortress> fortressList) {
		this.fortressesList = fortressList;
	}

	public List<Source> getSourcesList() {
		return sourcesList;
	}

	public void setSourcesList(List<Source> sourceList) {
		this.sourcesList = sourceList;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "fortress")
	public static class Fortress {

		@XmlAttribute(required = true)
		private int id;
		@XmlElement(name = "siegeTime", required = true)
		private List<String> siegeTimes;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public List<String> getSiegeTimes() {
			return siegeTimes;
		}

		public void setSiegeTimes(List<String> siegeTimes) {
			this.siegeTimes = siegeTimes;
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "source")
	public static class Source {

		@XmlAttribute(required = true)
		private int id;
		@XmlElement(name = "siegeTime", required = true)
		private List<String> siegeTime;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public List<String> getSiegeTimes() {
			return siegeTime;
		}

		public void setSiegeTimes(List<String> siegeTime) {
			this.siegeTime = siegeTime;
		}

	}

	public static SiegeSchedule load() {
		SiegeSchedule ss;
		try {
			String xml = FileUtils.readFileToString(new File("./config/shedule/siege_schedule.xml"));
			ss = JAXBUtil.deserialize(xml, SiegeSchedule.class);
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to initialize sieges", e);
		}
		return ss;
	}

}
