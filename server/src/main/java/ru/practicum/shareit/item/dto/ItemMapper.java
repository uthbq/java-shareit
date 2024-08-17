package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public class ItemMapper {

    public ItemShortDto mapToShortDto(Item item) {
        return ItemShortDto.builder().id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .build();
    }


    public ItemFullDto mapToFullDto(Item item, List<CommentDto> comments, Booking lastBooking, Booking nextBooking) {
        ItemFullDto.BookingDto lastBookingDto = null;
        ItemFullDto.BookingDto nextBookingDto = null;
        if (lastBooking != null) {
            lastBookingDto = new ItemFullDto.BookingDto(lastBooking.getId(), lastBooking.getBooker().getId());
        }
        if (nextBooking != null) {
            nextBookingDto = new ItemFullDto.BookingDto(nextBooking.getId(), nextBooking.getBooker().getId());
        }
        return ItemFullDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBookingDto)
                .nextBooking(nextBookingDto)
                .comments(comments)
                .request(item.getRequest() != null ? item.getRequest() : null)
                .build();
    }


    public Item mapToItem(ItemCreateDto item) {
        return Item.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}

