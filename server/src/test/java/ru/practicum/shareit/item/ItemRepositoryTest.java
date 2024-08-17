package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class ItemRepositoryTest {


    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private Item item;
    private User user;
    private User user2;
    private Item item2;

    @Transactional
    @BeforeEach
    void setUp() {
        this.user = createUser("John", "jdbc@mail.com");
        this.item = createItem("Drill", "Drilling the wall", true, user);
        this.user2 = createUser("Jane", "jpa@yandex.ru");
        this.item2 = createItem("Bike", "Fastest bike in the world", true, user2);
    }

    private Item createItem(String name, String description, boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    @Test
    void testFindByOwner() {

        List<Item> actualItems = itemRepository.findByOwner(user);
        assertEquals(1, actualItems.size());
        assertEquals(item.getId(), actualItems.get(0).getId());

        List<Item> actualItems2 = itemRepository.findByOwner(user2);
        assertEquals(1, actualItems2.size());
        assertEquals(item2.getId(), actualItems2.get(0).getId());
    }

    @Test
    void testSearchByNameOrDescription() {
        List<Item> actualItems = itemRepository.searchByNameOrDescription("Bike");
        assertEquals(1, actualItems.size());
        assertEquals(item2, actualItems.get(0));

        List<Item> actualItems2 = itemRepository.searchByNameOrDescription("the");
        assertEquals(2, actualItems2.size());
    }

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}