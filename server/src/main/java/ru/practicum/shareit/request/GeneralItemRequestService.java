package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class GeneralItemRequestService implements ItemRequestService {
    private static final Sort SORT_BY_CREATED = Sort.by(Sort.Direction.DESC, "created");
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Transactional
    @Override
    public ItemRequestShortDto create(ItemRequestDto itemRequestDto, long userId) {
        User savedUser = getUserFromRepository(userId);
        itemRequestDto.setRequestor(savedUser);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        return itemRequestMapper.toItemRequestShortDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestFullDto> getUserRequests(long userId) {
        List<ItemRequest> requests = itemRequestRepository.findByRequestor_Id(userId, SORT_BY_CREATED);
        List<Item> itemByRequestIn = itemRepository.findItemByRequestIn(requests);
        Map<ItemRequest, List<Item>> items;
        if (itemByRequestIn.isEmpty()) {
            return requests.stream()
                    .map(r -> itemRequestMapper.toItemRequestFullDto(r, null)).toList();
        }
        items = itemByRequestIn.stream()
                .collect(groupingBy(Item::getRequest));
        return requests.stream()
                .map(r -> itemRequestMapper.toItemRequestFullDto(r, items.get(r))).toList();
    }

    @Override
    public ItemRequestFullDto getRequestById(long requestId, long userId) {
        getUserFromRepository(userId);
        ItemRequest savedItemRequest = getItemRequestFromRepository(requestId);
        List<Item> itemByRequest = itemRepository.findItemByRequest(savedItemRequest);
        if (itemByRequest.isEmpty()) {
            itemByRequest = null;
        }
        return itemRequestMapper.toItemRequestFullDto(savedItemRequest, itemByRequest);
    }

    @Override
    public List<ItemRequestFullDto> getAllRequest(ItemRequestParams params) {
        Integer size = params.getSize();
        Integer from = params.getFrom();
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size, SORT_BY_CREATED);
        Page<ItemRequest> requests = itemRequestRepository.findAll(page);
        List<ItemRequest> requestList = requests.getContent();
        List<Item> itemByRequestIn = itemRepository.findItemByRequestIn(requestList);
        Map<ItemRequest, List<Item>> items;
        if (itemByRequestIn.isEmpty()) {
            return requests.stream()
                    .map(r -> itemRequestMapper.toItemRequestFullDto(r, null)).toList();
        }
        items = itemByRequestIn.stream()
                .collect(groupingBy(Item::getRequest));
        return requests.stream()
                .map(r -> itemRequestMapper.toItemRequestFullDto(r, items.get(r))).toList();
    }


    private ItemRequest getItemRequestFromRepository(long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest not found with id: " + requestId));
    }

    private User getUserFromRepository(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }
}
