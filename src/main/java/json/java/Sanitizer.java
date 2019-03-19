/**
 *  Copyright IBM Corporation 2018, 2019
 */

package json.java;

import java.util.Locale;

public class Sanitizer {
	private Sanitizer(){}
	
	public static Object sanitize(String key, Object value) {
    	// want to be able to case-insensitive checking of the key
		// Note: toLowerCase is locale sensitive - if the code executes in a foreign locale
		// we might get unexpected results. Currently, we expect our keys to be in english,
		// so Locale.English is used. Regardless of the locale, if the key was not in english
		// we wouldn't be able to sanitize the input anyways.
    	String keyToCheck = (key != null) ? key.toLowerCase(Locale.ENGLISH) : "";
    	// if we see password, make sure we hide the value with something
    	if(keyToCheck.contains("password")) {
    		return "********";
    	}
    	
    	// by default return value;
    	return value;
	}
	
}
