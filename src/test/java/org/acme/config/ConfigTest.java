package org.acme.config;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class ConfigTest {
    @Inject
    Config config;

    @Test
    void config() {
        String value = config.getValue("mp.jwt.verify.publickey.location", String.class);
        System.out.println("value = " + value);
    }
}
