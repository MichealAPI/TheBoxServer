package it.mikeslab.thebox.service;

import it.mikeslab.thebox.entity.User;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureDataMongo
public class UserServiceTests {
    @Autowired
    private UserService userService;

    @Test
    public void testCreateUser() {
        User user = new User();

        user.setUsername("testuser");
        user.setPassword("testpassword");

        User savedUser = userService.createUser(user);

        assertNotNull(savedUser);

    }

    @Test
    @Order(1)
    public void deleteUser() {
        assert userService.deleteUser("testuser");
    }

}
