/**
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBM Corporation 2019
 */

package application.springSecurity;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import annotations.CheckHeader;

@CheckHeader
@Component
@Provider
public class AuthRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext ctx) throws IOException {
		MultivaluedMap<String, String> headers = ctx.getHeaders();

		if (headers.get("hostname") == null || headers.get("port") == null) {
			ctx.abortWith(Response.status(Response.Status.BAD_REQUEST)
					.entity("hostname and port headers must be populated with values")
					.build());

		}


		if (headers.get("username") == null && headers.get("password") == null) {

			List<String> authHeader = headers.get("Authorization");
			if (authHeader != null) {

				String auth = authHeader.get(0);
				StringTokenizer st = new StringTokenizer(auth);
				if (st.hasMoreTokens()) {
					String basic = st.nextToken();

					if (basic.equalsIgnoreCase("Basic")) {
						String credentials = new String(Base64.decodeBase64(st.nextToken()), "UTF-8");
						int p = credentials.indexOf(":");
						if (p != -1) {
							String username = credentials.substring(0, p).trim();
							String password = credentials.substring(p + 1).trim();

							headers.add("username", username);
							headers.add("password", password);

						}
					}
				}
			}
		}


	}

}