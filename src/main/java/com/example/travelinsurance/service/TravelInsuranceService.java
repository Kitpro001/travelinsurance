package com.example.travelinsurance.service;

import com.example.travelinsurance.dto.TravelInsuranceRequest;
import com.example.travelinsurance.entity.TravelInsurance;
import com.example.travelinsurance.dto.PriceCalculationRequest;
import com.example.travelinsurance.dto.PriceCalculationResponse;

public interface TravelInsuranceService {

    TravelInsurance createTravelInsurance(TravelInsuranceRequest request);

    PriceCalculationResponse calculatePrice(PriceCalculationRequest request);
}
