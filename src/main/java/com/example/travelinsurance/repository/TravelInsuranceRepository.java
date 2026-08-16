package com.example.travelinsurance.repository;

import com.example.travelinsurance.entity.TravelInsurance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelInsuranceRepository extends JpaRepository<TravelInsurance, Long> {
}
