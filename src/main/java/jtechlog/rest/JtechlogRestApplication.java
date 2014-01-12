package jtechlog.rest;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("resources")
public class JtechlogRestApplication extends ResourceConfig {

    public JtechlogRestApplication() {
        register(org.glassfish.jersey.jackson.JacksonFeature.class);
        packages("jtechlog.rest");
    }
}
