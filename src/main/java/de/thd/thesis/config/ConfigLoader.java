package de.thd.thesis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Path;

/** converting an app's YAML config file into an AppConfig object. */
public final class ConfigLoader {

    private static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory());

    private ConfigLoader() {}

    public static AppConfig load(Path file) {
        try {
            return YAML.readValue(file.toFile(), AppConfig.class);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read config file: " + file, e);
        }
    }
}