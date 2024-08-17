package com.example.demo.repository;

import com.example.demo.model.ParkingSpot;
import com.example.demo.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
    long countByIsOccupied(boolean isOccupied);

    @Query("SELECT ps FROM ParkingSpot ps WHERE ps.isOccupied = false")
    List<ParkingSpot> findAllAvailableSpots();
    @Query("SELECT ps FROM ParkingSpot ps WHERE ps.isOccupied = false AND ps.spotType = :vehicleType ORDER BY ps.id ASC")
    List<ParkingSpot> findAvailableSpots(@Param("vehicleType") VehicleType vehicleType);

}
