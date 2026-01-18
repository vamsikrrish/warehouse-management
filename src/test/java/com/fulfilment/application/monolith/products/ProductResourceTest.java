package com.fulfilment.application.monolith.products;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.panache.common.Sort;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductResourceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductResource productResource;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.name = "Laptop";
        product.description = "Gaming Laptop";
        product.price =new BigDecimal(1500.0);
        product.stock = 10;
    }

    @Test
    void testGet_ReturnsList() {
        when(productRepository.listAll(any(Sort.class))).thenReturn(List.of(product));
        List<Product> result = productResource.get();
        assertEquals(1, result.size());
        verify(productRepository).listAll(any(Sort.class));
    }

    @Test
    void testGetSingle_Success() {
        when(productRepository.findById(1L)).thenReturn(product);
        Product result = productResource.getSingle(1L);
        assertNotNull(result);
        assertEquals("Laptop", result.name);
    }

    @Test
    void testGetSingle_NotFound_Throws404() {
        when(productRepository.findById(99L)).thenReturn(null);
        WebApplicationException ex = assertThrows(WebApplicationException.class, 
            () -> productResource.getSingle(99L));
        assertEquals(404, ex.getResponse().getStatus());
    }

    @Test
    void testCreate_Success() {
        product.id = null; // Ensure ID is null for creation
        Response response = productResource.create(product);
        assertEquals(201, response.getStatus());
        verify(productRepository).persist(product);
    }

    @Test
    void testCreate_InvalidId_Throws422() {
        product.id = 1L; // Setting ID manually should fail
        WebApplicationException ex = assertThrows(WebApplicationException.class, 
            () -> productResource.create(product));
        assertEquals(422, ex.getResponse().getStatus());
    }

    @Test
    void testUpdate_Success() {
        Product existing = new Product();
        existing.name = "Old Name";
        
        when(productRepository.findById(1L)).thenReturn(existing);
        
        Product updatedData = new Product();
        updatedData.name = "New Name";
        
        Product result = productResource.update(1L, updatedData);
        
        assertEquals("New Name", result.name);
        verify(productRepository).persist(existing);
    }

    @Test
    void testUpdate_MissingName_Throws422() {
        product.name = null;
        WebApplicationException ex = assertThrows(WebApplicationException.class, 
            () -> productResource.update(1L, product));
        assertEquals(422, ex.getResponse().getStatus());
    }

    @Test
    void testDelete_Success() {
        when(productRepository.findById(1L)).thenReturn(product);
        Response response = productResource.delete(1L);
        assertEquals(204, response.getStatus());
        verify(productRepository).delete(product);
    }

    @Test
    void testDelete_NotFound_Throws404() {
        when(productRepository.findById(1L)).thenReturn(null);
        assertThrows(WebApplicationException.class, () -> productResource.delete(1L));
    }

    // --- Tests for ErrorMapper ---

    @Test
    void testErrorMapper_WebApplicationException() {
        ProductResource.ErrorMapper mapper = new ProductResource.ErrorMapper();
        mapper.objectMapper = this.objectMapper;
        
        WebApplicationException ex = new WebApplicationException("Custom Error", 404);
        Response response = mapper.toResponse(ex);
        
        assertEquals(404, response.getStatus());
        ObjectNode entity = (ObjectNode) response.getEntity();
        assertEquals("Custom Error", entity.get("error").asText());
    }

    @Test
    void testErrorMapper_GeneralException() {
        ProductResource.ErrorMapper mapper = new ProductResource.ErrorMapper();
        mapper.objectMapper = this.objectMapper;
        
        RuntimeException ex = new RuntimeException("Generic Failure");
        Response response = mapper.toResponse(ex);
        
        assertEquals(500, response.getStatus());
        ObjectNode entity = (ObjectNode) response.getEntity();
        assertEquals("Generic Failure", entity.get("error").asText());
    }
}