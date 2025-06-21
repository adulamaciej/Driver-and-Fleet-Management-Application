package org.example.driverandfleetmanagementapp.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;


@Entity
@Table(name = "drivers")
@SQLDelete(sql = "UPDATE drivers SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id") // prevents StackOverFlowError
@Builder(toBuilder = true)
public class Driver {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "driver_seq")
    @SequenceGenerator(name = "driver_seq", sequenceName = "driver_sequence", allocationSize = 1)
    private Long id;

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
    private Set<Vehicle> vehicles = new LinkedHashSet<>();

    public enum LicenseType {
        B, C, D, CE, DE
    }

    public enum DriverStatus {
        ACTIVE, ON_LEAVE, SUSPENDED, INACTIVE
    }

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

}
