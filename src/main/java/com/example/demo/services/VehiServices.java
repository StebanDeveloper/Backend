package com.example.demo.services;

import com.example.demo.model.ParkingSpot;
import com.example.demo.model.Vehicle;
import com.example.demo.repository.ParkingSpotRepository;
import com.example.demo.repository.VehicuRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VehiServices {

    private static final double MOTORCYCLE_COST_PER_HOUR = 62.0;
    private static final double LIGHT_VEHICLE_COST_PER_HOUR = 120.0;
    @Autowired
    private VehicuRepository vehicleRepository;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    public Vehicle registerVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }


    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> updateVehicle(Vehicle vehicle) {
        Map<String, Object> response = new HashMap<>();
        if (vehicle.getType() == null) {
            throw new RuntimeException("TIPO VEHICULO REQUERIDO");
        }

        List<ParkingSpot> availableSpots = parkingSpotRepository.findAvailableSpots(vehicle.getType());
        if (availableSpots.isEmpty()) {
            response.put("message", "NO HAY CUPO");
            return ResponseEntity.ok(response);
        }

        ParkingSpot parkingSpot = availableSpots.get(0);
        parkingSpot.setIsOccupied(true);
        parkingSpotRepository.save(parkingSpot);
        vehicle.setParkingSpot(parkingSpot);
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        response.put("vehicle", updatedVehicle);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public Map<String, Object> finalizeVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        if (vehicle.getExitTime() == null) {
            vehicle.setExitTime(LocalDateTime.now());
        }

        double cost = calculateCost(vehicle);

        // Actualiza la plaza de aparcamiento para que esté libre
        ParkingSpot parkingSpot = vehicle.getParkingSpot();
        if (parkingSpot != null) {
            parkingSpot.setIsOccupied(false);
            parkingSpotRepository.save(parkingSpot);
        }

        // Crear un mapa con la información del vehículo y el costo calculado
        Map<String, Object> response = new HashMap<>();
        response.put("id", vehicle.getId());
        response.put("licensePlate", vehicle.getLicensePlate());
        response.put("type", vehicle.getType().toString());
        response.put("entryTime", vehicle.getEntryTime());
        response.put("exitTime", vehicle.getExitTime());
        response.put("totalCost", cost);

        return response;
    }

    private double calculateCost(Vehicle vehicle) {
        LocalDateTime entryTime = vehicle.getEntryTime();
        LocalDateTime exitTime = vehicle.getExitTime();
        if (entryTime == null || exitTime == null) {
            throw new RuntimeException("Tiempo inicial null");
        }

        long hours = Duration.between(entryTime, exitTime).toHours();
        if (hours < 0) {
            throw new RuntimeException("Hora salida antes de inicial ");
        }

        double cost;
        switch (vehicle.getType()) {
            case MOTORCYCLE:
                cost = hours * MOTORCYCLE_COST_PER_HOUR;
                break;
            case CAR:
                cost = hours * LIGHT_VEHICLE_COST_PER_HOUR;
                break;
            default:
                throw new RuntimeException("Otros");
        }
        if (vehicle.isElectricOrHybrid()) {
            cost *= 0.75;
        }

        return cost;
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    @Transactional
    public double finalizeAllVehiclesAndCalculateTotal() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        double totalRevenue = 0.0;

        for (Vehicle vehicle : vehicles) {
            if (vehicle.getExitTime() == null) {
                vehicle.setExitTime(LocalDateTime.now());
            }

            double cost = calculateCost(vehicle);

            ParkingSpot parkingSpot = vehicle.getParkingSpot();
            if (parkingSpot != null) {
                parkingSpot.setIsOccupied(false);
                parkingSpotRepository.save(parkingSpot);
            }
            totalRevenue += cost;
        }

        vehicleRepository.saveAll(vehicles);

        return totalRevenue;
    }
}
