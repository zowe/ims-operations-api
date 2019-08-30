
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
import application.rest.responses.pgm.delete.DeleteProgram;
import application.rest.responses.pgm.delete.DeleteProgramOutput;
import zowe.mc.RequestUtils;
import zowe.mc.SuiteExtension;
import zowe.mc.TestProperties;

/**
 * Tests for "CREATE/DELETE PGM" IMS rest services
 * @author jerryli
 *
 */
@ExtendWith({SuiteExtension.class})
public class TestCreateDeletePgm 
{

	private static final Logger logger = LoggerFactory.getLogger(TestCreateDeletePgm.class);


	/**
	 * Setup rest client
	 */
	@BeforeAll
	public static void setUp() {
	}

	/**
	 * Tests rest service for submitting QUERY PGM IMS command
	 * @throws Exception
	 */
	@Test
	public void testCreateDeletePgm() {
		logger.info("TESTING CREATE and DELETE PGM");
		
		List<String[]> queryParamspre = new ArrayList<>();
		String[] namespre = new String[] {"name", "TEST"};
		queryParamspre.add(namespre);
		RequestUtils.deleteRequest(queryParamspre, TestProperties.contextPath + TestProperties.plex + "/program");
		
		List<String[]> queryParams = new ArrayList<>();
		String[] names = new String[] {"name", "TEST"};
		queryParams.add(names);
		Response response = RequestUtils.postRequest(queryParams, TestProperties.contextPath + TestProperties.plex + "/program");
		CreateProgramOutput cpr = RequestUtils.validateCPRSuccess(response);
		/*Check if data is correct*/
		logger.info(cpr.toString());
		for (CreateProgram q : cpr.getData()) {
			assertEquals("0", q.getCc());
			assertEquals("TEST", q.getPgm());
		}
		for (String key : cpr.getMessages().keySet()) {
			assertEquals(null, cpr.getMessages().get(key).getRc());
		}
		
		List<String[]> queryParams2 = new ArrayList<>();
		String[] names2 = new String[] {"name", "TEST"};
		queryParams2.add(names2);
		Response response2 = RequestUtils.deleteRequest(queryParams2, TestProperties.contextPath + TestProperties.plex + "/program");
		DeleteProgramOutput dpr2 = RequestUtils.validateDPRSuccess(response2);
		/*Check if data is correct*/
		logger.info(dpr2.toString());
		for (DeleteProgram q : dpr2.getData()) {
			assertEquals("0", q.getCc());
			assertEquals("TEST", q.getPgm());
		}
		for (String key : dpr2.getMessages().keySet()) {
			assertEquals(null, dpr2.getMessages().get(key).getRc());
		}


	}
}