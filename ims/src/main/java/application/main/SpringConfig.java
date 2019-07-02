
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


import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import filters.AuthRequestFilter;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

@Component
public class SpringConfig extends ResourceConfig{

	public SpringConfig() {
		packages("application.resources");
		register(OpenApiResource.class);
		register(AcceptHeaderOpenApiResource.class);
		register(AuthRequestFilter.class);
		packages("application");
	}

}
