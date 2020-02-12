package org.acme.travel.service;

import javax.enterprise.context.ApplicationScoped;

import org.acme.travel.Flight;
import org.acme.travel.Trip;
import java.util.Date;

@ApplicationScoped
public class FlightBookingService {
    public Flight getFlight(Trip trip) {
        return new Flight("MX555", "8F", "35", new Date(), new Date());
    }
}