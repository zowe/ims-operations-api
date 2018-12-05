package zowe.mc.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import application.rest.responses.pgm.StartProgramResponse;
import application.rest.responses.pgm.StartProgramResponses;
import zowe.mc.SuiteExtension;
import zowe.mc.TestProperties;

@ExtendWith({SuiteExtension.class})
public class TestStartPgm {
	
	private static final Logger logger = LoggerFactory.getLogger(TestStartPgm.class);
	private static Client client;


	@BeforeAll
	public static void setUp() {
		client = ClientBuilder.newClient();
	}
	
	/**
	 * Tests rest service for submitting START PGM IMS command
	 * @throws Exception
	 */
	@Test
	public void testStartPgm() {
		logger.info("TESTING START PGM");
		
		StartProgramResponses startProgramResponses= request200(new ArrayList<String[]>());
		for (String key : startProgramResponses.getMessages().keySet()) {
			assertEquals("00000000", startProgramResponses.getMessages().get(key).getRc());
		}

	}
	
	@Test
	public void testDeSerializingWithJsonSetter(){
	    String jsonString = "{\"id\": 231, \"name\": \"Mary Parker\"}";
	    ObjectMapper mapper = new ObjectMapper();
	    StartProgramResponse bean;
		try {
			bean = mapper.readValue(jsonString, StartProgramResponse.class);
			 System.out.println(bean);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	}
	
	/**
	 * Helper method for testing successful 200 rest requests. Specific to this classØ
	 * @param queryParams
	 * @return
	 */
	private StartProgramResponses request200(List<String[]> queryParams) {
		WebTarget webTarget = client.target("http://localhost:8080/");
		String path = "/pgm/start";

		for (String[] sArray : queryParams) {
			webTarget = webTarget.queryParam(sArray[0], sArray[1]);
		}

		Invocation.Builder builder =  webTarget.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true).path(path).request(MediaType.APPLICATION_JSON)
				.header("hostname", TestProperties.hostname)
				.header("port", TestProperties.port)
				.header("plex", TestProperties.plex).accept(MediaType.APPLICATION_JSON);
		
		Response responses = builder.put(Entity.json(null));
		StartProgramResponses startProgramResponses = responses.readEntity(StartProgramResponses.class);

		/*Check if request is successful*/
		assertNotEquals(null, startProgramResponses);
		assertNotEquals(0, startProgramResponses.getData().size());
		assertEquals(false, startProgramResponses.getData().get(0).get().isEmpty());
		assertEquals(200, responses.getStatus());

		return startProgramResponses;
	}
	

}
