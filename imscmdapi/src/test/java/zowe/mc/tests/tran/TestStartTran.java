package zowe.mc.tests.tran;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.rest.responses.pgm.start.StartProgramOutput;
import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;

/**
 * Tests for "START TRAN" IMS rest services
 * @author jerryli
 *
 */
@ExtendWith({SuiteExtension.class})
public class TestStartTran {

	private static final Logger logger = LoggerFactory.getLogger(TestStartTran.class);
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
		logger.info("TESTING START TRAN");
		
		Response response = RequestUtils.putRequest(new ArrayList<String[]>(), "/tran/start", client);
		StartProgramOutput spr = RequestUtils.validateSPRSuccess(response);
		logger.info(spr.toString());
		for (String key : spr.getMessages().keySet()) {
			assertEquals("00000000", spr.getMessages().get(key).getRc());
		}

	}
}
