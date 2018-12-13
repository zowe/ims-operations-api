package filters;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import annotations.CheckHeader;

@CheckHeader
public class HeaderRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext ctx) throws IOException {
		MultivaluedMap<String, String> headers = ctx.getHeaders();

		if (headers.get("hostname") == null || headers.get("port") == null || headers.get("plex") == null) {
			ctx.abortWith(Response.status(Response.Status.BAD_REQUEST)
					.entity("hostname, port, and plex headers must be populated with values")
					.build());

		}

	}

}
