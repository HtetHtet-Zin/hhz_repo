/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.service.impl;

import com.dat.event.common.CommonUtility;
import com.dat.event.common.constant.Constants;
import com.dat.event.common.mappers.AccessoriesMapper;
import com.dat.event.dto.AccessoriesDto;
import com.dat.event.dto.BookingDto;
import com.dat.event.entity.BookingEntity;
import com.dat.event.entity.EventScheduleEntity;
import com.dat.event.entity.RequestedAccessoriesEntity;
import com.dat.event.entity.embeddabel.RequestedAccessoriesKey;
import com.dat.event.repository.AccessoriesRepository;
import com.dat.event.repository.BookingRepository;
import com.dat.event.repository.EventScheduleRepository;
import com.dat.event.repository.RequestedAccessoriesRepository;
import com.dat.event.repository.StaffRepository;
import com.dat.event.service.BookingService;
import com.dat.event.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final AccessoriesRepository accessoriesRepository;
    private final AccessoriesMapper accessoriesMapper;
    private final EventScheduleRepository eventScheduleRepository;
    private final ImageStorageService imageStorageService;
    private final StaffRepository staffRepository;
    private final EventScheduleRepository scheduleRepository;
    private final RequestedAccessoriesRepository requestedAccessoriesRepository;

    @Override
    public Page<BookingDto> findAllBooking(String keyword, int page) {
        Page<Object[]> bookings= bookingRepository.getAllBooking(keyword, PageRequest.of(page, Constants.PAGE_LIMIT));
       Page<BookingDto> bookingDtos =  bookings.map(objects -> BookingDto.builder()
                .id(objects[0] != null ? Long.valueOf(objects[0].toString()) : null)
                .eventName(objects[1] != null
                                  ? objects[1].toString()
                                  : null)
                .bookedDate(objects[2] != null
                                ? LocalDateTime.parse(objects[2].toString(),CommonUtility.formatToLocalDateTime)
                                : null)
                .status(objects[3] != null ? objects[3].toString() : null)
                .staffName(objects[4] != null ? objects[4].toString() : null)
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
                .scheduleId(objects[10] != null ? Long.valueOf(objects[10].toString()) : null)
                .attendees(objects[11] != null ? Integer.parseInt((objects[11].toString())) : null)
                .build()
        );
         bookingDtos.forEach(bookingDto -> {
            var Accessories = requestedAccessoriesRepository.getAccessories(bookingDto.getId());
            String accessory = String.join(", ", Accessories);
            bookingDto.setAccessories(accessory);
        });

        return bookingDtos;

    }

    @Override
    public List<AccessoriesDto> getAccessories() {
        return accessoriesMapper.toDtoList(accessoriesRepository.findAll());
    }

    @Override
    public void makeBooking(Long scheduleId, int attendees, List<Long> accessories, String purpose, MultipartFile signature, String staffId, String eventName) {
        EventScheduleEntity schedule = eventScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        BookingEntity booking = new BookingEntity();
        booking.setSchedule(schedule);
        booking.setEventName(eventName);
        booking.setStatus(Constants.PENDING);
        booking.setBookedDate(LocalDate.now());
        booking.setBookedBy(staffId);
        booking.setAttendees(attendees);
        booking.setDelFlag(false);
        var savedBooking = bookingRepository.save(booking);

        if (accessories != null) {
            List<RequestedAccessoriesEntity> requestedList = accessories.stream().map(accId -> {
                RequestedAccessoriesEntity req = new RequestedAccessoriesEntity();
                RequestedAccessoriesKey key = new RequestedAccessoriesKey(savedBooking.getId(), accId);
                req.setId(key);
                req.setBooking(booking);
                req.setAccessories(accessoriesRepository.findById(accId)
                        .orElseThrow(() -> new RuntimeException("Accessory not found: " + accId)));
                return req;
            }).toList();
            requestedAccessoriesRepository.saveAll(requestedList);
        }

        schedule.setBookingFlag(false);
        schedule.setUpdatedAt(LocalDateTime.now());
        schedule.setUpdatedBy(staffId);
        eventScheduleRepository.save(schedule);
        imageStorageService.saveImage(signature, staffId, false);
    }

    @Override
    public void checkBookingANDApproverFlag(Long bookingId , String staffNo) {
       bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not Found"));
       var staff = staffRepository.findByStaffNo(staffNo).orElseThrow(() ->new IllegalArgumentException("Staff not Found"));
      if(!staff.getApproverFlag()){
          throw new IllegalArgumentException(staffNo +" don't have approve permission");
      }
    }

    @Override
    public void approveBookingById(Long bookingId, String name, String reason, String action) {

       var booking = bookingRepository.findById(bookingId).orElseThrow();
       var schedule = scheduleRepository.findById(booking.getSchedule().getId()).orElseThrow();
       schedule.setBookingFlag("approve".equalsIgnoreCase(action) ? true : false);
        booking.setSchedule(schedule);
       booking.setConfirmedBy(name);
       booking.setConfirmedDate(LocalDate.now());
       booking.setStatus( "approve".equalsIgnoreCase(action) ? Constants.APPROVED : Constants.REJECTED);
       booking.setUpdatedAt(LocalDateTime.now());
       booking.setUpdatedBy(name);
       bookingRepository.save(booking);
    }
}
