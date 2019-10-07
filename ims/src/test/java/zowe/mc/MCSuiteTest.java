
/**
* This program and the accompanying materials are made available under the terms of the
* Eclipse Public License v2.0 which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Copyright IBM Corporation 2019
*/

package zowe.mc;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

import zowe.mc.tests.TestBasicAuthenticationDeleteUser;
import zowe.mc.tests.TestBasicAuthenticationGetUser;
import zowe.mc.tests.TestBasicAuthenticationPgmUser;
import zowe.mc.tests.TestBasicAuthenticationPostUser;
import zowe.mc.tests.TestBasicAuthenticationRegionUser;
import zowe.mc.tests.TestOMConnection;
import zowe.mc.tests.pgm.TestCreateDeletePgm;
import zowe.mc.tests.pgm.TestQueryPgm;
import zowe.mc.tests.pgm.TestUpdatePgm;
import zowe.mc.tests.tran.TestCreateDeleteTran;
import zowe.mc.tests.tran.TestQueryTran;
import zowe.mc.tests.tran.TestUpdateTran;

/**
 * These tests use Spring Boot with the Apache CXF implementation of JAX-RS
 * 
 * 
 * Right now we have to use JUnit 4 runner for test suites because test suites are
 * currently not supported in JUnit 5. Wondering if we should get rid of test suites 
 * since Maven runs all of the tests during build anyway.
 * @author jerryli
 *
 */
@RunWith(JUnitPlatform.class)
@SelectClasses( { TestOMConnection.class, 
				  TestQueryPgm.class , 
				  TestUpdatePgm.class,
				  TestCreateDeletePgm.class,
				  TestQueryTran.class,
				  TestUpdateTran.class,
				  TestCreateDeleteTran.class
//				  TestBasicAuthenticationPgmUser.class,
//				  TestBasicAuthenticationDeleteUser.class,
//				  TestBasicAuthenticationRegionUser.class,
//				  TestBasicAuthenticationGetUser.class,
//				  TestBasicAuthenticationPostUser.class
				  } )
public class MCSuiteTest {

}
