package model; //

import java.time.LocalDateTime;

public class Booking
{
    private int bookingId;
    private LocalDateTime createdAt;
    private BookingStatus status;

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }

    public boolean isActive() {
        return this.status == BookingStatus.ACTIVE;
    }

    public Booking(int bookingId, LocalDateTime createdAt, BookingStatus status)
    {
        this.bookingId = bookingId;
        this.createdAt = createdAt;
        this.status = status;
    }

    public int getBookingId() {return bookingId;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public BookingStatus getStatus() {return status;}

    public void setBookingId(int bookingId) {this.bookingId = bookingId;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public void setStatus(BookingStatus status) {this.status = status;}

}
