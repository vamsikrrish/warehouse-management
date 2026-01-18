package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.warehouse.api.beans.Warehouse;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class WarehouseResourceImplTest {

    @Mock
    WarehouseRepository warehouseRepository;

    @Mock
    LocationGateway locationGateway;

    @InjectMocks
    WarehouseResourceImpl warehouseResource;

    @Test
    void testListAllWarehousesUnits() {
        var domainWh = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
        domainWh.businessUnitCode = "WH001";
        
        when(warehouseRepository.getAll()).thenReturn(List.of(domainWh));

        List<Warehouse> result = warehouseResource.listAllWarehousesUnits();
        
        assertEquals(1, result.size());
        assertEquals("WH001", result.get(0).getBusinessUnitCode());
    }

    @Test
    void testGetAWarehouseUnitByID_NotFound() {
        when(warehouseRepository.findByBusinessUnitCode("NONE")).thenReturn(null);
        
        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> 
            warehouseResource.getAWarehouseUnitByID("NONE")
        );
        assertEquals(404, ex.getResponse().getStatus());
    }

    @Test
    void testCreateANewWarehouseUnit_Conflict() {
        Warehouse input = new Warehouse();
        input.setBusinessUnitCode("EXISTING");
        
        when(warehouseRepository.existsActive("EXISTING")).thenReturn(true);

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> 
            warehouseResource.createANewWarehouseUnit(input)
        );
        assertEquals(409, ex.getResponse().getStatus());
    }

    @Test
    void testCreateANewWarehouseUnit_InvalidLocation() {
        Warehouse input = new Warehouse();
        input.setBusinessUnitCode("NEW");
        input.setLocation("MARS");
        
        when(warehouseRepository.existsActive("NEW")).thenReturn(false);
        when(locationGateway.resolveByIdentifier("MARS")).thenReturn(null);

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> 
            warehouseResource.createANewWarehouseUnit(input)
        );
        assertEquals(422, ex.getResponse().getStatus());
    }

    @Test
    void testCreateANewWarehouseUnit_Success() {
        Warehouse input = new Warehouse();
        input.setBusinessUnitCode("WH001");
        input.setLocation("LON");
        input.setCapacity(100);
        input.setStock(50);

        Location loc = new Location("LON", 200, 500);

        when(warehouseRepository.existsActive("WH001")).thenReturn(false);
        when(locationGateway.resolveByIdentifier("LON")).thenReturn(loc);
        when(warehouseRepository.countActiveByLocation("LON")).thenReturn(1L);

        Warehouse result = warehouseResource.createANewWarehouseUnit(input);

        assertNotNull(result);
        verify(warehouseRepository).create(any());
    }
}