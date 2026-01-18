package com.fulfilment.application.monolith.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fulfilment.application.monolith.stores.LegacyStoreManagerGateway;
import com.fulfilment.application.monolith.stores.Store;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class LegacyStoreManagerGatewayTest {

    @InjectMocks
    LegacyStoreManagerGateway legacyStoreManagerGateway;

    private Store mockStore;

    @BeforeEach
    void setUp() {
        mockStore = new Store();
        mockStore.name = "TestStore";
        mockStore.quantityProductsInStock = 50;
    }

    @Test
    void testCreateStoreOnLegacySystem_Success() {
        
        assertDoesNotThrow(() -> {
            legacyStoreManagerGateway.createStoreOnLegacySystem(mockStore);
        });
    }

    @Test
    void testUpdateStoreOnLegacySystem_Success() {
        assertDoesNotThrow(() -> {
            legacyStoreManagerGateway.updateStoreOnLegacySystem(mockStore);
        });
    }

    @Test
    void testWriteToFile_HandleException() {
        Store invalidStore = new Store();
        invalidStore.name = null;

        assertDoesNotThrow(() -> {
            legacyStoreManagerGateway.createStoreOnLegacySystem(invalidStore);
        });
    }
}