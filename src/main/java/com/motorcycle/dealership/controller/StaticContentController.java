package com.motorcycle.dealership.controller;

import com.motorcycle.dealership.dto.PaginatedResponse;
import com.motorcycle.dealership.dto.staticcontent.Motorcycle;
import com.motorcycle.dealership.service.CountryService;
import com.motorcycle.dealership.service.MotorcycleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Static Content", description = "APIs for retrieving static data like countries and motorcycles")
public class StaticContentController {

    private final CountryService countryService;
    private final MotorcycleService motorcycleService;

    @GetMapping("/countries")
    @Operation(summary = "Get a list of all countries")
    public ResponseEntity<List<String>> getCountries() {
        return ResponseEntity.ok(countryService.getAllCountries());
    }

    @GetMapping("/motorcycles")
    @Operation(summary = "Get a paginated list of motorcycles")
    public ResponseEntity<PaginatedResponse<Motorcycle>> getMotorcycles(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(motorcycleService.getMotorcycles(page, limit));
    }
}
