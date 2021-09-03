/**
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBM Corporation 2019
 */

package application.main;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import application.rest.OMServlet;
import application.rest.services.PgmService;
import application.rest.services.RegionService;
import application.rest.services.TranService;
import application.security.AuthRequestFilter;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * Configuration class for REST endpoint
 * @author jerryli
 *
 */
@OpenAPIDefinition(
		info = @Info(
				title = "IMS Operations API",
				version = "1.0.0",
				description = "The IMS Operations API allows you to use RESTful APIs to perform IMS operational tasks. You can call the RESTful APIs to invoke the IMS commands that are required to deploy IMS applications and manage application resources."),
		servers = {@Server(url = "/ims")},
		security = {@SecurityRequirement(name = "Basic Auth")})
@ApplicationPath("/api/v1")
public class App extends Application{

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
		resources.add(App.class);
		return resources;
	}

}