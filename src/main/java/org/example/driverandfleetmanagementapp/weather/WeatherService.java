package org.example.driverandfleetmanagementapp.weather;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.example.driverandfleetmanagementapp.exception.custom.CityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WebClient webClient;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;


    @Timed("fleet.weather.api.call.time")
    public WeatherData getCurrentWeather(String city) {
        try {
            return webClient.get()
                    .uri(apiUrl + "?q=" + city + "&appid=" + apiKey + "&units=metric")
                    .retrieve()
                    .bodyToMono(WeatherData.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new CityNotFoundException("City '" + city + "' not found.");
        }
    }
}