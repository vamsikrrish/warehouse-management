package com.fulfilment.application.monolith.warehouses.adapters.database;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.adapters.restapi.WarehouseResourceImpl;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.warehouse.api.beans.Warehouse;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class WarehouseRepositoryTest {

    @Mock
    WarehouseRepository warehouseRepository;

    @Mock
    LocationGateway locationGateway;

    @InjectMocks
    WarehouseResourceImpl warehouseResource;

    private Warehouse warehouseBean;
    private Location mockLocation;
    private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domainWarehouse;

    @BeforeEach
    void setUp() {
      
        warehouseBean = new Warehouse();
        warehouseBean.setBusinessUnitCode("BU-123");
        warehouseBean.setLocation("LOC-1");
        warehouseBean.setCapacity(100);
        warehouseBean.setStock(50);

        domainWarehouse = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
        domainWarehouse.businessUnitCode = "BU-123";
        domainWarehouse.location = "LOC-1";
        domainWarehouse.capacity = 100;
        domainWarehouse.stock = 50;

         mockLocation = new Location("LON", 500, 10);
    }

    @Test
    void testListAllWarehouses() {
        when(warehouseRepository.getAll()).thenReturn(List.of(domainWarehouse));
        List<Warehouse> result = warehouseResource.listAllWarehousesUnits();
        assertEquals(1, result.size());
        assertEquals("BU-123", result.get(0).getBusinessUnitCode());
    }

    @Test
    void testGetByID_Success() {
        when(warehouseRepository.findByBusinessUnitCode("BU-123")).thenReturn(domainWarehouse);
        Warehouse result = warehouseResource.getAWarehouseUnitByID("BU-123");
        assertNotNull(result);
    }

    @Test
    void testGetByID_NotFound_Throws404() {
        when(warehouseRepository.findByBusinessUnitCode(anyString())).thenReturn(null);
        WebApplicationException ex = assertThrows(WebApplicationException.class, 
            () -> warehouseResource.getAWarehouseUnitByID("ERR"));
        assertEquals(404, ex.getResponse().getStatus());
    }

    @Test
    void testCreate_Conflict_Throws409() {
        when(warehouseRepository.existsActive("BU-123")).thenReturn(true);
        WebApplicationException ex = assertThrows(WebApplicationException.class, 
            () -> warehouseResource.createANewWarehouseUnit(warehouseBean));
        assertEquals(409, ex.getResponse().getStatus());
    }

    @Test
    void testCreate_InvalidLocation_Throws422() {
        when(locationGateway.resolveByIdentifier(anyString())).thenReturn(null);
        WebApplicationException ex = assertThrows(WebApplicationException.class, 
            () -> warehouseResource.createANewWarehouseUnit(warehouseBean));
        assertEquals(422, ex.getResponse().getStatus());
        assertEquals("Invalid warehouse location", ex.getMessage());
    }

    @Test
    void testCreate_MaxWarehousesReached_Throws422() {
        mockLocation.setMaxNumberOfWarehouses(2);
        when(locationGateway.resolveByIdentifier(anyString())).thenReturn(mockLocation);
        when(warehouseRepository.countActiveByLocation(anyString())).thenReturn(3L);

        WebApplicationException ex = assertThrows(WebApplicationException.class, 
            () -> warehouseResource.createANewWarehouseUnit(warehouseBean));
        assertEquals(422, ex.getResponse().getStatus());
    }

    @Test
    void testCreate_StockExceedsCapacity_Throws422() {
        warehouseBean.setStock(200);
        warehouseBean.setCapacity(100);
        when(locationGateway.resolveByIdentifier(anyString())).thenReturn(mockLocation);

        WebApplicationException ex = assertThrows(WebApplicationException.class, 
            () -> warehouseResource.createANewWarehouseUnit(warehouseBean));
        assertTrue(ex.getMessage().contains("Stock cannot exceed capacity"));
    }

    @Test
    void testArchive_Success() {
        when(warehouseRepository.findByBusinessUnitCode("BU-123")).thenReturn(domainWarehouse);
        warehouseResource.archiveAWarehouseUnitByID("BU-123");
        verify(warehouseRepository).remove(domainWarehouse);
    }

    @Test
    void testReplace_StockMismatch_Throws422() {
        domainWarehouse.stock = 50;
        warehouseBean.setStock(60);  
        
        when(warehouseRepository.findByBusinessUnitCode("BU-123")).thenReturn(domainWarehouse);
        when(locationGateway.resolveByIdentifier(anyString())).thenReturn(mockLocation);

        WebApplicationException ex = assertThrows(WebApplicationException.class, 
            () -> warehouseResource.replaceTheCurrentActiveWarehouse("BU-123", warehouseBean));
        assertEquals(422, ex.getResponse().getStatus());
    }

    @Test
    void testReplace_CapacityTooLow_Throws422() {
        domainWarehouse.stock = 80;
        warehouseBean.setStock(80);
        warehouseBean.setCapacity(70); 
        
        when(warehouseRepository.findByBusinessUnitCode("BU-123")).thenReturn(domainWarehouse);
        when(locationGateway.resolveByIdentifier(anyString())).thenReturn(mockLocation);

        WebApplicationException ex = assertThrows(WebApplicationException.class, 
            () -> warehouseResource.replaceTheCurrentActiveWarehouse("BU-123", warehouseBean));
        assertEquals(422, ex.getResponse().getStatus());
    }
}