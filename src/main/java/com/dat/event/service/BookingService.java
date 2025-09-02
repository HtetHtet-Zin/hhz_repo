/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.service;

import com.dat.event.dto.AccessoriesDto;
import com.dat.event.dto.BookingDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

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

    void checkBookingANDApproverFlag(Long bookingId , String staffNo);

    void approveBookingById(Long bookingId,String name,String reason,String action);

    List<AccessoriesDto> getAccessories();

    void makeBooking(Long scheduleId, int attendees, List<Long> accessories, String purpose, MultipartFile signature, String staffId, String eventName);
    void updateBooking(Long bookingId, int attendees, List<Long> accessories, String purpose, String staffId);

    BookingDto getBookingSchedule(Long scheduleId);

    void changeEventName(List<Long> scheduleIds , String eventName);
}
