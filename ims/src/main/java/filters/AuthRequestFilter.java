/**
 *  Copyright IBM Corporation 2018, 2019
 */

package filters;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.tomcat.util.codec.binary.Base64;

import annotations.CheckHeader;

@CheckHeader
public class AuthRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext ctx) throws IOException {
		MultivaluedMap<String, String> headers = ctx.getHeaders();

		if (headers.get("hostname") == null || headers.get("port") == null) {
			ctx.abortWith(Response.status(Response.Status.BAD_REQUEST)
					.entity("hostname and port headers must be populated with values")
					.build());

		}

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
