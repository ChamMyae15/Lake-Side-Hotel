package com.myprojects.lakeSideHotel.service;

import com.myprojects.lakeSideHotel.exception.InvalidBookingRequestException;
import com.myprojects.lakeSideHotel.exception.ResourceNotFoundException;
import com.myprojects.lakeSideHotel.model.BookedRoom;
import com.myprojects.lakeSideHotel.model.Room;
import com.myprojects.lakeSideHotel.repository.BookedRoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookedRoomServiceImpl implements BookedRoomService{

    private final BookedRoomRepository repo;

    private final RoomService roomService;

    @Override
    public List<BookedRoom> getAllBookings() {
        return repo.findAll();
    }

    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return repo.findByRoomId(roomId);
    }

    @Override
    public BookedRoom findByBookingConfirmationCode(String confirmationCode) {
        return repo.findByBookingConfirmationCode(confirmationCode)
                .orElseThrow( () -> new ResourceNotFoundException("No booking found with confirmation code : " + confirmationCode));
    }

    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        if(bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
            throw new InvalidBookingRequestException("Check-in date must come before Check-out date !");
        }
        Room room = roomService.getRoomById(roomId).get();
        List<BookedRoom> existingBookings = room.getBookings();
        boolean isRoomAvailable = isRoomAvailable(bookingRequest,existingBookings );
        if(isRoomAvailable){
            room.addBooking(bookingRequest);
            repo.save(bookingRequest);
        }
        else{
            throw new InvalidBookingRequestException("Sorry, this room is not available for the selected date!");
        }
        return bookingRequest.getBookingConfirmationCode();
    }

    @Override
    public void cancelBooking(Long bookingId) {
        repo.deleteById(bookingId);
    }

    private boolean isRoomAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        return existingBookings.stream().noneMatch(
                existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                     || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                     || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                     && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                     || (bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate())
                     && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate()))
                     || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())
                     && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))
                     || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                     && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))
                     || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                      && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
        );

    }

}
