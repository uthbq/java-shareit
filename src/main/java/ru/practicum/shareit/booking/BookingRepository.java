package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select (count(b) > 0) from Booking b " +
            "where b.item.id = ?1  and b.status = 'APPROVED' " +
            "and b.start <= ?3  and b.end >= ?2")
    boolean isAvailableForBooking(Long itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerAndStartAfter(User booker, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerAndEndAfterAndStartBefore(User booker, LocalDateTime now1, LocalDateTime now2, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.owner = ?1 and b.status in (?2, ?3)")
    List<Booking> findByItemOwnerAndStatusIn(User owner, BookingStatus status1, BookingStatus status2, Sort sort);

    List<Booking> findByItemOwnerAndStatusEquals(User booker, BookingStatus status, Sort sort);

    List<Booking> findByBooker(User booker, Sort sort);

    List<Booking> findByItemOwner(User owner, Sort sort);

    List<Booking> findByItemOwnerAndEndBefore(User savedUser, LocalDateTime now, Sort sort);

    List<Booking> findByBookerAndEndAfterAndStartBefore(User savedUser, LocalDateTime now, LocalDateTime now1, Sort sort);

    List<Booking> findByBookerAndEndBefore(User savedUser, LocalDateTime now, Sort sort);

    List<Booking> findByBookerAndStartAfter(User savedUser, LocalDateTime now, Sort sort);

    List<Booking> findByBookerAndStatusEquals(User savedUser, BookingStatus bookingStatus, Sort sort);

    @Query("select b from Booking b " +
            "where b.booker = ?1 and b.status in (?2, ?3)")
    List<Booking> findByBookerAndStatusIn(User booker, BookingStatus status1, BookingStatus status2, Sort sort);

    Optional<Booking> findByBookerAndItem(User user, Item item);

    @Query("select b from Booking b " +
            "where b.item IN ?1 and b.status = ('APPROVED')")
    List<Booking> findApprovedForItems(List<Item> items, Sort start);

    @Query("select b from Booking b " +
            "where b.item = ?1 and b.status = ('APPROVED')")
    List<Booking> findApprovedForItem(Item savedItem, Sort start);
}
