package com.carrental.service;

import com.carrental.dto.BookingVO;
import com.carrental.dto.PickupDTO;
import com.carrental.dto.ReturnDTO;

public interface RentalService {

    BookingVO pickup(PickupDTO dto);

    BookingVO returnVehicle(ReturnDTO dto);
}
