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
class ArchiveWarehouseUseCaseTest {

    @Mock
    private WarehouseStore warehouseStore;

    @InjectMocks
    private ArchiveWarehouseUseCase archiveWarehouseUseCase;

    @Test
    void testArchive_ShouldCallUpdateOnStore() {
        // Arrange
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "WH-001";
        warehouse.location = "NLR-01";

        // Act
        archiveWarehouseUseCase.archive(warehouse);

        // Assert: Verify that the warehouseStore.update() was called exactly once with the correct object
        verify(warehouseStore, times(1)).update(warehouse);
    }
}