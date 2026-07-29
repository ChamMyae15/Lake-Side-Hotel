package com.myprojects.lakeSideHotel.service;

import com.myprojects.lakeSideHotel.exception.InternalServerException;
import com.myprojects.lakeSideHotel.exception.ResourceNotFoundException;
import com.myprojects.lakeSideHotel.model.Room;
import com.myprojects.lakeSideHotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;

    @Override
    public Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice) throws SQLException, IOException {
        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        if(!photo.isEmpty()){
            byte[] photoBytes = photo.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            room.setPhoto(photoBlob);
        }
        return roomRepository.save(room);
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException {
        Optional<Room> selectedRoom = roomRepository.findById(roomId);
        if(selectedRoom.isEmpty()){
            throw new ResourceNotFoundException("sorry, Room is not found!");
        }
        Blob photoBlob = selectedRoom.get().getPhoto();
        if(photoBlob != null ){
            return photoBlob.getBytes(1, (int) photoBlob.length());
        }
        return null;
    }

    @Override
    public void deleteRoom(Long roomId) {
        Optional<Room> selectedRoom = roomRepository.findById(roomId);
        if(selectedRoom.isPresent()){
            roomRepository.deleteById(roomId);
        }
    }

    @Override
    public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes) {
        Room selectedRoom = roomRepository.findById(roomId).orElseThrow( () -> new ResourceNotFoundException("Room not found!"));
        if(roomType != null) selectedRoom.setRoomType(roomType);
        if(roomPrice != null ) selectedRoom.setRoomPrice(roomPrice);
        if(photoBytes != null && photoBytes.length > 0 ){
            try{
                selectedRoom.setPhoto(new SerialBlob(photoBytes));
            } catch (SQLException e) {
                throw new InternalServerException("Error updating room!");
            }
        }

        return roomRepository.save(selectedRoom);
    }

    @Override
    public Optional<Room> getRoomById(Long roomId) {
        return Optional.of(roomRepository.findById(roomId).get());
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        return roomRepository.findAvailableByDatesAndType(checkInDate, checkOutDate, roomType);
    }
}
