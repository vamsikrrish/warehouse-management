package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DbWarehouseTest {

    @Test
    void testToWarehouse_ShouldMapAllFields() {
        // Arrange
        DbWarehouse entity = new DbWarehouse();
        entity.id = 1L;
        entity.businessUnitCode = "BU001";
        entity.location = "Location A";
        entity.capacity = 1000;
        entity.stock = 500;
        entity.createdAt = LocalDateTime.now();
        entity.archivedAt = null;

        // Act
        Warehouse domain = entity.toWarehouse();

        // Assert
        assertEquals(entity.businessUnitCode, domain.businessUnitCode);
        assertEquals(entity.location, domain.location);
        assertEquals(entity.capacity, domain.capacity);
        assertEquals(entity.stock, domain.stock);
        assertEquals(entity.createdAt, domain.createdAt);
        assertEquals(entity.archivedAt, domain.archivedAt);
    }

    @Test
    void testFromWarehouse_ShouldCreateEntity() {
        // Arrange
        Warehouse domain = new Warehouse();
        domain.businessUnitCode = "BU002";
        domain.location = "Location B";
        domain.capacity = 2000;
        domain.stock = 100;
        domain.createdAt = LocalDateTime.now().minusDays(1);

        // Act
        DbWarehouse entity = DbWarehouse.fromWarehouse(domain);

        // Assert
        assertEquals(domain.businessUnitCode, entity.businessUnitCode);
        assertEquals(domain.location, entity.location);
        assertEquals(domain.capacity, entity.capacity);
        assertEquals(domain.stock, entity.stock);
        assertEquals(domain.createdAt, entity.createdAt);
    }

    @Test
    void testFromWarehouse_NullCreatedAt_ShouldSetCurrentTime() {
        // Arrange
        Warehouse domain = new Warehouse();
        domain.createdAt = null;

        // Act
        DbWarehouse entity = DbWarehouse.fromWarehouse(domain);

        // Assert
        assertNotNull(entity.createdAt);
        assertTrue(entity.createdAt.isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testUpdateFrom_ShouldModifyFields() {
        // Arrange
        DbWarehouse entity = new DbWarehouse();
        entity.location = "Old Location";
        entity.capacity = 100;
        entity.stock = 10;

        Warehouse updateData = new Warehouse();
        updateData.location = "New Location";
        updateData.capacity = 500;
        updateData.stock = 250;

        // Act
        entity.updateFrom(updateData);

        // Assert
        assertEquals("New Location", entity.location);
        assertEquals(500, entity.capacity);
        assertEquals(250, entity.stock);
    }
}