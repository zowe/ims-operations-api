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

import application.rest.responses.pgm.create.CreateProgram;
import application.rest.responses.pgm.create.CreateProgramOutput;
import application.rest.responses.pgm.delete.DeleteProgram;
import application.rest.responses.pgm.delete.DeleteProgramOutput;
import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;
import zowe.mc.TestProperties;

@ExtendWith({SuiteExtension.class})
public class TestBasicAuthenticationPgmUser {
	
	private static final Logger logger = LoggerFactory.getLogger(TestBasicAuthenticationPgmUser.class);
	private static final String ADMIN_USER = "admin";
	private static final String PGM_USER = "pgm";
	private static final String DEFAULT_PASSWORD = "password";
	private static String PGM_PATH = TestProperties.contextPath + TestProperties.plex + "/program";
	private static String TRAN_PATH = TestProperties.contextPath + TestProperties.plex + "/transaction";
	private static String REGION_PATH = TestProperties.contextPath + TestProperties.plex + "/region";
	
	/**
	 * Setup rest client
	 */
	@BeforeAll
	public static void setUp() {
	}
	
	@Test
	public void testNoCredentials() {
		logger.info("TESTING No Credentials");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), PGM_PATH, null, null);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testCorrectAdminCredentials() {
		logger.info("TESTING Correct Admin Credentials");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void testPgmUserAccessPgmGetService() {
		logger.info("TESTING Pgm User Access Pgm GET Service");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), PGM_PATH, PGM_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testPgmUserAccessPgmPutService() {
		logger.info("TESTING Pgm User Access Pgm PUT Service");
		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "DBF*"};
		String[] stop = new String[] {"stop", "SCHD"};
		queryParams.add(names);
		queryParams.add(stop);
		
		Response response = RequestUtils.putRequest(queryParams, PGM_PATH, PGM_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testPgmUserAccessPgmPostService() {
		logger.info("TESTING Pgm User Access Pgm POST Service");
		/* 
		 * For POST service, we must create and delete the resource that was created
		 * NOTE: Using ADMIN_USER for portions of the test that require deleting resources.
		 *  
		 * */
		
		/* Delete the TEST pgm if already created */
		List<String[]> queryParamspre = new ArrayList<>();
		String[] namespre = new String[] {"name", "TEST"};
		queryParamspre.add(namespre);
		RequestUtils.deleteRequest(queryParamspre, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		
		/* Create the TEST pgm */
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "TEST"};
		queryParams.add(names);
		Response response = RequestUtils.postRequest(queryParams, PGM_PATH, PGM_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		CreateProgramOutput cpr = RequestUtils.validateCPRSuccess(response);
		
		/* Verifying if pgm resource was created */
		logger.info(cpr.toString());
		for (CreateProgram q : cpr.getData()) {
			assertEquals("0", q.getCc());
			assertEquals("TEST", q.getPgm());
		}
		for (String key : cpr.getMessages().keySet()) {
			assertEquals(null, cpr.getMessages().get(key).getRc());
		}
		
		/* Deleting the TEST pgm */
		List<String[]> queryParams2 = new ArrayList<>();
		String[] names2 = new String[] {"name", "TEST"};
		queryParams2.add(names2);
		Response response2 = RequestUtils.deleteRequest(queryParams2, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		DeleteProgramOutput dpr2 = RequestUtils.validateDPRSuccess(response2);
		
		/* Verifying if pgm resource was deleted */
		logger.info(dpr2.toString());
		for (DeleteProgram q : dpr2.getData()) {
			assertEquals("0", q.getCc());
			assertEquals("TEST", q.getPgm());
		}
		for (String key : dpr2.getMessages().keySet()) {
			assertEquals(null, dpr2.getMessages().get(key).getRc());
		}
	}
	
	@Test
	public void testPgmUserAccessTranGetService() {
		logger.info("TESTING Pgm User Access Tran GET Service");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), TRAN_PATH, PGM_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testPgmUserAccessTranPutService() {
		logger.info("TESTING Pgm User Access Tran PUT Service (stopping scheduling of a transaction)");		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		String[] stop = new String[] {"stop", "SCHD"};
		queryParams.add(names);
		queryParams.add(stop);
		Response response = RequestUtils.putRequest(queryParams, TRAN_PATH, PGM_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testPgmUserAccessTranPostService() {
		logger.info("TESTING Pgm User Access Tran POST Service");	
		// Will use ADMIN_USER for any necessary delete requests
		// Need to create a program first
		logger.info("Creating test program 'JUNIT'");
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		queryParams.add(names);
		RequestUtils.postRequest(queryParams, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
		
		// Attempt to create transaction
		logger.info("Attempting to create test transaction");
		List<String[]> queryParams1 = new ArrayList<>();
		String[] names1 = new String[] {"name", "TEST"};
		String[] pgm1 = new String[] {"pgm", "JUNIT"};
		queryParams1.add(names1);
		queryParams1.add(pgm1);
		Response response1 = RequestUtils.postRequest(queryParams1, TRAN_PATH, PGM_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response1.getStatus());
		
		// Delete program that was created
		logger.info("Deleting test program 'JUNIT'");
		List<String[]> queryParams3 = new ArrayList<>();
		String[] names3 = new String[] {"name", "JUNIT"};
		queryParams3.add(names3);
		RequestUtils.deleteRequest(queryParams3, PGM_PATH, ADMIN_USER, DEFAULT_PASSWORD);
	}
	
	@Test
	public void testPgmUserAccessRegionGetService() {
		logger.info("TESTING Pgm User Access Region GET Service");
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(), REGION_PATH, PGM_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void testPgmUserAccessRegionPutService() {
		logger.info("TESTING Pgm User Access Region PUT Service");
		Response response = RequestUtils.putRequest(new ArrayList<String[]>(), REGION_PATH + "/stop", PGM_USER, DEFAULT_PASSWORD);
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
	

	
//	@AfterEach
//	public void sit() {
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
}
