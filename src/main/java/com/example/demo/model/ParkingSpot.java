package com.example.demo.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "parking")
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "spottype", nullable = false)
    private VehicleType spotType;


    @Column(name = "isoccupied", nullable = false)
    private Boolean isOccupied;

    // Getters and setters
}

