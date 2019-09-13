package zowe.mc.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
	
	@Test
	public void testNoCredentials() {
		logger.info("TESTING No Credentials");
		String path = TestProperties.contextPath + TestProperties.plex + "/program";
		String username = null;
		String password = null;
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), path, username, password);
		assertEquals(response.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void testCorrectAdminCredentials() {
		logger.info("TESTING Correct Admin Credentials");
		String path = TestProperties.contextPath + TestProperties.plex + "/program";
		String username = "admin";
		String password = "password";
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), path, username, password);
		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
	}

	@Test
	public void testPgmUserAccessPgmGetService() {
		logger.info("TESTING Pgm User Access Pgm Get Service");
		String path = TestProperties.contextPath + TestProperties.plex + "/program";
		String username = "pgm";
		String password = "password";
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), path, username, password);
		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
	}
	
	@Test
	public void testPgmUserAccessPgmPutService() {
		logger.info("TESTING Pgm User Access Pgm Get Service");
		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "DBF*"};
		String[] stop = new String[] {"stop", "SCHD"};
		queryParams.add(names);
		queryParams.add(stop);
		
		String path = TestProperties.contextPath + TestProperties.plex + "/program";
		String username = "pgm";
		String password = "password";
		Response response = RequestUtils.putRequest(queryParams, path, username, password);
		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
	}
	
	@Test
	// INCOMPLETE
	public void testPgmUserAccessPgmPostService() {
		/*
		 * For Post Request, should we actually create a pgm and delete it? 
		 * 
		logger.info("TESTING Pgm User Access Pgm Get Service");
		String path = TestProperties.contextPath + TestProperties.plex + "/program";
		String username = "pgm";
		String password = "password";
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), path, username, password);
		assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
		*/
	}
	
	@Test
	public void testPgmUserAccessTranGetService() {
		logger.info("TESTING Pgm User Access Tran Service");
		String username = "pgm";
		String password = "password";
		String path = TestProperties.contextPath + TestProperties.plex + "/transaction";
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), path, username, password);
		assertEquals(response.getStatus(), Response.Status.FORBIDDEN.getStatusCode());
	}
	
	@Test
	public void testPgmUserAccessTranPutService() {
		return;
	}
	
	@Test
	//INCOMPLETE
	public void testPgmUserAccessTranPostService() {
		/*
		 * Will this actually create a transaction? 
		 * 
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		String[] pgm = new String[] {"pgm", "JUNIT"};
		queryParams.add(names);
		queryParams.add(pgm);
		String path = TestProperties.contextPath + TestProperties.plex + "/transaction";
		String username = "pgm";
		String password = "password";
		RequestUtils.postRequest(queryParams, path, username, password);
		*/
	}
	
	@Test
	public void testPgmUserAccessRegionGetService() {
		logger.info("TESTING Pgm User Access Region Service");
		String username = "pgm";
		String password = "password";
		String path = TestProperties.contextPath + TestProperties.plex + "/region";
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), path, username, password);
		assertEquals(response.getStatus(), Response.Status.FORBIDDEN.getStatusCode());
	}
	
//	@Test
//	public void testPgmUserAccessRegionPostService() {
//		return;
//	}
//	
//	@Test
//	public void testPgmUserAccessRegionPutService() {
//		return;
//	}
	
	
	public void testIncorrectCredentials() {
		
	}
	
	public void testCorrectUsernameIncorrectPassword() {
		
	}
	
	public void testIncorrectUsernameCorrectPassword() {
		
	}

	public void testRoles() {
		
	}
}
