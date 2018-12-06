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

import application.rest.responses.pgm.QueryProgramOutput;
import application.rest.responses.pgm.StartProgramOutput;
import application.rest.responses.pgm.UpdateProgamOutput;

public class RequestUtils {

	public static UpdateProgamOutput validateUPRSuccess(Response responses) {
		UpdateProgamOutput updatePgmResponses = responses.readEntity(UpdateProgamOutput.class);
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

	public static QueryProgramOutput validateQPRSuccess(Response responses){
		QueryProgramOutput queryPgmResponses = responses.readEntity(QueryProgramOutput.class);
		assertNotEquals(null, queryPgmResponses);
		assertNotEquals(0, queryPgmResponses.getData().size());
		assertEquals("0", queryPgmResponses.getData().get(0).getCc());
		assertEquals(200, responses.getStatus());
		return queryPgmResponses;
	}

	public static StartProgramOutput validateSPRSuccess(Response response) {
		StartProgramOutput startProgramResponses = response.readEntity(StartProgramOutput.class);
		/*Check if request is successful*/
		assertNotEquals(null, startProgramResponses);
		assertNotEquals(0, startProgramResponses.getData().size());
		assertEquals(false, startProgramResponses.getData().get(0).get().isEmpty());
		assertEquals(200, response.getStatus());
		return startProgramResponses;
	}
	


}
