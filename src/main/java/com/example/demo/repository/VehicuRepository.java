package com.example.demo.repository;

import com.example.demo.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface VehicuRepository  extends JpaRepository<Vehicle, Long> {
        List<Vehicle> findAll();
}
