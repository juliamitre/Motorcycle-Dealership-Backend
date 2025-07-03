package com.motorcycle.dealership.dto.transaction;

import com.motorcycle.dealership.entity.Role;
import com.motorcycle.dealership.entity.User;

public record UserResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    Role role
) {
    public static UserResponse fromUser(User user) {
        return new UserResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole()
        );
    }
}
