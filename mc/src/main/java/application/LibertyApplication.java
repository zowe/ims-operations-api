package application;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import filters.HeaderRequestFilter;
import io.swagger.jaxrs.config.BeanConfig;

@ApplicationPath("/services/*")
public class LibertyApplication extends Application{

	public LibertyApplication() {
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setVersion("1.0.2");
		beanConfig.setSchemes(new String[]{"http"});
		beanConfig.setHost("localhost:9080");
		beanConfig.setBasePath("/mc/services");
		beanConfig.setResourcePackage("rs");
		beanConfig.setScan(true);
		beanConfig.setTitle("Management Console for Zowe");
		beanConfig.setVersion("1.0.0");
		beanConfig.setDescription("Restful API for submitting IMS commands");
	}

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> resources = new HashSet();
		resources.add(rs.Pgm.class);
		resources.add(HeaderRequestFilter.class);
		resources.add(io.swagger.jaxrs.listing.ApiListingResource.class);
		resources.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);

		return resources;
	}
}
