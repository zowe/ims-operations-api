/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application.main;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import filters.HeaderRequestFilter;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

@Component
public class SpringConfig extends ResourceConfig{

	public SpringConfig() {
		packages("application.resources");
		register(OpenApiResource.class);
		register(AcceptHeaderOpenApiResource.class);
		register(HeaderRequestFilter.class);
		packages("application");
	}

}
