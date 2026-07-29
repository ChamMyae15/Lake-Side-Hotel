package com.myprojects.lakeSideHotel.service;

import com.myprojects.lakeSideHotel.model.BookedRoom;
import com.myprojects.lakeSideHotel.repository.BookedRoomRepository;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;


public interface BookedRoomService  {

    List<BookedRoom> getAllBookingsByRoomId(Long roomId);

    List<BookedRoom> getAllBookings();

    BookedRoom findByBookingConfirmationCode(String confirmationCode);

    String saveBooking(Long roomId, BookedRoom bookingRequest);

    void cancelBooking(Long bookingId);
}
