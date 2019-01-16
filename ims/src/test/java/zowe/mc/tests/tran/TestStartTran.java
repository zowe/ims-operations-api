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

import application.rest.responses.tran.start.StartTransactionOutput;
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
	 * Tests rest service for submitting START TRAN IMS command
	 * @throws Exception
	 */
	@Test
	public void testStartTran() {
		logger.info("TESTING START TRAN");
		
		Response response = RequestUtils.putRequest(new ArrayList<String[]>(), "/PLEX1/transaction/start", client);
		StartTransactionOutput spr = RequestUtils.validateSTRSuccess(response);
		logger.info(spr.toString());
		for (String key : spr.getMessages().keySet()) {
			assertEquals(null, spr.getMessages().get(key).getRc());
		}

	}
}
