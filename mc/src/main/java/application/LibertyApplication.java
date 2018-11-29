package application;

import javax.servlet.ServletConfig;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.server.ResourceConfig;

import filters.HeaderRequestFilter;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import rs.services.Pgm;
import rs.services.Region;
import rs.services.Tran;


@OpenAPIDefinition(
		info = @Info(
				title = "Management Console for Zowe",
				version = "1.0.0",
				description = "Management Console for Zowe allows users to use RESTFul APIs to submit IMS commmands"),
		tags = {@Tag(name="Program"), @Tag(name="Region")},
		servers = {@Server(url = "http://localhost:9080/mc/")}
)
@ApplicationPath("/services/")
public class LibertyApplication extends ResourceConfig{

	public LibertyApplication(@Context ServletConfig servletConfig) {
		register(OpenApiResource.class);
		register(AcceptHeaderOpenApiResource.class);
		register(Region.class);
		register(Pgm.class);
		register(Tran.class);
		register(HeaderRequestFilter.class);
	}

	//	@Override
	//	public Set<Class<?>> getClasses() {
	//		 return Stream.of(Region.class, HeaderRequestFilter.class, 
	//				 OpenApiResource.class, AcceptHeaderOpenApiResource.class).collect(Collectors.toSet());
	//		    
	//
	//	}
}
