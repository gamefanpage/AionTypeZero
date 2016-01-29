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
package com.aionemu.commons.database.dao;

/**
 * This class represents basic DAO. It should be subclasses by abstract class and that class has to implement method
 * {@link #getClassName()}.<br>
 * This class must return {@link Class#getName()}, {@link #getClassName()} should be final.<br>
 * DAO subclass must have public no-arg constructor, in other case {@link InstantiationException} will be thrown by
 * {@link com.aionemu.commons.database.dao.DAOManager}
 *
 * @author SoulKeeper
 */
public interface DAO {

	/**
	 * Unique identifier for DAO class, all subclasses must have same identifiers. Must return {@link Class#getName()} of
	 * abstract class
	 *
	 * @return identifier of DAO class
	 */
	public String getClassName();

	/**
	 * Returns true if DAO implementation supports database or false if not. Database information is provided by
	 * {@link java.sql.DatabaseMetaData}
	 *
	 * @param databaseName name of database
	 * @param majorVersion major version of database
	 * @param minorVersion minor version of database
	 * @return true if database is supported or false in other case
	 */
	public boolean supports(String databaseName, int majorVersion, int minorVersion);
}
