package org.example.driverandfleetmanagementapp.weather;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeatherRecommendationService {

    private final WeatherService weatherService;

    public WeatherDto getDetailedWeatherWithRating(String city) {
        WeatherData data = weatherService.getCurrentWeather(city);


        if (data == null || data.getMain() == null || data.getWeather().isEmpty()) {
            return new WeatherDto(
                    city, 0, 0, "Unknown", "Cannot give an advice");
        }


        double temp = data.getMain().getTemp();
        int humidity = data.getMain().getHumidity();
        String condition = data.getWeather().getFirst().getMain();
        String cityName = data.getName();

        String driverRating = calculateRating(temp, condition.toLowerCase());

        return new WeatherDto(
                cityName, temp, humidity, condition, driverRating
        );
    }

    private String calculateRating(double temp, String condition) {
        if (condition.contains("snow") || temp < -5) {
            return "Snow - possible snowed roads";
        } else if (condition.contains("rain") || condition.contains("thunderstorm")) {
            return "Raining - possible slippery roads";
        } else if (temp < 5) {
            return "Low temperature - possible frost on the road";
        } else if (condition.contains("fog") || condition.contains("mist")) {
            return "Fog - possible limited visibility";
        } else {
            return "No warnings - good conditions";
        }
    }
}
