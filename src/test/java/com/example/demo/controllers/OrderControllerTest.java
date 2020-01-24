package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setup() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submitOrderHappyPath() {
        User userFacade = new User();
        userFacade.setId(1L);
        userFacade.setUsername("testUser");
        userFacade.setPassword("testPassword");

        Cart cartFacade = new Cart();
        cartFacade.setUser(userFacade);
        cartFacade.setTotal(new BigDecimal("100"));
        userFacade.setCart(cartFacade);

        Item itemFacade = new Item();
        itemFacade.setId(1L);
        itemFacade.setName("Test Item");
        itemFacade.setDescription("Test Item Description");
        itemFacade.setPrice(new BigDecimal("10.00"));

        userFacade.getCart().addItem(itemFacade);
        userFacade.getCart().addItem(itemFacade);

        when(userRepository.findByUsername(userFacade.getUsername())).thenReturn(userFacade);
        final ResponseEntity<UserOrder> response = orderController.submit(userFacade.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder actual = response.getBody();
        assertNotNull(actual);
        assertNotNull(actual.getUser());
        assertEquals(userFacade.getId(), actual.getUser().getId());
        assertEquals(cartFacade.getItems().size(), actual.getItems().size());
        assertEquals(cartFacade.getTotal(), actual.getTotal());
    }

    @Test
    public void submitOrderReturnsNotFoundOnUsernameNotFound() {
        final ResponseEntity<UserOrder> response = orderController.submit("notfound");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUserHappyPath() {
        User userFacade = new User();
        userFacade.setId(1L);
        userFacade.setUsername("testUser");
        userFacade.setPassword("testPassword");

        Cart cartFacade = new Cart();
        cartFacade.setTotal(new BigDecimal("100"));
        userFacade.setCart(cartFacade);

        Item itemFacade = new Item();
        itemFacade.setId(1L);
        itemFacade.setName("Test Item");
        itemFacade.setDescription("Test Item Description");
        itemFacade.setPrice(new BigDecimal("10.00"));

        userFacade.getCart().addItem(itemFacade);
        userFacade.getCart().addItem(itemFacade);

        UserOrder userOrderFacade = UserOrder.createFromCart(userFacade.getCart());
        userOrderFacade.setId(1L);

        when(userRepository.findByUsername(userFacade.getUsername())).thenReturn(userFacade);
        when(orderRepository.findByUser(userFacade)).thenReturn(Arrays.asList(userOrderFacade));

        final ResponseEntity<List<UserOrder>> response =
            orderController.getOrdersForUser(userFacade.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> actual = response.getBody();
        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(actual.get(0).getId(), userOrderFacade.getId());
    }

    @Test
    public void getOrdersReturnsNotFoundOnUsernameNotFound() {
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("notfound");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
