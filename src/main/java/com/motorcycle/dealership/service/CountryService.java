package com.motorcycle.dealership.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CountryService {

    private static final List<String> COUNTRIES = Arrays.asList(
        "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Argentina", "Australia",
        "Austria", "Bahamas", "Bangladesh", "Belarus", "Belgium", "Brazil", "Bulgaria",
        "Canada", "Chile", "China", "Colombia", "Croatia", "Cuba", "Cyprus", "Czech Republic",
        "Denmark", "Egypt", "Estonia", "Finland", "France", "Germany", "Greece", "Hungary",
        "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy",
        "Jamaica", "Japan", "Kazakhstan", "Kenya", "Latvia", "Lebanon", "Libya", "Lithuania",
        "Luxembourg", "Malaysia", "Mexico", "Morocco", "Netherlands", "New Zealand", "Nigeria",
        "Norway", "Pakistan", "Peru", "Philippines", "Poland", "Portugal", "Qatar", "Romania",
        "Russia", "Saudi Arabia", "Serbia", "Singapore", "Slovakia", "Slovenia", "South Africa",
        "South Korea", "Spain", "Sweden", "Switzerland", "Syria", "Taiwan", "Thailand", "Tunisia",
        "Turkey", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States",
        "Uruguay", "Venezuela", "Vietnam", "Yemen", "Zambia", "Zimbabwe"
        // Add more countries as needed
    );

    public List<String> getAllCountries() {
        return COUNTRIES;
    }
}
