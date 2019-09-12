
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
	public void testUpdatePgmStopSchd() {

		//First we stop the program
		logger.info("Testing Update PGM by stopping scheduling of a program");		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "DBF*"};
		String[] stop = new String[] {"stop", "SCHD"};
		queryParams.add(names);
		queryParams.add(stop);
		Response responses = RequestUtils.putRequest(queryParams, TestProperties.contextPath + TestProperties.plex + "/program", DEFAULT_USER, DEFAULT_PASSWORD);
		UpdateProgamOutput upr = RequestUtils.validateUPRSuccess(responses);
		logger.info(upr.toString());
		
		//Then we verify it's status
		logger.info("Verifying status");	
		List<String[]> queryParams2 = new ArrayList<>();
		String[] names2 = new String[] {"name", "DBF*"};
		String[] show = new String[] {"attributes", "ALL"};
		queryParams2.add(names2);
		queryParams2.add(show);
		Response responses2 = RequestUtils.getRequest(queryParams2, TestProperties.contextPath + TestProperties.plex + "/program", DEFAULT_USER, DEFAULT_PASSWORD);
		QueryProgramOutput qpr = RequestUtils.validateQPRSuccess(responses2);
		for (QueryProgram r : qpr.getData()) {
			assert(r.getLstt().contains("STOSCHD"));
		}
		
		//First we start the program
		logger.info("Testing Update PGM by starting scheduling of a program");		
		List<String[]> queryParams3 = new ArrayList<>();
		String[] names3 = new String[] {"name", "DBF*"};
		String[] start = new String[] {"start", "SCHD"};
		queryParams3.add(names3);
		queryParams3.add(start);
		Response responses3 = RequestUtils.putRequest(queryParams3, TestProperties.contextPath + TestProperties.plex + "/program", DEFAULT_USER, DEFAULT_PASSWORD);
		UpdateProgamOutput upr2 = RequestUtils.validateUPRSuccess(responses3);
		logger.info(upr2.toString());
		
		//Then we verify it's status
		logger.info("Verifying status");	
		List<String[]> queryParams4 = new ArrayList<>();
		String[] names4 = new String[] {"name", "DBF*"};
		String[] show2 = new String[] {"attributes", "ALL"};
		queryParams4.add(names4);
		queryParams4.add(show2);
		Response responses4 = RequestUtils.getRequest(queryParams4, TestProperties.contextPath + TestProperties.plex + "/program", DEFAULT_USER, DEFAULT_PASSWORD);
		QueryProgramOutput qpr2 = RequestUtils.validateQPRSuccess(responses4);
		for (QueryProgram r : qpr2.getData()) {
			assertEquals(null, r.getLstt());
		}
		
		
	}
	
	

}
