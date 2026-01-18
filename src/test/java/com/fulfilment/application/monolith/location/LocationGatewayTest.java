package com.fulfilment.application.monolith.location;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;

public class LocationGatewayTest {

  @Test
  public void testWhenResolveExistingLocationShouldReturn() {
    LocationGateway locationGateway = new LocationGateway();

    Location location = locationGateway.resolveByIdentifier("ZWOLLE-001");

    assertEquals(location.identification, "ZWOLLE-001");
  }
  
  @Test
  public void testWhenResolveNonExistingLocationShouldReturn() {
    LocationGateway locationGateway = new LocationGateway();

    Location location = locationGateway.resolveByIdentifier("TEST");
    assertNull(location);
  }
  
}
