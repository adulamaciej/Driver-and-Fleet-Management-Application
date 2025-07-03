package org.example.driverandfleetmanagementapp.weather;

import lombok.Data;
import lombok.AllArgsConstructor;


@Data
@AllArgsConstructor
public class WeatherDto {

    private String city;
    private double temperature;
    private int humidity;
    private String condition;
    private String driverRating;
}