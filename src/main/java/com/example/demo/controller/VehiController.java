package com.example.demo.controller;

import com.example.demo.model.ParkingSpot;
import com.example.demo.model.Vehicle;
import com.example.demo.repository.ParkingSpotRepository;
import com.example.demo.repository.VehicuRepository;
import com.example.demo.services.ParkingSpotService;
import com.example.demo.services.VehiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app")
public class VehiController {
    private static final Logger logger = LoggerFactory.getLogger(VehiController.class);


    @Autowired
    private VehicuRepository vehicleRepository;

    @Autowired
    private ParkingSpotRepository parkingRepository;


    @Autowired
    private VehiServices vehiServices;

    @Autowired
    private ParkingSpotService parkingServices;


    @GetMapping("/available")
    public List<ParkingSpot> getAllParkingSpot() {
        return parkingServices.getAvailableParkingSpots();
    }

    @GetMapping("/vehicles")
    public List<Vehicle> getRegisteredVehicles() {
        return vehiServices.getAllVehicles();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addVehicle(@RequestBody Vehicle vehicle) {
        return vehiServices.updateVehicle(vehicle);
    }

    @PutMapping("/finalize/{vehicleId}")
    public Map<String, Object> finalizeVehicle(@PathVariable Long vehicleId) {
        return vehiServices.finalizeVehicle(vehicleId);
    }


    @PostMapping("/finalize-all")
    public ResponseEntity<Map<String, Object>> finalizeAllVehicles() {
        double totalRevenue = vehiServices.finalizeAllVehiclesAndCalculateTotal();

        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", totalRevenue);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{vehicleId}")
    public void deleteVehicle(@PathVariable Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        ParkingSpot parkingSpot = vehicle.getParkingSpot();
        if (parkingSpot != null) {
            parkingSpot.setIsOccupied(false);
            parkingRepository.save(parkingSpot);
        }

        vehicleRepository.delete(vehicle);
    }



}
