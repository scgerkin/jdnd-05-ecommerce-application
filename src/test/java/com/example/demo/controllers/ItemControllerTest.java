package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItemsHappyPath() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setPrice(new BigDecimal("10.00"));
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item));

        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void getItemsOnEmptyItemRepositoryReturnsOk() {
        when(itemRepository.findAll()).thenReturn(new ArrayList<>());

        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void getItemByIdHappyPath() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setPrice(new BigDecimal("10.00"));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        final ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(item.getId(), response.getBody().getId());
        assertEquals(item.getName(), response.getBody().getName());
        assertEquals(item.getDescription(), response.getBody().getDescription());
        assertEquals(item.getPrice(), response.getBody().getPrice());
    }

    @Test
    public void getItemByIdReturnsNotFoundForInvalidId() {
        final ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getItemsByNameHappyPath() {
        final String itemName = "Test item";
        Item item = new Item();
        item.setId(1L);
        item.setName(itemName);
        item.setDescription("Test Description");
        item.setPrice(new BigDecimal("10.00"));
        when(itemRepository.findByName(itemName)).thenReturn(Arrays.asList(item));

        final ResponseEntity<List<Item>> response = itemController.getItemsByName(itemName);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void getItemsByNameNullRepositoryResponseReturnsNotFound() {
        final String itemName = "Test item";
        when(itemRepository.findByName(itemName)).thenReturn(null);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName(itemName);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getItemsByNameEmptyListInRepositoryReturnsNotFound() {
        final String itemName = "Test item";
        when(itemRepository.findByName(itemName)).thenReturn(new ArrayList<>());

        final ResponseEntity<List<Item>> response = itemController.getItemsByName(itemName);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
