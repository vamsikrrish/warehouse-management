package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateWarehouseUseCaseTest {

    @Mock
    private WarehouseStore warehouseStore;

    @InjectMocks
    private CreateWarehouseUseCase createWarehouseUseCase;

    @Test
    void testCreate_ShouldInvokeStoreCreate() {
        // Arrange
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "WH-TEST-001";
        warehouse.location = "AMS-01";
        warehouse.capacity = 1500;
        warehouse.stock = 100;

        // Act
        createWarehouseUseCase.create(warehouse);

        // Assert
        verify(warehouseStore, times(1)).create(warehouse);
    }
}