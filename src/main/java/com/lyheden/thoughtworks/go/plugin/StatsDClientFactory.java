package com.lyheden.thoughtworks.go.plugin;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

/**
 * Created by johan on 28/06/15.
 */
public class StatsDClientFactory {

    public static StatsDClient getNonBlockingStatsDClient() {
        String hostname = "localhost";
        int port = 8125;
        String prefix = "";

        if (System.getenv().containsKey("statsd.hostname")) {
            hostname = System.getenv("statsd.hostname");
        }

        if (System.getenv().containsKey("statsd.port")) {
            port = Integer.valueOf(System.getenv("statsd.port"));
        }

        if (System.getenv().containsKey("statsd.prefix")) {
            prefix = System.getenv("statsd.prefix");
        }

        return new NonBlockingStatsDClient(prefix, hostname, port);
    }

}