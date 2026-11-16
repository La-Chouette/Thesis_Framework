package de.thd.thesis.config;

import java.util.List;
import java.util.Map;

/** Everything the framework needs to know about one application under test. */
public record AppConfig(
        String name,
        ComposeConfig compose,
        AuthConfig auth,
        List<ResourceConfig> resources) {

    /** starting the app with Docker Compose and where its API is. */
    public record ComposeConfig(
            List<String> files,
            String apiService,
            int apiPort,
            String readinessPath) {}

    /** log in. */
    public record AuthConfig(
            String type,
            String loginPath,
            String tokenField,
            Map<String, UserCredentials> users) {}

    /** One test user. */
    public record UserCredentials(String username, String password) {}

    /** One API resource to test, e.g. catalog items. */
    public record ResourceConfig(String name, String path, String listField) {}
}