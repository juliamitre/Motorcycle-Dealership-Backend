package com.motorcycle.dealership.dto.auth;

import com.motorcycle.dealership.entity.User;
public record UserResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    String role
) {
    public static UserResponse fromUser(User user) {
        return new UserResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole().name() // assuming Role has a getName()
        );
    }
}
