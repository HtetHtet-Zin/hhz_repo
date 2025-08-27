/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.service.impl;

import com.dat.event.common.CommonUtility;
import com.dat.event.common.constant.Constants;
import com.dat.event.dto.BookingDto;
import com.dat.event.dto.EventScheduleDto;
import com.dat.event.repository.BookingRepository;
import com.dat.event.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * BookingServiceImpl Class.
 * <p>
 * </p>
 *
 * @author kyi min khant
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;


    @Override
    public Page<BookingDto> findAllBooking(String keyword, int page) {
        Page<Object[]> bookings= bookingRepository.getAllBooking(keyword, PageRequest.of(page, Constants.PAGE_LIMIT));
        return bookings.map(objects -> BookingDto.builder()
                .id(objects[0] != null ? Long.valueOf(objects[0].toString()) : null)
                .eventName(objects[1] != null
                                  ? objects[1].toString()
                                  : null)
                .bookedDate(objects[2] != null
                                ? LocalDateTime.parse(objects[2].toString(),CommonUtility.formatToLocalDateTime)
                                : null)
                .status(objects[3] != null ? objects[3].toString() : null)
                .name(objects[4] != null ? objects[4].toString() : null)
                .team(objects[5] != null ? objects[5].toString() : null)
                .department(objects[6] != null ? objects[6].toString() : null)
                .date(objects[7] != null
                                    ? LocalDate.parse(objects[7].toString(),CommonUtility.formatToLocalDate)
                                    : null)
                .startTime(objects[8] != null
                                 ? LocalTime.parse(objects[8].toString()).format(CommonUtility.formatTo12Hrs)
                                 : null)
                .endTime(objects[9] != null
                                ? LocalTime.parse(objects[9].toString()).format(CommonUtility.formatTo12Hrs)
                                : null)
                .build()
        );
    }
}
