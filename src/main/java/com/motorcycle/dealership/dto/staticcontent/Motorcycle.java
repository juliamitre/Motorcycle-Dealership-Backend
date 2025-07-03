package com.motorcycle.dealership.dto.staticcontent;

// Using a record for a simple, immutable data carrier.
public record Motorcycle(
    String name,
    String image,
    String specs,
    String info,
    String price
) {}
