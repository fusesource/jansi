package org.fusesource.jansi.internal.loader.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

public class JansiEnvironment {

    private static final Logger LOG = Logger.getLogger(JansiEnvironment.class.getName());
    private static final String VERSION_RESOURCE = "/org/fusesource/jansi/jansi.properties";
    private static final String VERSION_PROPERTY = "version";
    private static final String JANSI_TMPDIR_PROPERTY = "jansi.tmpdir";

    private JansiEnvironment() {}

    public static String getVersion() {
        URL versionFile = JansiEnvironment.class.getResource(VERSION_RESOURCE);
        if (versionFile == null) {
            return "unknown";
        }
        try {
            Properties props = new Properties();
            props.load(versionFile.openStream());
            String version = props.getProperty(VERSION_PROPERTY, "unknown");
            return version.trim().replaceAll("[^0-9.]", "");
        } catch (IOException e) {
            LOG.warning("Could not read jansi version properties: " + e.getMessage());
            return "unknown";
        }
    }

    public static int getMajorVersion() {
        String[] parts = getVersion().split("\\.");
        return parts.length > 0 ? Integer.parseInt(parts[0]) : 1;
    }

    public static int getMinorVersion() {
        String[] parts = getVersion().split("\\.");
        return parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
    }

    public static File getTempDir() {
        return new File(System.getProperty(
                JANSI_TMPDIR_PROPERTY,
                System.getProperty("java.io.tmpdir")));
    }
}