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

package com.aionemu.commons.database;

import com.aionemu.commons.configs.DatabaseConfig;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * <b>Database Factory</b><br>
 * <br>
 * This file is used for creating a pool of connections for the server.<br>
 * It utilizes database.properties and creates a pool of connections and automatically recycles them when closed.<br>
 * <br>
 * DB.java utilizes the class.<br>
 * <br>
 * <p/>
 *
 * @author Disturbing
 * @author SoulKeeper
 */
public class DatabaseFactory {

	/**
	 * Logger for this class
	 */
	private static final Logger log = LoggerFactory.getLogger(DatabaseFactory.class);

	/**
	 * Connection Pool holds all connections - Idle or Active
	 */
	private static BoneCP connectionPool;

	/**
	 * Returns name of the database that is used For isntance, MySQL returns "MySQL"
	 */
	private static String databaseName;

	/**
	 * Retursn major version that is used For instance, MySQL 5.0.51 community edition returns 5
	 */
	private static int databaseMajorVersion;

	/**
	 * Retursn minor version that is used For instance, MySQL 5.0.51 community edition returns 0
	 */
	private static int databaseMinorVersion;

	/**
	 * Initializes DatabaseFactory.
	 */
	public synchronized static void init() {
		if (connectionPool != null) {
			return;
		}

		try {
			DatabaseConfig.DATABASE_DRIVER.newInstance();
		} catch (Exception e) {
			log.error("Error obtaining DB driver", e);
			throw new Error("DB Driver doesnt exist!");
		}

		if (DatabaseConfig.DATABASE_BONECP_PARTITION_CONNECTIONS_MIN > DatabaseConfig.DATABASE_BONECP_PARTITION_CONNECTIONS_MAX) {
			log.error("Please check your database configuration. Minimum amount of connections is > maximum");
			DatabaseConfig.DATABASE_BONECP_PARTITION_CONNECTIONS_MAX = DatabaseConfig.DATABASE_BONECP_PARTITION_CONNECTIONS_MIN;
		}

		BoneCPConfig config = new BoneCPConfig();
		config.setPartitionCount(DatabaseConfig.DATABASE_BONECP_PARTITION_COUNT);
		config.setMinConnectionsPerPartition(DatabaseConfig.DATABASE_BONECP_PARTITION_CONNECTIONS_MIN);
		config.setMaxConnectionsPerPartition(DatabaseConfig.DATABASE_BONECP_PARTITION_CONNECTIONS_MAX);
		config.setUsername(DatabaseConfig.DATABASE_USER);
		config.setPassword(DatabaseConfig.DATABASE_PASSWORD);
		config.setJdbcUrl(DatabaseConfig.DATABASE_URL);
		config.setDisableJMX(true);


		try {
			connectionPool = new BoneCP(config);
		} catch (SQLException e) {
			log.error("Error while creating DB Connection pool", e);
			throw new Error("DatabaseFactory not initialized!", e);
		}
		/* test if connection is still valid before returning */
		// connectionPool.setTestOnBorrow(true);

		try {
			Connection c = getConnection();
			DatabaseMetaData dmd = c.getMetaData();
			databaseName = dmd.getDatabaseProductName();
			databaseMajorVersion = dmd.getDatabaseMajorVersion();
			databaseMinorVersion = dmd.getDatabaseMinorVersion();
			c.close();
		} catch (Exception e) {
			log.error("Error with connection string: " + DatabaseConfig.DATABASE_URL, e);
			throw new Error("DatabaseFactory not initialized!");
		}

		log.info("Successfully connected to database");
	}

	/**
	 * Returns an active connection from pool. This function utilizes the dataSource which grabs an object from the
	 * ObjectPool within its limits. The GenericObjectPool.borrowObject()' function utilized in
	 * 'DataSource.getConnection()' does not allow any connections to be returned as null, thus a null check is not
	 * needed. Throws SQLException in case of a Failed Connection
	 *
	 * @return Connection pooled connection
	 * @throws java.sql.SQLException if can't get connection
	 */
	public static Connection getConnection() throws SQLException {
		Connection con = connectionPool.getConnection();

		if (!con.getAutoCommit()) {
			log.error("Connection Settings Error: Connection obtained from database factory should be in auto-commit" +
					" mode. Forsing auto-commit to true. Please check source code for connections beeing not properly" +
					" closed.");
			con.setAutoCommit(true);
		}

		return con;
	}

	/**
	 * Returns number of active connections in the pool.
	 *
	 * @return int Active DB Connections
	 */
	public int getActiveConnections() {
		return connectionPool.getTotalLeased();
	}

	/**
	 * Returns number of Idle connections. Idle connections represent the number of instances in Database Connections that
	 * have once been connected and now are closed and ready for re-use. The 'getConnection' function will grab idle
	 * connections before creating new ones.
	 *
	 * @return int Idle DB Connections
	 */
	public int getIdleConnections() {
		return connectionPool.getStatistics().getTotalFree();
	}

	/**
	 * Shuts down pool and closes connections
	 */
	public static synchronized void shutdown() {
		try {
			connectionPool.shutdown();
		} catch (Exception e) {
			log.warn("Failed to shutdown DatabaseFactory", e);
		}

		// set datasource to null so we can call init() once more...
		connectionPool = null;
	}


	/**
	 * Closes both prepared statement and result set
	 *
	 * @param st  prepared statement to close
	 * @param con connection to close
	 */
	public static void close(PreparedStatement st, Connection con) {
		close(st);
		close(con);
	}

	/**
	 * Helper method for silently close PreparedStament object.<br>
	 * Associated connection object will not be closed.
	 *
	 * @param st prepared statement to close
	 */
	public static void close(PreparedStatement st) {
		if (st == null) {
			return;
		}

		try {
			if (!st.isClosed()) {
				st.close();
			}
		} catch (SQLException e) {
			log.error("Can't close Prepared Statement", e);
		}
	}

	/**
	 * Closes connection and returns it to the pool.<br>
	 * It's ok to pass null variable here.<br>
	 * When closing connection - this method will make sure that connection returned to the pool in in
	 * autocommit mode.<br>. If it's not - autocommit mode will be forced to 'true'
	 *
	 * @param con Connection object to close, can be null
	 */
	public static void close(Connection con) {
		if (con == null)
			return;

		try {
			if (!con.getAutoCommit()) {
				con.setAutoCommit(true);
			}
		} catch (SQLException e) {
			log.error("Failed to set autocommit to true while closing connection: ", e);
		}

		try {
			con.close();
		} catch (SQLException e) {
			log.error("DatabaseFactory: Failed to close database connection!", e);
		}
	}

	/**
	 * Returns database name. For instance MySQL 5.0.51 community edition returns MySQL
	 *
	 * @return database name that is used.
	 */
	public static String getDatabaseName() {
		return databaseName;
	}

	/**
	 * Returns database version. For instance MySQL 5.0.51 community edition returns 5
	 *
	 * @return database major version
	 */
	public static int getDatabaseMajorVersion() {
		return databaseMajorVersion;
	}

	/**
	 * Returns database minor version. For instance MySQL 5.0.51 community edition reutnrs 0
	 *
	 * @return database minor version
	 */
	public static int getDatabaseMinorVersion() {
		return databaseMinorVersion;
	}

	/**
	 * Default constructor.
	 */
	private DatabaseFactory() {
		//
	}
}
