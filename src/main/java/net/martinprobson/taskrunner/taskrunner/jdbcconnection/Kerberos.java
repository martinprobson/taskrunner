package net.martinprobson.taskrunner.taskrunner.jdbcconnection;

import net.martinprobson.taskrunner.configurationservice.ConfigurationService;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Get a valid Kerberos ticket based on jdbc.user/jdbc.password and kerboros.principal specified in configuration.
 * <p>
 */
public class Kerberos {

    private static final String KERBEROS_USERNAME;
    private static final String KERBEROS_PASSWORD;
    private static final String KERBEROS_PRINCIPAL;

    private static final ImmutableConfiguration CONFIG;
    private static final Logger log = LoggerFactory.getLogger(Kerberos.class);

    static {
        CONFIG = ConfigurationService.getConfiguration();
        KERBEROS_USERNAME = CONFIG.getString("kerberos.username");
        KERBEROS_PASSWORD = CONFIG.getString("kerberos.password");
        KERBEROS_PRINCIPAL = CONFIG.getString("kerberos.principal");

    }

    public static void auth() {
        if (KERBEROS_PRINCIPAL != null || KERBEROS_USERNAME != null || KERBEROS_PASSWORD != null)
            auth_cmd();
    }

    private static String runCmd(String cmd, String input) {

        StringBuilder sb = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            if (input != null) {
                OutputStream out = p.getOutputStream();
                String s = input + "\n";
                out.write(s.getBytes());
                out.flush();
            }
            p.waitFor();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream())
            );
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException | InterruptedException e1) {
            e1.printStackTrace();
            System.exit(2);
        }
        return sb.toString();
    }

    private static String getLoginDomain() {
        String s = KERBEROS_USERNAME + "@" + KERBEROS_PRINCIPAL;
        log.trace("Kerberos LoginDomain: " + s);
        return s;
    }

    private static void auth_cmd() {
        String logon = " kinit " + getLoginDomain();
        log.trace("Attempting Kerberos logon using: " + logon);
        log.trace("Logon: " + runCmd(logon, KERBEROS_PASSWORD));
        log.trace("klist returns: " + runCmd("klist", null));
    }

    @SuppressWarnings("unused")
    private class MyCallbackHandler implements CallbackHandler {
        public void handle(Callback[] callbacks)
                throws UnsupportedCallbackException {

            for (Callback callback : callbacks) {
                if (callback instanceof NameCallback) {
                    NameCallback nc = (NameCallback) callback;
                    nc.setName(getLoginDomain());
                } else if (callback instanceof PasswordCallback) {
                    PasswordCallback pc = (PasswordCallback) callback;
                    pc.setPassword(KERBEROS_PASSWORD.toCharArray());
                } else throw new UnsupportedCallbackException
                        (callback, "Unrecognised callback");
            }
        }
    }
}
