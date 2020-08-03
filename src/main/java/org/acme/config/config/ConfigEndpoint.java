package org.acme.config.config;

import io.smallrye.config.ConfigValue;
import io.smallrye.config.SmallRyeConfig;
import io.smallrye.jwt.config.JWTAuthContextInfoProvider;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.sisu.Mediator;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("config")
@Produces(MediaType.APPLICATION_JSON)
public class ConfigEndpoint {
    @Inject
    Config config;
    @Inject
    JWTAuthContextInfoProvider contextInfoProvider;
    @Inject
    @ConfigProperty(name = "mp.jwt.verify.publickey.location")
    String mpJwtLocation;

    @GET
    @Path("byName/{name}")
    public ConfigValue get(@PathParam("name") String name) {
        return ((SmallRyeConfig) config).getConfigValue(name);
    }

    @GET
    @Path("byProvider")
    public String getByProvider() {
        return contextInfoProvider.getMpJwtLocation().get();
    }

    @GET
    @Path("byInject")
    public String getByInject() {
        return mpJwtLocation;
    }
}
