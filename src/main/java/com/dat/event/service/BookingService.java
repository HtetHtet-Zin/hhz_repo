/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.service;

import com.dat.event.dto.BookingDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * bookingService Class.
 * <p>
 * </p>
 *
 * @author kyi min khant
 */
public interface BookingService {

    Page<BookingDto> findAllBooking(String keyword, int page);
}
