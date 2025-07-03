package com.motorcycle.dealership.dto;

import java.util.List;

// Using Java Generics <T> to make this response reusable for any type of data.
public record PaginatedResponse<T>(
    List<T> data,
    long total,
    int page,
    int limit
) {}
