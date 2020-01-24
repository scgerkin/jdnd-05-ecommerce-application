package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void createUserHappyPath() {
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void createUserReturnsBadRequestOnExistingUsernameAttempt() {
        final String userName = "test";
        User user = new User();
        user.setId(1L);
        user.setUsername(userName);
        user.setPassword("testPassword");
        when(userRepository.findByUsername(userName)).thenReturn(user);

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(userName);
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createUserReturnsBadRequestOnNotMatchingPasswords() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword");
        request.setConfirmPassword("notTheSamePassword");

        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createUserReturnsBadRequestOnInvalidPasswordCriteria() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("2short");
        request.setConfirmPassword("2short");

        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void findByIdReturnsValidUser() {
        User expected = new User();
        expected.setId(1L);
        expected.setUsername("test");
        expected.setPassword("testPassword");
        Cart cart = new Cart();
        cart.setId(1L);
        expected.setCart(cart);
        when(userRepository.findById(1L)).thenReturn(Optional.of(expected));

        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        User actual = response.getBody();
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getCart().getId(), actual.getCart().getId());
    }

    @Test
    public void findByIdReturnsNotFoundOnUserIdNotFound() {
        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void findByUsernameReturnsValidUser() {
        final String username = "test";
        User expected = new User();
        expected.setId(1L);
        expected.setUsername(username);
        expected.setPassword("testPassword");
        Cart cart = new Cart();
        cart.setId(1L);
        expected.setCart(cart);
        when(userRepository.findByUsername(username)).thenReturn(expected);

        final ResponseEntity<User> response = userController.findByUserName(username);
        assertNotNull(response);
        User actual = response.getBody();
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getCart().getId(), actual.getCart().getId());
    }

    @Test
    public void findByUsernameReturnsNotFoundOnUsernameNotFound() {
        final ResponseEntity<User> response = userController.findByUserName("test");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
