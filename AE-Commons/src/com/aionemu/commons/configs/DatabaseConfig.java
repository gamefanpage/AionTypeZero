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
package com.aionemu.commons.configs;

import com.aionemu.commons.configuration.Property;

import java.io.File;

/**
 * This class holds all configuration of database
 *
 * @author SoulKeeper
 */
public class DatabaseConfig {

	/**
	 * Default database url.
	 */
	@Property(key = "database.url", defaultValue = "jdbc:mysql://localhost:3306/aion_uni")
	public static String DATABASE_URL;

	/**
	 * Name of database Driver
	 */
	@Property(key = "database.driver", defaultValue = "com.mysql.jdbc.Driver")
	public static Class<?> DATABASE_DRIVER;

	/**
	 * Default database user
	 */
	@Property(key = "database.user", defaultValue = "root")
	public static String DATABASE_USER;

	/**
	 * Default database password
	 */
	@Property(key = "database.password", defaultValue = "root")
	public static String DATABASE_PASSWORD;

	/**
	 * Amount of partitions used by BoneCP
	 */
	@Property(key = "database.bonecp.partition.count", defaultValue = "2")
	public static int DATABASE_BONECP_PARTITION_COUNT;

	/**
	 * Minimum amount of connections that are always active in bonecp partition
	 */
	@Property(key = "database.bonecp.partition.connections.min", defaultValue = "2")
	public static int DATABASE_BONECP_PARTITION_CONNECTIONS_MIN;

	/**
	 * Maximum amount of connections that are allowed to use in bonecp partition
	 */
	@Property(key = "database.bonecp.partition.connections.max", defaultValue = "5")
	public static int DATABASE_BONECP_PARTITION_CONNECTIONS_MAX;

	/**
	 * Location of database script context descriptor
	 */
	@Property(key = "database.scriptcontext.descriptor", defaultValue = "./data/scripts/system/database/database.xml")
	public static File DATABASE_SCRIPTCONTEXT_DESCRIPTOR;

}
