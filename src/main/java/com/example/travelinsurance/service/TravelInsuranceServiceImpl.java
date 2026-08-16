package com.example.travelinsurance.service;

import com.example.travelinsurance.dto.TravelInsuranceRequest;
import com.example.travelinsurance.entity.Customer;
import com.example.travelinsurance.entity.TravelInsurance;
import com.example.travelinsurance.repository.TravelInsuranceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.example.travelinsurance.dto.PriceCalculationRequest;
import com.example.travelinsurance.dto.PriceCalculationResponse;

@Service
public class TravelInsuranceServiceImpl implements TravelInsuranceService {

    private final TravelInsuranceRepository travelInsuranceRepository;
    private final CustomerService customerService;

    public TravelInsuranceServiceImpl(
            TravelInsuranceRepository travelInsuranceRepository,
            CustomerService customerService) {

        this.travelInsuranceRepository = travelInsuranceRepository;
        this.customerService = customerService;
    }


    @Override
    @Transactional
    public TravelInsurance createTravelInsurance(TravelInsuranceRequest request) {

        validateTravelInsurance(request);

        Customer customer =
                customerService.findOrCreateCustomer(
                        request.getCustomer()
                );


        BigDecimal price = calculatePrice(
                request.getPlan(),
                request.getCoverage(),
                request.getAreaOfTravel(),
                request.getStartDate(),
                request.getEndDate()
        );

        TravelInsurance travelInsurance = new TravelInsurance();

        travelInsurance.setPlan(request.getPlan());
        travelInsurance.setCoverage(request.getCoverage());
        travelInsurance.setStartDate(request.getStartDate());
        travelInsurance.setEndDate(request.getEndDate());
        travelInsurance.setAreaOfTravel(request.getAreaOfTravel());
        travelInsurance.setPrice(price);

        travelInsurance.setCustomer(customer);

        return travelInsuranceRepository.save(travelInsurance);

    }

    @Override
    public PriceCalculationResponse calculatePrice(PriceCalculationRequest request) {

        validatePriceCalculationRequest(request);

        BigDecimal price;

        if ("S".equals(request.getCoverage())) {

            BigDecimal dailyRate = getSingleTripDailyRate(
                    request.getPlan(),
                    request.getAreaOfTravel()
            );

            long numberOfDays = ChronoUnit.DAYS.between(
                    request.getStartDate(),
                    request.getEndDate()
            ) + 1;

            price = dailyRate.multiply(
                    BigDecimal.valueOf(numberOfDays)
            );

        } else {

            price = getAnnualRate(
                    request.getPlan(),
                    request.getAreaOfTravel()
            );
        }

        return new PriceCalculationResponse(price);
    }

    private void validatePriceCalculationRequest(
            PriceCalculationRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Request is required"
            );
        }

        if (!"A".equals(request.getPlan())
                && !"B".equals(request.getPlan())) {

            throw new IllegalArgumentException(
                    "Plan must be A or B"
            );
        }

        if (!"S".equals(request.getCoverage())
                && !"A".equals(request.getCoverage())) {

            throw new IllegalArgumentException(
                    "Coverage must be S or A"
            );
        }

        if (request.getAreaOfTravel() == null) {
            throw new IllegalArgumentException(
                    "Area of travel is required"
            );
        }

        if (request.getAreaOfTravel() < 1
                || request.getAreaOfTravel() > 4) {

            throw new IllegalArgumentException(
                    "Area of travel must be between 1 and 4"
            );
        }

        validateCoverageArea(
                request.getCoverage(),
                request.getAreaOfTravel()
        );

        if (request.getStartDate() == null) {

            throw new IllegalArgumentException(
                    "Start date is required"
            );
        }

        if (request.getEndDate() == null) {

            throw new IllegalArgumentException(
                    "End date is required"
            );
        }

        if (request.getEndDate()
                .isBefore(request.getStartDate())) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }
    }

    private void validateCoverageArea(
            String coverage,
            Integer areaOfTravel) {

        if ("A".equalsIgnoreCase(coverage)
                && areaOfTravel == 4) {

            throw new IllegalArgumentException(
                    "Area 4 is not available for annual coverage"
            );
        }
    }

    private void validateTravelInsurance(
            TravelInsuranceRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Travel insurance details are required"
            );
        }

        validatePlan(request.getPlan());

        validateCoverage(request.getCoverage());

        validateAreaOfTravel(request.getAreaOfTravel());

        validateCoverageArea(
                request.getCoverage(),
                request.getAreaOfTravel()
        );

        validateStartDate(request.getStartDate());
    }

    private void validatePlan(String plan) {

        if (plan == null || plan.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Plan is required"
            );
        }

        if (!plan.equalsIgnoreCase("A")
                && !plan.equalsIgnoreCase("B")) {

            throw new IllegalArgumentException(
                    "Plan must be either A or B"
            );
        }
    }

    private void validateCoverage(String coverage) {

        if (coverage == null || coverage.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Coverage is required"
            );
        }

        if (!coverage.equalsIgnoreCase("S")
                && !coverage.equalsIgnoreCase("A")) {

            throw new IllegalArgumentException(
                    "Coverage must be either S or A"
            );
        }
    }

    private void validateAreaOfTravel(Integer areaOfTravel) {

        if (areaOfTravel == null) {
            throw new IllegalArgumentException(
                    "Area of travel is required"
            );
        }

        if (areaOfTravel < 1 || areaOfTravel > 4) {
            throw new IllegalArgumentException(
                    "Area of travel must be between 1 and 4"
            );
        }
    }

    private void validateStartDate(LocalDate startDate) {

        if (startDate == null) {
            throw new IllegalArgumentException(
                    "Start date is required"
            );
        }

        LocalDate today = LocalDate.now();

        LocalDate maximumStartDate = today.plusYears(1);

        if (startDate.isBefore(today)) {
            throw new IllegalArgumentException(
                    "Start date cannot be before today"
            );
        }

        if (startDate.isAfter(maximumStartDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be more than 1 year from today"
            );
        }
    }

    private BigDecimal calculatePrice(
            String plan,
            String coverage,
            Integer areaOfTravel,
            LocalDate startDate,
            LocalDate endDate) {

        if (coverage.equalsIgnoreCase("S")) {

            BigDecimal dailyRate =
                    getSingleTripDailyRate(plan, areaOfTravel);

            long numberOfDays =
                    ChronoUnit.DAYS.between(
                            startDate,
                            endDate
                    ) + 1;

            return dailyRate.multiply(
                    BigDecimal.valueOf(numberOfDays)
            );
        }


        return getAnnualRate(plan, areaOfTravel);
    }

    private BigDecimal getSingleTripDailyRate(
            String plan,
            int area) {

        if (plan.equalsIgnoreCase("A")) {

            return switch (area) {
                case 1 -> BigDecimal.valueOf(10);
                case 2 -> BigDecimal.valueOf(15);
                case 3 -> BigDecimal.valueOf(20);
                case 4 -> BigDecimal.valueOf(5);
                default -> throw new IllegalArgumentException(
                        "Invalid area of travel"
                );
            };
        }

        return switch (area) {
            case 1 -> BigDecimal.valueOf(20);
            case 2 -> BigDecimal.valueOf(30);
            case 3 -> BigDecimal.valueOf(40);
            case 4 -> BigDecimal.valueOf(10);
            default -> throw new IllegalArgumentException(
                    "Invalid area of travel"
            );
        };
    }

    private BigDecimal getAnnualRate(
            String plan,
            int area) {

        if (plan.equalsIgnoreCase("A")) {

            return switch (area) {
                case 1 -> BigDecimal.valueOf(100);
                case 2 -> BigDecimal.valueOf(150);
                case 3 -> BigDecimal.valueOf(200);
                default -> throw new IllegalArgumentException(
                        "Annual coverage is not available for area 4"
                );
            };
        }

        return switch (area) {
            case 1 -> BigDecimal.valueOf(150);
            case 2 -> BigDecimal.valueOf(200);
            case 3 -> BigDecimal.valueOf(250);
            default -> throw new IllegalArgumentException(
                    "Annual coverage is not available for area 4"
            );
        };
    }
}
