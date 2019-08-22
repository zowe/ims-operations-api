
/**
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBM Corporation 2019
 */

package application;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import application.rest.OMServlet;
import application.rest.services.PgmService;
import application.rest.services.RegionService;
import application.rest.services.TranService;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

/**
 * Configuration class for REST endpoint
 * @author jerryli
 *
 */
@ApplicationPath("/apis/v1")
public class LibertyApp extends Application{

//	public LibertyConfig(@Context ServletConfig servletConfig) {
//		register(OpenApiResource.class);
//		register(AcceptHeaderOpenApiResource.class);
//		register(AuthRequestFilter.class);
//		register(OMServlet.class);
//		register(RolesAllowedDynamicFeature.class);
//		packages("application");
//	}

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> resources = new HashSet<>();
		resources.add(OpenApiResource.class);
		resources.add(AcceptHeaderOpenApiResource.class);
		resources.add(PgmService.class);
		resources.add(TranService.class);
		resources.add(RegionService.class);
		resources.add(OMServlet.class);
		//resources.add(org.apache.cxf.interceptor.security.SecureAnnotationsInterceptor.class);
		
		return resources;
	}

}
