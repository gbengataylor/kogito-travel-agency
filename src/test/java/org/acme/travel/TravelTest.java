package org.acme.travel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import io.quarkus.test.junit.QuarkusTest;
import org.acme.travel.Address;
import org.acme.travel.Flight;
import org.acme.travel.Hotel;
import org.acme.travel.Traveller;
import org.acme.travel.Trip;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class TravelTest {

   @Inject
   @Named("travels") // inject the travel request model
   Process<? extends Model> travelsProcess;

    @Test
    public void testTravelNoVisaRequired() {

       assertNotNull(travelsProcess);

       Model m = travelsProcess.createModel();
       Map<String, Object> parameters = new HashMap<>();
       parameters.put("traveller", new Traveller("John", "Doe", "john.doe@example.com", "American", new Address("main street", "Boston", "10005", "US")));
       parameters.put("trip", new Trip("New York", "US", new Date(), new Date()));

       m.fromMap(parameters);

       ProcessInstance<?> processInstance = travelsProcess.createInstance(m);
       processInstance.start();
       assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status());

       Model result = (Model) processInstance.variables();
       assertEquals(4, result.toMap().size());
       Hotel hotel = (Hotel) result.toMap().get("hotel");
       assertNotNull(hotel);
       assertEquals("Perfect hotel", hotel.getName());
       assertEquals("XX-012345", hotel.getBookingNumber());
       assertEquals("09876543", hotel.getPhone());

       Flight flight = (Flight) result.toMap().get("flight");
       assertNotNull(flight);
       assertEquals("MX555", flight.getFlightNumber());
       assertNotNull(flight.getArrival());
       assertNotNull(flight.getDeparture());

       List<WorkItem> workItems = processInstance.workItems();
       assertEquals(1, workItems.size());
       assertEquals("ConfirmTravel", workItems.get(0).getName()); // User based task

       processInstance.completeWorkItem(workItems.get(0).getId(), null);

       assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());
    }

    @Test
    public void testTravelVisaRequired() {

       assertNotNull(travelsProcess);

       Model m = travelsProcess.createModel();
       Map<String, Object> parameters = new HashMap<>();
       parameters.put("traveller", new Traveller("Jan", "Kowalski", "jan.kowalski@example.com", "Polish", new Address("polna", "Krakow", "32000", "Poland")));
       parameters.put("trip", new Trip("New York", "US", new Date(), new Date()));

       m.fromMap(parameters);

       ProcessInstance<?> processInstance = travelsProcess.createInstance(m);
       processInstance.start();
       assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status());

       List<WorkItem> workItems = processInstance.workItems();
       assertEquals(1, workItems.size());
       assertEquals("VisaApplication", workItems.get(0).getName()); // User based task

       processInstance.completeWorkItem(workItems.get(0).getId(), null);

       assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status());

       Model result = (Model) processInstance.variables();
       assertEquals(4, result.toMap().size());
       Hotel hotel = (Hotel) result.toMap().get("hotel");
       assertNotNull(hotel);
       assertEquals("Perfect hotel", hotel.getName());
       assertEquals("XX-012345", hotel.getBookingNumber());
       assertEquals("09876543", hotel.getPhone());

       Flight flight = (Flight) result.toMap().get("flight");
       assertNotNull(flight);
       assertEquals("MX555", flight.getFlightNumber());
       assertNotNull(flight.getArrival());
       assertNotNull(flight.getDeparture());

       workItems = processInstance.workItems();
       assertEquals(1, workItems.size());
       assertEquals("ConfirmTravel", workItems.get(0).getName()); // User based task

       processInstance.completeWorkItem(workItems.get(0).getId(), null);

       assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());
    }
}
