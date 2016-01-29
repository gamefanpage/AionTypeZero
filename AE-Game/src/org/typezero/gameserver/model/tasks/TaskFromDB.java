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

package org.typezero.gameserver.model.tasks;

import java.sql.Timestamp;

/**
 * @author Divinity
 */
public class TaskFromDB {

	private int id;
	private String name;
	private String type;
	private Timestamp lastActivation;
	private String startTime;
	private int delay;
	private String params[];

	/**
	 * Constructor
	 *
	 * @param id
	 *          : int
	 * @param name
	 *          : String
	 * @param type
	 *          : String
	 * @param lastActivation
	 *          : Timestamp
	 * @param startTime
	 *          : String
	 * @param delay
	 *          : int
	 * @param param
	 *          : String
	 */
	public TaskFromDB(int id, String name, String type, Timestamp lastActivation, String startTime, int delay,
		String param) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.lastActivation = lastActivation;
		this.startTime = startTime;
		this.delay = delay;

		if (param != null)
			this.params = param.split(" ");
		else
			this.params = new String[0];
	}

	/**
	 * Task's id
	 *
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Task's name
	 *
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Task's type : - FIXED_IN_TIME (HH:MM:SS)
	 *
	 * @return String
	 */
	public String getType() {
		return type;
	}

	/**
	 * Task's last activation
	 *
	 * @return Timestamp
	 */
	public Timestamp getLastActivation() {
		return lastActivation;
	}

	/**
	 * Task's starting time (HH:MM:SS format)
	 *
	 * @return String
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * Task's delay
	 *
	 * @return int
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Task's param(s)
	 *
	 * @return String[]
	 */
	public String[] getParams() {
		return params;
	}
}
