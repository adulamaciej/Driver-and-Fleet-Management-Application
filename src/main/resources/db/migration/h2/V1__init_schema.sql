CREATE SEQUENCE driver_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE vehicle_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE drivers (
                         id INT PRIMARY KEY,
                         first_name VARCHAR(30) NOT NULL,
                         last_name VARCHAR(30) NOT NULL,
                         license_number VARCHAR(9) NOT NULL UNIQUE,
                         license_type VARCHAR(30) NOT NULL,
                         date_of_birth DATE NOT NULL,
                         phone_number VARCHAR(9) NOT NULL,
                         email VARCHAR(50) NOT NULL,
                         status VARCHAR(30) NOT NULL
);

CREATE TABLE vehicles (
                          id INT PRIMARY KEY,
                          license_plate VARCHAR(20) NOT NULL UNIQUE,
                          brand VARCHAR(20) NOT NULL,
                          model VARCHAR(20) NOT NULL,
                          production_year INT NOT NULL,
                          type VARCHAR(20) NOT NULL,
                          registration_date DATE NOT NULL,
                          technical_inspection_date DATE NOT NULL,
                          mileage DOUBLE NOT NULL,
                          status VARCHAR(20) NOT NULL,
                          driver_id INT,
                          FOREIGN KEY (driver_id) REFERENCES drivers(id)
);

-- Indexes
CREATE INDEX idx_driver_status ON drivers(status);
CREATE INDEX idx_vehicle_status ON vehicles(status);
CREATE INDEX idx_vehicle_type ON vehicles(type);
CREATE INDEX idx_driver_license_number ON drivers(license_number);
CREATE INDEX idx_vehicle_license_plate ON vehicles(license_plate);
CREATE INDEX idx_vehicle_driver_id ON vehicles(driver_id);