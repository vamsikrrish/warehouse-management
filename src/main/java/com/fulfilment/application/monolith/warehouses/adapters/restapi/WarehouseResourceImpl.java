package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import java.util.List;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  @Inject
  WarehouseRepository warehouseRepository;

  @Inject
  LocationGateway locationGateway; 


  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll()
        .stream()
        .map(this::toWarehouseResponse)
        .toList();
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String businessUnitCode) {
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
        warehouseRepository.findByBusinessUnitCode(businessUnitCode);

    if (warehouse == null) {
      throw new WebApplicationException("Warehouse not found", 404);
    }

    return toWarehouseResponse(warehouse);
  }

  @Override
  @Transactional
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse warehouse) {

    if (warehouseRepository.existsActive(warehouse.getBusinessUnitCode())) {
      throw new WebApplicationException(
          "Business Unit Code already exists", 409);
    }

    Location location = locationGateway.resolveByIdentifier(warehouse.getLocation());
    
    if (location==null) {
      throw new WebApplicationException("Invalid warehouse location", 422);
    }
   
    Long currentWareHouseCount = warehouseRepository.countActiveByLocation(warehouse.getLocation());
    
    if (currentWareHouseCount>location.getMaxNumberOfWarehouses()) {
      throw new WebApplicationException(
          "Maximum number of warehouses reached for location", 422);
    }

    if (warehouse.getStock() > warehouse.getCapacity()) {
      throw new WebApplicationException("Stock cannot exceed capacity", 422);
    }

    if (warehouse.getCapacity() > location.getMaxCapacity()) {
      throw new WebApplicationException( "Warehouse capacity exceeds location max capacity", 422);
    }

    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse newWareHouse = 
    		com.fulfilment.application.monolith.warehouses.domain.models.Warehouse.getFrom(warehouse);

    warehouseRepository.create(newWareHouse);

    return warehouse;
  }

  @Override
  @Transactional
  public void archiveAWarehouseUnitByID(String businessUnitCode) {

    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
        warehouseRepository.findByBusinessUnitCode(businessUnitCode);

    if (warehouse == null) {
      throw new WebApplicationException("Warehouse not found", 404);
    }

    warehouseRepository.remove(warehouse);
  }

  @Override
  @Transactional
  public Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode, @NotNull Warehouse warehouse) {

    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse current =
        warehouseRepository.findByBusinessUnitCode(businessUnitCode);

    if (current == null) {
      throw new WebApplicationException(
          "Warehouse with businessUnitCode " + businessUnitCode + " does not exist",
          404);
    }

    Location location = locationGateway.resolveByIdentifier(warehouse.getLocation());

    if (location==null) {
      throw new WebApplicationException("Invalid warehouse location", 422);
    }
   
    if (!current.stock.equals(warehouse.getStock())) {
      throw new WebApplicationException(
          "Stock of the new warehouse must match the existing warehouse",
          422);
    }

    if (warehouse.getCapacity() < current.stock) {
      throw new WebApplicationException(
          "Warehouse capacity cannot be less than existing stock",
          422);
    }

    if (warehouse.getCapacity() > location.getMaxCapacity()) {
      throw new WebApplicationException( "Warehouse capacity exceeds location limit",422);
    }

    warehouseRepository.remove(current);

    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse newWareHouse = 
    		com.fulfilment.application.monolith.warehouses.domain.models.Warehouse.getFrom(warehouse);

    warehouseRepository.create(newWareHouse);

    return toWarehouseResponse(newWareHouse);
  }

 
  private Warehouse toWarehouseResponse(com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    Warehouse response = new Warehouse();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);
    return response;
  }
}
