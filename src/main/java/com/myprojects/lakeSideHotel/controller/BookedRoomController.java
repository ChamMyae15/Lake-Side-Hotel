package com.myprojects.lakeSideHotel.controller;

import com.myprojects.lakeSideHotel.exception.InvalidBookingRequestException;
import com.myprojects.lakeSideHotel.exception.ResourceNotFoundException;
import com.myprojects.lakeSideHotel.model.BookedRoom;
import com.myprojects.lakeSideHotel.model.Room;
import com.myprojects.lakeSideHotel.response.BookingResponse;
import com.myprojects.lakeSideHotel.response.RoomResponse;
import com.myprojects.lakeSideHotel.service.BookedRoomService;
import com.myprojects.lakeSideHotel.service.BookedRoomServiceImpl;
import com.myprojects.lakeSideHotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookedRoomController {

    private final BookedRoomServiceImpl bookingService;
    private final RoomService roomService;

    @GetMapping("all-bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings(){
        List<BookedRoom> bookings = bookingService.getAllBookings();
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for(BookedRoom booking : bookings){
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);

    }

    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode){
        try{
            BookedRoom booking = bookingService.findByBookingConfirmationCode(confirmationCode);
            BookingResponse response = getBookingResponse(booking);
            return ResponseEntity.ok(response);
        }
        catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        }
    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId,@RequestBody BookedRoom booking){
        try{
            String confirmationCode = bookingService.saveBooking(roomId,booking);
            return ResponseEntity.ok("Room booked successfully ! Your Booing Confirmation Code is : " + confirmationCode);

        } catch (InvalidBookingRequestException e) {
           return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public void cancelBooking(@PathVariable Long bookingId){
        bookingService.cancelBooking(bookingId);
    }

    private BookingResponse getBookingResponse(BookedRoom booking) {
        Room selectedRoom = roomService.getRoomById(booking.getRoom().getId()).get();
        RoomResponse room = new RoomResponse(selectedRoom.getId(),selectedRoom.getRoomType(),selectedRoom.getRoomPrice());
        return new BookingResponse(booking.getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getGuestFullName(),
                booking.getGuestEmail(),
                booking.getNumOfAdults(),
                booking.getNumOfChildren(),
                booking.getTotalNumOfGuest(),
                booking.getBookingConfirmationCode(), room);
    }


}
