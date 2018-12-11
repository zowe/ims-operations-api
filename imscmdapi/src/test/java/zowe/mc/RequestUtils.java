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

import application.rest.responses.pgm.create.CreateProgramOutput;
import application.rest.responses.pgm.delete.DeleteProgramOutput;
import application.rest.responses.pgm.query.QueryProgramOutput;
import application.rest.responses.pgm.start.StartProgramOutput;
import application.rest.responses.pgm.update.UpdateProgamOutput;
import application.rest.responses.tran.query.QueryTransactionOutput;
import application.rest.responses.tran.update.UpdateTranOutput;

public class RequestUtils {
	
	public static Response postRequest(List<String[]> queryParams, String path, Client client) {
		WebTarget webTarget = client.target("http://localhost:8080/");

		for (String[] sArray : queryParams) {
			webTarget = webTarget.queryParam(sArray[0], sArray[1]);
		}
		

		Invocation.Builder builder =  webTarget.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true).path(path).request(MediaType.APPLICATION_JSON)
				.header("hostname", TestProperties.hostname)
				.header("port", TestProperties.port)
				.header("plex", TestProperties.plex).accept(MediaType.APPLICATION_JSON);
		
		Response responses = builder.post(Entity.json(null));
		return responses;
		
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

	public static UpdateTranOutput validateUTRSuccess(Response responses) {
		UpdateTranOutput updateTranResponses = responses.readEntity(UpdateTranOutput.class);
		assertNotEquals(null, updateTranResponses);
		assertNotEquals(0, updateTranResponses.getData().size());
		assertEquals("0", updateTranResponses.getData().get(0).getCc());
		assertEquals(200, responses.getStatus());
		return updateTranResponses;
	
	}
	
	public static UpdateProgamOutput validateUPRSuccess(Response responses) {
		UpdateProgamOutput updatePgmResponses = responses.readEntity(UpdateProgamOutput.class);
		assertNotEquals(null, updatePgmResponses);
		assertNotEquals(0, updatePgmResponses.getData().size());
		assertEquals("0", updatePgmResponses.getData().get(0).getCc());
		assertEquals(200, responses.getStatus());
		return updatePgmResponses;
	
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
	
	public static CreateProgramOutput validateCPRSuccess(Response response) {
		CreateProgramOutput createProgramResponses = response.readEntity(CreateProgramOutput.class);
		/*Check if request is successful*/
		assertNotEquals(null, createProgramResponses);
		assertNotEquals(0, createProgramResponses.getData().size());
		assertEquals("0", createProgramResponses.getData().get(0).getCc());
		assertEquals(200, response.getStatus());
		return createProgramResponses;
	}
	
	public static DeleteProgramOutput validateDPRSuccess(Response response) {
		DeleteProgramOutput deleteProgramResponses = response.readEntity(DeleteProgramOutput.class);
		/*Check if request is successful*/
		assertNotEquals(null, deleteProgramResponses);
		assertNotEquals(0, deleteProgramResponses.getData().size());
		assertEquals("0", deleteProgramResponses.getData().get(0).getCc());
		assertEquals(200, response.getStatus());
		return deleteProgramResponses;
	}
	
	public static QueryTransactionOutput validateQTRSuccess(Response response) {
		QueryTransactionOutput queryTranResponses = response.readEntity(QueryTransactionOutput.class);
		/*Check if request is successful*/
		assertNotEquals(null, queryTranResponses);
		assertNotEquals(0, queryTranResponses.getData().size());
		assertEquals("0", queryTranResponses.getData().get(0).getCc());
		assertEquals(200, response.getStatus());
		return queryTranResponses;
	}
	


}
