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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Get a valid Kerberos ticket based on jdbc.user/jdbc.password and kerberos.principal specified in configuration.
 * <p></p>
 */
class Kerberos {

    private static final String KERBEROS_USERNAME;
    private static final String KERBEROS_PASSWORD;
    private static final String KERBEROS_PRINCIPAL;

    private static final Config CONFIG;
    private static final Logger log = LoggerFactory.getLogger(Kerberos.class);

    static {
        CONFIG = new GlobalConfigurationProvider().getConfiguration();
        KERBEROS_USERNAME = CONFIG.getString("kerberos.username");
        KERBEROS_PASSWORD = CONFIG.getString("kerberos.password");
        KERBEROS_PRINCIPAL = CONFIG.getString("kerberos.principal");

    }

    public static void auth() {
        if (!KERBEROS_PRINCIPAL.equals(""))
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
