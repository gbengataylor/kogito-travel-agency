package org.acme.travel.service;

import javax.enterprise.context.ApplicationScoped;

import org.acme.travel.Address;
import org.acme.travel.Hotel;
import org.acme.travel.Trip;

@ApplicationScoped
public class HotelBookingService {
    public Hotel getHotel(Trip trip) {
        return new Hotel("Perfect hotel", 
         new Address("street", "city", "90210", "USA"), 
        "09876543", "XX-012345");
    }
}