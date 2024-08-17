package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreatetDto;
import ru.practicum.shareit.booking.dto.BookingFullDTO;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingParams;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BookingStatusException;
import ru.practicum.shareit.exception.NotAvailableItem;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneralBookingService implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "start");

    @Transactional
    @Override
    public BookingFullDTO create(BookingCreatetDto bookingDto, long bookerId) {
        User booker = getUserFromRepository(bookerId);
        Item savedItem = getItemFromRepository(bookingDto.getItemId());
        if (!savedItem.getAvailable()) {
            throw new NotAvailableItem("Item does not available");
        }
        if (!bookingRepository.isAvailableForBooking(savedItem.getId(), bookingDto.getStart(), bookingDto.getEnd())) {
            throw new NotAvailableItem("Booking for these dates are not available");
        }
        if (bookerId == savedItem.getOwner().getId()) {
            throw new AccessDeniedException("Owner does  not allowed to create a booking");
        }
        bookingDto.setBooker(booker);
        Booking booking = bookingMapper.mapToBooking(bookingDto, savedItem);
        booking.setStatus(BookingStatus.WAITING);
        return bookingMapper.mapToDTO(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingFullDTO approved(BookingParams params) {
        Booking savedBooking = getBookingFromRepository(params.getId());
        if (!params.getUserId().equals(savedBooking.getItem().getOwner().getId())) {
            throw new AccessDeniedException("User with id " + params.getUserId() + " does not allowed to approve");
        }
        if (savedBooking.getStatus().equals(BookingStatus.WAITING)) {
            if (params.getApproved()) {
                savedBooking.setStatus(BookingStatus.APPROVED);
            } else {
                savedBooking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new BookingStatusException("Already " + savedBooking.getStatus());
        }
        return bookingMapper.mapToDTO(bookingRepository.save(savedBooking));
    }

    @Override
    public BookingFullDTO getById(BookingParams params) {
        Booking savedBooking = getBookingFromRepository(params.getId());
        User savedUser = getUserFromRepository(params.getUserId());
        if (!savedBooking.getBooker().getId().equals(savedUser.getId()) &&
                !savedBooking.getItem().getOwner().getId().equals(savedUser.getId())) {
            throw new AccessDeniedException("User with id: " + savedUser.getId() + " access denied");
        }
        return bookingMapper.mapToDTO(savedBooking);
    }

    @Override
    public List<BookingFullDTO> getUserBookings(BookingState state, Long userId) {
        User savedUser = getUserFromRepository(userId);
        return getBookingByStateAndBooker(state, savedUser).stream()
                .map(bookingMapper::mapToDTO).toList();
    }

    @Override
    public List<BookingFullDTO> getOwnerBookings(BookingState state, Long ownerId) {
        User savedUser = getUserFromRepository(ownerId);

        return getBookingByStateAndOwner(state, savedUser).stream()
                .map(bookingMapper::mapToDTO).toList();
    }

    private List<Booking> getBookingByStateAndOwner(BookingState state, User owner) {
        if (state.equals(BookingState.ALL)) {
            return bookingRepository.findByItemOwner(owner, NEWEST_FIRST);
        } else if (state.equals(BookingState.CURRENT)) {
            return bookingRepository.findByItemOwnerAndEndAfterAndStartBefore(owner, LocalDateTime.now(), LocalDateTime.now(), NEWEST_FIRST);
        } else if (state.equals(BookingState.PAST)) {
            return bookingRepository.findByItemOwnerAndEndBefore(owner, LocalDateTime.now(), NEWEST_FIRST);
        } else if (state.equals(BookingState.FUTURE)) {
            return bookingRepository.findByItemOwnerAndStartAfter(owner, LocalDateTime.now(), NEWEST_FIRST);
        } else if (state.equals(BookingState.REJECTED)) {
            return bookingRepository.findByItemOwnerAndStatusIn(owner, BookingStatus.REJECTED, BookingStatus.CANCELED, NEWEST_FIRST);
        } else if (state.equals(BookingState.WAITING)) {
            return bookingRepository.findByItemOwnerAndStatusEquals(owner, BookingStatus.WAITING, NEWEST_FIRST);
        } else {
            return List.of();
        }
    }

    private List<Booking> getBookingByStateAndBooker(BookingState state, User savedUser) {
        if (state.equals(BookingState.ALL)) {
            return bookingRepository.findByBooker(savedUser, NEWEST_FIRST);
        } else if (state.equals(BookingState.CURRENT)) {
            return bookingRepository.findByBookerAndEndAfterAndStartBefore(savedUser, LocalDateTime.now(), LocalDateTime.now(), NEWEST_FIRST);
        } else if (state.equals(BookingState.PAST)) {
            return bookingRepository.findByBookerAndEndBefore(savedUser, LocalDateTime.now(), NEWEST_FIRST);
        } else if (state.equals(BookingState.FUTURE)) {
            return bookingRepository.findByBookerAndStartAfter(savedUser, LocalDateTime.now(), NEWEST_FIRST);
        } else if (state.equals(BookingState.REJECTED)) {
            return bookingRepository.findByBookerAndStatusIn(savedUser, BookingStatus.REJECTED, BookingStatus.CANCELED, NEWEST_FIRST);
        } else if (state.equals(BookingState.WAITING)) {
            return bookingRepository.findByBookerAndStatusEquals(savedUser, BookingStatus.WAITING, NEWEST_FIRST);
        } else {
            return List.of();
        }
    }

    private Item getItemFromRepository(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));
    }

    private User getUserFromRepository(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    private Booking getBookingFromRepository(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

    }
}