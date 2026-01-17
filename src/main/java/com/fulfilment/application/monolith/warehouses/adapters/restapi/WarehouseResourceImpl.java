package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  @Inject private WarehouseRepository warehouseRepository;

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @Override
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse warehouse) {
	  //️ Business Unit Code must be unique (active warehouses only)
	    if (warehouseRepository.existsActive(warehouse.getBusinessUnitCode())) {
	      throw new WebApplicationException(
	          "Business Unit Code already exists", 409);
	    }

	    // Capacity & stock validation
	    if (warehouse.getStock() > warehouse.getCapacity()) {
	      throw new WebApplicationException(
	          "Stock cannot exceed capacity", 422);
	    }

	    warehouseRepository.create(com.fulfilment.application.monolith.warehouses.domain.models.Warehouse.getFrom(warehouse));

	    return warehouse;
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String businessUnitCode) {
	  com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =warehouseRepository.findByBusinessUnitCode(businessUnitCode);
	    if (warehouse == null) {
	        throw new WebApplicationException("Warehouse not found", 404);
	      }
	    return toWarehouseResponse(warehouse);
  }

  @Override
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
    var current =
        warehouseRepository.findByBusinessUnitCode(businessUnitCode);
    if (current == null) {
      throw new WebApplicationException(
          "Warehouse with businessUnitCode " + businessUnitCode + " does not exist",
          404);
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
    warehouseRepository.remove(current);
    var newWarehouse =
        new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    newWarehouse.businessUnitCode = warehouse.getBusinessUnitCode();
    newWarehouse.location = warehouse.getLocation();
    newWarehouse.capacity = warehouse.getCapacity();
    newWarehouse.stock = warehouse.getStock();
    newWarehouse.createdAt = java.time.LocalDateTime.now();
    warehouseRepository.create(newWarehouse);
    return toWarehouseResponse(newWarehouse);
  }


  private Warehouse toWarehouseResponse(
      com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new Warehouse();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);

    return response;
  }
  
}
