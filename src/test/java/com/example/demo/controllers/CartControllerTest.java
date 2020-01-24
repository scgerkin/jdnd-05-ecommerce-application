package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void addToCartHappyPath() {
        User userFacade = new User();
        userFacade.setId(1L);
        userFacade.setUsername("testUser");
        userFacade.setPassword("testPassword");
        when(userRepository.findByUsername(userFacade.getUsername())).thenReturn(userFacade);

        Cart cartFacade = new Cart();
        cartFacade.setId(1L);
        cartFacade.setUser(userFacade);
        userFacade.setCart(cartFacade);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Test Item 1");
        item1.setDescription("Test Description");
        item1.setPrice(new BigDecimal("10.00"));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        cartFacade.addItem(item1);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Test Item 2");
        item2.setDescription("Test Description");
        item2.setPrice(new BigDecimal("20.00"));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(userFacade.getUsername());
        modifyCartRequest.setItemId(item2.getId());
        modifyCartRequest.setQuantity(2);

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart actual = response.getBody();
        assertNotNull(actual);
        assertEquals(cartFacade.getId(), actual.getId());
        assertEquals(cartFacade.getUser().getId(), actual.getUser().getId());
        assertEquals(cartFacade.getItems().size(), actual.getItems().size());
        assertThat(cartFacade.getItems(), is(actual.getItems()));
        assertEquals(cartFacade.getTotal(), actual.getTotal());
    }

    @Test
    public void addToCartReturnsNotFoundOnInvalidUsername() {
        when(userRepository.findByUsername("none")).thenReturn(null);
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("none");
        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void addToCartReturnsNotFoundOnInvalidItem() {
        User userFacade = new User();
        userFacade.setId(1L);
        userFacade.setUsername("testUser");
        userFacade.setPassword("testPassword");
        when(userRepository.findByUsername(userFacade.getUsername())).thenReturn(userFacade);

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(userFacade.getUsername());
        modifyCartRequest.setItemId(1L);

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromCartHappyPath() {
        User userFacade = new User();
        userFacade.setId(1L);
        userFacade.setUsername("testUser");
        userFacade.setPassword("testPassword");
        when(userRepository.findByUsername(userFacade.getUsername())).thenReturn(userFacade);

        Cart cartFacade = new Cart();
        cartFacade.setId(1L);
        cartFacade.setUser(userFacade);
        userFacade.setCart(cartFacade);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Test Item 1");
        item1.setDescription("Test Description");
        item1.setPrice(new BigDecimal("10.00"));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        cartFacade.addItem(item1);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Test Item 2");
        item2.setDescription("Test Description");
        item2.setPrice(new BigDecimal("20.00"));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(userFacade.getUsername());
        modifyCartRequest.setItemId(item2.getId());
        modifyCartRequest.setQuantity(2);

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart actual = response.getBody();
        assertNotNull(actual);
        assertEquals(cartFacade.getId(), actual.getId());
        assertEquals(cartFacade.getUser().getId(), actual.getUser().getId());
        assertEquals(cartFacade.getItems().size(), actual.getItems().size());
        assertThat(cartFacade.getItems(), is(actual.getItems()));
        assertEquals(cartFacade.getTotal(), actual.getTotal());
    }

    @Test
    public void removeFromCartReturnsNotFoundOnInvalidUsername() {
        when(userRepository.findByUsername("none")).thenReturn(null);
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("none");

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromCartReturnsNotFoundOnInvalidItem() {
        User userFacade = new User();
        userFacade.setId(1L);
        userFacade.setUsername("testUser");
        userFacade.setPassword("testPassword");
        when(userRepository.findByUsername(userFacade.getUsername())).thenReturn(userFacade);

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(userFacade.getUsername());
        modifyCartRequest.setItemId(1L);

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
