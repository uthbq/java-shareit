package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentParams;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GeneralItemService implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;


    @Override
    public ItemFullDto getById(long itemId, long userId) {
        LocalDateTime now = LocalDateTime.now();
        Item savedItem = getItemFromRepository(itemId);
        Booking nextBooking = null;
        Booking lastBooking = null;
        List<CommentDto> comments = commentRepository.findByItem(savedItem).stream()
                .map(commentMapper::mapToCommentDto).toList();

        if (userId != savedItem.getOwner().getId()) {
            return itemMapper.mapToFullDto(savedItem, comments, lastBooking, nextBooking);
        }

        List<Booking> bookings = bookingRepository.findApprovedForItem(savedItem, Sort.by(Sort.Direction.DESC, "start"));
        if (!bookings.isEmpty()) {
            nextBooking = bookings.stream()
                    .filter(b -> b.isFuture(now))
                    .reduce((first, second) -> second)
                    .orElse(null);
            lastBooking = bookings.stream()
                    .filter(b -> b.isLastOrCurrent(now))
                    .findFirst()
                    .orElse(null);
        }

        return itemMapper.mapToFullDto(savedItem, comments, lastBooking, nextBooking);
    }


    @Override
    public List<ItemFullDto> getAllOwnerItems(long userId) {
        LocalDateTime now = LocalDateTime.now();
        User user = getUserFromRepository(userId);
        List<Item> items = itemRepository.findByOwner(user);
        Map<Item, List<Comment>> comments = commentRepository.findByItemIn(items, Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem));

        Map<Item, List<Booking>> approvedBookings =
                bookingRepository.findApprovedForItems(items, Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .collect(groupingBy(Booking::getItem));

        List<ItemFullDto> itemDtos = items.stream().map(item -> {
            List<CommentDto> commentDtos = new ArrayList<>();
            if (!comments.isEmpty()) {
                commentDtos = comments.get(item).stream()
                        .map(commentMapper::mapToCommentDto)
                        .toList();
            }
            Booking nextBooking = null;
            Booking lastBooking = null;
            if (!approvedBookings.isEmpty()) {
                List<Booking> bookings = approvedBookings.get(item);
                nextBooking = bookings.stream()
                        .filter(b -> b.isFuture(now))
                        .reduce((first, second) -> second)
                        .orElse(null);
                lastBooking = bookings.stream()
                        .filter(b -> b.isLastOrCurrent(now))
                        .findFirst()
                        .orElse(null);
            }
            return itemMapper.mapToFullDto(item, commentDtos, lastBooking, nextBooking);
        }).toList();
        return itemDtos;
    }


    @Transactional
    @Override
    public ItemShortDto create(ItemCreateDto itemCreateDto, long ownerId) {
        User owner = getUserFromRepository(ownerId);
        Item item = itemMapper.mapToItem(itemCreateDto);
        item.setOwner(owner);
        return itemMapper.mapToShortDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemShortDto update(ItemUpdateDTO itemCreateDto, long ownerId) {
        User owner = getUserFromRepository(ownerId);
        Item savedItem = getItemFromRepository(itemCreateDto.getId());
        if (!savedItem.getOwner().equals(owner)) {
            throw new AccessDeniedException("Incorrect owner");
        }
        if (itemCreateDto.getName() != null) {
            savedItem.setName(itemCreateDto.getName());
        }
        if (itemCreateDto.getDescription() != null) {
            savedItem.setDescription(itemCreateDto.getDescription());
        }
        if (itemCreateDto.getAvailable() != null) {
            savedItem.setAvailable(itemCreateDto.getAvailable());
        }
        return itemMapper.mapToShortDto(itemRepository.save(savedItem));
    }

    @Override
    public List<ItemShortDto> search(String text) {
        return itemRepository.searchByNameOrDescription(text).stream()
                .map(itemMapper::mapToShortDto)
                .collect(toList());
    }


    @Transactional
    @Override
    public CommentDto createComment(CommentParams params) {
        Item savedItem = getItemFromRepository(params.getItemId());
        User savedUser = getUserFromRepository(params.getAuthorId());
        Booking booking = getBookingFromRepository(savedUser, savedItem);
        if (!booking.getStatus().equals(BookingStatus.APPROVED) || booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new AccessDeniedException("Booking not ended");
        }
        params.setCreated(LocalDateTime.now());
        Comment comment = commentMapper.mapToComment(params, savedUser, savedItem);
        return commentMapper.mapToCommentDto(commentRepository.save(comment));
    }


    private Item getItemFromRepository(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));
    }

    private User getUserFromRepository(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

    }

    private Booking getBookingFromRepository(User booker, Item item) {
        return bookingRepository.findByBookerAndItem(booker, item)
                .orElseThrow(() -> new NotFoundException("Booking item with id " + item.getId() +
                        " for user with id: " + booker.getId() + " not found"));

    }
}
