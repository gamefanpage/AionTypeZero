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

package org.typezero.gameserver.skillengine.model;

import javax.xml.bind.annotation.*;

import org.typezero.gameserver.model.Gender;
import org.typezero.gameserver.model.Race;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "motion_time", propOrder = {"am" , "af" , "em" , "ef"})
public class MotionTime {

	protected Times am;
	protected Times af;
	protected Times em;
	protected Times ef;

	@XmlAttribute(required = true)
	protected String name;// TODO enum


	public String getName() {
		return name;
	}

	/**
	 * @return the am
	 */
	public Times getAm() {
		return am;
	}

	/**
	 * @param am the am to set
	 */
	public void setAm(Times am) {
		this.am = am;
	}

	/**
	 * @return the af
	 */
	public Times getAf() {
		return af;
	}

	/**
	 * @param af the af to set
	 */
	public void setAf(Times af) {
		this.af = af;
	}

	/**
	 * @return the em
	 */
	public Times getEm() {
		return em;
	}

	/**
	 * @param em the em to set
	 */
	public void setEm(Times em) {
		this.em = em;
	}

	/**
	 * @return the ef
	 */
	public Times getEf() {
		return ef;
	}

	/**
	 * @param ef the ef to set
	 */
	public void setEf(Times ef) {
		this.ef = ef;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public Times getTimes(Race race, Gender gender) {

		switch (race) {
			case ASMODIANS:
				if (gender == Gender.MALE)
					return this.getAm();
				else
					return this.getAf();
			case ELYOS:
				if (gender == Gender.MALE)
					return this.getEm();
				else
					return this.getEf();

		}

		return null;
	}

	public int getTimeForWeapon(Race race, Gender gender, WeaponTypeWrapper weapon) {

		switch (race) {
			case ASMODIANS:
				if (gender == Gender.MALE)
					return this.getAm().getTimeForWeapon(weapon);
				else
					return this.getAf().getTimeForWeapon(weapon);
			case ELYOS:
				if (gender == Gender.MALE)
					return this.getEm().getTimeForWeapon(weapon);
				else
					return this.getEf().getTimeForWeapon(weapon);

		}

		return 0;
	}

}
