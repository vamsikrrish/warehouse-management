package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ReplaceWarehouseUseCaseTest {

    @Mock
    private WarehouseStore warehouseStore;

    @InjectMocks
    private ReplaceWarehouseUseCase replaceWarehouseUseCase;

    @Test
    void testReplace_ShouldInvokeUpdateOnStore() {
        // Arrange: Create a warehouse object to represent the new data
        Warehouse newWarehouse = new Warehouse();
        newWarehouse.businessUnitCode = "WH-REPLACE-001";
        newWarehouse.location = "AMS-01";
        newWarehouse.capacity = 5000;

        // Act: Call the replace method
        replaceWarehouseUseCase.replace(newWarehouse);

        // Assert: Verify that the store's update method was called with the correct object
        verify(warehouseStore, times(1)).update(newWarehouse);
    }
}