
/**
* This program and the accompanying materials are made available under the terms of the
* Eclipse Public License v2.0 which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Copyright IBM Corporation 2019
*/

package zowe.mc.tests.pgm;

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
import application.rest.responses.pgm.query.QueryProgram;
import application.rest.responses.pgm.query.QueryProgramOutput;
import application.rest.responses.pgm.update.UpdateProgamOutput;
import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;
import zowe.mc.TestProperties;

/**
 * Tests for "UPDATE PGM" IMS rest services
 * @author jerryli
 *
 */
@ExtendWith({SuiteExtension.class})
public class TestUpdatePgm {
	
	private static final Logger logger = LoggerFactory.getLogger(TestUpdatePgm.class);
	private final String DEFAULT_USER = "admin";
	private final String POST_USER = "post";
	private final String DELETE_USER = "delete";
	private final String DEFAULT_PASSWORD = "password";
	
	/**
	 * Setup rest client
	 */
	@BeforeAll
	public static void setUp() {
	}

	/**
	 * Tests rest service for submitting UPDATE PGM IMS command
	 * @throws Exception
	 */
	@Test
	public void testUpdatePgmStop() {
		
		List<String[]> queryParamspre = new ArrayList<>();
		String[] namespre = new String[] {"name", "TESTUPD"};
		queryParamspre.add(namespre);
		RequestUtils.deleteRequest(queryParamspre, TestProperties.contextPath + TestProperties.plex + "/program", DELETE_USER, DEFAULT_PASSWORD);
		
		List<String[]> qp = new ArrayList<>();
		String[] nms = new String[] {"name", "TESTUPD"};
		qp.add(nms);
		Response rsp = RequestUtils.postRequest(qp, TestProperties.contextPath + TestProperties.plex + "/program", POST_USER, DEFAULT_PASSWORD);
		CreateProgramOutput cpr = RequestUtils.validateCPRSuccess(rsp);
		/*Check if data is correct*/
		logger.info(cpr.toString());
		for (CreateProgram q : cpr.getData()) {
			assertEquals("0", q.getCc());
			assertEquals("TESTUPD", q.getPgm());
		}
		for (String key : cpr.getMessages().keySet()) {
			assertEquals(null, cpr.getMessages().get(key).getRc());
		}

		//First we stop the program
		logger.info("Testing Update PGM by stopping scheduling of a program");		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "TESTUPD"};
		String[] stop = new String[] {"stop", "SCHD"};
		queryParams.add(names);
		queryParams.add(stop);
		Response responses = RequestUtils.putRequest(queryParams, TestProperties.contextPath + TestProperties.plex + "/program", DEFAULT_USER, DEFAULT_PASSWORD);
		UpdateProgamOutput upr = RequestUtils.validateUPRSuccess(responses);
		logger.info(upr.toString());
		
		//Then we verify it's status
		logger.info("Verifying status");	
		List<String[]> queryParams2 = new ArrayList<>();
		String[] names2 = new String[] {"name", "TESTUPD"};
		String[] show = new String[] {"attributes", "ALL"};
		queryParams2.add(names2);
		queryParams2.add(show);
		Response responses2 = RequestUtils.getRequest(queryParams2, TestProperties.contextPath + TestProperties.plex + "/program", DEFAULT_USER, DEFAULT_PASSWORD);
		QueryProgramOutput qpr = RequestUtils.validateQPRSuccess(responses2);
		for (QueryProgram r : qpr.getData()) {
			assert(r.getLstt().contains("STOSCHD"));
		}
		
		
	}
	
	

}
