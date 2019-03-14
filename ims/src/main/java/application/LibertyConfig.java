/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application;

import javax.servlet.ServletConfig;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.springframework.stereotype.Component;

import application.rest.OMServlet;
import filters.AuthRequestFilter;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

/**
 * Configuration class for REST endpoint
 * @author jerryli
 *
 */
@Component
@ApplicationPath("/apis/v1")
public class LibertyConfig extends ResourceConfig{

	public LibertyConfig(@Context ServletConfig servletConfig) {
		register(OpenApiResource.class);
		register(AcceptHeaderOpenApiResource.class);
		register(AuthRequestFilter.class);
		register(OMServlet.class);
		register(RolesAllowedDynamicFeature.class);
		packages("application");
	}

}
