package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "licenseplate", nullable = false, unique = true)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private VehicleType type;

    @Column(name = "is_electric_or_hybrid", nullable = false)
    private boolean isElectricOrHybrid;

    @Column(name = "entrytime", nullable = false)
    private LocalDateTime entryTime;

    @Column(name = "exittime")
    private LocalDateTime exitTime;

    @ManyToOne
    @JoinColumn(name = "parking_spot_id")
    private ParkingSpot parkingSpot;

    @Transient
    private double totalCost;
}
