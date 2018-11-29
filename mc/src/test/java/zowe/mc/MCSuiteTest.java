package zowe.mc;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;
//import org.junit.runners.Suite;
//import org.junit.runners.Suite.SuiteClasses;

import zowe.mc.tests.OMConnectionTest;
import zowe.mc.tests.QueryTest;

@RunWith(JUnitPlatform.class)
@SelectClasses( { OMConnectionTest.class, QueryTest.class } )
public class MCSuiteTest {

}
