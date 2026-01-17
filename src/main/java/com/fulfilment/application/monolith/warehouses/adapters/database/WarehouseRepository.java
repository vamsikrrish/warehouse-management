package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public List<Warehouse> getAll() {
    return this.listAll().stream().map(DbWarehouse::toWarehouse).toList();
    
  }

  @Override
  public void create(Warehouse warehouse) {
	  persist(DbWarehouse.fromWarehouse(warehouse));
  }

  @Override
  public void update(Warehouse warehouse) {
	  DbWarehouse entity =
		        find("businessUnitCode = ?1 and archivedAt is null",
		                warehouse.businessUnitCode)
		            .firstResultOptional()
		            .orElseThrow(
		                () -> new IllegalStateException("Warehouse not found"));

		    entity.updateFrom(warehouse);
  }

  @Override
  public void remove(Warehouse warehouse) {
	  DbWarehouse entity =
		        find("businessUnitCode = ?1 and archivedAt is null",
		                warehouse.businessUnitCode)
		            .firstResultOptional()
		            .orElseThrow(
		                () -> new IllegalStateException("Warehouse not found"));

		    entity.archivedAt = LocalDateTime.now();
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
	  return find("businessUnitCode = ?1 and archivedAt is null", buCode)
		        .firstResultOptional()
		        .map(DbWarehouse::toWarehouse)
		        .orElse(null);
  }
  
  public boolean existsActive(String businessUnitCode) {
	    return count(
	            "businessUnitCode = ?1 and archivedAt is null", businessUnitCode)
	        > 0;
	  }

	  public long countActiveByLocation(String location) {
	    return count("location = ?1 and archivedAt is null", location);
	  }
}
