package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);


    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerID, LocalDateTime start, LocalDateTime before);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(User itemOwner, LocalDateTime start, LocalDateTime before);


    @Query("select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.start < ?3 and " +
            "b.status != 'REJECTED' order by b.end desc")
    List<Booking> findPastBookings(Long itemId, Long ownerId, LocalDateTime now);

    @Query("select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.start > ?3 and " +
            "b.status != 'REJECTED' order by b.start asc")
    List<Booking> findFutureBookings(Long itemId, Long ownerId, LocalDateTime now);


    Long countAllByItemIdAndBookerIdAndEndBefore(Long itemId, Long userId, LocalDateTime localDateTime);
}