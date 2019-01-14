package zowe.mc.tests.pgm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.rest.responses.pgm.create.CreateProgram;
import application.rest.responses.pgm.create.CreateProgramOutput;
import application.rest.responses.pgm.delete.DeleteProgram;
import application.rest.responses.pgm.delete.DeleteProgramOutput;
import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;

/**
 * Tests for "CREATE/DELETE PGM" IMS rest services
 * @author jerryli
 *
 */
@ExtendWith({SuiteExtension.class})
public class TestCreateDeletePgm 
{

	private static final Logger logger = LoggerFactory.getLogger(TestCreateDeletePgm.class);
	private static Client client;


	/**
	 * Setup rest client
	 */
	@BeforeAll
	public static void setUp() {
		client = ClientBuilder.newClient();
	}

	/**
	 * Tests rest service for submitting QUERY PGM IMS command
	 * @throws Exception
	 */
	@Test
	public void testCreateDeletePgm() {
		logger.info("TESTING CREATE and DELETE PGM");
		
		List<String[]> queryParamspre = new ArrayList<>();
		String[] namespre = new String[] {"names", "TEST"};
		queryParamspre.add(namespre);
		RequestUtils.deleteRequest(queryParamspre, "/PLEX1/program", client);
		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"names", "TEST"};
		queryParams.add(names);
		Response response = RequestUtils.postRequest(queryParams, "/PLEX1/program", client);
		CreateProgramOutput cpr = RequestUtils.validateCPRSuccess(response);
		/*Check if data is correct*/
		logger.info(cpr.toString());
		for (CreateProgram q : cpr.getData()) {
			assertEquals("0", q.getCc());
			assertEquals("TEST", q.getPgm());
		}
		for (String key : cpr.getMessages().keySet()) {
			assertEquals("00000000", cpr.getMessages().get(key).getRc());
		}
		
		List<String[]> queryParams2 = new ArrayList<>();
		String[] names2 = new String[] {"names", "TEST"};
		queryParams2.add(names2);
		Response response2 = RequestUtils.deleteRequest(queryParams2, "/PLEX1/program", client);
		DeleteProgramOutput dpr2 = RequestUtils.validateDPRSuccess(response2);
		/*Check if data is correct*/
		logger.info(dpr2.toString());
		for (DeleteProgram q : dpr2.getData()) {
			assertEquals("0", q.getCc());
			assertEquals("TEST", q.getPgm());
		}
		for (String key : dpr2.getMessages().keySet()) {
			assertEquals("00000000", dpr2.getMessages().get(key).getRc());
		}


	}
}