package application.main;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import application.rest.OMServlet;
import application.rest.services.Pgm;
import application.rest.services.Region;
import application.rest.services.Tran;
import filters.HeaderRequestFilter;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

@Component
public class SpringConfig extends ResourceConfig{

	public SpringConfig() {
		packages("application.resources");
		register(OpenApiResource.class);
		register(AcceptHeaderOpenApiResource.class);
		register(Region.class);
		register(Pgm.class);
		register(Tran.class);
		register(OMServlet.class);
		register(HeaderRequestFilter.class);
	}

}
