
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

public abstract class ThreadHelper {

    /**
     * Returns the name of the current method this executes in
     * @return
     */
    public String getCurrentMethod() {
        return getCurrentMethodNameFromThreadStack(0);
    }

    /**
     * Returns the name of the caller who called the method this code
     * executes in
     * @return
     */
    public String getCallingMethodName() {
        return getCurrentMethodNameFromThreadStack(1);
    }

    /**
     * Returns the name of the callers caller that the method this code
     * executes in.
     * @return
     */
    public String getCallingCallerMethodName() {
        return getCurrentMethodNameFromThreadStack(2);
    }
    
    /**
     * 
     * @param stackLevel
     * @return
     */
    private String getCurrentMethodNameFromThreadStack(int stackPosition) {
        /*
         * Position:
         *  0 will dump the threads
         *  1 will get the stacktrace
         *  2 will get this current method "getCurrentMethodNameFromThread"
         *  3 will get the method calling this method
         *  4 will get method calling the calling method
         */
        int pos = 4;
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[pos + stackPosition];

       // String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();

        //return className + "." + methodName;
        return methodName;
    }
}
