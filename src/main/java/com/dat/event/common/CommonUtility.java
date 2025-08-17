/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.common;

import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Array;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;

/**
 * CommonUtility Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */

public final class CommonUtility {

    public static String ifNoDataReturnDash(final String input) {
        return isEmptyObject(input) ? "-" : input;
    }

    /**
     * @param input the object input to check
     * @return will be true if the object is null, 0, whitespace or empty.
     * @author Zwel Naing Oo
     */
    public static boolean isEmptyObject(final Object input) {
        return input == null ||
                (input instanceof final String str && str.isBlank()) ||
                (input instanceof final Number number && number.longValue() == 0L) ||
                (input instanceof final Collection collection && collection.isEmpty()) ||
                (input instanceof final Map map && map.isEmpty()) ||
                (input instanceof final MultipartFile multipartFile && multipartFile.isEmpty()) ||
                (input.getClass().isArray() && Array.getLength(input) == 0);
    }

    public static DateTimeFormatter formatTo12Hrs = DateTimeFormatter.ofPattern("hh:mm a");

}
