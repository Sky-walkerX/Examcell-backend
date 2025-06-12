package com.examcell.admin.dto;

import com.examcell.admin.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private User.Role role;

    // For student signup
    private String rollNo;
    private String semester;
    private String department;
    private String phoneNumber;
    private String address;

    // For teacher signup
    private String employeeId;
    private String designation;
    private String specialization;
}
