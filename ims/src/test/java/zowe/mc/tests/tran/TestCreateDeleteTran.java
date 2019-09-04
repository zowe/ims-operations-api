
/**
* This program and the accompanying materials are made available under the terms of the
* Eclipse Public License v2.0 which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Copyright IBM Corporation 2019
*/

package zowe.mc.tests.tran;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.rest.responses.tran.create.CreateTransaction;
import application.rest.responses.tran.create.CreateTransactionOutput;
import application.rest.responses.tran.delete.DeleteTransaction;
import application.rest.responses.tran.delete.DeleteTransactionOutput;
import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;
import zowe.mc.TestProperties;

/**
 * Tests for "CREATE/DELETE TRAN" IMS rest services
 * @author jerryli
 *
 */
@ExtendWith({SuiteExtension.class})
public class TestCreateDeleteTran 
{

	private static final Logger logger = LoggerFactory.getLogger(TestCreateDeleteTran.class);
	private final String POST_USER = "post";
	private final String DELETE_USER = "delete";
	private final String DEFAULT_PASSWORD = "password";


	/**
	 * Setup rest client
	 */
	@BeforeAll
	public static void setUp() {
		logger.info("TESTING CREATE and DELETE TRAN");

		logger.info("Need to create program first");
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "JUNIT"};
		queryParams.add(names);
		RequestUtils.postRequest(queryParams, TestProperties.contextPath + TestProperties.plex + "/program", "pgm","password");
		//CreateProgramOutput cpr = RequestUtils.validateCPRSuccess(response);
		/*Check if data is correct*/
//		logger.info(cpr.toString());
//		for (CreateProgram q : cpr.getData()) {
//			assertEquals("0", q.getCc());
//			assertEquals("TEST", q.getPgm());
//		}
//		for (String key : cpr.getMessages().keySet()) {
//			assertEquals(null, cpr.getMessages().get(key).getRc());
//		}
	}

	/**
	 * Tests rest service for submitting QUERY PGM IMS command
	 * @throws Exception
	 */
	@Test
	public void testCreateDeleteTran() {



		List<String[]> queryParams1 = new ArrayList<>();
		String[] names1 = new String[] {"name", "TEST"};
		String[] pgm1 = new String[] {"pgm", "JUNIT"};
		queryParams1.add(names1);
		queryParams1.add(pgm1);
		Response response1 = RequestUtils.postRequest(queryParams1, TestProperties.contextPath + TestProperties.plex + "/transaction", POST_USER, DEFAULT_PASSWORD);
		CreateTransactionOutput ctr = RequestUtils.validateCTRSuccess(response1);
		/*Check if data is correct*/
		logger.info(ctr.toString());
		for (CreateTransaction q : ctr.getData()) {
			assertEquals("0", q.getCc());
			assertEquals("TEST", q.getTran());
		}
		for (String key : ctr.getMessages().keySet()) {
			assertEquals(null, ctr.getMessages().get(key).getRc());
		}

		List<String[]> queryParams2 = new ArrayList<>();
		String[] names2 = new String[] {"name", "TEST"};
		queryParams2.add(names2);
		Response response2 = RequestUtils.deleteRequest(queryParams2, TestProperties.contextPath + TestProperties.plex + "/transaction", DELETE_USER, DEFAULT_PASSWORD);
		DeleteTransactionOutput dtr = RequestUtils.validateDTRSuccess(response2);
		/*Check if data is correct*/
		logger.info(dtr.toString());
		for (DeleteTransaction q : dtr.getData()) {
			assertEquals("0", q.getCc());
			assertEquals("TEST", q.getTran());
		}
		for (String key : dtr.getMessages().keySet()) {
			assertEquals(null, dtr.getMessages().get(key).getRc());
		}



	}

	@AfterAll
	public static void takeDown() {
		List<String[]> queryParams3 = new ArrayList<>();
		String[] names3 = new String[] {"name", "JUNIT"};
		queryParams3.add(names3);
		RequestUtils.deleteRequest(queryParams3, TestProperties.contextPath + TestProperties.plex + "/program", "pgm", "password");
	}
}