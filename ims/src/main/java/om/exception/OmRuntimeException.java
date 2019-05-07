
/**
* This program and the accompanying materials are made available under the terms of the
* Eclipse Public License v2.0 which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Copyright IBM Corporation 2019
*/

package om.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;


public class OmRuntimeException extends RuntimeException{

    private static final long serialVersionUID = 1L;
    
    private String stackTrace;
    private Exception originalException;
    
    /**
     * Converts an exception to an runtime exception.
     * NOTE: Use of this conversion can mask exceptions, a runtime exception and/or conversion to one should
     * only be used when its a program error that a user can not leverage the exception to recover. 
     * @param e
     */
    public OmRuntimeException(Exception e) {
      super(e.toString());
      originalException = e;
      
      //Set the stack trace 
      StringWriter stringWriter = new StringWriter();
      e.printStackTrace(new PrintWriter(stringWriter));
      stackTrace = stringWriter.toString();
    }
    
    @Override
    public void printStackTrace() { 
      printStackTrace(System.err);
    }
    
    @Override
    public void printStackTrace(PrintStream stream) { 
      synchronized(stream) {
          stream.print(getClass().getName() + ": ");
          stream.print(stackTrace);
      }
    }
    
    @Override
    public void printStackTrace(PrintWriter stream) { 
      synchronized(stream) {
          stream.print(getClass().getName() + ": ");
          stream.print(stackTrace);
      }
    }
    
    /**
     * Returns the orginal exception. 
     * @throws Exception
     */
    public void getOriginalException() throws Exception { 
        throw originalException; 
    }

    @Override
    public String toString(){
        return originalException.toString();
    }
}
