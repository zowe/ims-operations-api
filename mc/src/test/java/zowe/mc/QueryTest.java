package zowe.mc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests for QUERY IMS rest services
 * @author jerryli
 *
 */
public class QueryTest 
{

	private static final Logger logger = LoggerFactory.getLogger(QueryTest.class);
	private static WebTarget webTarget = null;

	
	/**
	 * Setup rest client and webtarget address
	 */
	@BeforeAll
	public static void setUp() {
		
		Client client = ClientBuilder.newClient();
		webTarget = client.target("http://localhost:9080/mc/services/");

	}
	
	
	/**
	 * Tests rest service for submitting QUERY PGM IMS command
	 * @throws Exception
	 */
	@Test
	public void testQueryPgm() throws Exception {
		logger.info("TESTING Query PGM");
		String path = "/Query/PGM";

		Response response =  webTarget.path(path).queryParam("names", "*").request(MediaType.APPLICATION_JSON).header("hostname", "ec32016a.vmec.svl.ibm.com")
				.header("port", "9999")
				.header("plex", "IM00P").get();
		System.out.println("getting response");

		InputStream in = (InputStream) response.getEntity();
		BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8")); 
		StringBuilder responseStrBuilder = new StringBuilder();

		String inputStr;
		while ((inputStr = streamReader.readLine()) != null) {
			responseStrBuilder.append(inputStr);
		}

		ObjectMapper mapper = new ObjectMapper();
		JsonNode result = mapper.readTree(responseStrBuilder.toString());
		JsonNode message = result.get("message");
		JsonNode omMessageContext = message.get("omMessageContext");
		JsonNode executeUserImsCommand = omMessageContext.get("executeUserImsCommand");
		JsonNode messageTitle = executeUserImsCommand.get(0);
		String status = messageTitle.get("status").textValue();

		assertEquals(200, response.getStatus());
		assertEquals("success", status);

	}

	
}
