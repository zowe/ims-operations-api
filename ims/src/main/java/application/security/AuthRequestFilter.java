/**
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBM Corporation 2019
 */

package application.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import annotations.CheckHeader;

@CheckHeader
@Component
@Provider
public class AuthRequestFilter implements ContainerRequestFilter {


	@Context
	private ResourceInfo resourceInfo;

	private SpringUsers springUsers;

	@Override
	public void filter(ContainerRequestContext ctx) throws IOException {


		MultivaluedMap<String, String> headers = ctx.getHeaders();
		Method method = resourceInfo.getResourceMethod();

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

							if(method.isAnnotationPresent(RolesAllowed.class)) {
								RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
								Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

								//Is user valid?
								isUserAllowed(username, password, rolesSet, ctx);

							}
						}
					}
				}
			} else {
				ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
						.entity("No credentials provided").build());
			}
		}
	}




	public boolean isUserAllowed(final String username, final String password, final Set<String> rolesSet, ContainerRequestContext ctx) {



		try {
			if (springUsers == null) {
				BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/users.xml")));

				XmlMapper mapper = new XmlMapper();
				String xml;

				xml = br.lines().collect(Collectors.joining());

				springUsers = mapper.readValue(xml, SpringUsers.class);
			}

			for (application.security.User u : springUsers.getUser()) {
				if (u.getUsername().equals(username)) {
					if (password.contentEquals(new String(Base64.decodeBase64(u.getPassword().getBytes())))) {
						if (rolesSet.contains(u.getRole())) {
							return true;
						} else {
							ctx.abortWith(Response.status(Response.Status.FORBIDDEN)
									.entity("User: " + "'"+username+"'" + " is forbidden from accessing this resource").build());
							return false;
						}
					} else {
						ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
								.entity("User: " + "'"+username+"'" + " could not be authenticated").build());
						return false;
					}
				} 
			}

		} catch (IOException e) {
			e.printStackTrace();
			ctx.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Server error, check Users.xml").build());
			return false;
		}
		ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
				.entity("User: " + "'"+username+"'" + " could not be authenticated").build());
		return false;


	}


}