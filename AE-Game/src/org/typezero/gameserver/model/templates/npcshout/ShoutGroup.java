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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * @author Rolandas
 */

/**
 * <p>
 * Java class for ShoutGroup complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ShoutGroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shout_npcs" type="{}ShoutList" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="client_ai" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShoutGroup", propOrder = { "shoutNpcs" })
public class ShoutGroup {

	@XmlElement(name = "shout_npcs", required = true)
	protected List<ShoutList> shoutNpcs;

	@XmlAttribute(name = "client_ai")
	protected String clientAi;

	/**
	 * Gets the value of the shoutNpcs property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the shoutNpcs property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getShoutNpcs().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link ShoutList }
	 */
	public List<ShoutList> getShoutNpcs() {
		if (shoutNpcs == null) {
			shoutNpcs = new ArrayList<ShoutList>();
		}
		return this.shoutNpcs;
	}

	/**
	 * Gets the value of the clientAi property.
	 *
	 * @return possible object is {@link String }
	 */
	public String getClientAi() {
		return clientAi;
	}

  public void makeNull() {
    this.shoutNpcs = null;
    this.clientAi = null;
  }

}
