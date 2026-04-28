package com.carrental.service;

import com.carrental.common.PageResult;
import com.carrental.dto.BookingSaveDTO;
import com.carrental.dto.BookingVO;

public interface BookingService {

    PageResult<BookingVO> listBookings(Integer page, Integer size, String status, Long userId);

    PageResult<BookingVO> listMyBookings(Integer page, Integer size, String status);

    BookingVO getBookingById(Long id);

    BookingVO createBooking(BookingSaveDTO dto);

    void confirmBooking(Long id);

    void cancelBooking(Long id);
}
