package com.fulfilment.application.monolith.warehouses.domain.models;

import java.time.LocalDateTime;

public class Warehouse {

  // unique identifier
  public String businessUnitCode;

  public String location;

  public Integer capacity;

  public Integer stock;

  public LocalDateTime createdAt;

  public LocalDateTime archivedAt;
  
  public static Warehouse getFrom(com.warehouse.api.beans.Warehouse data) {
	  Warehouse warehouse = new Warehouse();
	  warehouse.businessUnitCode=data.getBusinessUnitCode();
	  warehouse.capacity = data.getCapacity();
	  warehouse.location = data.getLocation();
	  warehouse.stock = data.getStock();
	  return warehouse;
  }
}
