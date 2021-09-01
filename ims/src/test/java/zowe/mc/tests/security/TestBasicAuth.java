

/**
* This program and the accompanying materials are made available under the terms of the
* Eclipse Public License v2.0 which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Copyright IBM Corporation 2019
*/

package zowe.mc.tests.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.rest.responses.pgm.query.QueryProgram;
import application.rest.responses.pgm.query.QueryProgramOutput;
import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;
import zowe.mc.TestProperties;

/**
 * Tests for "QUERY PGM" IMS rest services
 * @author jerryli
 *
 */
@ExtendWith({SuiteExtension.class})
public class TestBasicAuth 
{

	private static final Logger logger = LoggerFactory.getLogger(TestBasicAuth.class);
	private final String GET_USER = "get";
	private final String PGM_USER = "pgm";
	//private final String ADMIN_USER = "admin";
	private final String POST_USER = "post";
	private final String TRAN_USER = "tran";
	private final String DEFAULT_PASSWORD = "password";

	/**
	 * Setup rest client
	 */
	@BeforeAll
	public static void setUp() {
	}

	/**
	 * Tests successfull credential on query command
	 * @throws Exception
	 */
	@Test
	public void testPositivePgmQuery() {
		logger.info("TESTING Query PGM");
		
		
		//QUERY PGM
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(),  TestProperties.contextPath + TestProperties.plex + "/program", GET_USER, DEFAULT_PASSWORD);
		QueryProgramOutput qpr = RequestUtils.validateQPRSuccess(response);
		/*Check if data is correct*/
		logger.info(qpr.toString());
		for (QueryProgram q : qpr.getData()) {
			assertEquals("0", q.getCc());
		}
		for (String key : qpr.getMessages().keySet()) {
			assertEquals(null, qpr.getMessages().get(key).getRc());
		}
		
		Response response1 = RequestUtils.getRequest(new ArrayList<String[]>(),  TestProperties.contextPath + TestProperties.plex + "/program", PGM_USER, DEFAULT_PASSWORD);
		QueryProgramOutput qpr1 = RequestUtils.validateQPRSuccess(response1);
		/*Check if data is correct*/
		logger.info(qpr1.toString());
		for (QueryProgram q : qpr1.getData()) {
			assertEquals("0", q.getCc());
		}
		for (String key : qpr1.getMessages().keySet()) {
			assertEquals(null, qpr1.getMessages().get(key).getRc());
		}
	}
	
	/**
	 * Tests successfull credential on query command
	 * @throws Exception
	 */
	@Test
	public void testNegativePgmQuery() {
		logger.info("TESTING Query PGM");
		
		
		//QUERY PGM
		Response response = RequestUtils.getRequest(new ArrayList<String[]>(),  TestProperties.contextPath + TestProperties.plex + "/program", POST_USER, DEFAULT_PASSWORD);
		assertEquals(response.getStatus(), Response.Status.FORBIDDEN.getStatusCode());
		
		
		Response response1 = RequestUtils.getRequest(new ArrayList<String[]>(),  TestProperties.contextPath + TestProperties.plex + "/program", TRAN_USER, DEFAULT_PASSWORD);
		assertEquals(response1.getStatus(), Response.Status.FORBIDDEN.getStatusCode()); 
		//
	}

	
}
