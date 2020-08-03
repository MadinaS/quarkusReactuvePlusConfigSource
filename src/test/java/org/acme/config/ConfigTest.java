package org.acme.config;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.config.JWTAuthContextInfoProvider;
import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class ConfigTest {
    @Inject
    Config config;
    @Inject
    JWTAuthContextInfoProvider contextInfoProvider;

    @Test
    void config() {
        String value = config.getValue("mp.jwt.verify.publickey.location", String.class);
        System.out.println("value = " + value);

        System.out.println(contextInfoProvider.getMpJwtLocation());
    }
}
