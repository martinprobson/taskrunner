package net.martinprobson.taskrunner.taskrunner.jdbcconnection;


import net.martinprobson.taskrunner.configurationservice.ConfigurationService;
import org.apache.commons.configuration2.ImmutableConfiguration;
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
 * @author martinr
 */
class DBConnection implements ConnectionFactory {

    private static final String JDBC_USERNAME;
    private static final String JDBC_PASSWORD;
    private static final String JDBC_DRIVER_CLASSNAME;
    private static final String JDBC_URL;

    private static final ImmutableConfiguration CONFIG;
    private static final List<URLAppender> appenders = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(DBConnection.class);

    static {
        CONFIG = ConfigurationService.getConfiguration();
        JDBC_USERNAME = CONFIG.getString("jdbc.username");
        JDBC_PASSWORD = CONFIG.getString("jdbc.password");
        JDBC_DRIVER_CLASSNAME = CONFIG.getString("jdbc.driver");
        JDBC_URL = CONFIG.getString("jdbc.url");
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
    public static void addURLAppender(URLAppender appender) {
        appenders.add(appender);
    }

    /**
     * @see org.apache.commons.dbcp2.ConnectionFactory#createConnection()
     */
    @Override
    public Connection createConnection() throws SQLException {
        log.trace("Got connection: URL: " + url + " User: " + JDBC_USERNAME);
        Kerberos.auth();
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
