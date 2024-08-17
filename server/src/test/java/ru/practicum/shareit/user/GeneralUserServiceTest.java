package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class GeneralUserServiceTest {

    private final EntityManager em;
    private final UserService userService;
    private UserDto userWithId1;

    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");
        userWithId1 = userService.create(userDto);
    }

    @AfterEach
    void tearDown() {
        em.createNativeQuery("DELETE FROM Users").executeUpdate();
    }

    @Test
    void testSaveUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Alex Doe");
        userDto.setEmail("alex.doe@example.com");

        UserDto savedUser = userService.create(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", savedUser.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void testUpdateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Updated Name");
        userDto.setEmail("update.doe@example.com");
        userDto.setId(userWithId1.getId());
        UserDto savedUser = userService.update(userDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", savedUser.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));

        userDto.setEmail(null);
        savedUser = userService.update(userDto);
        query = em.createQuery("Select u from User u where u.id = :id", User.class);
        user = query.setParameter("id", savedUser.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo("update.doe@example.com"));
    }

    @Test
    void testGetById() {
        UserDto savedUser = userService.getById(userWithId1.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", savedUser.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(savedUser.getName()));
        assertThat(user.getEmail(), equalTo(savedUser.getEmail()));
    }

    @Test
    void testGetAll() {
        List<UserDto> allUsers = userService.getAll();

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<User> result = query
                .getResultList();

        assertEquals(1, allUsers.size());
        assertEquals(result.getFirst().getName(), allUsers.getFirst().getName());
        assertEquals(result.getFirst().getEmail(), allUsers.getFirst().getEmail());
    }

    @Test
    void testDelete() {
        userService.deleteUserById(userWithId1.getId());
        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<User> result = query
                .getResultList();

        assertEquals(0, result.size());
    }
}