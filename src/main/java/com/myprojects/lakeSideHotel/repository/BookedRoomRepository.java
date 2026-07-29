package com.myprojects.lakeSideHotel.repository;

import com.myprojects.lakeSideHotel.model.BookedRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookedRoomRepository extends JpaRepository<BookedRoom, Long> {

    Optional<BookedRoom> findByBookingConfirmationCode(String confirmation);

    List<BookedRoom> findByRoomId(Long roomId);
}
