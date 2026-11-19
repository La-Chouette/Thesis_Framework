package de.thd.thesis;

import de.thd.thesis.config.AppConfig;
import de.thd.thesis.config.ConfigLoader;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigLoaderTest {

    @Test
    void loadsEShopOnWebConfig() {
        AppConfig config = ConfigLoader.load(Path.of("apps/eshoponweb/config.yaml"));

        assertEquals("eshoponweb", config.name());
        assertEquals("eshoppublicapi-1", config.compose().apiService());
        assertEquals("/api/authenticate", config.auth().loginPath());
        assertEquals("admin@microsoft.com", config.auth().users().get("admin").username());
        assertEquals(1, config.resources().size());
    }
}