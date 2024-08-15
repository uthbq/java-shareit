package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreatetDto;
import ru.practicum.shareit.booking.dto.BookingFullDTO;
import ru.practicum.shareit.booking.dto.BookingParams;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotAvailableItem;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class GeneralBookingServiceTest {

    public static final LocalDateTime NOW = LocalDateTime.now();
    private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "start");
    private final EntityManager em;
    private User owner;
    private User booker;
    private Item newItem;
    private Booking approvedBooking;
    private Booking waitingBooking;
    private final BookingService service;

    @BeforeEach
    void setUp() {
        this.owner = new User();
        owner.setName("John Doe");
        owner.setEmail("john.doe@example.com");
        em.persist(owner);

        this.booker = new User();
        booker.setName("Alex Jin");
        booker.setEmail("alex.jin@example.com");
        em.persist(booker);

        this.newItem = new Item();
        newItem.setName("New Item");
        newItem.setDescription("New Description");
        newItem.setAvailable(true);
        newItem.setOwner(owner);
        em.persist(newItem);

        this.approvedBooking = new Booking();
        approvedBooking.setItem(newItem);
        approvedBooking.setBooker(booker);
        approvedBooking.setStatus(BookingStatus.APPROVED);
        approvedBooking.setStart(LocalDateTime.now().minusDays(2));
        approvedBooking.setEnd(LocalDateTime.now().minusDays(1));
        em.persist(approvedBooking);

        this.waitingBooking = new Booking();
        waitingBooking.setItem(newItem);
        waitingBooking.setBooker(booker);
        waitingBooking.setStatus(BookingStatus.WAITING);
        waitingBooking.setStart(LocalDateTime.now().plusDays(2));
        waitingBooking.setEnd(LocalDateTime.now().plusDays(10));
        em.persist(waitingBooking);
    }

    @AfterEach
    void tearDown() {
        em.createNativeQuery("DELETE FROM Items").executeUpdate();
        em.createNativeQuery("DELETE FROM Users").executeUpdate();
        em.createNativeQuery("DELETE FROM Bookings").executeUpdate();
    }

    @Test
    void testCreate() {
        long bookerId = booker.getId();
        BookingCreatetDto dto = makeBookingDto(NOW, NOW.plusDays(1), newItem.getId(), null);
        BookingFullDTO savedBooking = service.create(dto, bookerId);

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.id = :bookingId", Booking.class);
        Booking actualBooking = query.setParameter("bookingId", savedBooking.getId()).getSingleResult();

        assertThat(actualBooking.getStart(), equalTo(dto.getStart()));
        assertThat(actualBooking.getEnd(), equalTo(dto.getEnd()));
        assertThat(actualBooking.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(actualBooking.getBooker().getId(), equalTo(booker.getId()));
    }

    @Test
    void testCreateBookingNotExistBooker() {
        long bookerId = 10;
        BookingCreatetDto dto = makeBookingDto(NOW, NOW.plusDays(1), newItem.getId(), null);

        assertThrows(NotFoundException.class, () -> service.create(dto, bookerId));
    }

    @Test
    void testCreateBookingWrongDateAndTime() {
        long bookerId = booker.getId();
        BookingCreatetDto dto = makeBookingDto(NOW.minusDays(2), NOW.minusDays(1), newItem.getId(), null);

        assertThrows(NotAvailableItem.class, () -> service.create(dto, bookerId));
    }

    @Test
    void testCreateBookingByOwner() {
        long bookerId = owner.getId();
        BookingCreatetDto dto = makeBookingDto(NOW, NOW.plusDays(1), newItem.getId(), null);

        assertThrows(AccessDeniedException.class, () -> service.create(dto, bookerId));
    }

    @Test
    void approved() {
        Long bookingId = waitingBooking.getId();
        Long ownerId = owner.getId();
        Boolean approved = true;
        BookingParams params = BookingParams.builder()
                .id(bookingId)
                .userId(ownerId)
                .approved(approved)
                .build();

        service.approved(params);

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.id = :bookingId", Booking.class);
        query.setParameter("bookingId", bookingId);
        Booking actualBooking = query.getSingleResult();

        assertThat(actualBooking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getById() {
        long bookingId = approvedBooking.getId();
        Long ownerId = owner.getId();
        BookingParams params = BookingParams.builder()
                .id(bookingId)
                .userId(ownerId)
                .build();
        BookingFullDTO savedBooking = service.getById(params);

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.id = :bookingId", Booking.class);
        Booking actualBooking = query.setParameter("bookingId", savedBooking.getId()).getSingleResult();

        assertThat(actualBooking.getStart(), equalTo(savedBooking.getStart()));
        assertThat(actualBooking.getEnd(), equalTo(savedBooking.getEnd()));
        assertThat(actualBooking.getStatus(), equalTo(savedBooking.getStatus()));
        assertThat(actualBooking.getBooker().getId(), equalTo(savedBooking.getBooker().id()));

    }

    @Test
    void getUserBookings() {
        Long userId = booker.getId();
        BookingState past = BookingState.PAST;
        List<BookingFullDTO> pastBookings = service.getUserBookings(past, userId);

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.booker = :booker AND b.end< :now", Booking.class);
        query.setParameter("booker", booker);
        query.setParameter("now", NOW);
        List<Booking> resultList = query.getResultList();

        assertThat(resultList.size(), equalTo(pastBookings.size()));
        assertThat(resultList.getFirst().getId(), equalTo(pastBookings.get(0).getId()));

        BookingState waiting = BookingState.WAITING;
        List<BookingFullDTO> waitingBooking = service.getUserBookings(waiting, userId);

        query = em.createQuery("SELECT b FROM Booking b WHERE b.booker = :booker AND b.status = 'WAITING'", Booking.class);
        query.setParameter("booker", booker);
        resultList = query.getResultList();

        assertThat(resultList.size(), equalTo(pastBookings.size()));
        assertThat(resultList.getFirst().getId(), equalTo(waitingBooking.get(0).getId()));

        BookingState all = BookingState.ALL;
        List<BookingFullDTO> allBookings = service.getUserBookings(all, userId);


        query = em.createQuery("SELECT b FROM Booking b WHERE b.booker = :booker ORDER BY end desc", Booking.class);
        query.setParameter("booker", booker);
        resultList = query.getResultList();

        assertThat(resultList.size(), equalTo(allBookings.size()));
        assertThat(resultList.getFirst().getId(), equalTo(allBookings.get(0).getId()));
    }

    @Test
    void getOwnerBookings() {
        Long ownerId = owner.getId();
        BookingState past = BookingState.PAST;
        List<BookingFullDTO> pastBookings = service.getOwnerBookings(past, ownerId);

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.item.owner = :owner AND b.end< :now", Booking.class);
        query.setParameter("owner", owner);
        query.setParameter("now", NOW);
        List<Booking> resultList = query.getResultList();

        assertThat(resultList.size(), equalTo(pastBookings.size()));
        assertThat(resultList.getFirst().getId(), equalTo(pastBookings.get(0).getId()));

        BookingState waiting = BookingState.WAITING;
        List<BookingFullDTO> waitingBooking = service.getOwnerBookings(waiting, ownerId);

        query = em.createQuery("SELECT b FROM Booking b WHERE b.item.owner = :owner AND b.status = 'WAITING'", Booking.class);
        query.setParameter("owner", owner);
        resultList = query.getResultList();

        assertThat(resultList.size(), equalTo(pastBookings.size()));
        assertThat(resultList.getFirst().getId(), equalTo(waitingBooking.get(0).getId()));

        BookingState all = BookingState.ALL;
        List<BookingFullDTO> allBookings = service.getOwnerBookings(all, ownerId);

        query = em.createQuery("SELECT b FROM Booking b WHERE b.item.owner = :owner ORDER BY end desc", Booking.class);
        query.setParameter("owner", owner);
        resultList = query.getResultList();

        assertThat(resultList.size(), equalTo(allBookings.size()));
        assertThat(resultList.getFirst().getId(), equalTo(allBookings.get(0).getId()));
    }

    private BookingCreatetDto makeBookingDto(LocalDateTime start, LocalDateTime end, Long itemId, User booker) {
        BookingCreatetDto dto = new BookingCreatetDto();
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(itemId);
        dto.setBooker(booker);
        return dto;
    }
}