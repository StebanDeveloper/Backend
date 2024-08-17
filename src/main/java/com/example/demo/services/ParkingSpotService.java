package com.example.demo.services;

import com.example.demo.model.ParkingSpot;
import com.example.demo.repository.ParkingSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingSpotService {

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    public List<ParkingSpot> getAvailableParkingSpots() {
        return parkingSpotRepository.findAllAvailableSpots();
    }

}
