/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.common.exception;

/**
 * ResourceNotFoundException Class.
 * <p>
 * </p>
 *
 * @author Zwe Naing Oo
 */

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(){
        super();
    }

    public ResourceNotFoundException(final String message){
        super(message);
    }
}
