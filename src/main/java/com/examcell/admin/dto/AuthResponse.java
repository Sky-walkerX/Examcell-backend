package com.examcell.admin.dto;

import com.examcell.admin.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private User.Role role;
    private Long userId;
    private Long studentId;
    private Long teacherId;
    private String fullName;
    private String email;
    private String message;
}
