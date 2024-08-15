package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestParams;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class GeneralItemRequestServiceTest {

    private final ItemRequestService service;
    private final EntityManager em;
    private User owner;
    private User requestor;
    private Item newItem;
    private ItemRequest newRequest;

    @BeforeEach
    void setUp() {
        this.owner = new User();
        owner.setName("John Doe");
        owner.setEmail("john.doe@example.com");
        em.persist(owner);

        this.requestor = new User();
        requestor.setName("Alex Jin");
        requestor.setEmail("alex.jin@example.com");
        em.persist(requestor);

        this.newItem = new Item();
        newItem.setName("New Item");
        newItem.setDescription("New Description");
        newItem.setAvailable(true);
        newItem.setOwner(owner);


        this.newRequest = new ItemRequest();
        newRequest.setRequestor(requestor);
        newRequest.setCreated(LocalDateTime.now());
        newRequest.setDescription("New Description");
        em.persist(newRequest);

        newItem.setRequest(newRequest);
        em.persist(newItem);
    }

    @Transactional
    @AfterEach
    void tearDown() {
        em.createNativeQuery("DELETE FROM Items").executeUpdate();
        em.createNativeQuery("DELETE FROM Users").executeUpdate();
    }

    @Test
    void testCreateRequest() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("New Description");
        Long userId = requestor.getId();
        ItemRequestShortDto savedRequest = service.create(requestDto, userId);

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest request = query.setParameter("id", savedRequest.getId())
                .getSingleResult();

        assertEquals(request.getId(), savedRequest.getId());
        assertEquals(request.getCreated(), savedRequest.getCreated());
        assertEquals(request.getDescription(), savedRequest.getDescription());
    }

    @Test
    void testGetUserRequests() {
        long userId = requestor.getId();
        List<ItemRequestFullDto> savedRequests = service.getUserRequests(userId);

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.requestor = :requestor", ItemRequest.class);
        List<ItemRequest> requests = query.setParameter("requestor", requestor)
                .getResultList();

        assertEquals(requests.size(), savedRequests.size());
        assertEquals(requests.getFirst().getId(), savedRequests.getFirst().getId());
    }

    @Test
    void testGetRequestById() {
        Long userId = requestor.getId();
        long requestId = newRequest.getId();
        ItemRequestFullDto savedRequest = service.getRequestById(requestId, userId);

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest request = query.setParameter("id", savedRequest.getId())
                .getSingleResult();

        assertEquals(request.getId(), savedRequest.getId());
        assertEquals(request.getCreated(), savedRequest.getCreated());
        assertEquals(request.getDescription(), savedRequest.getDescription());
        assertEquals(newItem.getId(), savedRequest.getItems().getFirst().itemId());
        assertEquals(newItem.getName(), savedRequest.getItems().getFirst().name());
        assertEquals(newItem.getOwner().getId(), savedRequest.getItems().getFirst().userId());

    }

    @Test
    void getAllRequest() {
        long userId = requestor.getId();
        int from = 0;
        int size = 10;

        ItemRequestParams params = new ItemRequestParams(userId, from, size);
        List<ItemRequestFullDto> savedRequests = service.getAllRequest(params);

        TypedQuery<ItemRequest> query = em.createQuery("select i from ItemRequest i " +
                "order by created desc ", ItemRequest.class);
        List<ItemRequest> requests = query.getResultList();

        assertEquals(requests.size(), savedRequests.size());
        assertEquals(requests.getFirst().getId(), savedRequests.getFirst().getId());
    }


}