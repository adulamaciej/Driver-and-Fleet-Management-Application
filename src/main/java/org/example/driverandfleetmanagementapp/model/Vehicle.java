package org.example.driverandfleetmanagementapp.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;


@Entity
@Table(name = "vehicles")
@SQLDelete(sql = "UPDATE vehicles SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
public class Vehicle {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_seq")
    @SequenceGenerator(name = "vehicle_seq", sequenceName = "vehicle_sequence", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String licensePlate;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer productionYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    @Column(nullable = false)
    private LocalDate registrationDate;

    @Column(nullable = false)
    private LocalDate technicalInspectionDate;

    @Column(nullable = false)
    private Double mileage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;


    public enum VehicleType {
        CAR, VAN, TRUCK, BUS
    }

    public enum VehicleStatus {
        AVAILABLE, IN_USE, IN_SERVICE, OUT_OF_ORDER
    }

    @Column(nullable = false)
    private boolean deleted = false;


}