/**
 *  Copyright IBM Corporation 2018, 2019
 */

package zowe.mc;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import application.main.MC;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

/**
 * Because JUnit 5 doesn't have any "run these before test suite" annotations, we have to create a 
 * custom extension and then have text classes extend this through @ExtendWith({SuiteExtension.class})
 * annotation. Turns out @ExtendWith is pretty powerful and flexible. 
 * @author jerryli
 *
 */
public class SuiteExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

	private static boolean started = false;

	@Override
	public void beforeAll(ExtensionContext context) {
		//We start our Spring Boot instance before running tests
		if (!started) {
			started = true;
			MC.main(new String[] {});
			context.getRoot().getStore(GLOBAL).put("any unique name", this);
		}
	}

	@Override
	public void close() {
		// Your "after all tests" logic goes here
	}
}


