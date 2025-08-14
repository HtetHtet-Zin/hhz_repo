/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.common.exception;

/**
 * ExcelExportException Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */

public class ExcelExportException extends RuntimeException {
    public ExcelExportException(final String message) {
        super(message);
    }

    public ExcelExportException(final Throwable e) {
        super(e);
    }
}
