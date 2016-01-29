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
import org.typezero.gameserver.model.templates.rift.OpenRift;
import java.io.File;
import java.util.List;
import javax.xml.bind.annotation.*;
import org.apache.commons.io.FileUtils;

/**
 * @author Source
 */
@XmlRootElement(name = "rift_schedule")
@XmlAccessorType(XmlAccessType.FIELD)
public class RiftSchedule {

	@XmlElement(name = "rift", required = true)
	private List<Rift> riftsList;

	public List<Rift> getRiftsList() {
		return riftsList;
	}

	public void setRiftsList(List<Rift> fortressList) {
		this.riftsList = fortressList;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "rift")
	public static class Rift {

		@XmlAttribute(required = true)
		private int id;
		@XmlElement(name = "open")
		private List<OpenRift> openRift;

		public int getWorldId() {
			return id;
		}

		public List<OpenRift> getRift() {
			return openRift;
		}

	}

	public static RiftSchedule load() {
		RiftSchedule rs;
		try {
			String xml = FileUtils.readFileToString(new File("./config/shedule/rift_schedule.xml"));
			rs = JAXBUtil.deserialize(xml, RiftSchedule.class);
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to initialize rifts", e);
		}
		return rs;
	}

}
