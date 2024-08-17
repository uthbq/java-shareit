package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class BookingRepositoryTest {

    private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "start");
    public static final LocalDateTime NOW = LocalDateTime.now();
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private Booking booking;
    private User user;
    private Item item;
    private User user2;
    private Item item2;
    private Booking booking2;


    @BeforeEach
    void setUp() {
        this.user = createUser("Tester", "tester@test.com");
        this.item = createItem("Drill", "Drilling the wall", true, user);
        this.user2 = createUser("Jane", "jpa@yandex.ru");
        this.item2 = createItem("Bike", "Fastest bike in the world", true, user2);

        this.booking = createBooking(NOW, NOW.plusDays(1), user2, item, BookingStatus.APPROVED);


        this.booking2 = createBooking(NOW.plusDays(1), NOW.plusDays(2), user, item2, BookingStatus.REJECTED);

    }

    private Booking createBooking(LocalDateTime start, LocalDateTime end, User user, Item item, BookingStatus state) {
        Booking booking = new Booking();
        booking.setEnd(end);
        booking.setStart(start);
        booking.setItem(item);
        booking.setStatus(state);
        booking.setBooker(user);
        return bookingRepository.save(booking);
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item createItem(String name, String description, boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    @Test
    void testIsAvailableForBooking() {

        boolean notAvailableForBooking = bookingRepository.isAvailableForBooking(item.getId(), NOW, NOW.plusDays(1));

        boolean availableForBooking = bookingRepository.isAvailableForBooking(item.getId(), NOW.plusDays(2), NOW.plusDays(3));

        assertFalse(notAvailableForBooking);
        assertTrue(availableForBooking);
    }

    @Test
    void testFindByItemOwnerAndStatusIn() {
        Booking newestBooking = createBooking(NOW.plusDays(1), NOW.plusDays(2), user2, item, BookingStatus.CANCELED);
        List<Booking> bookings = bookingRepository
                .findByItemOwnerAndStatusIn(user, BookingStatus.APPROVED, BookingStatus.CANCELED, NEWEST_FIRST);
        assertEquals(2, bookings.size());
        assertEquals(bookings.getFirst(), newestBooking);
    }

    @Test
    void testFindApprovedForItems() {
        Booking newestBooking = createBooking(NOW.plusDays(4), NOW.plusDays(5), user2, item, BookingStatus.APPROVED);
        Booking waitingBooking = createBooking(NOW.plusDays(10), NOW.plusDays(20), user2, item, BookingStatus.WAITING);

        List<Item> list = List.of(item, item2);
        List<Booking> bookings = bookingRepository.findApprovedForItems(list, NEWEST_FIRST);
        assertEquals(2, bookings.size());
        assertEquals(bookings.getFirst(), newestBooking);
    }

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }


}