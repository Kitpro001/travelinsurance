package com.example.travelinsurance.controller;

import com.example.travelinsurance.dto.TravelInsuranceRequest;
import com.example.travelinsurance.entity.TravelInsurance;
import com.example.travelinsurance.service.TravelInsuranceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.travelinsurance.dto.PriceCalculationRequest;
import com.example.travelinsurance.dto.PriceCalculationResponse;

@RestController
@RequestMapping("/api/travel-insurance")
@CrossOrigin(origins = "http://localhost:4200")
public class TravelInsuranceController {

    private final TravelInsuranceService travelInsuranceService;

    public TravelInsuranceController(
            TravelInsuranceService travelInsuranceService) {
        this.travelInsuranceService = travelInsuranceService;
    }

    @PostMapping
    public ResponseEntity<TravelInsurance> createTravelInsurance(
            @Valid @RequestBody TravelInsuranceRequest request) {

        TravelInsurance travelInsurance =
                travelInsuranceService.createTravelInsurance(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(travelInsurance);
    }

    @PostMapping("/calculate-price")
    public ResponseEntity<PriceCalculationResponse> calculatePrice(
            @RequestBody PriceCalculationRequest request) {

        PriceCalculationResponse response =
                travelInsuranceService.calculatePrice(request);

        return ResponseEntity.ok(response);
    }
}
