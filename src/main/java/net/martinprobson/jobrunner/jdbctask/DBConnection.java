/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.martinprobson.jobrunner.jdbctask;

import com.typesafe.config.Config;
import net.martinprobson.jobrunner.configurationservice.GlobalConfigurationProvider;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * DBConnection implements ConnectionFactory (see
 * <a href="https://commons.apache.org/proper/commons-dbcp/api-2.1.1/org/apache/commons/dbcp2/ConnectionFactory.html">org.apache.commons.dbcp2.ConnectionFactory</a> )
 * to supply DB Connection pool with a new DB Connection when requested.
 *
 * @author Martin Robson
 */
class DBConnection implements ConnectionFactory {

    private static final String JDBC_USERNAME;
    private static final String JDBC_PASSWORD;
    private static final String JDBC_DRIVER_CLASSNAME;
    private static final String JDBC_URL;

    private static final List<URLAppender> appenders = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(DBConnection.class);

    static {
        Config cfg = new GlobalConfigurationProvider().getConfiguration();
        JDBC_USERNAME = cfg.getString("jdbc.username");
        JDBC_PASSWORD = cfg.getString("jdbc.password");
        JDBC_DRIVER_CLASSNAME = cfg.getString("jdbc.driver");
        JDBC_URL = cfg.getString("jdbc.url");
    }

    private String url;

    /**
     * Gives a new DB Connection.
     * <p>
     * Append current date (YYYYMMDD) to connection URL so HQL can use ${hiveconf:run_date} in scripts.
     */
    DBConnection() {
        url = buildURL();
    }

    /**
     * Allow callback (URLAppender) class to be added to allow connection URL to be modified with custom hive parameters.
     */
    @SuppressWarnings("unused")
    static void addURLAppender(URLAppender appender) {
        appenders.add(appender);
    }

    /**
     * @see org.apache.commons.dbcp2.ConnectionFactory#createConnection()
     */
    @Override
    public Connection createConnection() throws SQLException {
        log.trace("Got connection: URL: " + url + " User: " + JDBC_USERNAME);
        Kerberos.auth();
        DriverManager.setLoginTimeout(10);
        return DriverManager.getConnection(url, JDBC_USERNAME, JDBC_PASSWORD);
    }

    /**
     * Build connection URL and call registered URLAppenders to complete construction of the connection string.
     * <p>Hive connection URL is in the format: -
     * <pre>
     * jdbc:hive2://<host1>:<port1>,<host2>:<port2>/dbName;sess_var_list?hive_conf_list#hive_var_list
     * </pre>
     * <p>where: -
     * <ul>
     * <li><pre>host1:port1,host2:port2</pre> is a server instance or a comma separated list of server instances to connect to (if dynamic service discovery is enabled). If empty, the embedded server will be used.
     * <li><pre>sess_var_list</pre> is a semicolon separated list of key=value pairs of session variables (e.g., user=foo;password=bar).
     * <li><pre>hive_conf_list</pre>is a semicolon separated list of key=value pairs of Hive configuration variables for this session.
     * <li><pre>hive_var_list</pre>is a semicolon separated list of key=value pairs of Hive variables for this session.
     * </ul>
     */
    private String buildURL() {
        log.debug("Got JDBC Driver: " + JDBC_DRIVER_CLASSNAME);
        System.setProperty("JDBC_DRIVERS", JDBC_DRIVER_CLASSNAME);
        try {
            Class.forName(JDBC_DRIVER_CLASSNAME);
        } catch (ClassNotFoundException e) {
            log.error("JDBC Driver class not found" + JDBC_DRIVER_CLASSNAME, e);
            e.printStackTrace();
        }
        log.debug("JDBC Driver " + JDBC_DRIVER_CLASSNAME + " loaded successfully");

        url = JDBC_URL;
        // Allow all the registered callback classes to modify the URL as required

        if (appenders.size() > 0) url += "#";
        for (URLAppender appender : appenders) {
            url = appender.appendURL(url) + ";";
        }

        return url;
    }

}
