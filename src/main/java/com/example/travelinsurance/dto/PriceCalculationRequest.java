package com.example.travelinsurance.dto;

import java.time.LocalDate;

public class PriceCalculationRequest {

    private String plan;
    private String coverage;
    private Integer areaOfTravel;
    private LocalDate startDate;
    private LocalDate endDate;

    public PriceCalculationRequest() {
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getCoverage() {
        return coverage;
    }

    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }

    public Integer getAreaOfTravel() {
        return areaOfTravel;
    }

    public void setAreaOfTravel(Integer areaOfTravel) {
        this.areaOfTravel = areaOfTravel;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}