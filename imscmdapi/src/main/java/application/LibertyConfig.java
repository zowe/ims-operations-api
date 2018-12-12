package application;

import javax.servlet.ServletConfig;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import application.rest.OMServlet;
import filters.HeaderRequestFilter;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

/**
 * Configuration class for REST endpoint
 * @author jerryli
 *
 */
@Component
@ApplicationPath("/v1/api/")
public class LibertyConfig extends ResourceConfig{

	public LibertyConfig(@Context ServletConfig servletConfig) {
		register(OpenApiResource.class);
		register(AcceptHeaderOpenApiResource.class);
		register(HeaderRequestFilter.class);
		register(OMServlet.class);
		packages("application");
	}

}
