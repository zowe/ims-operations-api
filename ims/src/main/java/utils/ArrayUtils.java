
/**
* This program and the accompanying materials are made available under the terms of the
* Eclipse Public License v2.0 which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Copyright IBM Corporation 2019
*/

package utils;

import java.util.Properties;

public class ArrayUtils {

	/**
	 * Method to help merge two arrays of integer primitives
	 * @param a
	 * @param b
	 * @return
	 */
    public synchronized static int[] mergeArrays(int[] a, int[] b){
        int length = a.length + b.length;
        int[] result = new int[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
    
    public synchronized static Properties[] mergeArrays(Properties[] a, Properties[] b){
        int length = a.length + b.length;
        Properties[] result = new Properties[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
