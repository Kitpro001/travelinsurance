package com.example.travelinsurance.service;

import com.example.travelinsurance.dto.CustomerRequest;
import com.example.travelinsurance.entity.Customer;
import com.example.travelinsurance.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer findOrCreateCustomer(CustomerRequest request) {

        validateCustomer(request);

        return customerRepository.findByNric(request.getNric())
                .orElseGet(() -> {

                    Customer customer = new Customer();

                    customer.setFullName(request.getFullName());
                    customer.setNric(request.getNric());
                    customer.setDob(getDobFromNric(request.getNric()));
                    customer.setGender(getGenderFromNric(request.getNric()));
                    customer.setEmail(request.getEmail());
                    customer.setMobileNo(request.getMobileNo());
                    customer.setAddressLine1(request.getAddressLine1());
                    customer.setAddressLine2(request.getAddressLine2());
                    customer.setPostCode(request.getPostCode());

                    return customerRepository.save(customer);
                });

    }

    private void validateCustomer(CustomerRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Customer details are required");
        }


        validateFullName(request.getFullName());

        validateNric(request.getNric());

        validateEmail(request.getEmail());

        validateMobileNo(request.getMobileNo());

        validateAddress(request.getAddressLine1(), request.getAddressLine2());

        validatePostCode(request.getPostCode());

        LocalDate nricDob = getDobFromNric(request.getNric());
        String nricGender = getGenderFromNric(request.getNric());

        if (request.getDob() != null && !request.getDob().equals(nricDob)) {
            throw new IllegalArgumentException(
                    "DOB does not match the NRIC"
            );
        }

        if (request.getGender() != null &&
                !request.getGender().equalsIgnoreCase(nricGender)) {

            throw new IllegalArgumentException(
                    "Gender does not match the NRIC"
            );
        }
    }

    private void validateFullName(String fullName) {

        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Full name is required"
            );
        }

        if (fullName.length() > 50) {
            throw new IllegalArgumentException(
                    "Full name must not exceed 50 characters"
            );
        }

        if (fullName.matches(".*\\d.*")) {
            throw new IllegalArgumentException(
                    "Full name must not contain numbers"
            );
        }

        if (!fullName.matches("[a-zA-ZÀ-ÿ'\\- ]+")) {
            throw new IllegalArgumentException(
                    "Full name can only contain letters, spaces, apostrophes and hyphens"
            );
        }
    }

    private void validateNric(String nric) {

        if (nric == null || nric.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "NRIC is required"
            );
        }

        if (!nric.matches("\\d{12}")) {
            throw new IllegalArgumentException(
                    "NRIC must contain exactly 12 digits"
            );
        }

        getDobFromNric(nric);
    }

    private LocalDate getDobFromNric(String nric) {

        String datePart = nric.substring(0, 6);

        String yearPart = datePart.substring(0, 2);
        String monthPart = datePart.substring(2, 4);
        String dayPart = datePart.substring(4, 6);

        int year = Integer.parseInt(yearPart);
        int month = Integer.parseInt(monthPart);
        int day = Integer.parseInt(dayPart);

        int currentYear = LocalDate.now().getYear() % 100;

        int fullYear;

        if (year > currentYear) {
            fullYear = 1900 + year;
        } else {
            fullYear = 2000 + year;
        }

        try {
            return LocalDate.of(fullYear, month, day);
        } catch (java.time.DateTimeException e) {
            throw new IllegalArgumentException(
                    "Invalid date in NRIC"
            );
        }
    }

    private String getGenderFromNric(String nric) {

        int lastDigit = Character.getNumericValue(
                nric.charAt(nric.length() - 1)
        );

        if (lastDigit % 2 == 1) {
            return "M";
        }

        return "F";
    }

    private void validateEmail(String email) {

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        String emailRegex =
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException(
                    "Invalid email format"
            );
        }
    }

    private void validateMobileNo(String mobileNo) {

        if (mobileNo == null || mobileNo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mobile number is required"
            );
        }

        if (!mobileNo.matches("01\\d{7,9}")) {
            throw new IllegalArgumentException(
                    "Mobile number must start with 01 and contain 9 to 11 digits"
            );
        }
    }

    private void validateAddress(
            String addressLine1,
            String addressLine2) {

        if (addressLine1 == null ||
                addressLine1.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Address Line 1 is required"
            );
        }
    }

    private void validatePostCode(String postCode) {

        if (postCode == null || postCode.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Postcode is required"
            );
        }

        if (!postCode.matches("\\d{5}")) {
            throw new IllegalArgumentException(
                    "Postcode must contain exactly 5 digits"
            );
        }
    }
}
