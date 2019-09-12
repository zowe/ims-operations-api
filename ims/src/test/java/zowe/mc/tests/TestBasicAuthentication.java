package zowe.mc.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;
import zowe.mc.TestProperties;

@ExtendWith({SuiteExtension.class})
public class TestBasicAuthentication {
	
	private static final Logger logger = LoggerFactory.getLogger(TestBasicAuthentication.class);
	
	/**
	 * Setup rest client
	 */
	@BeforeAll
	public static void setUp() {
	}
	
	public void testNoCredentials() {
		logger.info("TESTING No Credentials");
		String path = TestProperties.contextPath + TestProperties.plex + "/program";
		String username = null;
		String password = null;
		Response response = RequestUtils.customGetRequest(new ArrayList<String[]>(), path, username, password);
//		QueryProgramOutput qpr = RequestUtils.validateQPRSuccess(response);
		assertEquals(response.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
	}
	
	public void testCorrectCredentials() {
		
	}
	
	public void testIncorrectCredentials() {
		
	}
	
	public void testCorrectUsernameIncorrectPassword() {
		
	}
	
	public void testIncorrectUsernameCorrectPassword() {
		
	}

	public void testRoles() {
		
	}
}
