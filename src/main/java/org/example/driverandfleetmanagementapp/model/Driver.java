package org.example.driverandfleetmanagementapp.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Driver {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LicenseType licenseType;


    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status;

    @Builder.Default
    @OneToMany(mappedBy = "driver")
    @BatchSize(size = 20)
    private List<Vehicle> vehicles = new ArrayList<>();

    public enum LicenseType {
        B, C, D, CE, DE
    }

    public enum DriverStatus {
        ACTIVE, ON_LEAVE, SUSPENDED, INACTIVE
    }
}
