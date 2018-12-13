/*********************************************************************************
 * Licensed Materials - Property of IBM
 * 5655-TAC
 * (C) Copyright IBM Corp. 2013 All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with 
 * IBM Corp.               
 *********************************************************************************/

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
