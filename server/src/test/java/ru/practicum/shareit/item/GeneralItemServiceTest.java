package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentParams;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemUpdateDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class GeneralItemServiceTest {

    private final EntityManager em;
    private User owner;
    private User booker;
    private Item newItem;
    private Booking newBooking;
    private final ItemService itemService;

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

        this.newBooking = new Booking();
        newBooking.setItem(newItem);
        newBooking.setBooker(booker);
        newBooking.setStatus(BookingStatus.APPROVED);
        newBooking.setStart(LocalDateTime.now().minusDays(2));
        newBooking.setEnd(LocalDateTime.now().minusDays(1));
        em.persist(newBooking);
    }

    @AfterEach
    void tearDown() {
        em.createNativeQuery("DELETE FROM Items").executeUpdate();
        em.createNativeQuery("DELETE FROM Users").executeUpdate();
    }

    @Test
    void testCreateItem() {
        ItemCreateDto newItem = new ItemCreateDto();
        newItem.setName("SecondItem");
        newItem.setDescription("New Description");
        newItem.setAvailable(true);
        ItemShortDto savedItem = itemService.create(newItem, owner.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", savedItem.getId())
                .getSingleResult();
        assertEquals(item.getId(), savedItem.getId());
        assertEquals(item.getOwner(), owner);
        assertEquals(item.getDescription(), savedItem.getDescription());
        assertEquals(item.getAvailable(), savedItem.getAvailable());
    }

    @Test
    void testGetById() {
        long ownerId = owner.getId();
        long itemId = newItem.getId();
        ItemFullDto savedItem = itemService.getById(itemId, ownerId);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", savedItem.getId())
                .getSingleResult();
        assertEquals(item.getId(), itemId);
        assertEquals(item.getOwner(), owner);
        assertEquals(item.getDescription(), savedItem.getDescription());
        assertEquals(item.getAvailable(), savedItem.getAvailable());
    }

    @Test
    void testGetAllOwnerItems() {
        long ownerId = owner.getId();
        List<ItemFullDto> allOwnerItems = itemService.getAllOwnerItems(ownerId);
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.owner = :owner", Item.class);
        List<Item> savedItems = query.setParameter("owner", owner).getResultList();

        assertThat(savedItems.size(), equalTo(allOwnerItems.size()));
        assertEquals(savedItems.get(0).getId(), allOwnerItems.getFirst().getId());
    }

    @Test
    void testSearch() {
        long userId = owner.getId();
        String text = "new";
        List<ItemShortDto> searched = itemService.search(text, userId);

        TypedQuery<Item> query = em.createQuery("select i from  Item i " +
                "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
                "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
                "AND (:text IS NOT NULL AND :text != '') " +
                "AND (i.available = TRUE)", Item.class);

        List<Item> savedItems = query.setParameter("text", text).getResultList();

        assertThat(savedItems.size(), equalTo(searched.size()));
        assertEquals(savedItems.get(0).getId(), searched.getFirst().getId());
    }

    @Test
    void testUpdateItem() {
        String description = "updated Description";
        String name = "updated Item";
        ItemUpdateDTO updatedItem = new ItemUpdateDTO();
        updatedItem.setId(newItem.getId());
        updatedItem.setDescription(description);
        updatedItem.setName(name);

        ItemShortDto savedItem = itemService.update(updatedItem, owner.getId());
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", newItem.getId())
                .getSingleResult();
        assertThat(item.getId(), equalTo(newItem.getId()));
        assertThat(item.getName(), equalTo(name));
        assertThat(item.getOwner(), equalTo(owner));
        assertThat(item.getDescription(), equalTo(description));
        assertThat(savedItem.getAvailable(), equalTo(newItem.getAvailable()));


        updatedItem.setId(newItem.getId());
        updatedItem.setDescription(null);
        updatedItem.setName(null);
        savedItem = itemService.update(updatedItem, owner.getId());
        query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        item = query.setParameter("id", newItem.getId())
                .getSingleResult();
        assertThat(item.getId(), equalTo(newItem.getId()));
        assertThat(item.getName(), equalTo(name));
        assertThat(item.getOwner(), equalTo(owner));
        assertThat(item.getDescription(), equalTo(description));
        assertThat(savedItem.getAvailable(), equalTo(newItem.getAvailable()));
    }

    @Test
    void testCreateComment() {
        long userId = booker.getId();
        long itemId = newItem.getId();
        String text = "Comment";
        CommentParams params = new CommentParams();
        params.setItemId(itemId);
        params.setText(text);
        params.setAuthorId(userId);
        CommentDto comment = itemService.createComment(params);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.id = :id", Comment.class);
        Comment savedComment = query.setParameter("id", comment.getId())
                .getSingleResult();

        assertThat(text, equalTo(savedComment.getText()));
        assertThat(newBooking.getBooker(), equalTo(savedComment.getAuthor()));
        assertThat(LocalDateTime.now().getDayOfYear(), equalTo(savedComment.getCreated().getDayOfYear()));
        assertThat(newBooking.getItem(), equalTo(savedComment.getItem()));
    }
}