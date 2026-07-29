package com.myprojects.lakeSideHotel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookedRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "check_in")
    private LocalDate checkInDate;
    @Column(name = "check_out")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;
    @Column(name = "guest_fullname")
    private String guestFullName;
    @Column(name = "guest_email")
    private String guestEmail;
    @Column(name = "adults")
    private Integer numOfAdults = 0;
    @Column(name = "children")
    private Integer numOfChildren = 0;
    @Column(name = "total_guest")
    private Integer totalNumOfGuest;
    @Column(name = "confirmation_code")
    private String bookingConfirmationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    public void calculateTotalNumOfGuest() {
        int adults = this.numOfAdults != null ? this.numOfAdults : 0;
        int children = this.numOfChildren != null ? this.numOfChildren : 0;

        this.totalNumOfGuest = adults + children;
    }

    public void setNumOfAdults(Integer numOfAdults) {
        this.numOfAdults = numOfAdults;
        calculateTotalNumOfGuest();
    }

    public void setNumOfChildren(Integer numOfChildren) {
        this.numOfChildren = numOfChildren;
        calculateTotalNumOfGuest();
    }

    public void setBookingConfirmationCode(String bookingConfirmationCode) {
        this.bookingConfirmationCode = bookingConfirmationCode;
    }

    @PrePersist
    @PreUpdate
    private void updateTotalGuests() {
        calculateTotalNumOfGuest();
    }
}
