package ru.practicum.shareit.request;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class ItemRequestRepositoryTest {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    ItemRequest request;
    User user;


    @BeforeEach
    void setUp() {
        this.user = createUser("Tester", "tester@test.com");
        this.request = createRequest("Want table", LocalDateTime.now(), user);
    }

    private ItemRequest createRequest(String description, LocalDateTime date, User user) {
        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setCreated(date);
        request.setRequestor(user);

        return itemRequestRepository.save(request);
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
    void testFindByRequestor_Id() {
        ItemRequest ballRequest = createRequest("Want ball", LocalDateTime.now().plusHours(1), user);
        User boatUser = createUser("Toster", "tester@yandex.com");
        ItemRequest boatRequest = createRequest("Want boat", LocalDateTime.now().plusHours(1), boatUser);
        List<ItemRequest> savedRequest = itemRequestRepository.findByRequestor_Id(user.getId(), Sort.by(Sort.Direction.DESC, "created"));

        assertEquals(2, savedRequest.size());
        assertEquals(ballRequest, savedRequest.get(0));
        assertEquals(request, savedRequest.get(1));

        List<ItemRequest> savedBoatRequest = itemRequestRepository.findByRequestor_Id(boatUser.getId(), Sort.by(Sort.Direction.DESC, "created"));
        assertEquals(1, savedBoatRequest.size());
        assertEquals(boatRequest, savedBoatRequest.get(0));

    }

    @AfterEach
    void tearDown() {
        itemRequestRepository.deleteAll();
    }
}