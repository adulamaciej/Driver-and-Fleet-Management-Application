package org.example.driverandfleetmanagementapp.weather;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Tag(name = "Weather & Driver Recommendations", description = "Weather information with driver safety recommendations")
public class WeatherController {


    private final WeatherRecommendationService weatherRecommendationService;


    @RateLimiter(name = "api")
    @GetMapping("/{city}")
    @Operation(
            summary = "Get detailed weather with driver recommendations",
            description = "Returns detailed weather information and driver safety recommendations for the specified city"
    )
    @ApiResponse(responseCode = "200", description = "Weather data retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid city parameter")
    @ApiResponse(responseCode = "503", description = "Weather service unavailable")
    public ResponseEntity<WeatherDto> getWeatherWithRating(@PathVariable String city) {
        return ResponseEntity.ok(weatherRecommendationService.getDetailedWeatherWithRating(city));
    }
}