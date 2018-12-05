package zowe.mc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;

import application.rest.responses.pgm.QueryProgramResponses;
import application.rest.responses.pgm.StartProgramResponses;
import application.rest.responses.pgm.UpdatePgmResponses;

public class RequestUtils {

	public static UpdatePgmResponses validateUPRSuccess(Response responses) {
		UpdatePgmResponses updatePgmResponses = responses.readEntity(UpdatePgmResponses.class);
		assertNotEquals(null, updatePgmResponses);
		assertNotEquals(0, updatePgmResponses.getData().size());
		assertEquals("0", updatePgmResponses.getData().get(0).getCc());
		assertEquals(200, responses.getStatus());
		return updatePgmResponses;

	}

	public static Response putRequest(List<String[]> queryParams, String path, Client client) {
		WebTarget webTarget = client.target("http://localhost:8080/");

		for (String[] sArray : queryParams) {
			webTarget = webTarget.queryParam(sArray[0], sArray[1]);
		}

		Invocation.Builder builder =  webTarget.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true).path(path).request(MediaType.APPLICATION_JSON)
				.header("hostname", TestProperties.hostname)
				.header("port", TestProperties.port)
				.header("plex", TestProperties.plex).accept(MediaType.APPLICATION_JSON);

		Response responses = builder.put(Entity.json(null));
		return responses;
	}

	/**
	 * Helper method for testing successful 200 rest requests. Specific to this class
	 * @param queryParams
	 * @return
	 */
	public static Response getRequest(List<String[]> queryParams, String path, Client client) {
		WebTarget webTarget = client.target("http://localhost:8080");
		for (String[] sArray : queryParams) {
			webTarget = webTarget.queryParam(sArray[0], sArray[1]);
		}

		Invocation.Builder builder =  webTarget.path(path).request(MediaType.APPLICATION_JSON).header("hostname", TestProperties.hostname)
				.header("port", TestProperties.port)
				.header("plex", TestProperties.plex).accept(MediaType.APPLICATION_JSON);

		Response responses = builder.get();
		return responses;
	}

	public static QueryProgramResponses validateQPRSuccess(Response responses){
		QueryProgramResponses queryPgmResponses = responses.readEntity(QueryProgramResponses.class);
		assertNotEquals(null, queryPgmResponses);
		assertNotEquals(0, queryPgmResponses.getData().size());
		assertEquals("0", queryPgmResponses.getData().get(0).getCc());
		assertEquals(200, responses.getStatus());
		return queryPgmResponses;
	}

	public static StartProgramResponses validateSPRSuccess(Response response) {
		StartProgramResponses startProgramResponses = response.readEntity(StartProgramResponses.class);
		/*Check if request is successful*/
		assertNotEquals(null, startProgramResponses);
		assertNotEquals(0, startProgramResponses.getData().size());
		assertEquals(false, startProgramResponses.getData().get(0).get().isEmpty());
		assertEquals(200, response.getStatus());
		return startProgramResponses;
	}
	


}
