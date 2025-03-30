INSERT INTO drivers (id, first_name, last_name, license_number, license_type, date_of_birth, phone_number, email, status) VALUES
                                                                                                                              (nextval('driver_sequence'),'Jan', 'Kowalski', '123456789', 'B', '1985-06-20', '123456789', 'jan.kowalski@example.com', 'ACTIVE'),
                                                                                                                              (nextval('driver_sequence'),'Anna', 'Nowak', '987654321', 'C',  '1990-03-15', '987654321', 'anna.nowak@example.com', 'ACTIVE'),
                                                                                                                              (nextval('driver_sequence'),'Piotr', 'Wiśniewski', '456789123', 'D',  '1982-11-30', '456789123', 'piotr.wisniewski@example.com', 'ON_LEAVE'),
                                                                                                                              (nextval('driver_sequence'),'Marta', 'Zielińska', '789123456', 'CE',  '1995-01-22', '789123456', 'marta.zielinska@example.com', 'ACTIVE'),
                                                                                                                              (nextval('driver_sequence'),'Tomasz', 'Wójcik', '654321987', 'B',  '1987-09-14', '654321987', 'tomasz.wojcik@example.com', 'INACTIVE'),
                                                                                                                              (nextval('driver_sequence'),'Kasia', 'Majewska', '321987654', 'DE',  '1992-12-12', '321987654', 'kasia.majewska@example.com', 'ACTIVE'),
                                                                                                                              (nextval('driver_sequence'),'Marcin', 'Szymański', '159753456', 'C',  '1980-02-28', '159753456', 'marcin.szymanski@example.com', 'ON_LEAVE'),
                                                                                                                              (nextval('driver_sequence'),'Agnieszka', 'Lewandowska', '456123789', 'D',  '1988-05-16', '456123789', 'agnieszka.lewandowska@example.com', 'ACTIVE'),
                                                                                                                              (nextval('driver_sequence'),'Grzegorz', 'Jankowski', '753159246', 'B',  '1994-03-11', '753159246', 'grzegorz.jankowski@example.com', 'INACTIVE'),
                                                                                                                              (nextval('driver_sequence'),'Ewa', 'Kamińska', '852456123', 'C',  '1981-07-19', '852456123', 'ewa.kaminska@example.com', 'ACTIVE'),
                                                                                                                              (nextval('driver_sequence'),'Karol', 'Dąbrowski', '741852963', 'D',  '1979-01-30', '741852963', 'karol.dabrowski@example.com', 'ACTIVE'),
                                                                                                                              (nextval('driver_sequence'),'Magdalena', 'Kwiatkowska', '963852741', 'CE',  '1993-11-01', '963852741', 'magdalena.kwiatkowska@example.com', 'ACTIVE'),
                                                                                                                              (nextval('driver_sequence'),'Robert', 'Pawlak', '258369147', 'B',  '1986-08-25', '258369147', 'robert.pawlak@example.com', 'INACTIVE'),
                                                                                                                              (nextval('driver_sequence'),'Dorota', 'Michalska', '147258369', 'DE',  '1990-04-09', '147258369', 'dorota.michalska@example.com', 'ACTIVE'),
                                                                                                                              (nextval('driver_sequence'),'Krzysztof', 'Zając', '369147258', 'C',  '1984-05-13', '369147258', 'krzysztof.zajac@example.com', 'ACTIVE');
INSERT INTO vehicles (id, license_plate, brand, model, production_year, type, registration_date, technical_inspection_date, mileage, status, driver_id) VALUES
                                                                                                                                                            (nextval('vehicle_sequence'),'ABC12345', 'Toyota', 'Corolla', 2015, 'CAR', '2016-01-10', '2025-04-20', 120000, 'IN_USE', 1),
                                                                                                                                                            (nextval('vehicle_sequence'),'DEF23456', 'Ford', 'Focus', 2018, 'CAR', '2019-03-20', '2025-07-15', 90000, 'AVAILABLE', 1),
                                                                                                                                                            (nextval('vehicle_sequence'),'GHI34567', 'Volkswagen', 'Passat', 2017, 'CAR', '2018-05-18', '2025-09-10', 110000, 'IN_SERVICE', 2),
                                                                                                                                                            (nextval('vehicle_sequence'),'JKL45678', 'Renault', 'Clio', 2020, 'CAR', '2020-10-30', '2026-11-05', 30000, 'AVAILABLE', 2),
                                                                                                                                                            (nextval('vehicle_sequence'),'MNO56789', 'Scania', 'R450', 2019, 'TRUCK', '2020-07-01', '2025-05-25', 200000, 'IN_USE', 3),
                                                                                                                                                            (nextval('vehicle_sequence'),'PQR67890', 'MAN', 'TGS', 2016, 'TRUCK', '2017-09-12', '2025-10-15', 320000, 'IN_USE', 3),
                                                                                                                                                            (nextval('vehicle_sequence'),'STU78901', 'Mercedes', 'Sprinter', 2018, 'VAN', '2018-12-09', '2025-08-20', 100000, 'AVAILABLE', 4),
                                                                                                                                                            (nextval('vehicle_sequence'),'VWX89012', 'Iveco', 'Daily', 2021, 'VAN', '2021-04-22', '2027-01-30', 45000, 'IN_USE', 4),
                                                                                                                                                            (nextval('vehicle_sequence'),'YZA90123', 'Volvo', 'FH16', 2015, 'TRUCK', '2015-11-11', '2024-12-31', 400000, 'IN_SERVICE', 5),
                                                                                                                                                            (nextval('vehicle_sequence'),'BCD12345', 'Dacia', 'Duster', 2020, 'CAR', '2021-03-03', '2025-06-01', 80000, 'AVAILABLE', 5),
                                                                                                                                                            (nextval('vehicle_sequence'),'EFG23456', 'Fiat', 'Ducato', 2019, 'VAN', '2020-06-18', '2025-04-16', 150000, 'IN_USE', 6),
                                                                                                                                                            (nextval('vehicle_sequence'),'HIJ34567', 'Peugeot', 'Partner', 2022, 'VAN', '2022-07-12', '2026-12-01', 30000, 'IN_USE', 6),
                                                                                                                                                            (nextval('vehicle_sequence'),'KLM45678', 'Mazda', 'CX-5', 2021, 'CAR', '2021-08-15', '2026-09-19', 40000, 'AVAILABLE', 7),
                                                                                                                                                            (nextval('vehicle_sequence'),'NOP56789', 'Hyundai', 'Tucson', 2018, 'CAR', '2019-02-05', '2024-05-15', 95000, 'IN_SERVICE', 8),
                                                                                                                                                            (nextval('vehicle_sequence'),'QRS67890', 'Renault', 'Trafic', 2020, 'VAN', '2021-01-20', '2026-03-25', 70000, 'IN_USE', 9),
                                                                                                                                                            (nextval('vehicle_sequence'),'TUV67891', 'Tesla', 'Model S', 2022, 'CAR', '2022-05-10', '2027-05-15', 25000, 'AVAILABLE', 10),
                                                                                                                                                            (nextval('vehicle_sequence'),'UVW45678', 'BMW', 'X5', 2021, 'CAR', '2021-11-01', '2026-10-20', 60000, 'IN_USE', 11),
                                                                                                                                                            (nextval('vehicle_sequence'),'XYZ78912', 'Audi', 'A6', 2019, 'CAR', '2020-03-19', '2025-12-10', 85000, 'IN_SERVICE', 12),
                                                                                                                                                            (nextval('vehicle_sequence'),'ABD12367', 'Opel', 'Astra', 2017, 'CAR', '2018-08-14', '2024-09-18', 95000, 'AVAILABLE', 13),
                                                                                                                                                            (nextval('vehicle_sequence'),'BCD45679', 'Suzuki', 'Vitara', 2020, 'CAR', '2021-02-10', '2025-03-16', 50000, 'IN_USE', 14);